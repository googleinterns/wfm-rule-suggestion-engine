package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import src.main.java.com.googleintern.wfm.ruleengine.action.generator.RuleIdGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

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
 *       combine their permission set Ids to form a new rules.
 * </ol>
 */
public class RuleConcentration {
  public static ImmutableSet<RuleModel> concentrate(ImmutableSet<RuleModel> generatedRules) {
    ImmutableSetMultimap<Long, RuleModel> rulesByWorkgroupId =
        mapRulesByWorkgroupId(generatedRules);
    RuleIdGenerator ruleIdGenerator = new RuleIdGenerator();
    ImmutableSet.Builder<RuleModel> concentratedRulesBuilder = ImmutableSet.builder();
    for (Long workgroupId : rulesByWorkgroupId.keySet()) {
      ImmutableSetMultimap<Long, RuleModel> rulesByCasePoolId =
          mapRulesByCasePoolId(rulesByWorkgroupId.get(workgroupId));
      for (Long casePoolId : rulesByCasePoolId.keySet()) {
        concentratedRulesBuilder.addAll(
            generateConcentratedRules(
                mapRulesByFilters(rulesByCasePoolId.get(casePoolId)), ruleIdGenerator));
      }
    }
    return concentratedRulesBuilder.build();
  }

  private static ImmutableSetMultimap<Long, RuleModel> mapRulesByWorkgroupId(
      ImmutableSet<RuleModel> generatedRules) {
    ImmutableSetMultimap.Builder<Long, RuleModel> rulesByWorkgroupIdBuilder =
        ImmutableSetMultimap.builder();
    generatedRules.forEach(rule -> rulesByWorkgroupIdBuilder.put(rule.workgroupId(), rule));
    return rulesByWorkgroupIdBuilder.build();
  }

  private static ImmutableSetMultimap<Long, RuleModel> mapRulesByCasePoolId(
      ImmutableSet<RuleModel> rulesForSameWorkgroup) {
    ImmutableSetMultimap.Builder<Long, RuleModel> rulesByCasePoolIdBuilder =
        ImmutableSetMultimap.builder();
    rulesForSameWorkgroup.forEach(rule -> rulesByCasePoolIdBuilder.put(rule.casePoolId(), rule));
    return rulesByCasePoolIdBuilder.build();
  }

  private static ImmutableListMultimap<ImmutableList<ImmutableSet<FilterModel>>, RuleModel>
      mapRulesByFilters(ImmutableSet<RuleModel> rulesForSameCasePoolId) {
    ImmutableListMultimap.Builder<ImmutableList<ImmutableSet<FilterModel>>, RuleModel>
        rulesByFiltersBuilder = ImmutableListMultimap.builder();
    rulesForSameCasePoolId.forEach(rule -> rulesByFiltersBuilder.put(rule.filters(), rule));
    return rulesByFiltersBuilder.build();
  }

  private static ImmutableSet<RuleModel> generateConcentratedRules(
      ImmutableListMultimap<ImmutableList<ImmutableSet<FilterModel>>, RuleModel> rulesByFilters,
      RuleIdGenerator ruleIdGenerator) {
    ImmutableSet.Builder<RuleModel> concentratedRulesBuilder = ImmutableSet.builder();
    for (ImmutableList<ImmutableSet<FilterModel>> filters : rulesByFilters.keySet()) {
      ImmutableList<RuleModel> sameFiltersRules = rulesByFilters.get(filters);
      concentratedRulesBuilder.add(
          RuleModel.builder()
              .setRuleId(ruleIdGenerator.getRuleId())
              .setWorkforceId(sameFiltersRules.get(0).workforceId())
              .setWorkgroupId(sameFiltersRules.get(0).workgroupId())
              .setCasePoolId(sameFiltersRules.get(0).casePoolId())
              .setPermissionSetIds(concentratePermissionSetIds(sameFiltersRules))
              .setFilters(filters)
              .build());
    }
    return concentratedRulesBuilder.build();
  }

  private static ImmutableSet<Long> concentratePermissionSetIds(
      ImmutableList<RuleModel> sameFiltersRules) {
    ImmutableSet.Builder<Long> permissionSetIdsBuilder = ImmutableSet.builder();
    sameFiltersRules.forEach(rule -> permissionSetIdsBuilder.addAll(rule.permissionSetIds()));
    return permissionSetIdsBuilder.build();
  }
}
