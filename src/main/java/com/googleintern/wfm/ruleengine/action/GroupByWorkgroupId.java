package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.*;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;
import java.util.Set;

/**
 * GroupByWorkgroupId class is used to group data by their workgroup Id value and generate possible
 * general rules for the same workgroup.
 */
public class GroupByWorkgroupId {
  /**
   * Group data by workgroup Id.
   * @param validData
   * @return
   */
  public static ImmutableListMultimap<Long, UserPoolAssignmentModel> groupByWorkGroupId(
      ImmutableList<UserPoolAssignmentModel> validData) {
    ImmutableListMultimap.Builder<Long, UserPoolAssignmentModel> mapByWorkGroupIdBuilder =
        ImmutableListMultimap.builder();
    for (UserPoolAssignmentModel data : validData) {
      mapByWorkGroupIdBuilder.put(data.workgroupId(), data);
    }
    return mapByWorkGroupIdBuilder.build();
  }

  /**
   * Generate rules that can apply to all users from the same workgroup Id.
   * @param mapByWorkgroupId
   * @param workgroupId
   * @return
   */
  public static ImmutableSet<RuleModel> generalRuleByWorkgroupId(
      ImmutableListMultimap<Long, UserPoolAssignmentModel> mapByWorkgroupId, Long workgroupId) {
    ImmutableList<UserPoolAssignmentModel> userFromSameWorkGroupId =
        mapByWorkgroupId.get(workgroupId);
    ImmutableSet<PoolAssignmentModel> permissionIntersections =
        findPermissionIntersection(userFromSameWorkGroupId);
    if (permissionIntersections == null) {
      return ImmutableSet.of();
    }
    ImmutableSetMultimap<Long, Long> permissionGroup =
        groupPermissionByCasePoolId(permissionIntersections);
    Long workforceId = userFromSameWorkGroupId.get(0).workforceId();
    return createGeneralRuleForWorkgroupId(permissionGroup, workforceId, workgroupId);
  }

  private static ImmutableSet<PoolAssignmentModel> findPermissionIntersection(
      ImmutableList<UserPoolAssignmentModel> userFromSameWorkGroupId) {
    Set<PoolAssignmentModel> permissionIntersections =
        userFromSameWorkGroupId.get(0).poolAssignments();

    for (UserPoolAssignmentModel user : userFromSameWorkGroupId) {
      permissionIntersections = Sets.intersection(permissionIntersections, user.poolAssignments());
      if (permissionIntersections.size() == 0) {
        return null;
      }
    }
    return ImmutableSet.copyOf(permissionIntersections);
  }

  private static ImmutableSetMultimap<Long, Long> groupPermissionByCasePoolId(
      ImmutableSet<PoolAssignmentModel> permissions) {
    ImmutableSetMultimap.Builder<Long, Long> permissionGroupBuilder =
        ImmutableSetMultimap.builder();
    for (PoolAssignmentModel permission : permissions) {
      Long casePoolId = permission.casePoolId();
      Long permissionSetId = permission.permissionSetId();
      permissionGroupBuilder.put(casePoolId, permissionSetId);
    }
    return permissionGroupBuilder.build();
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
