package src.main.java.com.googleintern.wfm.ruleengine.action.service;

import com.google.common.collect.*;
import com.opencsv.exceptions.CsvException;
import src.main.java.com.googleintern.wfm.ruleengine.action.*;
import src.main.java.com.googleintern.wfm.ruleengine.model.*;

import java.io.IOException;

public class RuleSuggestion implements RuleSuggestionService {
  @Override
  public String suggestRules(String csvFilePath) throws IOException, CsvException {
    ImmutableList<UserModel> userPoolAssignments = CsvParser.readFromCSVFile(csvFilePath);

    ImmutableList<UserModel> validUserPoolAssignments =
        DataProcessor.filterValidData(userPoolAssignments);
    System.out.println(validUserPoolAssignments.size());

    ImmutableListMultimap<Long, UserModel> usersByWorkgroupId =
        WorkgroupIdGroupingUtil.groupByWorkGroupId(validUserPoolAssignments);

    ImmutableSet.Builder<RuleModel> rulesBuilder = ImmutableSet.builder();

    for (Long workgroupId : usersByWorkgroupId.keySet()) {
      ImmutableSet<RuleModel> generalRulesForWorkgroupId =
          WorkgroupIdRuleGenerator.generateWorkgroupIdRules(usersByWorkgroupId, workgroupId);
      rulesBuilder.addAll(generalRulesForWorkgroupId);
      ImmutableSet<PoolAssignmentModel> coveredPoolAssignments =
          findPoolAssignmentsCoveredByRules(generalRulesForWorkgroupId);

      ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
          filtersByCasePoolIdAndPermissionSetId =
              CasePoolIdAndPermissionIdGroupingUtil.groupByCasePoolIdAndPermissionSetId(
                  usersByWorkgroupId.get(workgroupId));

      for (PoolAssignmentModel poolAssignment : filtersByCasePoolIdAndPermissionSetId.keySet()) {
        if (coveredPoolAssignments.contains(poolAssignment)) {
          continue;
        }
        rulesBuilder.addAll(
            ProductOfSumReduction(
                filtersByCasePoolIdAndPermissionSetId, poolAssignment, 1033L, workgroupId));
      }
    }
    ImmutableSet<RuleModel> rules = rulesBuilder.build();

    String CSV_OUTPUT_FILE_PATH =
        System.getProperty("user.home")
            + "/Project/wfm-rule-suggestion-engine/output/generated_rules.csv";
    CsvWriter.writeDataIntoCsvFile(CSV_OUTPUT_FILE_PATH, rules.asList());

    String CSV_VALIDATION_RESULT_FILE_PATH =
        System.getProperty("user.home")
            + "/Project/wfm-rule-suggestion-engine/output/validate_results.csv";
    RuleValidation ruleValidation = new RuleValidation(validUserPoolAssignments);
    RuleValidationReport ruleValidationReport = ruleValidation.validate(rules);
    ruleValidationReport.writeToCsvFile(CSV_VALIDATION_RESULT_FILE_PATH);
    return null;
  }

  private ImmutableSet<PoolAssignmentModel> findPoolAssignmentsCoveredByRules(
      ImmutableSet<RuleModel> rules) {
    ImmutableSet.Builder<PoolAssignmentModel> coveredPoolAssignmentsBuilder =
        ImmutableSet.builder();
    for (RuleModel rule : rules) {
      for (Long permissionSetId : rule.permissionSetIds()) {
        coveredPoolAssignmentsBuilder.add(
            PoolAssignmentModel.builder()
                .setCasePoolId(rule.casePoolId())
                .setPermissionSetId(permissionSetId)
                .build());
      }
    }
    return coveredPoolAssignmentsBuilder.build();
  }

  private ImmutableSet<RuleModel> ProductOfSumReduction(
      ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
          filtersByCasePoolIdAndPermissionSetId,
      PoolAssignmentModel poolAssignment,
      Long workforceId,
      Long workgroupId) {
    ImmutableList<ImmutableSet<FilterModel>> reducedFilters =
        ProductOfSumReduction.reduce(filtersByCasePoolIdAndPermissionSetId, poolAssignment);
    return ProductOfSumReduction.generateRules(
        workforceId, workgroupId, poolAssignment, reducedFilters);
  }

  @Override
  public String suggestRules(String csvFilePath, int percentage) {
    return null;
  }

  public static void main(String[] args) throws IOException, CsvException {
    String CSV_FILE_PATH =
        System.getProperty("user.home")
            + "/Project/wfm-rule-suggestion-engine/data/support_test_agents_anonymized.csv";
    RuleSuggestion ruleSuggestion = new RuleSuggestion();
    ruleSuggestion.suggestRules(CSV_FILE_PATH);
  }
}
