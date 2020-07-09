package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.KarnaughMapRuleGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

/**
 * KarnaughMapRuleGeneratorTest class is used to test the functionality of KarnaughMapRuleGenerator
 * class.
 */
public class KarnaughMapRuleGeneratorTest {
  private static final Long WORK_FORCE_ID = 10334L;

  private static final Long WORK_GROUP_ID = 105607L;

  private static final Long CASE_POOL_ID = 44667L;

  private static final ImmutableSet<Long> PERMISSION_SET_IDS = ImmutableSet.of(4477L);

  private static final FilterModel FILTER_0 =
      FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(1111L).build();
  private static final FilterModel FILTER_1 =
      FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(2222L).build();
  private static final FilterModel FILTER_2 =
      FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(3333L).build();

  private static final ImmutableBiMap<FilterModel, Integer> EXPECTED_FILTER_BY_INDEX =
      ImmutableBiMap.<FilterModel, Integer>builder()
          .put(FILTER_0, 0)
          .put(FILTER_1, 1)
          .put(FILTER_2, 2)
          .build();

  private static final RuleModel EXPECTED_GENERATED_RULE =
      RuleModel.builder()
          .setWorkforceId(WORK_FORCE_ID)
          .setWorkgroupId(WORK_GROUP_ID)
          .setCasePoolId(CASE_POOL_ID)
          .setPermissionSetIds(PERMISSION_SET_IDS)
          .setFilters(
              ImmutableList.of(ImmutableSet.of(FILTER_0, FILTER_1), ImmutableSet.of(FILTER_2)))
          .build();

  private static final ImmutableSet<ImmutableList<Integer>> MINIMIZED_TERMS =
      ImmutableSet.of(
          ImmutableList.of(0, 0, -1), ImmutableList.of(-1, -1, 0), ImmutableList.of(1, 1, 1));

  @Test
  public void singleRuleGenerator() {
    RuleModel rule =
        KarnaughMapRuleGenerator.generateSingleRule(
            MINIMIZED_TERMS,
            EXPECTED_FILTER_BY_INDEX,
            WORK_FORCE_ID,
            WORK_GROUP_ID,
            CASE_POOL_ID,
            PERMISSION_SET_IDS);
    Assert.assertEquals(EXPECTED_GENERATED_RULE, rule);
  }
}
