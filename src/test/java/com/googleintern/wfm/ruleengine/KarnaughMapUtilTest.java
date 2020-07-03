package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.KarnaughMapReduction;
import src.main.java.com.googleintern.wfm.ruleengine.action.KarnaughMapRuleGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.action.KarnaughMapTermGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

public class KarnaughMapUtilTest {
  private static final FilterModel FILTER_0 =
      FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(1111L).build();
  private static final FilterModel FILTER_1 =
      FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(2222L).build();
  private static final FilterModel FILTER_2 =
      FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(3333L).build();
  private static final FilterModel FILTER_3 =
      FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(4444L).build();

  private static final ImmutableBiMap<FilterModel, Integer> EXPECTED_FILTER_BY_INDEX =
      ImmutableBiMap.<FilterModel, Integer>builder()
          .put(FILTER_0, 0)
          .put(FILTER_1, 1)
          .put(FILTER_2, 2)
          .put(FILTER_3, 3)
          .build();

  private static final ImmutableSet<ImmutableList<FilterModel>> FILTERS =
      ImmutableSet.<ImmutableList<FilterModel>>builder()
          .add(ImmutableList.of(FILTER_0, FILTER_1))
          .add(ImmutableList.of(FILTER_1, FILTER_3))
          .add(ImmutableList.of(FILTER_0, FILTER_2))
          .build();

  private static final ImmutableSet<ImmutableList<Integer>> EXPECTED_ALL_ZEROS_CASES =
      ImmutableSet.<ImmutableList<Integer>>builder()
          .add(ImmutableList.of(0, 0, 0, 0))
          .add(ImmutableList.of(0, 0, 0, 1))
          .add(ImmutableList.of(0, 0, 1, 0))
          .add(ImmutableList.of(0, 0, 1, 1))
          .add(ImmutableList.of(0, 1, 0, 0))
          .add(ImmutableList.of(0, 1, 1, 0))
          .add(ImmutableList.of(1, 0, 0, 0))
          .add(ImmutableList.of(1, 0, 0, 1))
          .build();

  private static final ImmutableSet<ImmutableList<Integer>> EXPECTED_MINIMIZED_TERMS =
      ImmutableSet.of(
          ImmutableList.of(0, 0, -1, -1),
          ImmutableList.of(-1, 0, 0, -1),
          ImmutableList.of(0, -1, -1, 0));

  private static final PoolAssignmentModel POOL_ASSIGNMENT =
      PoolAssignmentModel.builder().setCasePoolId(2020L).setPermissionSetId(4455L).build();

  private static final ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
      FILTER_BY_CASE_POOL_ID_AND_PERMISSION_SET_ID =
          ImmutableSetMultimap.<PoolAssignmentModel, ImmutableList<FilterModel>>builder()
              .put(POOL_ASSIGNMENT, ImmutableList.of(FILTER_0, FILTER_1))
              .put(POOL_ASSIGNMENT, ImmutableList.of(FILTER_0, FILTER_2))
              .put(POOL_ASSIGNMENT, ImmutableList.of(FILTER_1, FILTER_3))
              .build();

  private static final int EXPECTED_SIZE_OF_KEY_SET = 4;

  private static final ImmutableSet<FilterModel> EXPECTED_KEY_SET =
      ImmutableSet.of(FILTER_0, FILTER_1, FILTER_2, FILTER_3);

  private static final Long WORK_FORCE_ID = 10334L;

  private static final Long WORK_GROUP_ID = 105607L;

  private static final Long CASE_POOL_ID = 44667L;

  private static final ImmutableSet<Long> PERMISSION_SET_IDS = ImmutableSet.of(4477L);

  private static final RuleModel EXPECTED_RULE =
      RuleModel.builder()
          .setWorkforceId(WORK_FORCE_ID)
          .setWorkgroupId(WORK_GROUP_ID)
          .setCasePoolId(CASE_POOL_ID)
          .setPermissionSetIds(PERMISSION_SET_IDS)
          .setFilters(
              ImmutableList.of(
                  ImmutableSet.of(FILTER_1, FILTER_2),
                  ImmutableSet.of(FILTER_0, FILTER_3),
                  ImmutableSet.of(FILTER_0, FILTER_1)))
          .build();

  @Test
  public void karnaughMapAlgorithmTest() {
    ImmutableBiMap<FilterModel, Integer> filterByIndex =
        KarnaughMapTermGenerator.mapFiltersByIndex(
            FILTER_BY_CASE_POOL_ID_AND_PERMISSION_SET_ID, POOL_ASSIGNMENT);
    Assert.assertEquals(EXPECTED_SIZE_OF_KEY_SET, filterByIndex.keySet().size());
    Assert.assertEquals(EXPECTED_KEY_SET, filterByIndex.keySet());

    for (FilterModel filter : filterByIndex.keySet()) {
      System.out.println(filter.value() + "   " + filterByIndex.get(filter));
    }

    ImmutableSet<ImmutableList<Integer>> allZerosCases =
        KarnaughMapTermGenerator.findAllZeroCases(filterByIndex, FILTERS);
    Assert.assertTrue(allZerosCases.equals(EXPECTED_ALL_ZEROS_CASES));

    ImmutableSet<ImmutableList<Integer>> minimizedTerms =
        KarnaughMapReduction.minimizeKMapTerms(allZerosCases);
    Assert.assertTrue(minimizedTerms.equals(EXPECTED_MINIMIZED_TERMS));

    RuleModel rule =
        KarnaughMapRuleGenerator.singleRuleGenerator(
            minimizedTerms,
            filterByIndex,
            WORK_FORCE_ID,
            WORK_GROUP_ID,
            CASE_POOL_ID,
            PERMISSION_SET_IDS);
    Assert.assertEquals(EXPECTED_RULE.filters(), rule.filters());
  }
}
