package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

/** RuleValidationReport class is used to store detailed results from RuleValidation class. */
@AutoValue
public abstract class RuleValidationReport {

  public abstract double ruleCoveragePercentage();

  public abstract ImmutableSet<UserPoolAssignmentModel> lessCoveredUserPoolAssignments();

  public abstract ImmutableSet<UserPoolAssignmentModel> moreCoveredUserPoolAssignments();

  public abstract ImmutableSet<PoolAssignmentModel> uncoveredPoolAssignments();

  public static Builder builder() {
    return new AutoValue_RuleValidationReport.Builder();
  }

  /**
   * Builder class is used to set variables and create an instance for RuleValidationReport class.
   */
  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setRuleCoveragePercentage(double ruleCoveragePercentage);

    public abstract Builder setLessCoveredUserPoolAssignments(
        Set<UserPoolAssignmentModel> lessCoveredUserPoolAssignments);

    public abstract Builder setMoreCoveredUserPoolAssignments(
        Set<UserPoolAssignmentModel> moreCoveredUserPoolAssignments);

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

    ImmutableList<String[]> validationResult = convertRuleValidationResultsToCsvRows();

    csvWriter.writeAll(validationResult);
    csvWriter.close();
  }

  private ImmutableList<String[]> convertRuleValidationResultsToCsvRows() {
    return ImmutableList.<String[]>builder()
        .add(
            new String[] {
              RULE_COVERAGE_PERCENT_HEADER,
              String.format("%.2f", ruleCoveragePercentage()) + PERCENTAGE_SIGN
            })
        .addAll(convertUncoveredPoolAssignmentsToCsvRows())
        .build();
  }

  private ImmutableList<String[]> convertUncoveredPoolAssignmentsToCsvRows() {
    ImmutableList.Builder<String[]> uncoveredPoolAssignmentsBuilder = ImmutableList.builder();
    uncoveredPoolAssignmentsBuilder.add(POOL_ASSIGNMENT_HEADER);
    for (PoolAssignmentModel poolAssignment : uncoveredPoolAssignments()) {
      uncoveredPoolAssignmentsBuilder.add(
          new String[] {
            Long.toString(poolAssignment.casePoolId()),
            Long.toString(poolAssignment.permissionSetId())
          });
    }
    return uncoveredPoolAssignmentsBuilder.build();
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

  private static String convertPoolAssignmentsToCsvString(
      Set<PoolAssignmentModel> poolAssignments) {
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

//  private static ImmutableList<String[]> convertWrongUserPoolAssignmentsToCsvRows(
//      Set<UserPoolAssignmentModel> usersWithWrongAssignedPermissions) {
//    ImmutableList.Builder<String[]> wrongUserPoolAssignmentsBuilder = ImmutableList.builder();
//    wrongUserPoolAssignmentsBuilder.add(WRONG_USER_POOL_ASSIGNMENT_HEADER);
//    for (UserPoolAssignmentModel user : usersWithWrongAssignedPermissions) {
//      String actualPermissionAssignmentString =
//          actualAssignedPermissionsByUserPoolAssignment.containsKey(user)
//              ? convertPoolAssignmentsToCsvString(
//                  actualAssignedPermissionsByUserPoolAssignment.get(user))
//              : RuleValidation.Separator.SQUARE_BRACKET_LEFT.symbol
//                  + RuleValidation.Separator.SQUARE_BRACKET_RIGHT.symbol;
//      wrongUserPoolAssignmentsBuilder.add(
//          new String[] {
//            Long.toString(user.userId()),
//            Long.toString(user.workforceId()),
//            Long.toString(user.workgroupId()),
//            convertRoleIdsToCsvString(user.roleIds()),
//            convertSkillIdsToCsvString(user.skillIds(), user.roleSkillIds()),
//            convertPoolAssignmentsToCsvString(user.poolAssignments()),
//            actualPermissionAssignmentString
//          });
//    }
//    return wrongUserPoolAssignmentsBuilder.build();
//  }
}
