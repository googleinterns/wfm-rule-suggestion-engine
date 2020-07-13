package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;

import java.util.ArrayList;
import java.util.List;

public class FiltersReduction {
  public static ImmutableList<ImmutableSet<FilterModel>> reduce(
      ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
          filtersByCasePoolIdAndPermissionSetId,
      PoolAssignmentModel targetPoolAssignment) {
    ImmutableSet<ImmutableList<FilterModel>> filters =
        filtersByCasePoolIdAndPermissionSetId.get(targetPoolAssignment);
    List<ImmutableSet<FilterModel>> reducedFilterResults = new ArrayList<>();
    for (ImmutableList<FilterModel> currentFilters : filters) {
      if (reducedFilterResults.isEmpty()) {
        reducedFilterResults.add(ImmutableSet.copyOf(currentFilters));
        continue;
      }
      reducedFilterResults = updateReducedFilterResults(reducedFilterResults, currentFilters);
    }
    return ImmutableList.copyOf(reducedFilterResults);
  }

  private static List<ImmutableSet<FilterModel>> updateReducedFilterResults(
      List<ImmutableSet<FilterModel>> reducedFilterResults,
      ImmutableList<FilterModel> currentFilters) {
    boolean canBeAdded = true;
    List<ImmutableSet<FilterModel>> removedFilters = new ArrayList<>();
    for (ImmutableSet<FilterModel> reducedFilters : reducedFilterResults) {
      if (reducedFilters.containsAll(currentFilters)) {
        removedFilters.add(reducedFilters);
      } else if (currentFilters.containsAll(reducedFilters)) {
        canBeAdded = false;
        break;
      }
    }
    if (canBeAdded) {
      reducedFilterResults.add(ImmutableSet.copyOf(currentFilters));
    }
    removedFilters.forEach(removedFiltersGroup -> reducedFilterResults.remove(removedFiltersGroup));
    return reducedFilterResults;
  }
}
