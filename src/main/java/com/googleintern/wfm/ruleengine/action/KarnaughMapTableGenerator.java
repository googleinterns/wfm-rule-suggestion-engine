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
public class KarnaughMapTableGenerator {

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
    ImmutableBiMap.Builder<FilterModel, Integer> filterByIndexBuilder = ImmutableBiMap.builder();
    int index = 0;
    for (FilterModel filter : allTypesOfFilters) {
      filterByIndexBuilder.put(filter, index);
      index = index + 1;
    }
    return filterByIndexBuilder.build();
  }

  public static ImmutableSet<ImmutableList<Integer>> findAllZeroCases(
      ImmutableBiMap<FilterModel, Integer> filterByIndex,
      ImmutableSet<ImmutableList<FilterModel>> filters) {
    int totalNumberOfFilters = filterByIndex.keySet().size();
    ImmutableList<Integer> filterNumberAllZeros =
        ImmutableList.copyOf(new ArrayList<>(Collections.nCopies(totalNumberOfFilters, 0)));
    ImmutableSet<ImmutableList<Integer>> allFilterPermutations =
        findAllPermutations(filterNumberAllZeros, totalNumberOfFilters);
    ImmutableSet<ImmutableList<Integer>> allOneCases =
        findAllOneCases(filterByIndex, allFilterPermutations, filters);
    return ImmutableSet.copyOf(Sets.difference(allFilterPermutations, allOneCases));
  }

  private static ImmutableSet<FilterModel> findAllTypesOfFilters(
      ImmutableSet<ImmutableList<FilterModel>> filters) {
    if (filters.size() == 0) {
      return ImmutableSet.of();
    }
    ImmutableSet.Builder<FilterModel> allTypesOfFiltersBuilder = ImmutableSet.builder();
    for (ImmutableList<FilterModel> filterList : filters) {
      for (FilterModel filter : filterList) {
        allTypesOfFiltersBuilder.add(filter);
      }
    }
    return allTypesOfFiltersBuilder.build();
  }

  private static ImmutableSet<ImmutableList<Integer>> findAllIncludedCases(
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
      if (areFilterCaseIncluded(filterPermutation, onesIndex)) {
        allIncludedCasesBuilder.add(filterPermutation);
      }
    }
    return allIncludedCasesBuilder.build();
  }

  private static boolean areFilterCaseIncluded(
      ImmutableList<Integer> filterPermutation, ImmutableSet<Integer> onesIndex) {
    for (Integer index : onesIndex) {
      if (filterPermutation.get(index) == 0) {
        return false;
      }
    }
    return true;
  }

  private static ImmutableSet<ImmutableList<Integer>> findAllOneCases(
      ImmutableBiMap<FilterModel, Integer> filterByIndex,
      ImmutableSet<ImmutableList<Integer>> allFilterPermutations,
      ImmutableSet<ImmutableList<FilterModel>> filters) {
    ImmutableSet.Builder<ImmutableList<Integer>> allOneCasesBuilder = ImmutableSet.builder();
    for (ImmutableList<FilterModel> filterList : filters) {
      allOneCasesBuilder.addAll(
          findAllIncludedCases(filterByIndex, allFilterPermutations, filterList));
    }
    return allOneCasesBuilder.build();
  }

  private static ImmutableSet<ImmutableList<Integer>> findAllPermutations(
      List<Integer> filterNumber, int index) {
    if (index < 0) {
      return ImmutableSet.of();
    }
    ImmutableSet.Builder<ImmutableList<Integer>> permutationsBuilder = ImmutableSet.builder();
    filterNumber.set(index, 0);
    permutationsBuilder.add(ImmutableList.copyOf(filterNumber));
    permutationsBuilder.addAll(findAllPermutations(filterNumber, index - 1));
    filterNumber.set(index, 1);
    permutationsBuilder.add(ImmutableList.copyOf(filterNumber));
    permutationsBuilder.addAll(findAllPermutations(filterNumber, index - 1));
    return permutationsBuilder.build();
  }
}
