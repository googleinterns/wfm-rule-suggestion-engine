package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Sets;
import com.opencsv.CSVWriter;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RuleValidation class is used to test the performance of generated rules and write validation
 * results into a csv file.
 */
public class RuleValidation {
  enum Separator {
    COMMA(","),
    SQUARE_BRACKET_LEFT("["),
    SQUARE_BRACKET_RIGHT("]"),
    CURLY_BRACKET_LEFT("{"),
    CURLY_BRACKET_RIGHT("}"),
    DOUBLE_QUOTATION_MARK("\"");

    final String symbol;

    Separator(String symbol) {
      this.symbol = symbol;
    }
  }
  /** Generated rules */
  public static ImmutableSet<RuleModel> rules;
  /** Map permissions assigned by generated rules to each user */
  public static ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
      actualAssignedPermissionsByUserPoolAssignment;

  /** Constant string variables used for result outputs. */
  private static final String RULE_COVERAGE_PERCENT_HEADER = "Coverage % for Rule Set:";

  private static final String[] POOL_ASSIGNMENT_HEADER =
      new String[] {"Case Pool ID", "Permission Set ID"};
  private static final String[] WRONG_USER_POOL_ASSIGNMENT_HEADER =
      new String[] {
        "User Id",
        "Workforce Id",
        "Workgroup Id",
        "Role Id",
        "Skill Id",
        "Expected Pool Assignments",
        "Actual Pool Assignments"
      };
  private static final String PERCENTAGE_SIGN = "%";
  private static final String CASE_POOL_ID_PREFIX =
      Separator.CURLY_BRACKET_LEFT.symbol + "\"case_pool_id\":\"";
  private static final String PERMISSION_SET_ID_PREFIX = "\",\"permission_set_id\":\"";

  public RuleValidation(
      Set<RuleModel> rules, List<UserPoolAssignmentModel> expectedUserPoolAssignments) {
    this.rules = ImmutableSet.copyOf(rules);
    this.actualAssignedPermissionsByUserPoolAssignment =
        assignPermissionsBasedOnGeneratedRules(expectedUserPoolAssignments);
  }

  public static void writeRuleValidationResultsIntoCsvFile(
      String outputCsvFilePath, List<UserPoolAssignmentModel> expectedUserPoolAssignments)
      throws IOException {
    File outputFile = new File(outputCsvFilePath);
    Files.deleteIfExists(outputFile.toPath());
    outputFile.createNewFile();

    FileWriter outputFileWriter = new FileWriter(outputFile);
    CSVWriter csvWriter = new CSVWriter(outputFileWriter);

    ImmutableList<String[]> validationResult =
        convertRuleValidationResultsToCsvRows(expectedUserPoolAssignments);

    csvWriter.writeAll(validationResult);
    csvWriter.close();
  }

  public static double calculateRulesCoverage(
      Set<UserPoolAssignmentModel> usersWithWrongAssignedPermissions,
      List<UserPoolAssignmentModel> expectedUserPoolAssignments) {
    double numberOfWrongAssignment = usersWithWrongAssignedPermissions.size();
    double totalNumberOfUsers = expectedUserPoolAssignments.size();
    return (totalNumberOfUsers - numberOfWrongAssignment) / totalNumberOfUsers;
  }

  public static ImmutableSet<UserPoolAssignmentModel> findUncoveredUsers(
      List<UserPoolAssignmentModel> expectedUserPoolAssignments) {
    return expectedUserPoolAssignments.stream()
        .filter(user -> isUserUncovered(user))
        .collect(ImmutableSet.toImmutableSet());
  }

  private static boolean isUserUncovered(UserPoolAssignmentModel user) {
    if ((!actualAssignedPermissionsByUserPoolAssignment.containsKey(user)
            && !user.poolAssignments().isEmpty())
        || (!user.poolAssignments()
            .equals(actualAssignedPermissionsByUserPoolAssignment.get(user)))) {
      return true;
    }
    return false;
  }

  public static ImmutableSet<PoolAssignmentModel> findUncoveredPoolAssignments(
      List<UserPoolAssignmentModel> expectedUserPoolAssignments) {
    ImmutableSet<PoolAssignmentModel> expectedAllPoolAssignments =
        findExpectedAllPoolAssignments(expectedUserPoolAssignments);
    ImmutableSet<PoolAssignmentModel> actualAllPoolAssignments = findActualAllPoolAssignments();
    return ImmutableSet.copyOf(
        Sets.difference(expectedAllPoolAssignments, actualAllPoolAssignments));
  }

