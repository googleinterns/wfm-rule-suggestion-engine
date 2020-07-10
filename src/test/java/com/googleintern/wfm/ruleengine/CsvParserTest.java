package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.opencsv.exceptions.CsvException;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.CsvParser;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

import java.io.IOException;
import java.util.*;

/** CsvParserTest class is used to testing the functionality of CsvParser class. */
public class CsvParserTest {
  private static final String TEST_CSV_FILE_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/src/"
          + "test/resources/com/googleintern/wfm/ruleengine/csv_parser_test_data.csv";

  /**
   * Expected values of fields for Case 0
   *
   * <p>Case 0 : Empty for role_ids, skills, role_skills and pool_assignments columns. Invalid value
   * for workgroup_id column.
   */
  private static final long USER_ID_CASE_0 = 0;

  private static final long WORKFORCE_ID_CASE_0 = 1024;
  private static final long WORKGROUP_ID_CASE_0 = 0;
  private static final ImmutableList<Long> ROLE_IDS_CASE_0 = ImmutableList.of();
  private static final ImmutableList<Long> SKILL_IDS_CASE_0 = ImmutableList.of();
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_0 = ImmutableList.of();
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_0 =
      ImmutableSet.of();
  private static final UserModel EXPECTED_USERPOOLASSIGNMENT_CASE_0 =
      UserModel.builder()
          .setUserId(USER_ID_CASE_0)
          .setWorkforceId(WORKFORCE_ID_CASE_0)
          .setWorkgroupId(WORKGROUP_ID_CASE_0)
          .setRoleIds(ROLE_IDS_CASE_0)
          .setSkillIds(SKILL_IDS_CASE_0)
          .setRoleSkillIds(ROLESKILL_IDS_CASE_0)
          .setPoolAssignments(POOL_ASSIGNMENTS_CASE_0)
          .build();

  /**
   * Expected values of fields for Case 1
   *
   * <p>Case 1: Single value for role_ids, skills, role_skills and pool_assignments columns. Valid
   * value for workgroup_id column.
   */
  private static final long USER_ID_CASE_1 = 1;

