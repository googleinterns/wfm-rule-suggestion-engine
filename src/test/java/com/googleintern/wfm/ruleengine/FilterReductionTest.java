package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.FiltersReduction;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;

/** FilterReductionTest class is used to test the functionality of FilterReduction class. */
public class FilterReductionTest {

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

  private static final Long CASE_POOL_ID = 44667L;

  private static final Long PERMISSION_SET_ID = 11067L;

  private static final PoolAssignmentModel POOL_ASSIGNMENT =
      PoolAssignmentModel.builder()
          .setCasePoolId(CASE_POOL_ID)
          .setPermissionSetId(PERMISSION_SET_ID)
          .build();

  private static final PoolAssignmentModel NONEXISTENT_POOL_ASSIGNMENT =
      PoolAssignmentModel.builder().setCasePoolId(CASE_POOL_ID).setPermissionSetId(1122L).build();

  private static final ImmutableSet<ImmutableList<FilterModel>> FILTERS =
      ImmutableSet.of(
          ImmutableList.of(FILTER_0, FILTER_4, FILTER_5),
          ImmutableList.of(FILTER_0, FILTER_5, FILTER_4),
          ImmutableList.of(FILTER_0, FILTER_5, FILTER_4),
          ImmutableList.of(FILTER_0, FILTER_1, FILTER_4, FILTER_5),
          ImmutableList.of(FILTER_2, FILTER_5, FILTER_4, FILTER_1, FILTER_3, FILTER_0),
          ImmutableList.of(FILTER_2, FILTER_3, FILTER_0),
          ImmutableList.of(FILTER_0, FILTER_1, FILTER_3));

  private static final ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
      FILTERS_BY_POOL_ASSIGNMENTS =
          ImmutableSetMultimap.<PoolAssignmentModel, ImmutableList<FilterModel>>builder()
              .putAll(POOL_ASSIGNMENT, FILTERS)
              .build();

  private static final int EXPECTED_NUMBER_OF_REDUCED_FILTERS = 3;

  ImmutableList<ImmutableSet<FilterModel>> EXPECTED_REDUCED_FILTERS =
      ImmutableList.of(
          ImmutableSet.of(FILTER_0, FILTER_4, FILTER_5),
          ImmutableSet.of(FILTER_2, FILTER_3, FILTER_0),
          ImmutableSet.of(FILTER_0, FILTER_1, FILTER_3));
  
  private static final int
      EXPECTED_NUMBER_OF_REDUCED_FILTER_WITH_EMPTY_FILTERS_BY_POOL_ASSIGNMENTS = 0;

  private static final int EXPECTED_NUMBER_OF_REDUCED_FILTER_WITH_NONEXISTENT_POOL_ASSIGNMENT = 0;

  @Test
  public void reduceTest() {
    ImmutableList<ImmutableSet<FilterModel>> reducedFilters =
        FiltersReduction.reduce(FILTERS_BY_POOL_ASSIGNMENTS, POOL_ASSIGNMENT);
    Assert.assertEquals(EXPECTED_NUMBER_OF_REDUCED_FILTERS, reducedFilters.size());
    Assert.assertTrue(reducedFilters.equals(EXPECTED_REDUCED_FILTERS));
  }

  @Test
  public void reduceTest_WithEmptyFiltersByPoolAssignments() {
    ImmutableList<ImmutableSet<FilterModel>> reducedFilters =
        FiltersReduction.reduce(
            ImmutableSetMultimap.<PoolAssignmentModel, ImmutableList<FilterModel>>builder().build(),
            POOL_ASSIGNMENT);
    Assert.assertEquals(
        EXPECTED_NUMBER_OF_REDUCED_FILTER_WITH_EMPTY_FILTERS_BY_POOL_ASSIGNMENTS,
        reducedFilters.size());
    Assert.assertTrue(reducedFilters.equals(ImmutableList.of()));
  }

  @Test
  public void reduceTest_WithNonexistentPoolAssignment() {
    ImmutableList<ImmutableSet<FilterModel>> reducedFilters =
        FiltersReduction.reduce(FILTERS_BY_POOL_ASSIGNMENTS, NONEXISTENT_POOL_ASSIGNMENT);
    Assert.assertEquals(
        EXPECTED_NUMBER_OF_REDUCED_FILTER_WITH_NONEXISTENT_POOL_ASSIGNMENT, reducedFilters.size());
    Assert.assertTrue(reducedFilters.equals(ImmutableList.of()));
  }
}
