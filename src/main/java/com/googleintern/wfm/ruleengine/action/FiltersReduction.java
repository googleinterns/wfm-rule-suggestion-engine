package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * FiltersReduction class is used to minimize the size of filter groups that can lead to permission
 * assignments.
 *
 * <p>Steps:
 *
 * <ol>
 *   <li>Step 1: Find all filter groups that are associated with the target {@link
 *       PoolAssignmentModel}.
 *   <li>Step 2: For each filter group i, if the reducedFilterResults list is empty, add to the list
 *       and continue. If the reducedFilterResults is not empty, loop through every filter group j
 *       in the list and compare filter group i with filter group j. If filter group j includes all
 *       elements in filter group i, filter group j is not a minimized group and can be removed from
 *       reducedFilterResults list. Filter group i will be added to the list. On the other hand, if
 *       filter group i includes all elements in filter group j, filter group i is not a minimized
 *       group. Stop looping through the reducedFilterResults list and move to another filter group.
 * </ol>
 */
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
