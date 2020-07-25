package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.RuleConcentration;
import src.main.java.com.googleintern.wfm.ruleengine.action.generator.RuleIdGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

/** RuleConcentrationTest class is used to test the functionality of RuleConcentration class. */
public class RuleConcentrationTest {
  private static final FilterModel FILTER_0 =
      FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(1111L).build();
  private static final FilterModel FILTER_1 =
      FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(2222L).build();
  private static final FilterModel FILTER_2 =
      FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(3333L).build();
  private static final FilterModel FILTER_3 =
      FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(4444L).build();
  private static final FilterModel FILTER_4 =
      FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(5555L).build();

  private static final ImmutableList<ImmutableSet<FilterModel>> FILTERS_0 =
      ImmutableList.of(ImmutableSet.of(FILTER_0, FILTER_1));

  private static final ImmutableList<ImmutableSet<FilterModel>> FILTERS_1 =
      ImmutableList.of(ImmutableSet.of(FILTER_2), ImmutableSet.of(FILTER_3, FILTER_4));

  private static final RuleModel RULE_0 =
      RuleModel.builder()
          .setRuleId(0L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2020L)
          .setCasePoolId(2000L)
          .setPermissionSetIds(ImmutableSet.of(1122L))
          .setFilters(FILTERS_0)
          .build();

  private static final RuleModel RULE_1 =
      RuleModel.builder()
          .setRuleId(1L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2020L)
          .setCasePoolId(2000L)
          .setPermissionSetIds(ImmutableSet.of(2233L))
          .setFilters(FILTERS_0)
          .build();

  private static final RuleModel RULE_2 =
      RuleModel.builder()
          .setRuleId(2L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2020L)
          .setCasePoolId(2001L)
          .setPermissionSetIds(ImmutableSet.of(1122L, 2233L))
          .setFilters(FILTERS_1)
          .build();

  private static final RuleModel RULE_3 =
      RuleModel.builder()
          .setRuleId(3L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2020L)
          .setCasePoolId(2001L)
          .setPermissionSetIds(ImmutableSet.of(2233L, 2244L))
          .setFilters(FILTERS_1)
          .build();

  private static final RuleModel RULE_4 =
      RuleModel.builder()
          .setRuleId(4L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2020L)
          .setCasePoolId(2000L)
          .setPermissionSetIds(ImmutableSet.of(9999L))
          .setFilters(ImmutableList.of())
          .build();

  private static final RuleModel RULE_5 =
      RuleModel.builder()
          .setRuleId(5L)
          .setWorkforceId(1035L)
          .setWorkgroupId(2020L)
          .setCasePoolId(2010L)
          .setPermissionSetIds(ImmutableSet.of(9999L))
          .setFilters(ImmutableList.of())
          .build();

  private static final ImmutableSet<RuleModel> RULES =
      ImmutableSet.of(RULE_0, RULE_1, RULE_2, RULE_3, RULE_4, RULE_5);

  private static final RuleModel CONCENTRATED_RULE_0 =
      RuleModel.builder()
          .setRuleId(0L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2020L)
          .setCasePoolId(2000L)
          .setPermissionSetIds(ImmutableSet.of(1122L, 2233L))
          .setFilters(FILTERS_0)
          .build();

  private static final RuleModel CONCENTRATED_RULE_1 =
      RuleModel.builder()
          .setRuleId(1L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2020L)
          .setCasePoolId(2000L)
          .setPermissionSetIds(ImmutableSet.of(9999L))
          .setFilters(ImmutableList.of())
          .build();

  private static final RuleModel CONCENTRATED_RULE_2 =
      RuleModel.builder()
          .setRuleId(2L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2020L)
          .setCasePoolId(2001L)
          .setPermissionSetIds(ImmutableSet.of(1122L, 2233L, 2244L))
          .setFilters(FILTERS_1)
          .build();

  private static final RuleModel CONCENTRATED_RULE_3 =
      RuleModel.builder()
          .setRuleId(3L)
          .setWorkforceId(1035L)
          .setWorkgroupId(2020L)
          .setCasePoolId(2010L)
          .setPermissionSetIds(ImmutableSet.of(9999L))
          .setFilters(ImmutableList.of())
          .build();

  private static final int EXPECTED_NUMBER_OF_CONCENTRATED_RULES = 4;

  private static final ImmutableSet<RuleModel> EXPECTED_CONCENTRATED_RULES =
      ImmutableSet.of(
          CONCENTRATED_RULE_0, CONCENTRATED_RULE_1, CONCENTRATED_RULE_2, CONCENTRATED_RULE_3);

  private static final int EXPECTED_NUMBER_OF_CONCENTRATED_RULES_WITH_EMPTY_INPUT = 0;

  @Test
  public void ruleConcentrationTest() {
    ImmutableSet<RuleModel> concentratedRules =
        RuleConcentration.concentrate(RULES);
    Assert.assertEquals(EXPECTED_NUMBER_OF_CONCENTRATED_RULES, concentratedRules.size());
    Assert.assertTrue(concentratedRules.equals(EXPECTED_CONCENTRATED_RULES));
  }

  @Test
  public void ruleConcentrationTest_WithEmptyRules() {
    ImmutableSet<RuleModel> concentratedRules =
        RuleConcentration.concentrate(ImmutableSet.of());
    Assert.assertEquals(
        EXPECTED_NUMBER_OF_CONCENTRATED_RULES_WITH_EMPTY_INPUT, concentratedRules.size());
    Assert.assertTrue(concentratedRules.isEmpty());
  }
}
