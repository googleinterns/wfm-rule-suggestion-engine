package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.*;
import src.main.java.com.googleintern.wfm.ruleengine.model.*;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * RuleValidation class is used to validate the performance of generated rules based on the input
 * data set.
 *
 * <p>Steps:
 *
 * <ol>
 *   <li>Step 1: Assign {@link PoolAssignmentModel} to each user based on generated rules. Save
 *       permission assignments by users in an immutable set multimap.
 *   <li>Step 2: Find users that have wrong pool assignments assigned.
 *   <li>Step 3: Calculate the generated rules' coverage(% of users that have the right pool
 *       assignments).
 *   <li>Step 4: Find users that have less pool assignments assigned.
 *   <li>Step 5: Find users that have more pool assignments assigned.
 *   <li>Step 6: Find pool assignments that have no related rules.
 *   <li>Step 7: Use finding results from above steps to create {@link RuleValidationReport}.
 * </ol>
 */
public class RuleValidation {

  public static RuleValidationReport validate(
      ImmutableSet<RuleModel> generatedRules,
      ImmutableList<UserPoolAssignmentModel> existingUserPoolAssignments) {

    ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
        assignedPermissionsByUserPoolAssignment =
            assignPermissionsBasedOnGeneratedRules(existingUserPoolAssignments, generatedRules);

    ImmutableSet<UserPoolAssignmentModel> usersWithWrongAssignedPermissions =
        findUncoveredUsers(existingUserPoolAssignments, assignedPermissionsByUserPoolAssignment);

    return RuleValidationReport.builder()
        .setAssignedPoolAssignmentsByUsers(assignedPermissionsByUserPoolAssignment)
        .setRuleCoverage(
            calculateRulesCoverage(
                usersWithWrongAssignedPermissions.size(), existingUserPoolAssignments.size()))
        .setUsersWithLessAssignedPermissions(
            findUsersWithLessPermissionsAssigned(
                usersWithWrongAssignedPermissions, assignedPermissionsByUserPoolAssignment))
        .setUsersWithMoreAssignedPermissions(
            findUsersWithMorePermissionsAssigned(
                usersWithWrongAssignedPermissions, assignedPermissionsByUserPoolAssignment))
        .setUncoveredPoolAssignments(
            findUncoveredPoolAssignments(existingUserPoolAssignments, generatedRules))
        .build();
  }

  private static ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
      assignPermissionsBasedOnGeneratedRules(
          ImmutableList<UserPoolAssignmentModel> existingUserPoolAssignments,
          ImmutableSet<RuleModel> generatedRules) {
    ImmutableSetMultimap.Builder<UserPoolAssignmentModel, PoolAssignmentModel>
        filtersByUserPoolAssignmentBuilder = ImmutableSetMultimap.builder();
    for (UserPoolAssignmentModel user : existingUserPoolAssignments) {
      filtersByUserPoolAssignmentBuilder.putAll(user, assignPermissions(user, generatedRules));
    }
    return filtersByUserPoolAssignmentBuilder.build();
  }

  private static ImmutableSet<PoolAssignmentModel> assignPermissions(
      UserPoolAssignmentModel user, ImmutableSet<RuleModel> generatedRules) {
    ImmutableMap<ImmutableSet<FilterModel>, ImmutableList<ImmutableSet<Long>>>
        skillIdsAndRoleIdsByFilters = mapSkillIdsAndRoleIdsByFilters(generatedRules);
    return generatedRules.stream()
        .filter(rule -> shouldAssignPermissions(rule, user, skillIdsAndRoleIdsByFilters))
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

  private static ImmutableMap<ImmutableSet<FilterModel>, ImmutableList<ImmutableSet<Long>>>
      mapSkillIdsAndRoleIdsByFilters(ImmutableSet<RuleModel> generatedRules) {
    ImmutableMap.Builder<ImmutableSet<FilterModel>, ImmutableList<ImmutableSet<Long>>>
        skillIdsAndRoleIdsByFiltersBuilder = ImmutableMap.builder();
    generatedRules.forEach(
        generatedRule ->
            generatedRule
                .filters()
                .forEach(
                    orFilters ->
                        skillIdsAndRoleIdsByFiltersBuilder.put(
                            orFilters, convertFiltersToSkillIdsAndRoleIds(orFilters))));
    return skillIdsAndRoleIdsByFiltersBuilder.build();
  }

  private static boolean shouldAssignPermissions(
      RuleModel generateRule,
      UserPoolAssignmentModel user,
      ImmutableMap<ImmutableSet<FilterModel>, ImmutableList<ImmutableSet<Long>>>
          skillIdsAndRoleIdsByFilters) {
    if ((generateRule.workforceId() != user.workforceId())
        || (generateRule.workgroupId() != user.workgroupId())) {
      return false;
    }
    for (ImmutableSet<FilterModel> orFilters : generateRule.filters()) {
      if (Sets.intersection(
                  ImmutableSet.copyOf(user.skillIds()),
                  skillIdsAndRoleIdsByFilters.get(orFilters).get(0))
              .isEmpty()
          && Sets.intersection(
                  ImmutableSet.copyOf(user.roleSkillIds()),
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

  private static ImmutableList<ImmutableSet<Long>> convertFiltersToSkillIdsAndRoleIds(
      ImmutableSet<FilterModel> filters) {
    ImmutableSet.Builder<Long> skillIdsBuilder = ImmutableSet.builder();
    ImmutableSet.Builder<Long> roleIdsBuilder = ImmutableSet.builder();
    filters.forEach(
        filter -> {
          if (filter.type() == FilterModel.FilterType.SKILL) {
            skillIdsBuilder.add(filter.value());
          } else {
            roleIdsBuilder.add(filter.value());
          }
        });
    return ImmutableList.of(skillIdsBuilder.build(), roleIdsBuilder.build());
  }

  private static double calculateRulesCoverage(
      Integer numberOfWrongAssignment, Integer totalNumberOfUsers) {
    return (totalNumberOfUsers - numberOfWrongAssignment) / (double) totalNumberOfUsers;
  }

  private static ImmutableSet<UserPoolAssignmentModel> findUncoveredUsers(
      ImmutableList<UserPoolAssignmentModel> existingUserPoolAssignments,
      ImmutableSetMultimap<UserPoolAssignmentModel, PoolAssignmentModel>
          assignedPermissionsByUserPoolAssignment) {
    return existingUserPoolAssignments.stream()
        .filter(
            user ->
                !(user.poolAssignments().equals(assignedPermissionsByUserPoolAssignment.get(user))))
        .collect(toImmutableSet());
  }

  private static ImmutableSet<UserPoolAssignmentModel> findUsersWithLessPermissionsAssigned(
      ImmutableSet<UserPoolAssignmentModel> usersWithWrongAssignedPermissions,
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
      ImmutableSet<UserPoolAssignmentModel> usersWithWrongAssignedPermissions,
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
      ImmutableList<UserPoolAssignmentModel> existingUserPoolAssignments,
      ImmutableSet<RuleModel> generatedRules) {
    return ImmutableSet.copyOf(
        Sets.difference(
            findExpectedAllPoolAssignments(existingUserPoolAssignments),
            getActualAllPoolAssignments(generatedRules)));
  }

  private static ImmutableSet<PoolAssignmentModel> findExpectedAllPoolAssignments(
      ImmutableList<UserPoolAssignmentModel> existingUserPoolAssignments) {
    return existingUserPoolAssignments.stream()
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
