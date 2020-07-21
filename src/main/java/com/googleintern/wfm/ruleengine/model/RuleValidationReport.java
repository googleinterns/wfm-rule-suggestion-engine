package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.*;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
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

  private static final String[] RULE_HEADER =
      new String[] {
        "Rule ID", "Workforce ID", "Workgroup ID", "Case Pool ID", "Permission Set IDs", "Filters"
      };

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
            "Wrong Assigned Pool Assignments",
            "Related Rules"
          });

  private static final String[] POOL_ASSIGNMENT_HEADER =
      new String[] {"Case Pool ID", "Permission Set ID"};

  private static final String PERCENTAGE_SIGN = "%";

  private static final String CASE_POOL_ID_PREFIX =
      Separator.CURLY_BRACKET_LEFT.symbol + "\"case_pool_id\":\"";

  private static final String PERMISSION_SET_ID_PREFIX = "\",\"permission_set_id\":\"";

  private static final String RULE_ID_PREFIX = "\"rule_id\":\"";

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

  public String convertRuleValidationReportToString() {
    StringBuilder ruleValidationReportBuilder = new StringBuilder();
    convertRuleValidationReportToCsvRows()
        .forEach(
            stringArray -> ruleValidationReportBuilder.append(Arrays.toString(stringArray) + "\n"));
    return ruleValidationReportBuilder.toString();
  }

  private ImmutableList<String[]> convertRuleValidationReportToCsvRows() {
    return ImmutableList.<String[]>builder()
        .add(convertRuleCoverageToCsvString())
        .add(RULE_HEADER)
        .addAll(convertRulesToCsvRows())
        .add(POOL_ASSIGNMENT_HEADER)
        .addAll(convertUncoveredPoolAssignmentsToCsvRows())
        .addAll(USERS_WITH_LESS_ASSIGNED_PERMISSIONS_HEADER)
        .addAll(convertUsersWithIncorrectPoolAssignmentsToCsvRows(false))
        .addAll(USERS_WITH_MORE_ASSIGNED_PERMISSIONS_HEADER)
        .addAll(convertUsersWithIncorrectPoolAssignmentsToCsvRows(true))
        .build();
  }

  private String[] convertRuleCoverageToCsvString() {
    return new String[] {
      RULE_COVERAGE_PERCENT_HEADER, String.format("%.2f", ruleCoverage() * 100) + PERCENTAGE_SIGN
    };
  }

  private ImmutableList<String[]> convertRulesToCsvRows() {
    ImmutableList.Builder<String[]> rulesCsvRowsBuilder = ImmutableList.builder();
    generatedRules().forEach(rule -> rulesCsvRowsBuilder.add(rule.convertRuleToCsvRow()));
    return rulesCsvRowsBuilder.build();
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

  private ImmutableList<String[]> convertUsersWithIncorrectPoolAssignmentsToCsvRows(
      boolean isMorePermissions) {
    if (isMorePermissions) {
      return usersWithMoreAssignedPermissions().stream()
          .map(
              user ->
                  convertUserWithIncorrectPoolAssignmentsToCsvRow(
                      user,
                      Sets.difference(
                              assignedPoolAssignmentsByUsers().get(user), user.poolAssignments())
                          .immutableCopy(),
                      true))
          .collect(toImmutableList());
    } else {
      return usersWithLessAssignedPermissions().stream()
          .map(
              user ->
                  convertUserWithIncorrectPoolAssignmentsToCsvRow(
                      user,
                      Sets.difference(
                              user.poolAssignments(), assignedPoolAssignmentsByUsers().get(user))
                          .immutableCopy(),
                      false))
          .collect(toImmutableList());
    }
  }

  private String[] convertUserWithIncorrectPoolAssignmentsToCsvRow(
      UserModel user,
      ImmutableSet<PoolAssignmentModel> wrongAssignedPoolPermissions,
      boolean isMorePermissions) {
    List<String> csvRow = new ArrayList<>();
    csvRow.addAll(
        List.of(
            Long.toString(user.userId()),
            Long.toString(user.workforceId()),
            Long.toString(user.workgroupId()),
            convertRoleIdsToCsvString(user.roleIds()),
            convertSkillIdsToCsvString(user.skillIds(), user.roleSkillIds()),
            convertPoolAssignmentsToCsvString(wrongAssignedPoolPermissions)));
    if (isMorePermissions) {
      csvRow.add(
          convertRulesToCsvString(
              findRulesAssignedMorePermissions(
                  groupRulesByPoolAssignments(), user, wrongAssignedPoolPermissions)));
    }
    return csvRow.toArray(String[]::new);
  }

  private static String convertRulesToCsvString(ImmutableSet<RuleModel> rules) {
    StringBuilder rulesStringBuilder = new StringBuilder();
    rulesStringBuilder.append(Separator.SQUARE_BRACKET_LEFT.symbol);
    rulesStringBuilder.append(Separator.CURLY_BRACKET_LEFT.symbol);
    for (RuleModel rule : rules) {
      rulesStringBuilder.append(rulesStringBuilder.length() == 2 ? "" : Separator.COMMA.symbol);
      rulesStringBuilder.append(
          RULE_ID_PREFIX
              + rule.ruleId()
              + Separator.DOUBLE_QUOTATION_MARK.symbol
              + Separator.CURLY_BRACKET_RIGHT.symbol);
    }
    rulesStringBuilder.append(Separator.SQUARE_BRACKET_RIGHT.symbol);
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
          rules.stream().filter(rule -> rule.isUserCoveredByRule(user)).collect(toImmutableSet()));
    }
    return rulesAssignedMorePermissionsBuilder.build();
  }
}
