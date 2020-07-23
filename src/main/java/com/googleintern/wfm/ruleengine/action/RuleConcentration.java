package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import src.main.java.com.googleintern.wfm.ruleengine.action.generator.RuleIdGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.ImmutableSetMultimap.toImmutableSetMultimap;

/**
 * RuleConcentration class is used to decrease the number of generated rules by combining rules from
 * the same workgroup Id, same case Pool Id and same filters.
 *
 * <p>Steps:
 *
 * <ol>
 *   <li>Step 1: Group generated rules by their workgroup Id.
 *   <li>Step 2: For rules having the same workgroup Id, group them by their case pool Id.
 *   <li>Step 3: For rules having the same workgroup Id and the same case pool Id, group them by
 *       filters.
 *   <li>Step 4: For rules having the same workgroup Id, the same case pool Id and same filters,
 *       combine their permission set Ids to form a new rules. Rule Ids for new rules are separated
 *       from input parameter generatedRules and start from 0 again.
 * </ol>
 */
public class RuleConcentration {
  public static ImmutableSet<RuleModel> concentrate(ImmutableSet<RuleModel> generatedRules) {
    ImmutableSetMultimap<Long, RuleModel> rulesByWorkgroupId =
        generatedRules.stream()
            .collect(toImmutableSetMultimap(rule -> rule.workgroupId(), rule -> rule));
    RuleIdGenerator ruleIdGenerator = new RuleIdGenerator();
    ImmutableSet.Builder<RuleModel> concentratedRulesBuilder = ImmutableSet.builder();
    for (Long workgroupId : rulesByWorkgroupId.keySet()) {
      ImmutableSetMultimap<Long, RuleModel> rulesByCasePoolId =
          rulesByWorkgroupId.get(workgroupId).stream()
              .collect(toImmutableSetMultimap(rule -> rule.casePoolId(), rule -> rule));
      for (Long casePoolId : rulesByCasePoolId.keySet()) {
        concentratedRulesBuilder.addAll(
            generateConcentratedRules(
                rulesByCasePoolId.get(casePoolId).stream()
                    .collect(toImmutableListMultimap(rule -> rule.filters(), rule -> rule)),
                ruleIdGenerator));
      }
    }
    return concentratedRulesBuilder.build();
  }

  private static ImmutableSet<RuleModel> generateConcentratedRules(
      ImmutableListMultimap<ImmutableList<ImmutableSet<FilterModel>>, RuleModel> rulesByFilters,
      RuleIdGenerator ruleIdGenerator) {
    ImmutableSet.Builder<RuleModel> concentratedRulesBuilder = ImmutableSet.builder();
    for (ImmutableList<ImmutableSet<FilterModel>> filters : rulesByFilters.keySet()) {
      ImmutableList<RuleModel> sameFiltersRules = rulesByFilters.get(filters);
      if (!sameFiltersRules.isEmpty()) {
        concentratedRulesBuilder.add(
            RuleModel.builder()
                .setRuleId(ruleIdGenerator.getRuleId())
                .setWorkforceId(sameFiltersRules.get(0).workforceId())
                .setWorkgroupId(sameFiltersRules.get(0).workgroupId())
                .setCasePoolId(sameFiltersRules.get(0).casePoolId())
                .setPermissionSetIds(
                    sameFiltersRules.stream()
                        .flatMap(rule -> rule.permissionSetIds().stream())
                        .collect(toImmutableSet()))
                .setFilters(filters)
                .build());
      }
    }
    return concentratedRulesBuilder.build();
  }
}
