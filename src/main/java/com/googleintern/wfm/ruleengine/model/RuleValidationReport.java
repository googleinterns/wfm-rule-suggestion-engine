package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.*;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

/** RuleValidationReport class is used to store detailed results from RuleValidation class. */
@AutoValue
public abstract class RuleValidationReport {

  public abstract ImmutableSetMultimap<UserModel, PoolAssignmentModel>
      assignedPoolAssignmentsByUsers();

  public abstract double ruleCoverage();

  public abstract ImmutableSet<RuleModel> generatedRules();

  public abstract ImmutableSet<UserModel> usersWithLessAssignedPermissions();

  public abstract ImmutableSet<UserModel> usersWithMoreAssignedPermissions();

  public abstract ImmutableSet<PoolAssignmentModel> uncoveredPoolAssignments();

  public static Builder builder() {
    return new AutoValue_RuleValidationReport.Builder();
  }

  /**
   * Builder class is used to set variables and create an instance for RuleValidationReport class.
   */
  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setAssignedPoolAssignmentsByUsers(
        SetMultimap<UserModel, PoolAssignmentModel> assignedPoolAssignmentsByUsers);

    public abstract Builder setGeneratedRules(ImmutableSet<RuleModel> generatedRules);

    public abstract Builder setRuleCoverage(double ruleCoverage);

    public abstract Builder setUsersWithLessAssignedPermissions(
        Set<UserModel> usersWithLessAssignedPermissions);

    public abstract Builder setUsersWithMoreAssignedPermissions(
        Set<UserModel> usersWithMoreAssignedPermissions);

    public abstract Builder setUncoveredPoolAssignments(
        Set<PoolAssignmentModel> uncoveredPoolAssignments);

