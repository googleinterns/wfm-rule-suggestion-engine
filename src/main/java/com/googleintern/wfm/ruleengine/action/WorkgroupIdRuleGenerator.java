package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.*;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

import java.util.List;
import java.util.Set;

/**
 * WorkgroupIdRuleGenerator class is used to create rules that can apply to all users from the same
 * work group.
 *
 * <p>Steps:
 *
 * <ol>
 *   <li>Step 1: Check whether the target work group ID is a valid or not.
 *   <li>Step 2: Get all({@link UserModel userPoolAssignments}) associated with the
 *       target work group ID as an Immutable List and check its size.
 *   <li>Step 3: Loop through all userPoolAssignments in the list and find their common {@link
 *       PoolAssignmentModel}. Store the finding results as an immutable set.
 *   <li>Step 4: Group the finding result from step 3 by Case Pool ID. Store the grouping results in
 *       an immutable set multimap where key represent Case Pool IDs and values represent Permission
 *       Set IDs.
 *   <li>Step 5: Form Rules based on the grouping results from step 4.
 * </ol>
 */
public class WorkgroupIdRuleGenerator {
  /** Generate rules that can apply to all users from the same workgroup Id. */
  public static ImmutableSet<RuleModel> generateWorkgroupIdRules(
      ImmutableListMultimap<Long, UserModel> userPoolAssignmentsByWorkGroupId,
      Long workgroupId) {
    if (!userPoolAssignmentsByWorkGroupId.containsKey(workgroupId)) {
      return ImmutableSet.of();
    }
    ImmutableList<UserModel> userPoolAssignmentsFromSameWorkGroupId =
        userPoolAssignmentsByWorkGroupId.get(workgroupId);
    if (userPoolAssignmentsFromSameWorkGroupId.size() == 0) {
      return ImmutableSet.of();
    }
    ImmutableSet<PoolAssignmentModel> permissionIntersections =
        findCommonPermissionsInsideOneWorkgroup(userPoolAssignmentsFromSameWorkGroupId);
    ImmutableSetMultimap<Long, Long> permissionGroup =
        groupPermissionByCasePoolId(permissionIntersections);
    Long workforceId = userPoolAssignmentsFromSameWorkGroupId.get(0).workforceId();
    return createGeneralRuleForWorkgroupId(permissionGroup, workforceId, workgroupId);
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
      ImmutableSetMultimap<Long, Long> permissions, Long workforceId, Long workgroupId) {
    ImmutableList<ImmutableSet<FilterModel>> emptyFilters =
        ImmutableList.<ImmutableSet<FilterModel>>builder().build();
    ImmutableSet.Builder<RuleModel> generalRulesForWorkgroupBuilder =
        ImmutableSet.<RuleModel>builder();
    permissions.forEach(
        (casePoolId, permissionId) -> {
          generalRulesForWorkgroupBuilder.add(
              RuleModel.builder()
                  .setWorkforceId(workforceId)
                  .setWorkgroupId(workgroupId)
                  .setCasePoolId(casePoolId)
                  .setPermissionSetIds(permissions.get(casePoolId))
                  .setFilters(emptyFilters)
                  .build());
        });
    return generalRulesForWorkgroupBuilder.build();
  }
}
