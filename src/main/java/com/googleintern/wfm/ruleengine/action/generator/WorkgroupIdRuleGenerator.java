package src.main.java.com.googleintern.wfm.ruleengine.action.generator;

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
<<<<<<< HEAD
      List<UserModel> userPoolAssignmentsFromSameWorkGroupId) {
=======
      ImmutableListMultimap<Long, UserModel> userPoolAssignmentsByWorkGroupId,
      Long workgroupId,
      RuleIdGenerator ruleIdGenerator) {
    if (!userPoolAssignmentsByWorkGroupId.containsKey(workgroupId)) {
      return ImmutableSet.of();
    }
    ImmutableList<UserModel> userPoolAssignmentsFromSameWorkGroupId =
        userPoolAssignmentsByWorkGroupId.get(workgroupId);
>>>>>>> dc844fb... Add Rule Id to Rule Model
    if (userPoolAssignmentsFromSameWorkGroupId.size() == 0) {
      return ImmutableSet.of();
    }
    ImmutableSet<PoolAssignmentModel> permissionIntersections =
        findCommonPermissionsInsideOneWorkgroup(userPoolAssignmentsFromSameWorkGroupId);
    ImmutableSetMultimap<Long, Long> permissionGroup =
        groupPermissionByCasePoolId(permissionIntersections);
<<<<<<< HEAD
    return createGeneralRuleForWorkgroupId(
        permissionGroup,
        userPoolAssignmentsFromSameWorkGroupId.get(0).workforceId(),
        userPoolAssignmentsFromSameWorkGroupId.get(0).workgroupId());
=======
    Long workforceId = userPoolAssignmentsFromSameWorkGroupId.get(0).workforceId();
    return createGeneralRuleForWorkgroupId(
        permissionGroup, workforceId, workgroupId, ruleIdGenerator);
>>>>>>> dc844fb... Add Rule Id to Rule Model
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
    ImmutableList<ImmutableSet<FilterModel>> emptyFilters =
        ImmutableList.<ImmutableSet<FilterModel>>builder().build();
    ImmutableSet.Builder<RuleModel> generalRulesForWorkgroupBuilder =
        ImmutableSet.<RuleModel>builder();
    for (Long casePoolId : permissions.keySet()) {
      generalRulesForWorkgroupBuilder.add(
          RuleModel.builder()
              .setRuleId(ruleIdGenerator.getRuleId())
              .setWorkforceId(workforceId)
              .setWorkgroupId(workgroupId)
              .setCasePoolId(casePoolId)
              .setPermissionSetIds(permissions.get(casePoolId))
              .setFilters(emptyFilters)
              .build());
    }
    return generalRulesForWorkgroupBuilder.build();
  }
}
