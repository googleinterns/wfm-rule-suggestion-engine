package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSetMultimap;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

import java.util.List;

/**
 * CasePoolIdAndPermissionIdGroupingUtil class is used to group data by (Case Pool ID, Permission
 * Set ID).
 */
public class CasePoolIdAndPermissionIdGroupingUtil {

  /** Group data from the same workgroup by (Case Pool ID, Permission Set ID). */
  public static ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
      groupByCasePoolIdAndPermissionSetId(List<UserModel> dataFromSameWorkGroupId) {
    ImmutableSetMultimap.Builder<PoolAssignmentModel, ImmutableList<FilterModel>>
        filtersByCasePoolIdAndPermissionSetIdBuilder = ImmutableSetMultimap.builder();
    for (UserModel data : dataFromSameWorkGroupId) {
      ImmutableList<FilterModel> filters = convertSkillIdRoleIdToFilter(data);
      for (PoolAssignmentModel permission : data.poolAssignments()) {
        filtersByCasePoolIdAndPermissionSetIdBuilder.put(permission, filters);
      }
    }
    return filtersByCasePoolIdAndPermissionSetIdBuilder.build();
  }

  public static ImmutableList<FilterModel> convertSkillIdRoleIdToFilter(
      UserModel user) {
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
              .setType(FilterModel.FilterType.SKILL)
              .setValue(roleSkillId)
              .build());
    }
    return filtersBuilder.build();
  }
}
