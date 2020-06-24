package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.*;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;

import java.util.List;
import java.util.Set;

/**
 * GroupByWorkgroupId class is used to group data by their workgroup Id value and generate possible
 * general rules for the same workgroup.
 */
public class WorkgroupIdGroupingUtil {
  /** Group data by workgroup Id. */
  public static ImmutableListMultimap<Long, UserPoolAssignmentModel> groupByWorkGroupId(
      ImmutableList<UserPoolAssignmentModel> validData) {
    ImmutableListMultimap.Builder<Long, UserPoolAssignmentModel>
        userPoolAssignmentsByWorkGroupIdBuilder = ImmutableListMultimap.builder();
    for (UserPoolAssignmentModel data : validData) {
      userPoolAssignmentsByWorkGroupIdBuilder.put(data.workgroupId(), data);
    }
    return userPoolAssignmentsByWorkGroupIdBuilder.build();
  }

  /** Generate rules that can apply to all users from the same workgroup Id. */
  public static ImmutableSet<RuleModel> generalRuleByWorkgroupId(
      ImmutableListMultimap<Long, UserPoolAssignmentModel> userPoolAssignmentsByWorkGroupIdBuilder,
      Long workgroupId) {
    ImmutableList<UserPoolAssignmentModel> userPoolAssignmentsFromSameWorkGroupId =
        userPoolAssignmentsByWorkGroupIdBuilder.get(workgroupId);
    ImmutableSet<PoolAssignmentModel> permissionIntersections =
        findPermissionIntersection(userPoolAssignmentsFromSameWorkGroupId);
    ImmutableSetMultimap<Long, Long> permissionGroup =
        groupPermissionByCasePoolId(permissionIntersections);
    Long workforceId = userPoolAssignmentsFromSameWorkGroupId.get(0).workforceId();
    return createGeneralRuleForWorkgroupId(permissionGroup, workforceId, workgroupId);
  }

  private static ImmutableSet<PoolAssignmentModel> findPermissionIntersection(
      List<UserPoolAssignmentModel> userPoolAssignmentsFromSameWorkGroupId) {
    ImmutableSet<PoolAssignmentModel> permissionIntersections =
        userPoolAssignmentsFromSameWorkGroupId.get(0).poolAssignments();

    for (UserPoolAssignmentModel user : userPoolAssignmentsFromSameWorkGroupId) {
      permissionIntersections =
          ImmutableSet.copyOf(Sets.intersection(permissionIntersections, user.poolAssignments()));
      if (permissionIntersections.size() == 0) {
        return ImmutableSet.of();
      }
    }
    return permissionIntersections;
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
