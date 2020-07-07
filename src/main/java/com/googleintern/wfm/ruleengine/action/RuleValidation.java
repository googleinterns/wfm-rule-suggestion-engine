package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.*;
import src.main.java.com.googleintern.wfm.ruleengine.model.*;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * RuleValidation class is used to test the performance of generated rules and write validation
 * results into a csv file.
 */
public class RuleValidation {

  private static ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
      assignPermissionsBasedOnGeneratedRules(
          List<UserPoolAssignmentModel> expectedUserPoolAssignments,
          ImmutableSet<RuleModel> generatedRules) {
    ImmutableSetMultimap.Builder<UserPoolAssignmentModel, PoolAssignmentModel>
        filtersByUserPoolAssignmentBuilder = ImmutableSetMultimap.builder();
    for (UserPoolAssignmentModel user : expectedUserPoolAssignments) {
      filtersByUserPoolAssignmentBuilder.putAll(user, assignPermissions(user, generatedRules));
    }
    return filtersByUserPoolAssignmentBuilder.build();
  }

  private static ImmutableSet<PoolAssignmentModel> assignPermissions(
      UserPoolAssignmentModel user, ImmutableSet<RuleModel> generatedRules) {
    ImmutableMap<ImmutableSet<FilterModel>, ImmutableList<ImmutableSet<Long>>>
        skillIdsAndRoleIdsByFilters = mapSkillIdsAndRoleIdsByFilters(generatedRules);
    return generatedRules.stream()
        .filter(rule -> shouldAssignPermissions(user, rule, skillIdsAndRoleIdsByFilters))
        .flatMap(
            rule ->
                rule.permissionSetIds().stream()
                    .map(
                        permissionSetId ->
                            PoolAssignmentModel.builder()
                                .setCasePoolId(rule.casePoolId())
                                .setPermissionSetId(permissionSetId)
                                .build()))
        .collect(toImmutableSet());
  }

  private static boolean shouldAssignPermissions(
      UserPoolAssignmentModel user,
      RuleModel rule,
      ImmutableMap<ImmutableSet<FilterModel>, ImmutableList<ImmutableSet<Long>>>
          skillIdsAndRoleIdsByFilters) {
    if ((rule.workforceId() != user.workforceId()) || (rule.workgroupId() != user.workgroupId())) {
      return false;
    }
    for (ImmutableSet<FilterModel> orFilters : rule.filters()) {
      if (Sets.intersection(
                  ImmutableSet.copyOf(user.skillIds()),
                  skillIdsAndRoleIdsByFilters.get(orFilters).get(0))
              .isEmpty()
          && Sets.intersection(
                  ImmutableSet.copyOf(user.roleIds()),
                  skillIdsAndRoleIdsByFilters.get(orFilters).get(1))
              .isEmpty()) {
        return false;
      }
    }
    return true;
  }

  private static ImmutableMap<ImmutableSet<FilterModel>, ImmutableList<ImmutableSet<Long>>>
      mapSkillIdsAndRoleIdsByFilters(ImmutableSet<RuleModel> generatedRules) {
    ImmutableMap.Builder<ImmutableSet<FilterModel>, ImmutableList<ImmutableSet<Long>>>
        skillIdsAndRoleIdsByFiltersBuilder = ImmutableMap.builder();
    for (RuleModel rule : generatedRules) {
      for (ImmutableSet<FilterModel> orFilters : rule.filters()) {
        skillIdsAndRoleIdsByFiltersBuilder.put(
            orFilters, convertFiltersToSkillIdsAndRoleIds(orFilters));
      }
    }
    return skillIdsAndRoleIdsByFiltersBuilder.build();
  }

  private static ImmutableList<ImmutableSet<Long>> convertFiltersToSkillIdsAndRoleIds(
      ImmutableSet<FilterModel> filters) {
    ImmutableSet.Builder<Long> skillIdsBuilder = ImmutableSet.builder();
    ImmutableSet.Builder<Long> roleIdsBuilder = ImmutableSet.builder();
    for (FilterModel filter : filters) {
      if (filter.type() == FilterModel.FilterType.SKILL) {
        skillIdsBuilder.add(filter.value());
      } else {
        roleIdsBuilder.add(filter.value());
      }
    }
    return ImmutableList.of(skillIdsBuilder.build(), roleIdsBuilder.build());
  }

