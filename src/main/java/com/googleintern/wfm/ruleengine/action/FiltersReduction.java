package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
<<<<<<< HEAD
 * FiltersReduction class is used to minimize/reduce/simplify the filters set for each {@link
 * PoolAssignmentModel}.
 */
public class FiltersReduction {
  /**
   * Find out all minimized common filter groups that can lead to the assignment of the target
   * {@link PoolAssignmentModel}.
   *
   * <p>Steps:
   *
   * <ol>
   *   <li>Step 1: Get all filter groups that are associated with the target {@link
   *       PoolAssignmentModel}. All corresponding filter groups are stored in a set. The
   *       relationships between any two filter groups in the set is OR. Each filter group is stored
   *       as a list. The relationships between any two filters inside one filter group is AND(e.g.
   *       if the filters for permission x is {{a, b}, {a, b, c}}, this means x = a * b + a * b *
   *       c). There is no negative filter. (e.g. all the filters are ones that can lead to the
   *       assignment)
   *   <li>Step 2: For each filter group, call the updateReducedFilters function to update the
   *       reducedFilters list.
   *   <li>Step 3: Return the reduced filter groups.
   * </ol>
   */
  public static ImmutableList<ImmutableSet<FilterModel>> reduce(
      SetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>> filtersByPoolAssignments,
      PoolAssignmentModel targetPoolAssignment) {
    Set<ImmutableList<FilterModel>> filters = filtersByPoolAssignments.get(targetPoolAssignment);
    List<ImmutableSet<FilterModel>> reducedFilters = new ArrayList<>();
    for (ImmutableList<FilterModel> currentFilters : filters) {
      reducedFilters = updateReducedFilters(reducedFilters, currentFilters);
    }
    return ImmutableList.copyOf(reducedFilters);
  }

  /**
   * Update the reducedFilters list using the input parameter currentFilters
   *
   * <p>Steps:
   *
   * <ol>
   *   <li>Step 1: Find all filter groups in the reducedFilters list that can be covered by the
   *       currentFilters. Store results in filtersContainingCurrentFilters list.
   *   <li>Step 2: Check whether there exists filter groups in the reducedFilters list that cover
   *       the currentFilters. Store result in the boolean variable
   *       isCurrentFiltersCoveredByReducedFilters.
   *   <li>Step 3: If the current filter is completely redundant to the reduced list, then do not
   *       update the list. Update the reducedFilters list when: (1) The currentFilter overlaps with
   *       the reduced filter list and it contains less filters(filtersContainingCurrentFilters list
   *       is not empty); (2) The currentFilter is mutually exclusive to the reduced
   *       list(isCurrentFiltersCoveredByReducedFilters is false).
   *   <li>Step 4: Return the updated reducedFilters list.
   * </ol>
   */
  private static List<ImmutableSet<FilterModel>> updateReducedFilters(
      List<ImmutableSet<FilterModel>> reducedFilters, ImmutableList<FilterModel> currentFilters) {
    ImmutableList<ImmutableSet<FilterModel>> filtersContainingCurrentFilters =
        reducedFilters.stream()
            .filter(filters -> filters.containsAll(currentFilters))
            .collect(toImmutableList());
    boolean isCurrentFiltersCoveredByReducedFilters =
        reducedFilters.stream().anyMatch(filters -> currentFilters.containsAll(filters));
    if (!filtersContainingCurrentFilters.isEmpty() || !isCurrentFiltersCoveredByReducedFilters) {
      reducedFilters.removeAll(filtersContainingCurrentFilters);
      reducedFilters.add(ImmutableSet.copyOf(currentFilters));
    }
    return reducedFilters;
  }
}
