package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class ProductOfSumReduction {
  public static ImmutableList<ImmutableSet<FilterModel>> reduce(
      ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
          filtersByCasePoolIdAndPermissionSetId,
      PoolAssignmentModel targetPoolAssignment) {
    ImmutableSet<ImmutableList<FilterModel>> filters =
        filtersByCasePoolIdAndPermissionSetId.get(targetPoolAssignment);
    List<ImmutableSet<FilterModel>> reducedResults = new ArrayList<>();
    for (ImmutableList<FilterModel> filterGroup : filters) {
      ImmutableSet<FilterModel> currentFilterGroup = ImmutableSet.copyOf(filterGroup);
      if (reducedResults.isEmpty()) {
        reducedResults.add(currentFilterGroup);
        continue;
      }
      boolean canBeAdded = true;
      List<ImmutableSet<FilterModel>> removedFilters = new ArrayList<>();
      for (ImmutableSet<FilterModel> reducedFilterGroup : reducedResults) {
        if (reducedFilterGroup.containsAll(currentFilterGroup)) {
          removedFilters.add(reducedFilterGroup);
        }
        if (currentFilterGroup.containsAll(reducedFilterGroup)) {
          canBeAdded = false;
          break;
        }
      }
      if (canBeAdded) {
        reducedResults.add(currentFilterGroup);
      }
      for (ImmutableSet<FilterModel> removedFilterGroup : removedFilters) {
        reducedResults.remove(removedFilterGroup);
      }
    }
    return ImmutableList.copyOf(reducedResults);
  }

  public static ImmutableSet<RuleModel> generateRules(
      Long workforceId,
      Long workgroupId,
      PoolAssignmentModel poolAssignment,
      ImmutableList<ImmutableSet<FilterModel>> reducedFilters) {
    ImmutableSet.Builder<RuleModel> generateRulesBuilder = ImmutableSet.builder();
    for (ImmutableSet<FilterModel> filtersGroup : reducedFilters) {
      ImmutableList<ImmutableSet<FilterModel>> filters =
          filtersGroup.stream().map(filter -> ImmutableSet.of(filter)).collect(toImmutableList());
      generateRulesBuilder.add(
          RuleModel.builder()
              .setWorkforceId(workforceId)
              .setWorkgroupId(workgroupId)
              .setCasePoolId(poolAssignment.casePoolId())
              .setPermissionSetIds(ImmutableSet.of(poolAssignment.permissionSetId()))
              .setFilters(filters)
              .build());
    }
    return generateRulesBuilder.build();
  }
}
