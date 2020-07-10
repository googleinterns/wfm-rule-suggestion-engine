package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * KarnaughMapRuleGenerator class is used to create rule based on the minimized results from
 * KarnaughMapReduction class.
 *
 * <p>Steps:
 *
 * <ol>
 *   <li>Step 1: For each term(ImmutableList<Integer>) in the minimizedTerms set, check whether the
 *       term can be converted or not.
 *   <li>Step 2: Check whether the term can be converted or not. If there is one index in the term
 *       equal to 0, the term can be converted. Otherwise, the term can not be converted.
 *   <li>Step 3: If the term can be converted, find out all indexes in term that are 0. Use the
 *       filterByIndex BiMap to get the {@link FilterModel} that are corresponding to each index.
 *   <li>Step 4: Create a new {@link RuleModel} based on converted results and passed-in values.
 * </ol>
 */
public class KarnaughMapRuleGenerator {

  public static RuleModel generateSingleRule(
      ImmutableSet<ImmutableList<Integer>> minimizedTerms,
      ImmutableBiMap<FilterModel, Integer> filterByIndex,
      Long workforceId,
      Long workgroupId,
      Long casePoolId,
      Set<Long> permissionSetIds) {
    return RuleModel.builder()
        .setWorkforceId(workforceId)
        .setWorkgroupId(workgroupId)
        .setCasePoolId(casePoolId)
        .setPermissionSetIds(permissionSetIds)
        .setFilters(convertEligibleTermsToFilters(minimizedTerms, filterByIndex))
        .build();
  }

  private static ImmutableList<ImmutableSet<FilterModel>> convertEligibleTermsToFilters(
      ImmutableSet<ImmutableList<Integer>> minimizedTerms,
      ImmutableBiMap<FilterModel, Integer> filterByIndex) {
    return minimizedTerms.stream()
        .filter(term -> term.stream().anyMatch(value -> value == 0))
        .map(term -> convertTermToFilters(filterByIndex, term))
        .collect(toImmutableList());
  }

  private static ImmutableSet<FilterModel> convertTermToFilters(
      ImmutableBiMap<FilterModel, Integer> filterByIndex, ImmutableList<Integer> term) {
    ImmutableSet.Builder<FilterModel> filtersBuilder = ImmutableSet.builder();
    for (int index = 0; index < term.size(); index++) {
      if (term.get(index) == 0) {
        filtersBuilder.add(filterByIndex.inverse().get(index));
      }
    }
    return filtersBuilder.build();
  }
}
