package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.DataProcessor;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

/** DataProcessorTest class is used to test the functionality of DataProcessing class. */
public class DataProcessorTest {
  private static final PoolAssignmentModel POOL_ASSIGNMENT_0 =
      PoolAssignmentModel.builder().setCasePoolId(2020L).setPermissionSetId(0000L).build();
  private static final PoolAssignmentModel POOL_ASSIGNMENT_1 =
      PoolAssignmentModel.builder().setCasePoolId(2020L).setPermissionSetId(1111L).build();
  private static final PoolAssignmentModel POOL_ASSIGNMENT_2 =
      PoolAssignmentModel.builder().setCasePoolId(2020L).setPermissionSetId(2222L).build();
  private static final PoolAssignmentModel POOL_ASSIGNMENT_3 =
      PoolAssignmentModel.builder().setCasePoolId(2020L).setPermissionSetId(3333L).build();
  private static final PoolAssignmentModel POOL_ASSIGNMENT_4 =
      PoolAssignmentModel.builder().setCasePoolId(2020L).setPermissionSetId(4444L).build();

  private static final UserModel USER_0 =
      UserModel.builder()
          .setUserId(0L)
          .setWorkforceId(1033L)
          .setWorkgroupId(0L)
          .setRoleIds(ImmutableList.of(1111L, 2222L, 4444L))
          .setSkillIds(ImmutableList.of())
          .setRoleSkillIds(ImmutableList.of())
          .setPoolAssignments(ImmutableSet.of())
          .build();

  private static final UserModel USER_1 =
      UserModel.builder()
          .setUserId(1L)
          .setWorkforceId(1034L)
          .setWorkgroupId(0L)
          .setRoleIds(ImmutableList.of())
          .setSkillIds(ImmutableList.of())
          .setRoleSkillIds(ImmutableList.of())
          .setPoolAssignments(ImmutableSet.of())
          .build();

  private static final UserModel USER_2 =
      UserModel.builder()
          .setUserId(2L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2020L)
          .setRoleIds(ImmutableList.of(1111L, 2222L, 4444L))
          .setSkillIds(ImmutableList.of(1111L, 2222L))
          .setRoleSkillIds(ImmutableList.of(3333L))
          .setPoolAssignments(ImmutableSet.of(POOL_ASSIGNMENT_0, POOL_ASSIGNMENT_1))
          .build();

  private static final UserModel USER_3 =
      UserModel.builder()
          .setUserId(3L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2020L)
          .setRoleIds(ImmutableList.of(1111L))
          .setSkillIds(ImmutableList.of(1111L, 2222L))
          .setRoleSkillIds(ImmutableList.of(3333L))
          .setPoolAssignments(
              ImmutableSet.of(POOL_ASSIGNMENT_0, POOL_ASSIGNMENT_2, POOL_ASSIGNMENT_3))
          .build();

  private static final UserModel USER_4 =
      UserModel.builder()
          .setUserId(4L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2022L)
          .setRoleIds(ImmutableList.of(1111L))
          .setSkillIds(ImmutableList.of(1111L, 2222L))
          .setRoleSkillIds(ImmutableList.of(3333L))
          .setPoolAssignments(
              ImmutableSet.of(POOL_ASSIGNMENT_0, POOL_ASSIGNMENT_2, POOL_ASSIGNMENT_3))
          .build();

  private static final UserModel USER_5 =
      UserModel.builder()
          .setUserId(5L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2020L)
          .setRoleIds(ImmutableList.of(1111L))
          .setSkillIds(ImmutableList.of(1111L, 2222L))
          .setRoleSkillIds(ImmutableList.of(3333L))
          .setPoolAssignments(ImmutableSet.of(POOL_ASSIGNMENT_0, POOL_ASSIGNMENT_4))
          .build();

  private static final UserModel USER_6 =
      UserModel.builder()
          .setUserId(6L)
          .setWorkforceId(1033L)
          .setWorkgroupId(2020L)
          .setRoleIds(ImmutableList.of(1111L, 2222L))
          .setSkillIds(ImmutableList.of(1111L))
          .setRoleSkillIds(ImmutableList.of())
          .setPoolAssignments(ImmutableSet.of(POOL_ASSIGNMENT_0, POOL_ASSIGNMENT_1))
          .build();

