package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import src.main.java.com.googleintern.wfm.ruleengine.model.KarnaughMapComparisionResultModel;

/**
 * KarnaughMapReduction class is used to minimize Karnaugh Map terms formed by
 * KarnaughMapTermGenerator class.
 *
 * <p>Steps:
 *
 * <ol>
 *   <li>Step 1: Compare every pairs of terms and check whether they can be minimized. If two terms
 *       are mismatched in only one index, they can be minimized.
 *   <li>Step 2: If the pair of terms can be minimized, generate the minimized result that has same
 *       values in every index except giving -1 for the index where the compared terms are
 *       different.(1: Filter related to this index should be presented; 0: Filter related to this
 *       index should not be presented; -1: do not care whether Filter related to this index is
 *       presented or not.)
 *   <li>Step 3: Save minimized results in {@link KarnaughMapComparisionResultModel}. The
 *       minimizedResults set stores the results that are produced by minimizing a pair of terms.
 *       The minimizedTerms set stores every terms that can be minimized.
 *   <li>Step 4: Terms that can be minimized are in their simplest form already. Save these terms in
 *       the finalMinimizedResults.
 *   <li>Step 5: Set termsNeedToMinimize to minimizedResults. Go back to Step 1 and continue looping
 *       until there are no more terms in the termsNeedToMinimize.
 * </ol>
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
          minimizedTermsBuilder
              .add(allZeroTerms.get(termIndex))
              .add(allZeroTerms.get(compareIndex));
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
      if (difference > 1) {
        return false;
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
