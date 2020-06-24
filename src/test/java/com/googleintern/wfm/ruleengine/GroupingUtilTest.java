package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.opencsv.exceptions.CsvException;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.CasePoolIdAndPermissionIdGroupingUtil;
import src.main.java.com.googleintern.wfm.ruleengine.action.CsvParser;
import src.main.java.com.googleintern.wfm.ruleengine.action.WorkgroupIdGroupingUtil;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;

import java.io.IOException;

/**
 * GroupingUtilTest class is used to test the functionality of both WorkgroupIdGroupingUtil and
 * CasePoolIdAndPermissionIdGroupingUtil classes.
 */
public class GroupingUtilTest {
  private static final String TEST_CSV_FILE_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/src/"
          + "test/resources/com/googleintern/wfm/ruleengine/csv_grouping_test_data.csv";
  private static final int EXPECTED_WORKGROUP_ID_NUMBER = 2;
  private static final Long EXPECTED_FIRST_WORKGROUP_ID = 1122L;
  private static final Long EXPECTED_SECOND_WORKGROUP_ID = 2233L;

  /** Possible Rule Filters for User 0 - 4. */
  private static final ImmutableList<FilterModel> FILTERS_FOR_USER_0 =
      ImmutableList.of(
          FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(2020L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(2000L).build());

  private static final ImmutableList<FilterModel> FILTERS_FOR_USER_1 =
      ImmutableList.of(
          FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(2020L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(2019L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(54321L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(2000L).build());

  private static final ImmutableList<FilterModel> FILTERS_FOR_USER_2 =
      ImmutableList.of(
          FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(2018L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(1990L).build());

  private static final ImmutableList<FilterModel> FILTERS_FOR_USER_3 =
      ImmutableList.of(
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(2010L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(1990L).build());

  private static final ImmutableList<FilterModel> FILTERS_FOR_USER_4 =
      ImmutableList.of(
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(2017L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(1990L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(1989L).build());

  /** All permissions appeared in 'csv_grouping_test_data.csv' file. */
  private static final PoolAssignmentModel PERMISSION_0 =
      PoolAssignmentModel.builder().setCasePoolId(2000543L).setPermissionSetId(2048L).build();

  private static final PoolAssignmentModel PERMISSION_1 =
      PoolAssignmentModel.builder().setCasePoolId(2000555L).setPermissionSetId(2048L).build();

  private static final PoolAssignmentModel PERMISSION_2 =
      PoolAssignmentModel.builder().setCasePoolId(2000543L).setPermissionSetId(2028L).build();

  private static final PoolAssignmentModel PERMISSION_3 =
      PoolAssignmentModel.builder().setCasePoolId(2000544L).setPermissionSetId(2009L).build();

  private static final PoolAssignmentModel PERMISSION_4 =
      PoolAssignmentModel.builder().setCasePoolId(2000543L).setPermissionSetId(2048L).build();

  private static final PoolAssignmentModel PERMISSION_5 =
      PoolAssignmentModel.builder().setCasePoolId(2000543L).setPermissionSetId(2051L).build();

  /** Expected results after grouping by (Case Pool ID, Permission Set ID). */
  private static final ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
      EXPECTED_FIRST_PERMISSION_GROUP =
          ImmutableSetMultimap.<PoolAssignmentModel, ImmutableList<FilterModel>>builder()
              .put(PERMISSION_0, FILTERS_FOR_USER_0)
              .put(PERMISSION_1, FILTERS_FOR_USER_0)
              .put(PERMISSION_0, FILTERS_FOR_USER_2)
              .put(PERMISSION_1, FILTERS_FOR_USER_2)
              .put(PERMISSION_0, FILTERS_FOR_USER_4)
              .put(PERMISSION_1, FILTERS_FOR_USER_4)
              .build();

  private static final ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>>
      EXPECTED_SECOND_PERMISSION_GROUP =
          ImmutableSetMultimap.<PoolAssignmentModel, ImmutableList<FilterModel>>builder()
              .put(PERMISSION_2, FILTERS_FOR_USER_1)
              .put(PERMISSION_3, FILTERS_FOR_USER_1)
              .put(PERMISSION_4, FILTERS_FOR_USER_1)
              .put(PERMISSION_5, FILTERS_FOR_USER_1)
              .put(PERMISSION_4, FILTERS_FOR_USER_3)
              .put(PERMISSION_5, FILTERS_FOR_USER_3)
              .build();

  @Test
  public void groupByWorkGroupIdTest() throws IOException, CsvException {
    ImmutableList<UserPoolAssignmentModel> userPoolAssignments =
        CsvParser.readFromCSVFile(TEST_CSV_FILE_PATH);

    ImmutableListMultimap<Long, UserPoolAssignmentModel> mapByWorkGroupId =
        WorkgroupIdGroupingUtil.groupByWorkGroupId(userPoolAssignments);
    Assert.assertEquals(EXPECTED_WORKGROUP_ID_NUMBER, mapByWorkGroupId.keySet().size());

    mapByWorkGroupId.forEach(
        (workgroupId, user) -> {
          Assert.assertEquals(workgroupId, (Long) user.workgroupId());
        });
  }

  @Test
  public void groupByCasePoolIdAndPermissionSetIdTest() throws IOException, CsvException {
    ImmutableList<UserPoolAssignmentModel> userPoolAssignments =
        CsvParser.readFromCSVFile(TEST_CSV_FILE_PATH);
    ImmutableListMultimap<Long, UserPoolAssignmentModel> mapByWorkGroupId =
        WorkgroupIdGroupingUtil.groupByWorkGroupId(userPoolAssignments);

    ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>> firstPermissionGroup =
        CasePoolIdAndPermissionIdGroupingUtil.groupByCasePoolIdAndPermissionSetId(
            mapByWorkGroupId.get(EXPECTED_FIRST_WORKGROUP_ID));
    Assert.assertEquals(EXPECTED_FIRST_PERMISSION_GROUP, firstPermissionGroup);

    ImmutableSetMultimap<PoolAssignmentModel, ImmutableList<FilterModel>> secondPermissionGroup =
        CasePoolIdAndPermissionIdGroupingUtil.groupByCasePoolIdAndPermissionSetId(
            mapByWorkGroupId.get(EXPECTED_SECOND_WORKGROUP_ID));
    Assert.assertEquals(EXPECTED_SECOND_PERMISSION_GROUP, secondPermissionGroup);
  }
}