  private static final ImmutableList<UserModel> INPUT_USERS_INCLUDE_INVALID_WORKGROUP_ID =
      ImmutableList.of(USER_0, USER_1, USER_2, USER_3, USER_4, USER_5, USER_6);

  private static final ImmutableList<UserModel> EXPECTED_USERS_WITH_VALID_WORKGROUP_ID =
      ImmutableList.of(USER_2, USER_3, USER_4, USER_5, USER_6);

  private static final int EXPECTED_NUMBER_OF_USERS_USERS_WITH_VALID_WORKGROUP_ID = 5;

  private static final ImmutableList<UserModel> INPUT_USERS_INCLUDE_CONFLICTS_1 =
      ImmutableList.of(USER_3, USER_2, USER_4, USER_5, USER_6);

  private static final ImmutableList<UserModel> INPUT_USERS_INCLUDE_CONFLICTS_2 =
      ImmutableList.of(USER_2, USER_3, USER_4, USER_5, USER_6);

  private static final ImmutableList<UserModel> EXPECTED_USERS_WITHOUT_CONFLICTS =
      ImmutableList.of(USER_2, USER_4, USER_6);

  private static final int EXPECTED_NUMBER_OF_USERS_USERS_WITHOUT_CONFLICTS = 3;

  private static final int EXPECTED_NUMBER_OF_USERS_USERS_WITH_EMPTY_INPUT = 0;

  @Test
  public void filterInvalidWorkgroupIdTest() {
    ImmutableList<UserModel> validUsers =
        DataProcessor.filterUsersWithValidWorkgroupId(INPUT_USERS_INCLUDE_INVALID_WORKGROUP_ID);
    Assert.assertEquals(EXPECTED_NUMBER_OF_USERS_USERS_WITH_VALID_WORKGROUP_ID, validUsers.size());
    Assert.assertTrue(validUsers.equals(EXPECTED_USERS_WITH_VALID_WORKGROUP_ID));
  }

  @Test
  public void filterInvalidWorkgroupIdWithEmptyInputTest() {
    ImmutableList<UserModel> validUsers =
        DataProcessor.filterUsersWithValidWorkgroupId(ImmutableList.of());
    Assert.assertEquals(EXPECTED_NUMBER_OF_USERS_USERS_WITH_EMPTY_INPUT, validUsers.size());
    Assert.assertTrue(validUsers.equals(ImmutableList.of()));
  }

  @Test
  public void filterConflictDataTest_Case1() {
    ImmutableList<UserModel> validUsers =
        DataProcessor.removeConflictUsers(INPUT_USERS_INCLUDE_CONFLICTS_1);
    Assert.assertEquals(EXPECTED_NUMBER_OF_USERS_USERS_WITHOUT_CONFLICTS, validUsers.size());
    Assert.assertTrue(validUsers.equals(EXPECTED_USERS_WITHOUT_CONFLICTS));
  }

  @Test
  public void filterConflictDataTest_Case2(){
    ImmutableList<UserModel> validUsers =
            DataProcessor.removeConflictUsers(INPUT_USERS_INCLUDE_CONFLICTS_2);
    Assert.assertEquals(EXPECTED_NUMBER_OF_USERS_USERS_WITHOUT_CONFLICTS, validUsers.size());
    Assert.assertTrue(validUsers.equals(EXPECTED_USERS_WITHOUT_CONFLICTS));
  }

  @Test
  public void filterConflictDataWithEmptyInputTest() {
    ImmutableList<UserModel> validUsers = DataProcessor.removeConflictUsers(ImmutableList.of());
    Assert.assertEquals(EXPECTED_NUMBER_OF_USERS_USERS_WITH_EMPTY_INPUT, validUsers.size());
    Assert.assertTrue(validUsers.equals(ImmutableList.of()));
  }
}
