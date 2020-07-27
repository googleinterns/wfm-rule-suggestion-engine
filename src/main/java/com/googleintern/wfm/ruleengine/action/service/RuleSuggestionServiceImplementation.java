package src.main.java.com.googleintern.wfm.ruleengine.action.service;

import com.google.common.collect.*;
import com.opencsv.exceptions.CsvException;
import src.main.java.com.googleintern.wfm.ruleengine.action.*;
import src.main.java.com.googleintern.wfm.ruleengine.action.generator.CasePoolIdAndPermissionIdRuleGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.action.generator.RuleIdGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.action.generator.WorkgroupIdRuleGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/** RuleSuggestionService class is used to suggest rules based on the input data reading. */
public class RuleSuggestionServiceImplementation implements RuleSuggestionService {
  private static final String CSV_INPUT_FILE_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/data/support_test_agents_anonymized.csv";

  private static final String CSV_OUTPUT_FILE_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/output/rule_suggestion_results.csv";

  private static RuleIdGenerator RULE_ID_GENERATOR = new RuleIdGenerator();

  public static void main(String[] args) throws IOException, CsvException {
    RuleSuggestionServiceImplementation ruleSuggestion = new RuleSuggestionServiceImplementation();
    ruleSuggestion.suggestRules(CSV_INPUT_FILE_PATH, false);
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

    ImmutableList<UserModel> usersWithValidWorkgroupId =
        DataProcessor.filterUsersWithValidWorkgroupId(userPoolAssignments);

    ImmutableSet<RuleModel> rules = suggestRules(usersWithValidWorkgroupId);

    ImmutableSet<RuleModel> concentratedRules = RuleConcentration.concentrate(rules);

    RuleValidation ruleValidation = new RuleValidation(usersWithValidWorkgroupId);
    RuleValidationReport ruleValidationReport = ruleValidation.validate(concentratedRules);
    ruleValidationReport.writeToCsvFile(CSV_OUTPUT_FILE_PATH);

    return ruleValidationReport.convertRuleValidationReportToString();
  }

  @Override
  public String suggestRules(String csvFilePath, boolean assignMorePermissions)
      throws IOException, CsvException {
    ImmutableList<UserModel> userPoolAssignments = CsvParser.readFromCSVFile(csvFilePath);

    ImmutableList<UserModel> usersWithValidWorkgroupId =
        DataProcessor.filterUsersWithValidWorkgroupId(userPoolAssignments);

    ImmutableList<UserModel> validUsers =
        assignMorePermissions
            ? usersWithValidWorkgroupId
            : DataProcessor.removeConflictUsers(usersWithValidWorkgroupId);

    ImmutableSet<RuleModel> rules = suggestRules(validUsers);

    ImmutableSet<RuleModel> concentratedRules = RuleConcentration.concentrate(rules);

    RuleValidation ruleValidation = new RuleValidation(usersWithValidWorkgroupId);
    RuleValidationReport ruleValidationReport = ruleValidation.validate(concentratedRules);
    ruleValidationReport.writeToCsvFile(CSV_OUTPUT_FILE_PATH);
    return ruleValidationReport.convertRuleValidationReportToString();
  }

  private ImmutableSet<RuleModel> suggestRules(List<UserModel> validUserPoolAssignments) {
    if (validUserPoolAssignments.isEmpty()) {
      return ImmutableSet.of();
    }
    ImmutableListMultimap<Long, UserModel> usersByWorkgroupId =
        WorkgroupIdGroupingUtil.groupByWorkGroupId(validUserPoolAssignments);
    ImmutableSet.Builder<RuleModel> rulesBuilder = ImmutableSet.builder();
    for (Long workgroupId : usersByWorkgroupId.keySet()) {
      ImmutableSet<RuleModel> workgroupIdRulesWithEmptyFilters =
          WorkgroupIdRuleGenerator.generateWorkgroupIdRules(
              usersByWorkgroupId.get(workgroupId), RULE_ID_GENERATOR);
      rulesBuilder.addAll(workgroupIdRulesWithEmptyFilters);
      ImmutableSet<PoolAssignmentModel> poolAssignmentCoveredByWorkgroupIdRules =
          findPoolAssignmentsCoveredByRules(workgroupIdRulesWithEmptyFilters);

      ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
          filtersByPoolAssignments =
              CasePoolIdAndPermissionIdGroupingUtil.groupByCasePoolIdAndPermissionSetId(
                  usersByWorkgroupId.get(workgroupId));

      for (PoolAssignmentModel poolAssignment : filtersByPoolAssignments.keySet()) {
        if (poolAssignmentCoveredByWorkgroupIdRules.contains(poolAssignment)) {
          continue;
        }
        rulesBuilder.addAll(
            reduceFiltersToCreateRules(
                filtersByPoolAssignments,
                poolAssignment,
                validUserPoolAssignments.get(0).workforceId(),
                workgroupId));
      }
    }
    return rulesBuilder.build();
  }

  private ImmutableSet<PoolAssignmentModel> findPoolAssignmentsCoveredByRules(
      Set<RuleModel> generalRulesForWorkgroupId) {
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
      SetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>> filterByPoolAssignment,
      PoolAssignmentModel poolAssignment,
      Long workforceId,
      Long workgroupId) {
    ImmutableList<ImmutableSet<FilterModel>> reducedFilters =
        FiltersReduction.reduce(filterByPoolAssignment, poolAssignment);
    return CasePoolIdAndPermissionIdRuleGenerator.generateRules(
        workforceId, workgroupId, poolAssignment, reducedFilters, RULE_ID_GENERATOR);
  }
}
