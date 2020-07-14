package src.main.java.com.googleintern.wfm.ruleengine.action.generator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Sets;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * WorkgroupIdRuleGenerator class is used to create rules that can apply to all users from the same
 * work group.
 *
 * <p>Steps:
 *
 * <ol>
 *   <li>Step 1: Check the size of ({@link UserModel userPoolAssignmentsFromSameWorkGroupId}). If it
 *       does not have a valid size, return an empty set.
 *   <li>Step 2: Loop through all userPoolAssignments in the list and find their common {@link
 *       PoolAssignmentModel}. Store the finding results as an immutable set.
 *   <li>Step 3: Group the finding result from step 3 by Case Pool ID. Store the grouping results in
 *       an immutable set multimap where key represent Case Pool IDs and values represent Permission
 *       Set IDs.
 *   <li>Step 4: Form Rules based on the grouping results from step 3.
 * </ol>
 */
public class WorkgroupIdRuleGenerator {
  /** Generate rules that can apply to all users from the same workgroup Id. */
  public static ImmutableSet<RuleModel> generateWorkgroupIdRules(
      List<UserModel> userPoolAssignmentsFromSameWorkGroupId, RuleIdGenerator ruleIdGenerator) {
    if (userPoolAssignmentsFromSameWorkGroupId.size() == 0) {
      return ImmutableSet.of();
    }
    ImmutableSet<PoolAssignmentModel> permissionIntersections =
        findCommonPermissionsInsideOneWorkgroup(userPoolAssignmentsFromSameWorkGroupId);
    ImmutableSetMultimap<Long, Long> permissionGroup =
        groupPermissionByCasePoolId(permissionIntersections);
    return createGeneralRuleForWorkgroupId(
        permissionGroup,
        userPoolAssignmentsFromSameWorkGroupId.get(0).workforceId(),
        userPoolAssignmentsFromSameWorkGroupId.get(0).workgroupId(),
        ruleIdGenerator);
  }

  private static ImmutableSet<PoolAssignmentModel> findCommonPermissionsInsideOneWorkgroup(
      List<UserModel> userPoolAssignmentsFromSameWorkGroupId) {
    return userPoolAssignmentsFromSameWorkGroupId.stream()
        .map(userPoolAssignment -> userPoolAssignment.poolAssignments())
        .reduce(
            userPoolAssignmentsFromSameWorkGroupId.get(0).poolAssignments(),
            (intersections, user) -> Sets.intersection(intersections, user).immutableCopy());
  }

  private static ImmutableSetMultimap<Long, Long> groupPermissionByCasePoolId(
      Set<PoolAssignmentModel> permissions) {
    ImmutableSetMultimap.Builder<Long, Long> permissionSetIdsByCasePoolIdBuilder =
        ImmutableSetMultimap.builder();
    for (PoolAssignmentModel permission : permissions) {
      permissionSetIdsByCasePoolIdBuilder.put(
          permission.casePoolId(), permission.permissionSetId());
    }
    return permissionSetIdsByCasePoolIdBuilder.build();
  }

  private static ImmutableSet<RuleModel> createGeneralRuleForWorkgroupId(
      ImmutableSetMultimap<Long, Long> permissions,
      Long workforceId,
      Long workgroupId,
      RuleIdGenerator ruleIdGenerator) {
    return permissions.keySet().stream()
        .map(
            casePoolId ->
                RuleModel.builder()
                    .setRuleId(ruleIdGenerator.getRuleId())
                    .setWorkforceId(workforceId)
                    .setWorkgroupId(workgroupId)
                    .setCasePoolId(casePoolId)
                    .setPermissionSetIds(permissions.get(casePoolId))
                    .setFilters(ImmutableList.of())
                    .build())
        .collect(toImmutableSet());
  }
}
