package src.main.java.com.googleintern.wfm.ruleengine.action.service;

import com.google.common.collect.*;
import com.opencsv.exceptions.CsvException;
import src.main.java.com.googleintern.wfm.ruleengine.action.*;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

import java.io.IOException;

public class RuleSuggestion implements RuleSuggestionService {
  @Override
  public String suggestRules(String csvFilePath) throws IOException, CsvException {
    ImmutableList<UserModel> userPoolAssignments = CsvParser.readFromCSVFile(csvFilePath);
    System.out.println("Size of userPoolAssignments: " + userPoolAssignments.size());

    ImmutableList<UserModel> validUserPoolAssignments =
        DataProcessor.filterValidData(userPoolAssignments);
    System.out.println("Size of validUserPoolAssignments: " + validUserPoolAssignments.size());

    ImmutableListMultimap<Long, UserModel> usersByWorkgroupId =
        WorkgroupIdGroupingUtil.groupByWorkGroupId(validUserPoolAssignments);
    System.out.println("Number of workgroup IDs: " + usersByWorkgroupId.keySet().size());

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
      System.out.println("------------------------------------------------------------------------");
      System.out.println("Workgroup ID: " + workgroupId);
      System.out.println("Number of covered pool assignments: " + coveredPoolAssignments.size());
      System.out.println("Number of pool assignments: " + filtersByCasePoolIdAndPermissionSetId.keySet().size());
      for (PoolAssignmentModel poolAssignment : filtersByCasePoolIdAndPermissionSetId.keySet()) {
        if (coveredPoolAssignments.contains(poolAssignment)) continue;
        System.out.println("PoolAssignment: " + poolAssignment.casePoolId() + "   " + poolAssignment.permissionSetId());
//        ImmutableBiMap<FilterModel, Integer> filterByIndex =
//                KarnaughMapTermGenerator.mapFiltersByIndex(
//                        filtersByCasePoolIdAndPermissionSetId, poolAssignment);
//        System.out.println("Number of different filters: " + filterByIndex.keySet().size());
        rulesBuilder.add(
            karnaughMapAlgorithm(
                filtersByCasePoolIdAndPermissionSetId, poolAssignment, workgroupId, workgroupId));
      }
    }
    ImmutableSet<RuleModel> rules = rulesBuilder.build();
    System.out.println("-------------------------------------------------------------------------");
    System.out.println("Number of Rules: " + rules.size());

    String CSV_OUTPUT_FILE_PATH =
            System.getProperty("user.home")
                    + "/Project/wfm-rule-suggestion-engine/output/generated_rules.csv";
    CsvWriter.writeDataIntoCsvFile(CSV_OUTPUT_FILE_PATH, rules.asList());
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

  private RuleModel karnaughMapAlgorithm(
      ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
          filtersByCasePoolIdAndPermissionSetId,
      PoolAssignmentModel poolAssignment,
      Long workforceId,
      Long workgroupId) {
    ImmutableBiMap<FilterModel, Integer> filterByIndex =
        KarnaughMapTermGenerator.mapFiltersByIndex(
            filtersByCasePoolIdAndPermissionSetId, poolAssignment);
    System.out.println("Number of different filters: " + filterByIndex.keySet().size());
    ImmutableSet<ImmutableList<Integer>> allZeroTerms =
        KarnaughMapTermGenerator.findAllZeroTerms(
            filterByIndex, filtersByCasePoolIdAndPermissionSetId.get(poolAssignment));
    System.out.println("Finish finding all zero terms.");
    ImmutableSet<ImmutableList<Integer>> minimizedTerms =
        KarnaughMapReduction.minimizeKMapTerms(allZeroTerms);
    System.out.println("Finish Minimizing.");
    return KarnaughMapRuleGenerator.generateSingleRule(
        minimizedTerms,
        filterByIndex,
        workforceId,
        workgroupId,
        poolAssignment.casePoolId(),
        ImmutableSet.of(poolAssignment.casePoolId()));
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