    public abstract RuleValidationReport build();
  }

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

  /** Constant string variables used for result outputs. */
  private static final String RULE_COVERAGE_PERCENT_HEADER = "Coverage % for Rule Set:";

  private static final ImmutableList<String[]> USERS_WITH_LESS_ASSIGNED_PERMISSIONS_HEADER =
      ImmutableList.of(
          new String[] {"Users with Less Assigned Permissions:"},
          new String[] {
            "User Id",
            "Workforce Id",
            "Workgroup Id",
            "Role Id",
            "Skill Id",
            "Less Assigned Pool Assignments"
          });

  private static final ImmutableList<String[]> USERS_WITH_MORE_ASSIGNED_PERMISSIONS_HEADER =
      ImmutableList.of(
          new String[] {"Users with More Assigned Permissions:"},
          new String[] {
            "User Id",
            "Workforce Id",
            "Workgroup Id",
            "Role Id",
            "Skill Id",
            "More Assigned Pool Assignments",
            "Relative Rules"
          });

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

  public void writeToCsvFile(String outputCsvFilePath) throws IOException {
    File outputFile = new File(outputCsvFilePath);
    Files.deleteIfExists(outputFile.toPath());
    outputFile.createNewFile();

    FileWriter outputFileWriter = new FileWriter(outputFile);
    CSVWriter csvWriter = new CSVWriter(outputFileWriter);

    ImmutableList<String[]> validationResult = convertRuleValidationReportToCsvRows();

    csvWriter.writeAll(validationResult);
    csvWriter.close();
  }

  private ImmutableList<String[]> convertRuleValidationReportToCsvRows() {
    return ImmutableList.<String[]>builder()
        .add(convertRuleCoverageToCsvString())
        .add(POOL_ASSIGNMENT_HEADER)
        .addAll(convertUncoveredPoolAssignmentsToCsvRows())
        .addAll(USERS_WITH_LESS_ASSIGNED_PERMISSIONS_HEADER)
        .addAll(convertUsersWithLessAssignedPoolAssignmentsToCsvRows())
        .addAll(USERS_WITH_MORE_ASSIGNED_PERMISSIONS_HEADER)
        .addAll(convertUsersWithMoreAssignedPoolAssignmentsToCsvRows())
        .build();
  }

  private String[] convertRuleCoverageToCsvString() {
    return new String[] {
      RULE_COVERAGE_PERCENT_HEADER, String.format("%.2f", ruleCoverage() * 100) + PERCENTAGE_SIGN
    };
  }

  private ImmutableList<String[]> convertUncoveredPoolAssignmentsToCsvRows() {
    return uncoveredPoolAssignments().stream()
        .map(
            poolAssignment ->
                new String[] {
                  Long.toString(poolAssignment.casePoolId()),
                  Long.toString(poolAssignment.permissionSetId())
                })
        .collect(toImmutableList());
  }

  private static String convertRoleIdsToCsvString(List<Long> roleIds) {
    StringBuilder roleIdBuilder = new StringBuilder();
    roleIdBuilder.append(Separator.SQUARE_BRACKET_LEFT.symbol);
    for (Long roleId : roleIds) {
      roleIdBuilder.append(roleIdBuilder.length() == 1 ? "" : Separator.COMMA.symbol);
      roleIdBuilder.append(roleId);
    }
    roleIdBuilder.append(Separator.SQUARE_BRACKET_RIGHT.symbol);
    return roleIdBuilder.toString();
  }

  private static String convertSkillIdsToCsvString(List<Long> skillIds, List<Long> roleSkillIds) {
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

  private ImmutableList<String[]> convertUsersWithLessAssignedPoolAssignmentsToCsvRows() {
    return usersWithLessAssignedPermissions().stream()
        .map(
            user ->
                new String[] {
                  Long.toString(user.userId()),
                  Long.toString(user.workforceId()),
                  Long.toString(user.workgroupId()),
                  convertRoleIdsToCsvString(user.roleIds()),
                  convertSkillIdsToCsvString(user.skillIds(), user.roleSkillIds()),
                  convertPoolAssignmentsToCsvString(
                      Sets.difference(
                              user.poolAssignments(), assignedPoolAssignmentsByUsers().get(user))
                          .immutableCopy())
                })
        .collect(toImmutableList());
  }

  private ImmutableList<String[]> convertUsersWithMoreAssignedPoolAssignmentsToCsvRows() {
    ImmutableSetMultimap<PoolAssignmentModel, RuleModel> rulesByPoolAssignments =
        groupRulesByPoolAssignments();
    return usersWithMoreAssignedPermissions().stream()
        .map(
            user ->
                convertUserWithMoreAssignedPoolAssignmentsToCsvRow(
                    user,
                    rulesByPoolAssignments,
                    Sets.difference(
                            assignedPoolAssignmentsByUsers().get(user), user.poolAssignments())
                        .immutableCopy()))
        .collect(toImmutableList());
  }

  private String[] convertUserWithMoreAssignedPoolAssignmentsToCsvRow(
      UserModel user,
      ImmutableSetMultimap<PoolAssignmentModel, RuleModel> rulesByPoolAssignments,
      ImmutableSet<PoolAssignmentModel> wrongAssignedPoolPermissions) {
    return new String[] {
      Long.toString(user.userId()),
      Long.toString(user.workforceId()),
      Long.toString(user.workgroupId()),
      convertRoleIdsToCsvString(user.roleIds()),
      convertSkillIdsToCsvString(user.skillIds(), user.roleSkillIds()),
      convertPoolAssignmentsToCsvString(wrongAssignedPoolPermissions),
      convertRulesToCsvString(
          findRulesAssignedMorePermissions(
              rulesByPoolAssignments, user, wrongAssignedPoolPermissions))
    };
  }

  private static String convertRulesToCsvString(ImmutableSet<RuleModel> rules) {
    StringBuilder rulesStringBuilder = new StringBuilder();
    rulesStringBuilder.append(Separator.CURLY_BRACKET_LEFT.symbol);
    for (RuleModel rule : rules) {
      rulesStringBuilder.append(rulesStringBuilder.length() == 1 ? "" : Separator.COMMA.symbol);
      rulesStringBuilder.append(
          Separator.CURLY_BRACKET_LEFT.symbol
              + rule.toString()
              + Separator.CURLY_BRACKET_RIGHT.symbol);
    }
    rulesStringBuilder.append(Separator.CURLY_BRACKET_RIGHT.symbol);
    return rulesStringBuilder.toString();
  }

  private static String convertPoolAssignmentsToCsvString(
      ImmutableSet<PoolAssignmentModel> poolAssignments) {
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

  private ImmutableSetMultimap<PoolAssignmentModel, RuleModel> groupRulesByPoolAssignments() {
    ImmutableSetMultimap.Builder<PoolAssignmentModel, RuleModel> rulesByPoolAssignmentsBuilder =
        ImmutableSetMultimap.builder();
    generatedRules()
        .forEach(
            rule ->
                rule.permissionSetIds()
                    .forEach(
                        permissionSetId ->
                            rulesByPoolAssignmentsBuilder.put(
                                PoolAssignmentModel.builder()
                                    .setCasePoolId(rule.casePoolId())
                                    .setPermissionSetId(permissionSetId)
                                    .build(),
                                rule)));
    return rulesByPoolAssignmentsBuilder.build();
  }

  private ImmutableSet<RuleModel> findRulesAssignedMorePermissions(
      ImmutableSetMultimap<PoolAssignmentModel, RuleModel> rulesByPoolAssignments,
      UserModel user,
      ImmutableSet<PoolAssignmentModel> poolAssignments) {
    ImmutableSet.Builder<RuleModel> rulesAssignedMorePermissionsBuilder = ImmutableSet.builder();
    for (PoolAssignmentModel poolAssignment : poolAssignments) {
      ImmutableSet<RuleModel> rules = rulesByPoolAssignments.get(poolAssignment);
      rulesAssignedMorePermissionsBuilder.addAll(
          rules.stream()
              .filter(rule -> isUserCoveredByRules(rule, user))
              .collect(toImmutableSet()));
    }
    return rulesAssignedMorePermissionsBuilder.build();
  }

  private static boolean isUserCoveredByRules(RuleModel generateRule, UserModel user) {
    if ((generateRule.workforceId() != user.workforceId())
        || (generateRule.workgroupId() != user.workgroupId())) {
      return false;
    }
    for (ImmutableSet<FilterModel> orFilters : generateRule.filters()) {
      if (Sets.intersection(ImmutableSet.copyOf(user.skillIds()), getSkillIdsFromFilters(orFilters))
              .isEmpty()
          && Sets.intersection(
                  ImmutableSet.copyOf(user.roleSkillIds()), getSkillIdsFromFilters(orFilters))
              .isEmpty()
          && Sets.intersection(
                  ImmutableSet.copyOf(user.roleIds()), getRoleIdsFromFilters(orFilters))
              .isEmpty()) {
        return false;
      }
    }
    return true;
  }

  private static ImmutableSet<Long> getSkillIdsFromFilters(ImmutableSet<FilterModel> filters) {
    return filters.stream()
        .filter(filer -> filer.type() == FilterModel.FilterType.SKILL)
        .map(filter -> filter.value())
        .collect(toImmutableSet());
  }

  private static ImmutableSet<Long> getRoleIdsFromFilters(ImmutableSet<FilterModel> filters) {
    return filters.stream()
        .filter(filer -> filer.type() == FilterModel.FilterType.ROLE)
        .map(filter -> filter.value())
        .collect(toImmutableSet());
  }
}