  public static RuleValidationReport validate(
      ImmutableSet<RuleModel> generatedRules,
      ImmutableList<UserPoolAssignmentModel> existingUserPoolAssignments) {
    ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
        assignedPermissionsByUserPoolAssignment =
            assignPermissionsBasedOnGeneratedRules(existingUserPoolAssignments, generatedRules);
    ImmutableSet<UserPoolAssignmentModel> usersWithWrongAssignedPermissions =
        findUncoveredUsers(existingUserPoolAssignments, assignedPermissionsByUserPoolAssignment);
    double rulesCoverage =
        calculateRulesCoverage(
            usersWithWrongAssignedPermissions.size(), existingUserPoolAssignments.size());
    ImmutableSet<UserPoolAssignmentModel> usersWithLessAssignedPermissions =
        findUsersWithLessPermissionsAssigned(
            usersWithWrongAssignedPermissions, assignedPermissionsByUserPoolAssignment);
    ImmutableSet<UserPoolAssignmentModel> usersWithMoreAssignedPermissions =
        findUsersWithMorePermissionsAssigned(
            usersWithWrongAssignedPermissions, assignedPermissionsByUserPoolAssignment);
    ImmutableSet<PoolAssignmentModel> uncoveredPoolAssignments =
        findUncoveredPoolAssignments(existingUserPoolAssignments, generatedRules);
    return RuleValidationReport.builder()
        .setRuleCoveragePercentage(rulesCoverage)
        .setLessCoveredUserPoolAssignments(usersWithLessAssignedPermissions)
        .setMoreCoveredUserPoolAssignments(usersWithMoreAssignedPermissions)
        .setUncoveredPoolAssignments(uncoveredPoolAssignments)
        .build();
  }

  private static double calculateRulesCoverage(
      Integer numberOfWrongAssignment, Integer totalNumberOfUsers) {
    return (totalNumberOfUsers - numberOfWrongAssignment) / (double) totalNumberOfUsers;
  }

  private static ImmutableSet<UserPoolAssignmentModel> findUncoveredUsers(
      List<UserPoolAssignmentModel> expectedUserPoolAssignments,
      ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
          assignedPermissionsByUserPoolAssignment) {
    return expectedUserPoolAssignments.stream()
        .filter(
            user ->
                !(assignedPermissionsByUserPoolAssignment.containsKey(user)
                    && user.poolAssignments()
                        .equals(assignedPermissionsByUserPoolAssignment.get(user))))
        .collect(toImmutableSet());
  }

  private static ImmutableSet<UserPoolAssignmentModel> findUsersWithLessPermissionsAssigned(
      Set<UserPoolAssignmentModel> usersWithWrongAssignedPermissions,
      ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
          assignedPermissionsByUserPoolAssignment) {
    return usersWithWrongAssignedPermissions.stream()
        .filter(
            user ->
                !assignedPermissionsByUserPoolAssignment
                    .get(user)
                    .containsAll(user.poolAssignments()))
        .collect(toImmutableSet());
  }

  private static ImmutableSet<UserPoolAssignmentModel> findUsersWithMorePermissionsAssigned(
      Set<UserPoolAssignmentModel> usersWithWrongAssignedPermissions,
      ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
          assignedPermissionsByUserPoolAssignment) {
    return usersWithWrongAssignedPermissions.stream()
        .filter(
            user ->
                !user.poolAssignments()
                    .containsAll(assignedPermissionsByUserPoolAssignment.get(user)))
        .collect(toImmutableSet());
  }

  private static ImmutableSet<PoolAssignmentModel> findUncoveredPoolAssignments(
      List<UserPoolAssignmentModel> expectedUserPoolAssignments, ImmutableSet<RuleModel> generatedRules) {
    ImmutableSet<PoolAssignmentModel> expectedAllPoolAssignments =
        findExpectedAllPoolAssignments(expectedUserPoolAssignments);
    ImmutableSet<PoolAssignmentModel> actualAllPoolAssignments = getActualAllPoolAssignments(generatedRules);
    return ImmutableSet.copyOf(
        Sets.difference(expectedAllPoolAssignments, actualAllPoolAssignments));
  }

  private static ImmutableSet<PoolAssignmentModel> findExpectedAllPoolAssignments(
      List<UserPoolAssignmentModel> expectedUserPoolAssignments) {
    return expectedUserPoolAssignments.stream()
        .map(user -> user.poolAssignments())
        .reduce(ImmutableSet.of(), (union, current) -> Sets.union(union, current).immutableCopy());
  }

  private static ImmutableSet<PoolAssignmentModel> getActualAllPoolAssignments(
      ImmutableSet<RuleModel> generatedRules) {
    return generatedRules.stream()
        .flatMap(
            rule ->
                rule.permissionSetIds().stream()
                    .map(
                        permissionSetId ->
                            PoolAssignmentModel.builder()
                                .setCasePoolId(rule.casePoolId())
                                .setPermissionSetId(permissionSetId)
                                .build()))
        .collect(toImmutableSet());
  }
}