  private static final long WORKFORCE_ID_CASE_1 = 1024;
  private static final long WORKGROUP_ID_CASE_1 = 2048;
  private static final ImmutableList<Long> ROLE_IDS_CASE_1 = ImmutableList.of(2020L);
  private static final ImmutableList<Long> SKILL_IDS_CASE_1 = ImmutableList.of(2000L);
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_1 = ImmutableList.of(1990L);
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_1 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(2048).build());
  private static final UserModel EXPECTED_USERPOOLASSIGNMENT_CASE_1 =
      UserModel.builder()
          .setUserId(USER_ID_CASE_1)
          .setWorkforceId(WORKFORCE_ID_CASE_1)
          .setWorkgroupId(WORKGROUP_ID_CASE_1)
          .setRoleIds(ROLE_IDS_CASE_1)
          .setSkillIds(SKILL_IDS_CASE_1)
          .setRoleSkillIds(ROLESKILL_IDS_CASE_1)
          .setPoolAssignments(POOL_ASSIGNMENTS_CASE_1)
          .build();

  /**
   * Expected values of fields for Case 2
   *
   * <p>Case 2: Multiple distinctive values for role_ids, skills, and role_skills columns. Valid
   * value for workgroup_id column. Same value of pool id and different values of permission set id
   * for workgroup_id column. Same value of pool id and different values of permission set id for
   * elements in the pool_assignments column.
   */
  private static final long USER_ID_CASE_2 = 2;

  private static final long WORKFORCE_ID_CASE_2 = 1024;
  private static final long WORKGROUP_ID_CASE_2 = 2048;
  private static final ImmutableList<Long> ROLE_IDS_CASE_2 =
      ImmutableList.of(2020L, 2019L, 2018L, 2017L);
  private static final ImmutableList<Long> SKILL_IDS_CASE_2 = ImmutableList.of(2000L, 2001L);
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_2 =
      ImmutableList.of(1990L, 1989L, 1991L);
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_2 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1133).build());
  private static final UserModel EXPECTED_USERPOOLASSIGNMENT_CASE_2 =
      UserModel.builder()
          .setUserId(USER_ID_CASE_2)
          .setWorkforceId(WORKFORCE_ID_CASE_2)
          .setWorkgroupId(WORKGROUP_ID_CASE_2)
          .setRoleIds(ROLE_IDS_CASE_2)
          .setSkillIds(SKILL_IDS_CASE_2)
          .setRoleSkillIds(ROLESKILL_IDS_CASE_2)
          .setPoolAssignments(POOL_ASSIGNMENTS_CASE_2)
          .build();

  /**
   * Expected values of fields for Case 3
   *
   * <p>Case 3: Multiple distinctive values for skills and pool_assignments columns. Valid value for
   * workgroup_id column. The role_skills column has no proficiency element and is in form of
   * {"skill_id": number}. Same value of permission set id and different values of pool ids for
   * elements in the pool_assignments column.
   */
  private static final long USER_ID_CASE_3 = 3;

  private static final long WORKFORCE_ID_CASE_3 = 1024;
  private static final long WORKGROUP_ID_CASE_3 = 2048;
  private static final ImmutableList<Long> ROLE_IDS_CASE_3 =
      ImmutableList.of(2020L, 2019L, 2018L, 2017L);
  private static final ImmutableList<Long> SKILL_IDS_CASE_3 = ImmutableList.of(1998L, 2038L, 2249L);
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_3 = ImmutableList.of(1990L);
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_3 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000516).setPermissionSetId(1111).build());
  private static final UserModel EXPECTED_USERPOOLASSIGNMENT_CASE_3 =
      UserModel.builder()
          .setUserId(USER_ID_CASE_3)
          .setWorkforceId(WORKFORCE_ID_CASE_3)
          .setWorkgroupId(WORKGROUP_ID_CASE_3)
          .setRoleIds(ROLE_IDS_CASE_3)
          .setSkillIds(SKILL_IDS_CASE_3)
          .setRoleSkillIds(ROLESKILL_IDS_CASE_3)
          .setPoolAssignments(POOL_ASSIGNMENTS_CASE_3)
          .build();

  /**
   * Expected values of fields for Case 4
   *
   * <p>Case 4: Multiple distinctive values for role_skills, skills and pool_assignments columns.
   * Valid value for workgroup_id column. The role_skills column has no proficiency element and is
   * in form of {"skill_id": number}. Same value of pool id and different values of permission set
   * id for elements in the pool_assignments column.
   */
  private static final long USER_ID_CASE_4 = 4;

  private static final long WORKFORCE_ID_CASE_4 = 1024;
  private static final long WORKGROUP_ID_CASE_4 = 2048;
  private static final ImmutableList<Long> ROLE_IDS_CASE_4 =
      ImmutableList.of(2020L, 2019L, 2018L, 2017L);
  private static final ImmutableList<Long> SKILL_IDS_CASE_4 = ImmutableList.of(1998L, 2038L, 2249L);
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_4 =
      ImmutableList.of(1990L, 1991L, 1992L, 1993L);
  private static final Set<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_4 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000516).setPermissionSetId(1111).build(),
          PoolAssignmentModel.builder().setCasePoolId(2001052).setPermissionSetId(1111).build());
  private static final UserModel EXPECTED_USERPOOLASSIGNMENT_CASE_4 =
      UserModel.builder()
          .setUserId(USER_ID_CASE_4)
          .setWorkforceId(WORKFORCE_ID_CASE_4)
          .setWorkgroupId(WORKGROUP_ID_CASE_4)
          .setRoleIds(ROLE_IDS_CASE_4)
          .setSkillIds(SKILL_IDS_CASE_4)
          .setRoleSkillIds(ROLESKILL_IDS_CASE_4)
          .setPoolAssignments(POOL_ASSIGNMENTS_CASE_4)
          .build();

  /**
   * Expected values of fields for Case 5
   *
   * <p>Case 5: Role_skills, skills and pool_assignments columns have same values of id as inputs.
   * Valid value for workgroup_id column. The role_skills column has no proficiency element and is
   * in form of {"skill_id": number}. Same value of pool id and different values of permission set
   * id for elements in the pool_assignments column.
   */
  private static final long USER_ID_CASE_5 = 5;

  private static final long WORKFORCE_ID_CASE_5 = 1024;
  private static final long WORKGROUP_ID_CASE_5 = 2048;
  private static final ImmutableList<Long> ROLE_IDS_CASE_5 = ImmutableList.of(1998L, 2038L, 2249L);
  private static final ImmutableList<Long> SKILL_IDS_CASE_5 = ImmutableList.of(1998L, 2038L, 2249L);
  private static final ImmutableList<Long> ROLESKILL_IDS_CASE_5 =
      ImmutableList.of(1998L, 2038L, 2249L);
  private static final ImmutableSet<PoolAssignmentModel> POOL_ASSIGNMENTS_CASE_5 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1098).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1112).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(2249).build());
  private static final UserModel EXPECTED_USERPOOLASSIGNMENT_CASE_5 =
      UserModel.builder()
          .setUserId(USER_ID_CASE_5)
          .setWorkforceId(WORKFORCE_ID_CASE_5)
          .setWorkgroupId(WORKGROUP_ID_CASE_5)
          .setRoleIds(ROLE_IDS_CASE_5)
          .setSkillIds(SKILL_IDS_CASE_5)
          .setRoleSkillIds(ROLESKILL_IDS_CASE_5)
          .setPoolAssignments(POOL_ASSIGNMENTS_CASE_5)
          .build();

  /** Expected list of UserPoolAssignments */
  private static final ImmutableList<UserModel> EXPECTED_USERPOOLASSIGNMENTS =
      ImmutableList.<UserModel>builder()
          .add(EXPECTED_USERPOOLASSIGNMENT_CASE_0)
          .add(EXPECTED_USERPOOLASSIGNMENT_CASE_1)
          .add(EXPECTED_USERPOOLASSIGNMENT_CASE_2)
          .add(EXPECTED_USERPOOLASSIGNMENT_CASE_3)
          .add(EXPECTED_USERPOOLASSIGNMENT_CASE_4)
          .add(EXPECTED_USERPOOLASSIGNMENT_CASE_5)
          .build();

  @Test
  public void parserFully() throws IOException, CsvException {
    List<UserModel> userPoolAssignments =
        CsvParser.readFromCSVFile(TEST_CSV_FILE_PATH);
    Assert.assertEquals(EXPECTED_USERPOOLASSIGNMENTS.size(), userPoolAssignments.size());
    for (final UserModel userPoolAssignment : userPoolAssignments) {
      Assert.assertNotNull(userPoolAssignment);
    }
  }

  @Test
  public void parserReadingTest() throws IOException, CsvException {
    List<UserModel> userPoolAssignments =
        CsvParser.readFromCSVFile(TEST_CSV_FILE_PATH);
    Assert.assertEquals(EXPECTED_USERPOOLASSIGNMENTS, userPoolAssignments);
  }
}
