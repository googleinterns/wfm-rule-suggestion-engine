package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * FiltersReduction class is used to minimize the size of filter groups that can lead to permission
 * assignments.
 *
 * <p>Steps:
 *
 * <ol>
 *   <li>Step 1: Find all filter groups that are associated with the target {@link
 *       PoolAssignmentModel}.
 *   <li>Step 2: For each {@link FilterModel} set i, if the reducedFilterResults list is empty, add
 *       to the list and continue. If the reducedFilterResults is not empty, loop through every
 *       {@link FilterModel} set j stored in the list and compare {@link FilterModel} set i with
 *       filter group j. If {@link FilterModel} set j includes all elements in {@link FilterModel}
 *       set i, {@link FilterModel} set j is not a minimized filters group and can be removed from
 *       reducedFilterResults list. Collect these filters groups in removedFilters set and delete
 *       them together at the end. Minimized filters group are the least common filters that can
 *       assign the target {@link PoolAssignmentModel}. The {@link FilterModel} set i will be added
 *       to the list. On the other hand, if {@link FilterModel} set i includes all elements in
 *       filter group j, filter group i is not a minimized filters group. Stop looping through the
 *       reducedFilterResults list and move to another filter group.
 * </ol>
 */
public class FiltersReduction {
  public static ImmutableList<ImmutableSet<FilterModel>> reduce(
      SetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>> filtersByPoolAssignments,
      PoolAssignmentModel targetPoolAssignment) {
    Set<ImmutableList<FilterModel>> filters = filtersByPoolAssignments.get(targetPoolAssignment);
    List<ImmutableSet<FilterModel>> reducedFilterResults = new ArrayList<>();
    for (ImmutableList<FilterModel> currentFilters : filters) {
      reducedFilterResults = updateReducedFilterResults(reducedFilterResults, currentFilters);
    }
    return ImmutableList.copyOf(reducedFilterResults);
  }

  private static List<ImmutableSet<FilterModel>> updateReducedFilterResults(
      List<ImmutableSet<FilterModel>> reducedFilterResults,
      ImmutableList<FilterModel> currentFilters) {
    ImmutableList<ImmutableSet<FilterModel>> reducedFiltersCoveredByCurrentFilters =
        reducedFilterResults.stream()
            .filter(reducedFilters -> reducedFilters.containsAll(currentFilters))
            .collect(toImmutableList());
    ImmutableList<ImmutableSet<FilterModel>> reducedFiltersIncludedCurrentFilters =
        reducedFilterResults.stream()
            .filter(reducedFilters -> currentFilters.containsAll(reducedFilters))
            .collect(toImmutableList());
    if (!reducedFiltersCoveredByCurrentFilters.isEmpty()
        || reducedFiltersIncludedCurrentFilters.isEmpty()) {
      reducedFilterResults.add(ImmutableSet.copyOf(currentFilters));
      reducedFilterResults.removeAll(reducedFiltersCoveredByCurrentFilters);
    }
    return reducedFilterResults;
  }
}
