package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.KarnaughMapTableGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;

/**
 * KarnaughMapTableGeneratorTest is used to test the functionality of KarnaughMapTableGenerator
 * class.
 */
public class KarnaughMapTableGeneratorTest {
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

  private static final ImmutableSet<ImmutableList<FilterModel>> FILTERS =
      ImmutableSet.<ImmutableList<FilterModel>>builder()
          .add(ImmutableList.of(FILTER_0, FILTER_1))
          .add(ImmutableList.of(FILTER_2))
          .build();

  private static final ImmutableSet<ImmutableList<Integer>> EXPECTED_ALL_ZEROS_CASES =
      ImmutableSet.<ImmutableList<Integer>>builder()
          .add(ImmutableList.of(0, 0, 0))
          .add(ImmutableList.of(0, 1, 0))
          .add(ImmutableList.of(1, 0, 0))
          .build();

  private static final PoolAssignmentModel POOL_ASSIGNMENT =
      PoolAssignmentModel.builder().setCasePoolId(2020L).setPermissionSetId(4455L).build();

  private static final ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
      FILTER_BY_CASE_POOL_ID_AND_PERMISSION_SET_ID =
          ImmutableSetMultimap.<PoolAssignmentModel, ImmutableList<FilterModel>>builder()
              .put(POOL_ASSIGNMENT, ImmutableList.of(FILTER_0, FILTER_1))
              .put(POOL_ASSIGNMENT, ImmutableList.of(FILTER_2))
              .build();

  private static final int EXPECTED_SIZE_OF_KEY_SET = 3;

  private static final ImmutableSet<FilterModel> EXPECTED_KEY_SET =
      ImmutableSet.of(FILTER_0, FILTER_1, FILTER_2);

  @Test
  public void mapFiltersByIndexTest() {
    ImmutableBiMap<FilterModel, Integer> filterByIndex =
        KarnaughMapTableGenerator.mapFiltersByIndex(
            FILTER_BY_CASE_POOL_ID_AND_PERMISSION_SET_ID, POOL_ASSIGNMENT);
    Assert.assertEquals(EXPECTED_SIZE_OF_KEY_SET, filterByIndex.keySet().size());
    Assert.assertEquals(EXPECTED_KEY_SET, filterByIndex.keySet());
  }

  @Test
  public void findAllZeroCasesTest() {
    ImmutableSet<ImmutableList<Integer>> allZerosCases =
        KarnaughMapTableGenerator.findAllZeroCases(EXPECTED_FILTER_BY_INDEX, FILTERS);
    Assert.assertEquals(allZerosCases, EXPECTED_ALL_ZEROS_CASES);
  }
}
