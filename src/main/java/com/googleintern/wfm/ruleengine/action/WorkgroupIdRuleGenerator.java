package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.*;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;

import java.util.List;
import java.util.Set;

/**
 * WorkgroupIdRuleGenerator class is used to create rules that can apply to all users from the same
 * work group.
 */
public class WorkgroupIdRuleGenerator {
  /** Generate rules that can apply to all users from the same workgroup Id. */
  public static ImmutableSet<RuleModel> generateWorkgroupIdRules(
      ImmutableListMultimap<Long, UserPoolAssignmentModel> userPoolAssignmentsByWorkGroupId,
      Long workgroupId) {
    if (!userPoolAssignmentsByWorkGroupId.containsKey(workgroupId)) {
      return ImmutableSet.of();
    }
    ImmutableList<UserPoolAssignmentModel> userPoolAssignmentsFromSameWorkGroupId =
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
      List<UserPoolAssignmentModel> userPoolAssignmentsFromSameWorkGroupId) {
    Set<PoolAssignmentModel> permissionIntersections =
        userPoolAssignmentsFromSameWorkGroupId.get(0).poolAssignments();

    for (UserPoolAssignmentModel user : userPoolAssignmentsFromSameWorkGroupId) {
      permissionIntersections = Sets.intersection(permissionIntersections, user.poolAssignments());
      if (permissionIntersections.size() == 0) {
        return ImmutableSet.of();
      }
    }
    return ImmutableSet.copyOf(permissionIntersections);
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
