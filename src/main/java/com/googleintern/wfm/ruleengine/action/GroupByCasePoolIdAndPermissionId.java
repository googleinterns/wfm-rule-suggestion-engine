package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;

import java.util.HashMap;

/**
 * GroupByCasePoolIdAndPermissionId class is used to group data by (Case Pool ID, Permission Set
 * ID).
 */
public class GroupByCasePoolIdAndPermissionId {

  /**
   * Group data from the same workgroup by (Case Pool ID, Permission Set ID).
   * @param dataFromSameWorkGroupId
   * @return
   */
  public static ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
      groupByCasePoolIdAndPermissionSetId(
          ImmutableList<UserPoolAssignmentModel> dataFromSameWorkGroupId) {
    ImmutableSetMultimap.Builder<PoolAssignmentModel, ImmutableList<FilterModel>>
        mapByCasePoolIdAndPermissionSetIdBuilder = ImmutableSetMultimap.builder();
    for (UserPoolAssignmentModel data : dataFromSameWorkGroupId) {
      ImmutableList<FilterModel> filters = convertSkillIdRoleIdToFilter(data);
      for (PoolAssignmentModel permission : data.poolAssignments()) {
        mapByCasePoolIdAndPermissionSetIdBuilder.put(permission, filters);
      }
    }
    return mapByCasePoolIdAndPermissionSetIdBuilder.build();
  }

  private static ImmutableList<FilterModel> convertSkillIdRoleIdToFilter(
      UserPoolAssignmentModel user) {
    ImmutableList.Builder<FilterModel> filtersBuilder = ImmutableList.builder();
    for (Long roleId : user.roleIds()) {
      filtersBuilder.add(
          FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(roleId).build());
    }
    for (Long skillId : user.skillIds()) {
      filtersBuilder.add(
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(skillId).build());
    }
    for (Long roleSkillId : user.roleSkillIds()) {
      filtersBuilder.add(
          FilterModel.builder()
              .setType(FilterModel.FilterType.ROLESKILL)
              .setValue(roleSkillId)
              .build());
    }
    return filtersBuilder.build();
  }
}
