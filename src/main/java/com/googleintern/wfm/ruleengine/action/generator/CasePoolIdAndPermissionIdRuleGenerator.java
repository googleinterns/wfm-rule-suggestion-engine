package src.main.java.com.googleintern.wfm.ruleengine.action.generator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * CasePoolIdAndPermissionIdRuleGenerator class is used to create rules based on reduced {@link
 * FilterModel} results from the FiltersReduction class.
 */
public class CasePoolIdAndPermissionIdRuleGenerator {
  public static ImmutableSet<RuleModel> generateRules(
      Long workforceId,
      Long workgroupId,
      PoolAssignmentModel poolAssignment,
      ImmutableList<ImmutableSet<FilterModel>> filters) {
    return filters.stream()
        .map(
            filtersGroup ->
                RuleModel.builder()
                    .setWorkforceId(workforceId)
                    .setWorkgroupId(workgroupId)
                    .setCasePoolId(poolAssignment.casePoolId())
                    .setPermissionSetIds(ImmutableSet.of(poolAssignment.permissionSetId()))
                    .setFilters(
                        filtersGroup.stream()
                            .map(filter -> ImmutableSet.of(filter))
                            .collect(toImmutableList()))
                    .build())
        .collect(toImmutableSet());
  }
}
