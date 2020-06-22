package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;

import java.util.HashMap;

public class GroupByCasePoolIdAndPermissionId {

  public static ImmutableListMultimap<PoolAssignmentModel, FilterModel>
      groupByCasePoolIdAndPermissionSetId(
          ImmutableList<UserPoolAssignmentModel> dataFromSameWorkGroupId) {
    ImmutableListMultimap.Builder<PoolAssignmentModel, FilterModel>
        mapByCasePoolIdAndPermissionSetIdBuilder =
            ImmutableListMultimap.<PoolAssignmentModel, FilterModel>builder();
    for (UserPoolAssignmentModel data : dataFromSameWorkGroupId) {
      ImmutableList<FilterModel> filters = convertSkillIdRoleIdToFilter(data);
      for (PoolAssignmentModel permission : data.poolAssignments()) {
        mapByCasePoolIdAndPermissionSetIdBuilder.putAll(permission, filters);
      }
    }
    return mapByCasePoolIdAndPermissionSetIdBuilder.build();
  }

  public static ImmutableList<FilterModel> convertSkillIdRoleIdToFilter(
      UserPoolAssignmentModel user) {
    ImmutableList.Builder<FilterModel> filtersBuilder = ImmutableList.builder();
    for (final Long skillId : user.skillIds()) {
      FilterModel filter =
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(skillId).build();
      filtersBuilder.add(filter);
    }
    for (final Long roleId : user.roleIds()) {
      FilterModel filter =
          FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(roleId).build();
      filtersBuilder.add(filter);
    }
    for (final Long roleSkillId : user.roleSkillIds()) {
      FilterModel filter =
          FilterModel.builder()
              .setType(FilterModel.FilterType.ROLESKILL)
              .setValue(roleSkillId)
              .build();
      filtersBuilder.add(filter);
    }
    return filtersBuilder.build();
  }

  public static ImmutableMap<FilterModel, Integer> countFilterTypesForPermissionId(
      ImmutableList<ImmutableList<FilterModel>> filterLists) {
    HashMap<FilterModel, Integer> map = new HashMap<>();
    int value = 0;
    for (final ImmutableList<FilterModel> filters : filterLists) {
      for (final FilterModel filter : filters) {
        if (!map.containsKey(filter)) {
          map.put(filter, value);
          value = value + 1;
        }
      }
    }
    return ImmutableMap.copyOf(map);
  }


}
