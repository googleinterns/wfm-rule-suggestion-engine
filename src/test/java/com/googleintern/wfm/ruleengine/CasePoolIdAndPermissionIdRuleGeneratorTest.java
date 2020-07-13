package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.generator.CasePoolIdAndPermissionIdRuleGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.action.generator.RuleIdGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

/**
 * CasePoolIdAndPermissionIdRuleGeneratorTest class is used to test the functionality of the
 * CasePoolIdAndPermissionIdRuleGenerator class.
 */
public class CasePoolIdAndPermissionIdRuleGeneratorTest {

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
  private static final FilterModel FILTER_5 =
      FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(6666L).build();
  private static final Long WORK_FORCE_ID = 10334L;

  private static final Long WORK_GROUP_ID = 105607L;

  private static final Long CASE_POOL_ID = 44667L;

  private static final Long PERMISSION_SET_ID = 11067L;

  private static final PoolAssignmentModel POOL_ASSIGNMENT =
      PoolAssignmentModel.builder()
          .setCasePoolId(CASE_POOL_ID)
          .setPermissionSetId(PERMISSION_SET_ID)
          .build();

  ImmutableList<ImmutableSet<FilterModel>> EXPECTED_REDUCED_FILTERS =
      ImmutableList.of(
          ImmutableSet.of(FILTER_0, FILTER_4, FILTER_5),
          ImmutableSet.of(FILTER_2, FILTER_3, FILTER_0),
          ImmutableSet.of(FILTER_0, FILTER_1, FILTER_3));

  private static final int EXPECTED_NUMBER_OF_RULES = 3;

  private static final ImmutableSet<RuleModel> EXPECTED_RULES =
      ImmutableSet.of(
          RuleModel.builder()
              .setRuleId(0L)
              .setWorkforceId(WORK_FORCE_ID)
              .setWorkgroupId(WORK_GROUP_ID)
              .setCasePoolId(CASE_POOL_ID)
              .setPermissionSetIds(ImmutableSet.of(PERMISSION_SET_ID))
              .setFilters(
                  ImmutableList.of(
                      ImmutableSet.of(FILTER_0),
                      ImmutableSet.of(FILTER_4),
                      ImmutableSet.of(FILTER_5)))
              .build(),
          RuleModel.builder()
              .setRuleId(1L)
              .setWorkforceId(WORK_FORCE_ID)
              .setWorkgroupId(WORK_GROUP_ID)
              .setCasePoolId(CASE_POOL_ID)
              .setPermissionSetIds(ImmutableSet.of(PERMISSION_SET_ID))
              .setFilters(
                  ImmutableList.of(
                      ImmutableSet.of(FILTER_2),
                      ImmutableSet.of(FILTER_3),
                      ImmutableSet.of(FILTER_0)))
              .build(),
          RuleModel.builder()
              .setRuleId(2L)
              .setWorkforceId(WORK_FORCE_ID)
              .setWorkgroupId(WORK_GROUP_ID)
              .setCasePoolId(CASE_POOL_ID)
              .setPermissionSetIds(ImmutableSet.of(PERMISSION_SET_ID))
              .setFilters(
                  ImmutableList.of(
                      ImmutableSet.of(FILTER_0),
                      ImmutableSet.of(FILTER_1),
                      ImmutableSet.of(FILTER_3)))
              .build());


  private static final int EXPECTED_NUMBER_OF_RULES_WITH_EMPTY_REDUCED_FILTERS = 0;

  @Test
  public void generateRulesTest() {
    RuleIdGenerator ruleIdGenerator = new RuleIdGenerator();
    ImmutableSet<RuleModel> generatedRules =
        CasePoolIdAndPermissionIdRuleGenerator.generateRules(
            WORK_FORCE_ID,
            WORK_GROUP_ID,
            POOL_ASSIGNMENT,
            EXPECTED_REDUCED_FILTERS,
            ruleIdGenerator);
    Assert.assertEquals(EXPECTED_NUMBER_OF_RULES, generatedRules.size());
    Assert.assertTrue(generatedRules.equals(EXPECTED_RULES));
  }

  @Test
  public void generateRulesTest_WithEmptyReducedFilters() {
    RuleIdGenerator ruleIdGenerator = new RuleIdGenerator();
    ImmutableSet<RuleModel> generatedRules =
        CasePoolIdAndPermissionIdRuleGenerator.generateRules(
            WORK_FORCE_ID, WORK_GROUP_ID, POOL_ASSIGNMENT, ImmutableList.of(), ruleIdGenerator);
    Assert.assertEquals(EXPECTED_NUMBER_OF_RULES_WITH_EMPTY_REDUCED_FILTERS, generatedRules.size());
    Assert.assertTrue(generatedRules.equals(ImmutableSet.of()));
  }
}
