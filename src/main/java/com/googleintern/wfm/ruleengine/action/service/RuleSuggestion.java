package src.main.java.com.googleintern.wfm.ruleengine.action.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.opencsv.exceptions.CsvException;
import src.main.java.com.googleintern.wfm.ruleengine.action.*;
import src.main.java.com.googleintern.wfm.ruleengine.action.generator.CasePoolIdAndPermissionIdRuleGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.action.generator.WorkgroupIdRuleGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

import java.io.IOException;

/** RuleSuggestionService class is used to suggest rules based on the input data reading. */
public class RuleSuggestion implements RuleSuggestionService {
  private static final String CSV_INPUT_FILE_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/data/support_test_agents_anonymized.csv";

  private static final String CSV_VALIDATION_RESULT_FILE_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/output/validate_results.csv";

  private static final String CSV_OUTPUT_FILE_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/output/generated_rules.csv";

  public static void main(String[] args) throws IOException, CsvException {
    RuleSuggestion ruleSuggestion = new RuleSuggestion();
    ruleSuggestion.suggestRules(CSV_INPUT_FILE_PATH);
  }

  /**
   * suggestRules function is used to generate new rules based on data at the csvFilePath.
   *
   * <p>Steps:
   *
   * <ol>
   *   <li>Step 1: Read user data from the input csvFilePath. Save the reading results in list as
   *       {@link UserModel}.
   *   <li>Step 2: Filter out valid {@link UserModel} data.
   *   <li>Step 3: Group valid user data by their work group ID.
   *   <li>Step 4: Find out general {@link RuleModel} that can cover all {@link UserModel} from the
   *       same work group ID.
   *   <li>Step 5: Group existing combinations of {@link FilterModel} from the same work group ID by
   *       {@link PoolAssignmentModel}.
   *   <li>Step 6: Run reduction algorithm on each {@link PoolAssignmentModel} and generate {@link
   *       RuleModel} based on reduced results.
   *   <li>Step 7: Write generated rules into output file at CSV_OUTPUT_FILE_PATH location.
   *   <li>Step 8: Valid the performance of generated rules and write validation results at
   *       CSV_VALIDATION_RESULT_FILE_PATH location.
   * </ol>
   */
  @Override
  public String suggestRules(String csvFilePath) throws IOException, CsvException {
    ImmutableList<UserModel> userPoolAssignments = CsvParser.readFromCSVFile(csvFilePath);

    ImmutableList<UserModel> validUserPoolAssignments =
        DataProcessor.filterValidData(userPoolAssignments);

    ImmutableSet<RuleModel> rules = suggestRules(validUserPoolAssignments);

    CsvWriter.writeDataIntoCsvFile(CSV_OUTPUT_FILE_PATH, rules.asList());

    RuleValidation ruleValidation = new RuleValidation(validUserPoolAssignments);
    ruleValidation.validate(rules).writeToCsvFile(CSV_VALIDATION_RESULT_FILE_PATH);
    return null;
  }

  @Override
  public String suggestRules(String csvFilePath, int percentage) {
    return null;
  }

  private ImmutableSet<RuleModel> suggestRules(ImmutableList<UserModel> validUserPoolAssignments) {
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
            reduceFiltersToCreateRules(
                filtersByCasePoolIdAndPermissionSetId, poolAssignment, 1033L, workgroupId));
      }
    }
    return rulesBuilder.build();
  }

  private ImmutableSet<PoolAssignmentModel> findPoolAssignmentsCoveredByRules(
      ImmutableSet<RuleModel> generalRulesForWorkgroupId) {
    ImmutableSet.Builder<PoolAssignmentModel> coveredPoolAssignmentsBuilder =
        ImmutableSet.builder();
    for (RuleModel rule : generalRulesForWorkgroupId) {
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

  private ImmutableSet<RuleModel> reduceFiltersToCreateRules(
      ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
          filtersByCasePoolIdAndPermissionSetId,
      PoolAssignmentModel poolAssignment,
      Long workforceId,
      Long workgroupId) {
    ImmutableList<ImmutableSet<FilterModel>> reducedFilters =
        FiltersReduction.reduce(filtersByCasePoolIdAndPermissionSetId, poolAssignment);
    return CasePoolIdAndPermissionIdRuleGenerator.generateRules(
        workforceId, workgroupId, poolAssignment, reducedFilters);
  }
}