  private static ImmutableSet<PoolAssignmentModel> findExpectedAllPoolAssignments(
      List<UserPoolAssignmentModel> expectedUserPoolAssignments) {
    return expectedUserPoolAssignments.stream()
        .map(user -> user.poolAssignments())
        .reduce(ImmutableSet.of(), (union, current) -> Sets.union(union, current).immutableCopy());
  }

  private static ImmutableSet<PoolAssignmentModel> findActualAllPoolAssignments() {
    return rules.stream()
        .flatMap(
            rule ->
                rule.permissionSetIds().stream()
                    .map(
                        permissionSetId ->
                            PoolAssignmentModel.builder()
                                .setCasePoolId(rule.casePoolId())
                                .setPermissionSetId(permissionSetId)
                                .build()))
        .collect(ImmutableSet.toImmutableSet());
  }

  private static ImmutableList<String[]> convertRuleValidationResultsToCsvRows(
      List<UserPoolAssignmentModel> expectedUserPoolAssignments) {
    ImmutableSet<UserPoolAssignmentModel> usersWithWrongAssignedPermissions =
        findUncoveredUsers(expectedUserPoolAssignments);
    return ImmutableList.<String[]>builder()
        .add(
            convertRuleCoveragePercentageToCsvRow(
                usersWithWrongAssignedPermissions, expectedUserPoolAssignments))
        .addAll(convertUncoveredPoolAssignmentsToCsvRows(expectedUserPoolAssignments))
        .addAll(convertWrongUserPoolAssignmentsToCsvRows(usersWithWrongAssignedPermissions))
        .build();
  }

  private static String[] convertRuleCoveragePercentageToCsvRow(
      Set<UserPoolAssignmentModel> usersWithWrongAssignedPermissions,
      List<UserPoolAssignmentModel> expectedUserPoolAssignments) {
    double ruleCoverage =
        calculateRulesCoverage(usersWithWrongAssignedPermissions, expectedUserPoolAssignments);
    return new String[] {
      RULE_COVERAGE_PERCENT_HEADER, String.format("%.2f", ruleCoverage * 100) + PERCENTAGE_SIGN
    };
  }

  private static ImmutableList<String[]> convertUncoveredPoolAssignmentsToCsvRows(
      List<UserPoolAssignmentModel> expectedUserPoolAssignments) {
    ImmutableSet<PoolAssignmentModel> uncoveredPoolAssignments =
        findUncoveredPoolAssignments(expectedUserPoolAssignments);
    ImmutableList.Builder<String[]> uncoveredPoolAssignmentsBuilder = ImmutableList.builder();
    uncoveredPoolAssignmentsBuilder.add(POOL_ASSIGNMENT_HEADER);
    for (PoolAssignmentModel poolAssignment : uncoveredPoolAssignments) {
      uncoveredPoolAssignmentsBuilder.add(
          new String[] {
            Long.toString(poolAssignment.casePoolId()),
            Long.toString(poolAssignment.permissionSetId())
          });
    }
    return uncoveredPoolAssignmentsBuilder.build();
  }

  private static ImmutableList<String[]> convertWrongUserPoolAssignmentsToCsvRows(
      Set<UserPoolAssignmentModel> usersWithWrongAssignedPermissions) {
    ImmutableList.Builder<String[]> wrongUserPoolAssignmentsBuilder = ImmutableList.builder();
    wrongUserPoolAssignmentsBuilder.add(WRONG_USER_POOL_ASSIGNMENT_HEADER);
    for (UserPoolAssignmentModel user : usersWithWrongAssignedPermissions) {
      String actualPermissionAssignmentString =
          actualAssignedPermissionsByUserPoolAssignment.containsKey(user)
              ? convertPoolAssignmentsToString(
                  actualAssignedPermissionsByUserPoolAssignment.get(user))
              : Separator.SQUARE_BRACKET_LEFT.symbol + Separator.SQUARE_BRACKET_RIGHT.symbol;
      wrongUserPoolAssignmentsBuilder.add(
          new String[] {
            Long.toString(user.userId()),
            Long.toString(user.workforceId()),
            Long.toString(user.workgroupId()),
            convertRoleIdsToString(user.roleIds()),
            convertSkillIdsToString(user.skillIds(), user.roleSkillIds()),
            convertPoolAssignmentsToString(user.poolAssignments()),
            actualPermissionAssignmentString
          });
    }
    return wrongUserPoolAssignmentsBuilder.build();
  }

