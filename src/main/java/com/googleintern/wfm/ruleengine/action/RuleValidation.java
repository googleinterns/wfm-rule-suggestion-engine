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

public class RuleValidation {
  public static ImmutableSet<RuleModel> rules;
  public static ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
      actualAssignedPermissionsByUserPoolAssignmentModel;
  private static String RULE_COVERAGE_PERCENT_HEADER = "Coverage % for Rule Set:";

  private static String[] POOL_ASSIGNMENT_HEADER =
      new String[] {"Case Pool ID", "Permission Set ID"};

  private static String[] WRONG_USER_POOL_ASSIGNMENT_HEADER =
      new String[] {
        "User Id", "Role Id", "Skill Id", "Expected Pool Assignments", "Actual Pool Assignments"
      };

  public RuleValidation(
      Set<RuleModel> rules, List<UserPoolAssignmentModel> expectedUserPoolAssignments) {
    this.rules = (ImmutableSet<RuleModel>) rules;
    this.actualAssignedPermissionsByUserPoolAssignmentModel =
        assignPermissionsBasedOnGeneratedRules(expectedUserPoolAssignments);
  }

  public static void writeRuleValidationResultsIntoCsvFile(String outputCsvFilePath)
      throws IOException {
    File outputFile = new File(outputCsvFilePath);
    Files.deleteIfExists(outputFile.toPath());
    outputFile.createNewFile();

    FileWriter outputFileWriter = new FileWriter(outputFile);
    CSVWriter csvWriter = new CSVWriter(outputFileWriter);

    ImmutableList<String[]> validationResult = convertRuleValidationResultsToString();

    csvWriter.writeAll(validationResult);
    csvWriter.close();
  }

  private static ImmutableList<String[]> convertRuleValidationResultsToString() {
    ImmutableSet<UserPoolAssignmentModel> usersWithWrongAssignedPermissions = findUncoveredUsers();
    double ruleCoverage = calculateRulesCoverage(usersWithWrongAssignedPermissions);
    ImmutableSet<PoolAssignmentModel> uncoveredPoolAssignments = findUncoveredPoolAssignments();

    ImmutableList.Builder<String[]> ruleValidationResultsBuilder = ImmutableList.builder();
    ruleValidationResultsBuilder.add(
        new String[] {
          RULE_COVERAGE_PERCENT_HEADER, String.format("%.2f", ruleCoverage * 100) + "%"
        });

    ruleValidationResultsBuilder.add(POOL_ASSIGNMENT_HEADER);
    for (PoolAssignmentModel poolAssignment : uncoveredPoolAssignments) {
      ruleValidationResultsBuilder.add(
          new String[] {
            Long.toString(poolAssignment.casePoolId()),
            Long.toString(poolAssignment.permissionSetId())
          });
    }

    ruleValidationResultsBuilder.add(WRONG_USER_POOL_ASSIGNMENT_HEADER);
    for (UserPoolAssignmentModel user : usersWithWrongAssignedPermissions) {
      String roleIds = convertRoleIdsToString(user.roleIds());
      String skillIds = convertSkillIdsToString(user.skillIds(), user.roleSkillIds());
      String expectedPoolAssignments = convertPoolAssignmentsToString(user.poolAssignments());
      String actualPoolAssignments =
          convertPoolAssignmentsToString(
              actualAssignedPermissionsByUserPoolAssignmentModel.get(user));

      ruleValidationResultsBuilder.add(
          new String[] {
            Long.toString(user.userId()),
            Long.toString(user.workforceId()),
            Long.toString(user.workgroupId()),
            roleIds,
            skillIds,
            expectedPoolAssignments,
            actualPoolAssignments
          });
    }

    return ruleValidationResultsBuilder.build();
  }

  private static String convertRoleIdsToString(List<Long> roleIds) {
    StringBuilder roleIdBuilder = new StringBuilder();
    for (Long roleId : roleIds) {
      roleIdBuilder.append(roleIdBuilder.length() == 0 ? "[" : ",");
      roleIdBuilder.append(roleId);
    }
    roleIdBuilder.append("]");
    return roleIdBuilder.toString();
  }

  private static String convertSkillIdsToString(List<Long> skillIds, List<Long> roleSkillIds) {
    StringBuilder skillIdBuilder = new StringBuilder();
    for (Long skillId : skillIds) {
      skillIdBuilder.append(skillIdBuilder.length() == 0 ? "[" : ",");
      skillIdBuilder.append(skillId);
    }
    for (Long roleSkillId : roleSkillIds) {
      skillIdBuilder.append(skillIdBuilder.length() == 0 ? "[" : ",");
      skillIdBuilder.append(roleSkillId);
    }
    skillIdBuilder.append("]");
    return skillIdBuilder.toString();
  }

  private static String convertPoolAssignmentsToString(Set<PoolAssignmentModel> poolAssignments) {
    StringBuilder poolAssignmentsStringBuilder = new StringBuilder();
    for (PoolAssignmentModel poolAssignment : poolAssignments) {
      poolAssignmentsStringBuilder.append(poolAssignmentsStringBuilder.length() == 0 ? "[" : ",");
      poolAssignmentsStringBuilder.append(
          "{\"case_pool_id\":\""
              + poolAssignment.casePoolId()
              + "\",\"permission_set_id\":\""
              + poolAssignment.permissionSetId()
              + "\"}");
    }
    return poolAssignmentsStringBuilder.toString();
  }

