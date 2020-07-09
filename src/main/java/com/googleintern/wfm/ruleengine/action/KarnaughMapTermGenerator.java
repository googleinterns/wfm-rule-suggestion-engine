package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.*;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * KarnaughMapTableGenerator class is used to prepare information/variables for Karnaugh Map
 * Reduction algorithm.
 */
public class KarnaughMapTermGenerator {

  /**
   * Create a BiMap that links each type of filter with an unique Integer.
   *
   * <p>Steps:
   *
   * <ol>
   *   <li>Step 1: Check whether the target {@link PoolAssignmentModel} is a valid or not.
   *   <li>Step 2: Find different types of {@link FilterModel} and stored them in allTypesOfFilters.
   *   <li>Step 3: Map each {@link FilterModel} by an unique Integer. Store the mapping results as
   *       BiMap format.
   * </ol>
   */
  public static ImmutableBiMap<FilterModel, Integer> mapFiltersByIndex(
      ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
          filtersByCasePoolIdAndPermissionSetId,
      PoolAssignmentModel poolAssignment) {
    if (!filtersByCasePoolIdAndPermissionSetId.containsKey(poolAssignment)) {
      return ImmutableBiMap.of();
    }
    ImmutableSet<ImmutableList<FilterModel>> filters =
        filtersByCasePoolIdAndPermissionSetId.get(poolAssignment);
    if (filters.size() == 0) {
      return ImmutableBiMap.of();
    }
    ImmutableSet<FilterModel> allTypesOfFilters = findAllTypesOfFilters(filters);
    return createFiltersByIndexBiMap(allTypesOfFilters);
  }

  /**
   * Find all combinations of filters that are not covered by the input data set.
   *
   * <p>Steps:
   *
   * <ol>
   *   <li>Step 1: Find all possible combinations of terms using Filter index in BiMap. Save
   *       generated results in allFilterPermutations.
   *   <li>Step 2: Find all one terms. One terms are defined as existing terms(Filter combinations)
   *       and covered terms from input data set. Save finding results in allOneTerms.
   *   <li>Step 3: Find all zero terms. Zero terms are terms that are not covered by the input data
   *       set.
   * </ol>
   */
  public static ImmutableSet<ImmutableList<Integer>> findAllZeroTerms(
      ImmutableBiMap<FilterModel, Integer> filterByIndex,
      ImmutableSet<ImmutableList<FilterModel>> filters) {
    int totalNumberOfFilters = filterByIndex.keySet().size();
    List<Integer> baseCaseTermWithAllZeros =
        new ArrayList<>(Collections.nCopies(totalNumberOfFilters, 0));
    ImmutableSet<ImmutableList<Integer>> allPossibleTerms =
        findAllPossibleTerms(baseCaseTermWithAllZeros, totalNumberOfFilters - 1);
    ImmutableSet<ImmutableList<Integer>> allOneTerms =
        findAllOneTerms(filterByIndex, allPossibleTerms, filters);
    return ImmutableSet.copyOf(Sets.difference(allPossibleTerms, allOneTerms));
  }

  private static ImmutableSet<FilterModel> findAllTypesOfFilters(
      ImmutableSet<ImmutableList<FilterModel>> filters) {
    if (filters.size() == 0) {
      return ImmutableSet.of();
    }
    ImmutableSet.Builder<FilterModel> allTypesOfFiltersBuilder = ImmutableSet.builder();
    filters.forEach(
        filterList -> filterList.forEach(filter -> allTypesOfFiltersBuilder.add(filter)));
    return allTypesOfFiltersBuilder.build();
  }

  private static ImmutableBiMap<FilterModel, Integer> createFiltersByIndexBiMap(
          ImmutableSet<FilterModel> allTypesOfFilters) {
    ImmutableBiMap.Builder<FilterModel, Integer> filterByIndexBuilder = ImmutableBiMap.builder();
    int index = 0;
    for (FilterModel filter : allTypesOfFilters) {
      filterByIndexBuilder.put(filter, index);
      index = index + 1;
    }
    return filterByIndexBuilder.build();
  }

  private static ImmutableSet<ImmutableList<Integer>> findAllIncludedTerms(
      ImmutableBiMap<FilterModel, Integer> filterByIndex,
      ImmutableSet<ImmutableList<Integer>> allTerms,
      ImmutableList<FilterModel> filters) {
    ImmutableSet<Integer> onesIndex =
        filters.stream().map(filter -> filterByIndex.get(filter)).collect(toImmutableSet());
    return allTerms.stream()
        .filter(term -> onesIndex.stream().filter(index -> term.get(index) == 0).count() == 0)
        .collect(toImmutableSet());
  }

  private static ImmutableSet<ImmutableList<Integer>> findAllOneTerms(
      ImmutableBiMap<FilterModel, Integer> filterByIndex,
      ImmutableSet<ImmutableList<Integer>> allFilterPermutations,
      ImmutableSet<ImmutableList<FilterModel>> filters) {
    ImmutableSet.Builder<ImmutableList<Integer>> allOneCasesBuilder = ImmutableSet.builder();
    filters.forEach(
        filterList ->
            allOneCasesBuilder.addAll(
                findAllIncludedTerms(filterByIndex, allFilterPermutations, filterList)));
    return allOneCasesBuilder.build();
  }

  private static ImmutableSet<ImmutableList<Integer>> findAllPossibleTerms(
      List<Integer> term, int index) {
    if (index < 0) {
      return ImmutableSet.of();
    }
    ImmutableSet.Builder<ImmutableList<Integer>> permutationsBuilder = ImmutableSet.builder();
    term.set(index, 0);
    permutationsBuilder.add(ImmutableList.copyOf(term));
    permutationsBuilder.addAll(findAllPossibleTerms(term, index - 1));
    term.set(index, 1);
    permutationsBuilder.add(ImmutableList.copyOf(term));
    permutationsBuilder.addAll(findAllPossibleTerms(term, index - 1));
    return permutationsBuilder.build();
  }
}
