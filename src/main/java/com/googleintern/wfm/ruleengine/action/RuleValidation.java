package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Sets;
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
    return generatedRules.stream()
        .filter(rule -> isUserCoveredByRules(rule, user))
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