  public static double calculateRulesCoverage(
      ImmutableSet<UserPoolAssignmentModel> usersWithWrongAssignedPermissions) {
    int numberOfWrongAssignment = usersWithWrongAssignedPermissions.size();
    int totalNumberOfUsers = actualAssignedPermissionsByUserPoolAssignmentModel.keySet().size();
    return (totalNumberOfUsers - numberOfWrongAssignment) / totalNumberOfUsers;
  }

  public static ImmutableSet<UserPoolAssignmentModel> findUncoveredUsers() {
    ImmutableSet.Builder<UserPoolAssignmentModel> usersWithWrongAssignedPermissionsBuilder =
        ImmutableSet.builder();
    for (UserPoolAssignmentModel user :
        actualAssignedPermissionsByUserPoolAssignmentModel.keySet()) {
      if (user.poolAssignments() != actualAssignedPermissionsByUserPoolAssignmentModel.get(user)) {
        usersWithWrongAssignedPermissionsBuilder.add(user);
      }
    }
    return usersWithWrongAssignedPermissionsBuilder.build();
  }

  public static ImmutableSet<PoolAssignmentModel> findUncoveredPoolAssignments() {
    ImmutableSet.Builder<PoolAssignmentModel> expectedAllPoolAssignmentsBuilder =
        ImmutableSet.builder();
    for (UserPoolAssignmentModel user :
        actualAssignedPermissionsByUserPoolAssignmentModel.keySet()) {
      expectedAllPoolAssignmentsBuilder.addAll(user.poolAssignments());
    }
    ImmutableSet<PoolAssignmentModel> expectedAllPoolAssignments =
        expectedAllPoolAssignmentsBuilder.build();

    ImmutableSet.Builder<PoolAssignmentModel> actualAllPoolAssignmentsBuilder =
        ImmutableSet.builder();
    for (RuleModel rule : rules) {
      for (Long permissionSetId : rule.permissionSetIds()) {
        actualAllPoolAssignmentsBuilder.add(
            PoolAssignmentModel.builder()
                .setCasePoolId(rule.casePoolId())
                .setPermissionSetId(permissionSetId)
                .build());
      }
    }
    ImmutableSet<PoolAssignmentModel> actualAllPoolAssignments =
        actualAllPoolAssignmentsBuilder.build();
    return ImmutableSet.copyOf(
        Sets.difference(expectedAllPoolAssignments, actualAllPoolAssignments));
  }

  private static ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
      assignPermissionsBasedOnGeneratedRules(
          List<UserPoolAssignmentModel> expectedUserPoolAssignments) {
    ImmutableSetMultimap.Builder<UserPoolAssignmentModel, PoolAssignmentModel>
        filtersByUserPoolAssignmentBuilder = ImmutableSetMultimap.builder();
    for (UserPoolAssignmentModel user : expectedUserPoolAssignments) {
      ImmutableSet<FilterModel> userFilters = convertSkillIdsAndRoleIdsToFilters(user);
      ImmutableSet.Builder<PoolAssignmentModel> userPoolAssignmentsBuilder = ImmutableSet.builder();
      for (RuleModel rule : rules) {
        if (decideToAssignPermissions(user.workforceId(), user.workgroupId(), userFilters, rule)) {
          for (Long permissionSetId : rule.permissionSetIds()) {
            filtersByUserPoolAssignmentBuilder.put(
                user,
                PoolAssignmentModel.builder()
                    .setCasePoolId(rule.casePoolId())
                    .setPermissionSetId(permissionSetId)
                    .build());
          }
        }
      }
    }
    return filtersByUserPoolAssignmentBuilder.build();
  }

  private static ImmutableSet<FilterModel> convertSkillIdsAndRoleIdsToFilters(
      UserPoolAssignmentModel user) {
    ImmutableSet.Builder<FilterModel> filtersBuilder = ImmutableSet.builder();
    for (Long roleId : user.roleIds()) {
      filtersBuilder.add(
          FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(roleId).build());
    }
    for (Long skillId : user.skillIds()) {
      filtersBuilder.add(
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(skillId).build());
    }
    for (Long roleSkillId : user.roleSkillIds()) {
      filtersBuilder.add(
          FilterModel.builder()
              .setType(FilterModel.FilterType.SKILL)
              .setValue(roleSkillId)
              .build());
    }
    return filtersBuilder.build();
  }

  private static boolean decideToAssignPermissions(
      Long workforceId, Long workgroupId, Set<FilterModel> userFilters, RuleModel rule) {
    if ((rule.workforceId() != workforceId) || (rule.workgroupId() != workgroupId)) {
      return false;
    }
    for (ImmutableSet orFilters : rule.filters()) {
      if (Sets.intersection(userFilters, orFilters).size() == 0) {
        return false;
      }
    }
    return true;
  }
}