  private static String convertRoleIdsToString(List<Long> roleIds) {
    StringBuilder roleIdBuilder = new StringBuilder();
    roleIdBuilder.append(Separator.SQUARE_BRACKET_LEFT.symbol);
    for (Long roleId : roleIds) {
      roleIdBuilder.append(roleIdBuilder.length() == 1 ? "" : Separator.COMMA.symbol);
      roleIdBuilder.append(roleId);
    }
    roleIdBuilder.append(Separator.SQUARE_BRACKET_RIGHT.symbol);
    return roleIdBuilder.toString();
  }

  private static String convertSkillIdsToString(List<Long> skillIds, List<Long> roleSkillIds) {
    StringBuilder skillIdBuilder = new StringBuilder();
    skillIdBuilder.append(Separator.SQUARE_BRACKET_LEFT.symbol);
    for (Long skillId : skillIds) {
      skillIdBuilder.append(skillIdBuilder.length() == 1 ? "" : Separator.COMMA.symbol);
      skillIdBuilder.append(skillId);
    }
    for (Long roleSkillId : roleSkillIds) {
      skillIdBuilder.append(skillIdBuilder.length() == 1 ? "" : Separator.COMMA.symbol);
      skillIdBuilder.append(roleSkillId);
    }
    skillIdBuilder.append(Separator.SQUARE_BRACKET_RIGHT.symbol);
    return skillIdBuilder.toString();
  }

  private static String convertPoolAssignmentsToString(Set<PoolAssignmentModel> poolAssignments) {
    StringBuilder poolAssignmentsStringBuilder = new StringBuilder();
    poolAssignmentsStringBuilder.append(Separator.SQUARE_BRACKET_LEFT.symbol);
    for (PoolAssignmentModel poolAssignment : poolAssignments) {
      poolAssignmentsStringBuilder.append(
          poolAssignmentsStringBuilder.length() == 1 ? "" : Separator.COMMA.symbol);
      poolAssignmentsStringBuilder.append(
          CASE_POOL_ID_PREFIX
              + poolAssignment.casePoolId()
              + PERMISSION_SET_ID_PREFIX
              + poolAssignment.permissionSetId()
              + Separator.DOUBLE_QUOTATION_MARK.symbol
              + Separator.CURLY_BRACKET_RIGHT.symbol);
    }
    poolAssignmentsStringBuilder.append(Separator.SQUARE_BRACKET_RIGHT.symbol);
    return poolAssignmentsStringBuilder.toString();
  }

  private static ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
      assignPermissionsBasedOnGeneratedRules(
          List<UserPoolAssignmentModel> expectedUserPoolAssignments) {
    ImmutableSetMultimap.Builder<UserPoolAssignmentModel, PoolAssignmentModel>
        filtersByUserPoolAssignmentBuilder = ImmutableSetMultimap.builder();
    for (UserPoolAssignmentModel user : expectedUserPoolAssignments) {
      filtersByUserPoolAssignmentBuilder.putAll(
          user,
          assignPermissions(
              user,
              ImmutableSet.copyOf(
                  CasePoolIdAndPermissionIdGroupingUtil.convertSkillIdRoleIdToFilter(user))));
    }
    return filtersByUserPoolAssignmentBuilder.build();
  }

  private static ImmutableSet<PoolAssignmentModel> assignPermissions(
      UserPoolAssignmentModel user, Set<FilterModel> userFilters) {
    return rules.stream()
        .filter(
            rule ->
                shouldAssignPermissions(user.workforceId(), user.workgroupId(), userFilters, rule))
        .flatMap(
            rule ->
                rule.permissionSetIds().stream()
                    .map(
                        permissionSetId ->
                            PoolAssignmentModel.builder()
                                .setCasePoolId(rule.casePoolId())
                                .setPermissionSetId(permissionSetId)
                                .build()))
        .collect(ImmutableSet.toImmutableSet());
  }

  private static boolean shouldAssignPermissions(
      Long workforceId, Long workgroupId, Set<FilterModel> userFilters, RuleModel rule) {
    if ((rule.workforceId() != workforceId) || (rule.workgroupId() != workgroupId)) {
      return false;
    }
    for (ImmutableSet orFilters : rule.filters()) {
      if (Sets.intersection(userFilters, orFilters).isEmpty()) {
        return false;
      }
    }
    return true;
  }
}
