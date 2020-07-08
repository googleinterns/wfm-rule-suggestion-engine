package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;

/** RuleValidationReport class is used to store detailed results from RuleValidation class. */
@AutoValue
public abstract class RuleValidationReport {

  public abstract ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
      assignedPoolAssignmentsByUsers();

  public abstract double ruleCoverage();

  public abstract ImmutableSet<UserPoolAssignmentModel> usersWithLessAssignedPermissions();

  public abstract ImmutableSet<UserPoolAssignmentModel> usersWithMoreAssignedPermissions();

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
        SetMultimap<UserPoolAssignmentModel, PoolAssignmentModel> assignedPoolAssignmentsByUsers);

    public abstract Builder setRuleCoverage(double ruleCoverage);

    public abstract Builder setUsersWithLessAssignedPermissions(
        Set<UserPoolAssignmentModel> usersWithLessAssignedPermissions);

    public abstract Builder setUsersWithMoreAssignedPermissions(
        Set<UserPoolAssignmentModel> usersWithMoreAssignedPermissions);

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

  private static final String[] USERS_WITH_LESS_ASSIGNED_PERMISSIONS_HEADER =
      new String[] {"Users with Less Assigned Permissions:"};

  private static final String[] USERS_WITH_MORE_ASSIGNED_PERMISSIONS_HEADER =
      new String[] {"Users with More Assigned Permissions:"};

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
        .add(USERS_WITH_LESS_ASSIGNED_PERMISSIONS_HEADER)
        .addAll(convertWrongUserPoolAssignmentsToCsvRows(usersWithLessAssignedPermissions()))
        .add(USERS_WITH_MORE_ASSIGNED_PERMISSIONS_HEADER)
        .addAll(convertWrongUserPoolAssignmentsToCsvRows(usersWithMoreAssignedPermissions()))
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

  private ImmutableList<String[]> convertWrongUserPoolAssignmentsToCsvRows(
      ImmutableSet<UserPoolAssignmentModel> usersWithWrongAssignedPermissions) {
    ImmutableList.Builder<String[]> wrongUserPoolAssignmentsBuilder = ImmutableList.builder();
    wrongUserPoolAssignmentsBuilder.add(WRONG_USER_POOL_ASSIGNMENT_HEADER);
    for (UserPoolAssignmentModel user : usersWithWrongAssignedPermissions) {
      wrongUserPoolAssignmentsBuilder.add(
          new String[] {
            Long.toString(user.userId()),
            Long.toString(user.workforceId()),
            Long.toString(user.workgroupId()),
            convertRoleIdsToCsvString(user.roleIds()),
            convertSkillIdsToCsvString(user.skillIds(), user.roleSkillIds()),
            convertPoolAssignmentsToCsvString(user.poolAssignments()),
            convertPoolAssignmentsToCsvString(assignedPoolAssignmentsByUsers().get(user))
          });
    }
    return wrongUserPoolAssignmentsBuilder.build();
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
}
