package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

import java.util.Set;

/**
 * KarnaughMapRuleGenerator class is used to create rule based on the minimized results from
 * KarnaughMapReduction class.
 */
public class KarnaughMapRuleGenerator {

  public static RuleModel singleRuleGenerator(
      ImmutableSet<ImmutableList<Integer>> minimizedTerms,
      ImmutableBiMap<FilterModel, Integer> filterByIndex,
      Long workgroupId,
      Long workforceId,
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
        .filter(term -> canBeConvertedInToFilters(term))
        .map(term -> convertTermToFilters(filterByIndex, term))
        .collect(ImmutableList.toImmutableList());
  }

  private static boolean canBeConvertedInToFilters(ImmutableList<Integer> term) {
    for (Integer value : term) {
      if (value == 0) {
        return true;
      }
    }
    return false;
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
