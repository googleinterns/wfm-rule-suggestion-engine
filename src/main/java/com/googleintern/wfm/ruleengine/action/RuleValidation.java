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

  private final ImmutableList<UserModel> existingUserPoolAssignments;

  public RuleValidation(ImmutableList<UserModel> existingUserPoolAssignments) {
    this.existingUserPoolAssignments = existingUserPoolAssignments;
  }

  public RuleValidationReport validate(ImmutableSet<RuleModel> generatedRules) {

    ImmutableSetMultimap<UserModel, PoolAssignmentModel> assignedPermissionsByUser =
        assignPermissionsByGeneratedRules(generatedRules);

    ImmutableSet<UserModel> usersWithWrongAssignedPermissions =
        findUncoveredUsers(assignedPermissionsByUser);

    return RuleValidationReport.builder()
        .setAssignedPoolAssignmentsByUsers(assignedPermissionsByUser)
        .setRuleCoverage(
            (existingUserPoolAssignments.size() - usersWithWrongAssignedPermissions.size())
                / (double) existingUserPoolAssignments.size())
        .setUsersWithLessAssignedPermissions(
            findUsersWithLessPermissionsAssigned(
                usersWithWrongAssignedPermissions, assignedPermissionsByUser))
        .setUsersWithMoreAssignedPermissions(
            findUsersWithMorePermissionsAssigned(
                usersWithWrongAssignedPermissions, assignedPermissionsByUser))
        .setUncoveredPoolAssignments(findUncoveredPoolAssignments(generatedRules))
        .build();
  }

  private ImmutableSetMultimap<UserModel, PoolAssignmentModel> assignPermissionsByGeneratedRules(
      ImmutableSet<RuleModel> generatedRules) {
    ImmutableSetMultimap.Builder<UserModel, PoolAssignmentModel>
        filtersByUserPoolAssignmentBuilder = ImmutableSetMultimap.builder();
    for (UserModel user : existingUserPoolAssignments) {
      filtersByUserPoolAssignmentBuilder.putAll(user, assignPermissions(user, generatedRules));
    }
    return filtersByUserPoolAssignmentBuilder.build();
  }

  private ImmutableSet<PoolAssignmentModel> assignPermissions(
      UserModel user, ImmutableSet<RuleModel> generatedRules) {
    ImmutableMap<ImmutableSet<FilterModel>, ImmutableList<ImmutableSet<Long>>>
        skillIdsAndRoleIdsByFilters = mapSkillIdsAndRoleIdsByFilters(generatedRules);
    return generatedRules.stream()
        .filter(rule -> isUserCoveredByRules(rule, user, skillIdsAndRoleIdsByFilters))
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

  private ImmutableMap<ImmutableSet<FilterModel>, ImmutableList<ImmutableSet<Long>>>
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

  private static boolean isUserCoveredByRules(
      RuleModel generateRule,
      UserModel user,
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

  private ImmutableList<ImmutableSet<Long>> convertFiltersToSkillIdsAndRoleIds(
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

  private ImmutableSet<UserModel> findUncoveredUsers(
      ImmutableSetMultimap<UserModel, PoolAssignmentModel> assignedPermissionsByUser) {
    return existingUserPoolAssignments.stream()
        .filter(user -> !(user.poolAssignments().equals(assignedPermissionsByUser.get(user))))
        .collect(toImmutableSet());
  }

  private static ImmutableSet<UserModel> findUsersWithLessPermissionsAssigned(
      ImmutableSet<UserModel> usersWithWrongAssignedPermissions,
      ImmutableSetMultimap<UserModel, PoolAssignmentModel> assignedPermissionsByUser) {
    return usersWithWrongAssignedPermissions.stream()
        .filter(user -> !assignedPermissionsByUser.get(user).containsAll(user.poolAssignments()))
        .collect(toImmutableSet());
  }

  private static ImmutableSet<UserModel> findUsersWithMorePermissionsAssigned(
      ImmutableSet<UserModel> usersWithWrongAssignedPermissions,
      ImmutableSetMultimap<UserModel, PoolAssignmentModel> assignedPermissionsByUser) {
    return usersWithWrongAssignedPermissions.stream()
        .filter(user -> !user.poolAssignments().containsAll(assignedPermissionsByUser.get(user)))
        .collect(toImmutableSet());
  }

  private ImmutableSet<PoolAssignmentModel> findUncoveredPoolAssignments(
      ImmutableSet<RuleModel> generatedRules) {
    return ImmutableSet.copyOf(
        Sets.difference(
            findExpectedAllPoolAssignments(), getActualAllPoolAssignments(generatedRules)));
  }

  private ImmutableSet<PoolAssignmentModel> findExpectedAllPoolAssignments() {
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
