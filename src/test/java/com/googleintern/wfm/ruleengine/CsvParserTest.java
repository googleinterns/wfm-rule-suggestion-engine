package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.opencsv.exceptions.CsvException;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.CsvParser;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;

import java.io.IOException;
import java.util.*;

/** * CsvParserTest class is used to testing the functionality of CsvParser class. */
public class CsvParserTest {
  private static final String TEST_CSV_FILE_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/src/"
          + "test/resources/com/googleintern/wfm/ruleengine/csv_parser_test_data.csv";

  // Expected values of fields for Case 0
  private static final int CASE_NUMBER_0 = 0;
  private static final long USER_ID_CASE_0 = 0;
  private static final long WORKFORCE_ID_CASE_0 = 1024;
  private static final long WORKGROUP_ID_CASE_0 = 0;
  private static final ImmutableList<Long> ROLE_IDS_CASE_0 = ImmutableList.of();
  private static final ImmutableList<Long> SKILL_IDS_CASE_0 = ImmutableList.of();
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_0 = ImmutableList.of();
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_0 =
      ImmutableSet.of();

  // Expected values of fields for Case 1
  private static final int CASE_NUMBER_1 = 1;
  private static final long USER_ID_CASE_1 = 1;
  private static final long WORKFORCE_ID_CASE_1 = 1024;
  private static final long WORKGROUP_ID_CASE_1 = 0;
  private static final ImmutableList<Long> ROLE_IDS_CASE_1 = ImmutableList.of(2020L);
  private static final ImmutableList<Long> SKILL_IDS_CASE_1 = ImmutableList.of();
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_1 = ImmutableList.of();
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_1 =
      ImmutableSet.of();

