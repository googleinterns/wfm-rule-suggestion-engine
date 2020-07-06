package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.*;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    List<Integer> filterNumberAllZeros =
        new ArrayList<>(Collections.nCopies(totalNumberOfFilters, 0));
    ImmutableSet<ImmutableList<Integer>> allFilterPermutations =
        findAllPossibleTerms(filterNumberAllZeros, totalNumberOfFilters - 1);
    ImmutableSet<ImmutableList<Integer>> allOneTerms =
        findAllOneTerms(filterByIndex, allFilterPermutations, filters);
    return ImmutableSet.copyOf(Sets.difference(allFilterPermutations, allOneTerms));
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

  private static ImmutableSet<ImmutableList<Integer>> findAllIncludedTerms(
      ImmutableBiMap<FilterModel, Integer> filterByIndex,
      ImmutableSet<ImmutableList<Integer>> allFilterPermutations,
      ImmutableList<FilterModel> filters) {
    ImmutableSet.Builder<Integer> onesIndexBuilder = ImmutableSet.builder();
    for (FilterModel filter : filters) {
      onesIndexBuilder.add(filterByIndex.get(filter));
    }
    ImmutableSet<Integer> onesIndex = onesIndexBuilder.build();
    ImmutableSet.Builder<ImmutableList<Integer>> allIncludedCasesBuilder = ImmutableSet.builder();
    for (ImmutableList filterPermutation : allFilterPermutations) {
      if (isTermIncluded(filterPermutation, onesIndex)) {
        allIncludedCasesBuilder.add(filterPermutation);
      }
    }
    return allIncludedCasesBuilder.build();
  }

  private static boolean isTermIncluded(
      ImmutableList<Integer> filterPermutation, ImmutableSet<Integer> onesIndex) {
    for (Integer index : onesIndex) {
      if (filterPermutation.get(index) == 0) {
        return false;
      }
    }
    return true;
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
      List<Integer> filterNumber, int index) {
    if (index < 0) {
      return ImmutableSet.of();
    }
    ImmutableSet.Builder<ImmutableList<Integer>> permutationsBuilder = ImmutableSet.builder();
    filterNumber.set(index, 0);
    permutationsBuilder.add(ImmutableList.copyOf(filterNumber));
    permutationsBuilder.addAll(findAllPossibleTerms(filterNumber, index - 1));
    filterNumber.set(index, 1);
    permutationsBuilder.add(ImmutableList.copyOf(filterNumber));
    permutationsBuilder.addAll(findAllPossibleTerms(filterNumber, index - 1));
    return permutationsBuilder.build();
  }
}
