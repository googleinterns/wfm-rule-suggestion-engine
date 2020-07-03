package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import src.main.java.com.googleintern.wfm.ruleengine.model.KarnaughMapComparisionResultModel;

/**
 * KarnaughMapReduction class is used to minimize Karnaugh Map terms formed by
 * KarnaughMapTermGenerator class.
 */
public class KarnaughMapReduction {

  public static ImmutableSet<ImmutableList<Integer>> minimizeKMapTerms(
      ImmutableSet<ImmutableList<Integer>> allZeroTerms) {
    ImmutableSet.Builder<ImmutableList<Integer>> finalMinimizedResultsBuilder =
        ImmutableSet.builder();
    ImmutableList<ImmutableList<Integer>> termsNeedToMinimize = allZeroTerms.asList();
    while (!termsNeedToMinimize.isEmpty()) {
      KarnaughMapComparisionResultModel compareResult = compareKMapTerms(termsNeedToMinimize);
      finalMinimizedResultsBuilder.addAll(
          Sets.difference(ImmutableSet.copyOf(termsNeedToMinimize), compareResult.minimizedTerms())
              .immutableCopy());
      termsNeedToMinimize = compareResult.minimizedResults();
    }
    return finalMinimizedResultsBuilder.build();
  }

  private static KarnaughMapComparisionResultModel compareKMapTerms(
      ImmutableList<ImmutableList<Integer>> allZeroTerms) {
    ImmutableList.Builder<ImmutableList<Integer>> minimizedResultBuilder = ImmutableList.builder();
    ImmutableSet.Builder<ImmutableList<Integer>> minimizedTermsBuilder = ImmutableSet.builder();
    for (int termIndex = 0; termIndex < allZeroTerms.size() - 1; termIndex++) {
      for (int compareIndex = termIndex + 1; compareIndex < allZeroTerms.size(); compareIndex++) {
        if (canMinimize(allZeroTerms.get(termIndex), allZeroTerms.get(compareIndex))) {
          minimizedResultBuilder.add(
              generateMinimizedTerm(allZeroTerms.get(termIndex), allZeroTerms.get(compareIndex)));
          minimizedTermsBuilder.add(allZeroTerms.get(termIndex));
          minimizedTermsBuilder.add(allZeroTerms.get(compareIndex));
        }
      }
    }
    return KarnaughMapComparisionResultModel.builder()
        .setMinimizedResults(minimizedResultBuilder.build())
        .setMinimizedTerms(minimizedTermsBuilder.build())
        .build();
  }

  private static boolean canMinimize(ImmutableList<Integer> term1, ImmutableList<Integer> term2) {
    if (term1.size() != term2.size()) {
      return false;
    }
    int difference = 0;
    for (int index = 0; index < term1.size(); index++) {
      if (term1.get(index) != term2.get(index)) {
        difference = difference + 1;
      }
    }
    return difference == 1;
  }

  private static ImmutableList<Integer> generateMinimizedTerm(
      ImmutableList<Integer> term1, ImmutableList<Integer> term2) {
    if (term1.size() != term2.size()) {
      return ImmutableList.of();
    }
    ImmutableList.Builder<Integer> simplifiedTermBuilder = ImmutableList.builder();
    for (int index = 0; index < term1.size(); index++) {
      if (term1.get(index) == term2.get(index)) {
        simplifiedTermBuilder.add(term1.get(index));
      } else {
        simplifiedTermBuilder.add(-1);
      }
    }
    return simplifiedTermBuilder.build();
  }
}