  // Expected values of fields for Case 2
  private static final int CASE_NUMBER_2 = 2;
  private static final long USER_ID_CASE_2 = 2;
  private static final long WORKFORCE_ID_CASE_2 = 1024;
  private static final long WORKGROUP_ID_CASE_2 = 0;
  private static final ImmutableList<Long> ROLE_IDS_CASE_2 = ImmutableList.of(2020L, 2019L, 2018L);
  private static final ImmutableList<Long> SKILL_IDS_CASE_2 = ImmutableList.of(2000L);
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_2 = ImmutableList.of(1990L);
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_2 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(2048).build());

  // Expected values of fields for Case 3
  private static final int CASE_NUMBER_3 = 3;
  private static final long USER_ID_CASE_3 = 3;
  private static final long WORKFORCE_ID_CASE_3 = 1024;
  private static final long WORKGROUP_ID_CASE_3 = 0;
  private static final ImmutableList<Long> ROLE_IDS_CASE_3 =
      ImmutableList.of(2020L, 2019L, 2018L, 2017L);
  private static final ImmutableList<Long> SKILL_IDS_CASE_3 = ImmutableList.of(2000L, 2001L);
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_3 =
      ImmutableList.of(1990L, 1989L, 1991L);
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_3 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1133).build());

  // Expected values of fields for Case 4
  private static final int CASE_NUMBER_4 = 4;
  private static final long USER_ID_CASE_4 = 4;
  private static final long WORKFORCE_ID_CASE_4 = 1024;
  private static final long WORKGROUP_ID_CASE_4 = 2048;
  private static final ImmutableList<Long> ROLE_IDS_CASE_4 = ImmutableList.of();
  private static final ImmutableList<Long> SKILL_IDS_CASE_4 = ImmutableList.of(1998L, 2038L, 2249L);
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_4 = ImmutableList.of(1990L, 1991L);
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_4 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000516).setPermissionSetId(1111).build());

  // Expected values of fields for Case 5
  private static final int CASE_NUMBER_5 = 5;
  private static final long USER_ID_CASE_5 = 5;
  private static final long WORKFORCE_ID_CASE_5 = 1024;
  private static final long WORKGROUP_ID_CASE_5 = 2048;
  private static final ImmutableList<Long> ROLE_IDS_CASE_5 = ImmutableList.of();
  private static final ImmutableList<Long> SKILL_IDS_CASE_5 = ImmutableList.of();
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_5 =
      ImmutableList.of(1990L, 1991L, 1992L, 1993L);
  private static final Set<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_5 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000516).setPermissionSetId(1111).build(),
          PoolAssignmentModel.builder().setCasePoolId(2001052).setPermissionSetId(1111).build());

  // Expected values of fields for Case 6
  private static final int CASE_NUMBER_6 = 6;
  private static final long USER_ID_CASE_6 = 6;
  private static final long WORKFORCE_ID_CASE_6 = 1024;
  private static final long WORKGROUP_ID_CASE_6 = 1024;
  private static final ImmutableList<Long> ROLE_IDS_CASE_6 = ImmutableList.of();
  private static final ImmutableList<Long> SKILL_IDS_CASE_6 = ImmutableList.of();
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_6 = ImmutableList.of();
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_6 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1112).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1113).build());

  // Expected values of fields for Case 7
  private static final int CASE_NUMBER_7 = 7;
  private static final long USER_ID_CASE_7 = 7;
  private static final long WORKFORCE_ID_CASE_7 = 1024;
  private static final long WORKGROUP_ID_CASE_7 = 2048;
  private static final ImmutableList<Long> ROLE_IDS_CASE_7 = ImmutableList.of(1998L, 2038L, 2249L);
  private static final ImmutableList<Long> SKILL_IDS_CASE_7 = ImmutableList.of(1998L, 2038L, 2249L);
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_7 =
      ImmutableList.of(1998L, 2038L, 2249L);
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_7 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1098).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1112).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(2249).build());

  // Expected values of fields for Case 8
  private static final int CASE_NUMBER_8 = 8;
  private static final long USER_ID_CASE_8 = 8;
  private static final long WORKFORCE_ID_CASE_8 = 1024;
  private static final long WORKGROUP_ID_CASE_8 = 2048;
  private static final ImmutableList<Long> ROLE_IDS_CASE_8 = ImmutableList.of(1998L, 2038L, 2249L);
  private static final ImmutableList<Long> SKILL_IDS_CASE_8 = ImmutableList.of(1998L, 2038L, 2249L);
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_8 =
      ImmutableList.of(1990L, 1999L, 1991L);
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_8 =
      ImmutableSet.of();

  @Test
  public void parserFully() throws IOException, CsvException {
    List<UserPoolAssignmentModel> userPoolAssignmentList =
        CsvParser.readFromCSVFile(TEST_CSV_FILE_PATH);
    final int expectedUserPoolAssignmentListSize = 9;
    Assert.assertEquals(expectedUserPoolAssignmentListSize, userPoolAssignmentList.size());
    for (final UserPoolAssignmentModel userPoolAssignment : userPoolAssignmentList) {
      Assert.assertNotNull(userPoolAssignment);
    }
  }

  @Test
  public void parserReadingTest() throws IOException, CsvException {
    List<UserPoolAssignmentModel> userPoolAssignmentList =
        CsvParser.readFromCSVFile(TEST_CSV_FILE_PATH);

    // Verify for Case 0 Readings
    UserPoolAssignmentModel expectedUserPoolAssignmentValue =
        UserPoolAssignmentModel.builder()
            .setUserId(USER_ID_CASE_0)
            .setWorkforceId(WORKFORCE_ID_CASE_0)
            .setWorkgroupId(WORKGROUP_ID_CASE_0)
            .setRoleIds(ROLE_IDS_CASE_0)
            .setSkillIds(SKILL_IDS_CASE_0)
            .setRoleSkillIds(ROLESKILL_IDS_CASE_0)
            .setPoolAssignments(POOL_ASSIGNMENTS_CASE_0)
            .build();
    Assert.assertEquals(userPoolAssignmentList.get(CASE_NUMBER_0), expectedUserPoolAssignmentValue);

    // Verify for Case 1 Readings
    expectedUserPoolAssignmentValue =
        UserPoolAssignmentModel.builder()
            .setUserId(USER_ID_CASE_1)
            .setWorkforceId(WORKFORCE_ID_CASE_1)
            .setWorkgroupId(WORKGROUP_ID_CASE_1)
            .setRoleIds(ROLE_IDS_CASE_1)
            .setSkillIds(SKILL_IDS_CASE_1)
            .setRoleSkillIds(ROLESKILL_IDS_CASE_1)
            .setPoolAssignments(POOL_ASSIGNMENTS_CASE_1)
            .build();
    Assert.assertEquals(userPoolAssignmentList.get(CASE_NUMBER_1), expectedUserPoolAssignmentValue);

    // Verify for Case 2 Readings
    expectedUserPoolAssignmentValue =
        UserPoolAssignmentModel.builder()
            .setUserId(USER_ID_CASE_2)
            .setWorkforceId(WORKFORCE_ID_CASE_2)
            .setWorkgroupId(WORKGROUP_ID_CASE_2)
            .setRoleIds(ROLE_IDS_CASE_2)
            .setSkillIds(SKILL_IDS_CASE_2)
            .setRoleSkillIds(ROLESKILL_IDS_CASE_2)
            .setPoolAssignments(POOL_ASSIGNMENTS_CASE_2)
            .build();
    Assert.assertEquals(userPoolAssignmentList.get(CASE_NUMBER_2), expectedUserPoolAssignmentValue);

    // Verify for Case 3 Readings
    expectedUserPoolAssignmentValue =
        UserPoolAssignmentModel.builder()
            .setUserId(USER_ID_CASE_3)
            .setWorkforceId(WORKFORCE_ID_CASE_3)
            .setWorkgroupId(WORKGROUP_ID_CASE_3)
            .setRoleIds(ROLE_IDS_CASE_3)
            .setSkillIds(SKILL_IDS_CASE_3)
            .setRoleSkillIds(ROLESKILL_IDS_CASE_3)
            .setPoolAssignments(POOL_ASSIGNMENTS_CASE_3)
            .build();
    Assert.assertEquals(userPoolAssignmentList.get(CASE_NUMBER_3), expectedUserPoolAssignmentValue);

    // Verify for Case 4 Readings
    expectedUserPoolAssignmentValue =
        UserPoolAssignmentModel.builder()
            .setUserId(USER_ID_CASE_4)
            .setWorkforceId(WORKFORCE_ID_CASE_4)
            .setWorkgroupId(WORKGROUP_ID_CASE_4)
            .setRoleIds(ROLE_IDS_CASE_4)
            .setSkillIds(SKILL_IDS_CASE_4)
            .setRoleSkillIds(ROLESKILL_IDS_CASE_4)
            .setPoolAssignments(POOL_ASSIGNMENTS_CASE_4)
            .build();
    Assert.assertEquals(userPoolAssignmentList.get(CASE_NUMBER_4), expectedUserPoolAssignmentValue);

    // Verify for Case 5 Readings
    expectedUserPoolAssignmentValue =
        UserPoolAssignmentModel.builder()
            .setUserId(USER_ID_CASE_5)
            .setWorkforceId(WORKFORCE_ID_CASE_5)
            .setWorkgroupId(WORKGROUP_ID_CASE_5)
            .setRoleIds(ROLE_IDS_CASE_5)
            .setSkillIds(SKILL_IDS_CASE_5)
            .setRoleSkillIds(ROLESKILL_IDS_CASE_5)
            .setPoolAssignments(POOL_ASSIGNMENTS_CASE_5)
            .build();
    Assert.assertEquals(userPoolAssignmentList.get(CASE_NUMBER_5), expectedUserPoolAssignmentValue);

    // Verify for Case 6 Readings
    expectedUserPoolAssignmentValue =
        UserPoolAssignmentModel.builder()
            .setUserId(USER_ID_CASE_6)
            .setWorkforceId(WORKFORCE_ID_CASE_6)
            .setWorkgroupId(WORKGROUP_ID_CASE_6)
            .setRoleIds(ROLE_IDS_CASE_6)
            .setSkillIds(SKILL_IDS_CASE_6)
            .setRoleSkillIds(ROLESKILL_IDS_CASE_6)
            .setPoolAssignments(POOL_ASSIGNMENTS_CASE_6)
            .build();
    Assert.assertEquals(userPoolAssignmentList.get(CASE_NUMBER_6), expectedUserPoolAssignmentValue);

    // Verify for Case 7 Readings
    expectedUserPoolAssignmentValue =
        UserPoolAssignmentModel.builder()
            .setUserId(USER_ID_CASE_7)
            .setWorkforceId(WORKFORCE_ID_CASE_7)
            .setWorkgroupId(WORKGROUP_ID_CASE_7)
            .setRoleIds(ROLE_IDS_CASE_7)
            .setSkillIds(SKILL_IDS_CASE_7)
            .setRoleSkillIds(ROLESKILL_IDS_CASE_7)
            .setPoolAssignments(POOL_ASSIGNMENTS_CASE_7)
            .build();
    Assert.assertEquals(userPoolAssignmentList.get(CASE_NUMBER_7), expectedUserPoolAssignmentValue);

    // Verify for Case 8 Readings
    expectedUserPoolAssignmentValue =
        UserPoolAssignmentModel.builder()
            .setUserId(USER_ID_CASE_8)
            .setWorkforceId(WORKFORCE_ID_CASE_8)
            .setWorkgroupId(WORKGROUP_ID_CASE_8)
            .setRoleIds(ROLE_IDS_CASE_8)
            .setSkillIds(SKILL_IDS_CASE_8)
            .setRoleSkillIds(ROLESKILL_IDS_CASE_8)
            .setPoolAssignments(POOL_ASSIGNMENTS_CASE_8)
            .build();
    Assert.assertEquals(userPoolAssignmentList.get(CASE_NUMBER_8), expectedUserPoolAssignmentValue);
  }
}
