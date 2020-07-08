package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.RuleValidation;
import src.main.java.com.googleintern.wfm.ruleengine.model.*;

/** RuleValidationTest class is used to test the functionality of RuleValidation class. */
public class RuleValidationTest {
  /** Generated Rules */
  private static final ImmutableList<ImmutableSet<FilterModel>> FILTERS_RULE_1 =
      ImmutableList.<ImmutableSet<FilterModel>>builder()
          .add(
              ImmutableSet.of(
                  FilterModel.builder()
                      .setType(FilterModel.FilterType.SKILL)
                      .setValue(2020L)
                      .build()))
          .add(
              ImmutableSet.of(
                  FilterModel.builder()
                      .setType(FilterModel.FilterType.ROLE)
                      .setValue(1880L)
                      .build()))
          .build();

  private static final ImmutableList<ImmutableSet<FilterModel>> FILTERS_RULE_2 =
      ImmutableList.<ImmutableSet<FilterModel>>builder()
          .add(
              ImmutableSet.of(
                  FilterModel.builder()
                      .setType(FilterModel.FilterType.SKILL)
                      .setValue(1997L)
                      .build(),
                  FilterModel.builder()
                      .setType(FilterModel.FilterType.SKILL)
                      .setValue(2088L)
                      .build()))
          .build();

  private static final ImmutableSet<RuleModel> RULES =
      ImmutableSet.of(
          RuleModel.builder()
              .setWorkforceId(1024L)
              .setWorkgroupId(2048L)
              .setCasePoolId(2000543L)
              .setPermissionSetIds(ImmutableSet.of(1111L))
              .setFilters(ImmutableList.of())
              .build(),
          RuleModel.builder()
              .setWorkforceId(1024L)
              .setWorkgroupId(2048L)
              .setCasePoolId(2000543L)
              .setPermissionSetIds(ImmutableSet.of(1156L, 2233L))
              .setFilters(FILTERS_RULE_1)
              .build(),
          RuleModel.builder()
              .setWorkforceId(1024L)
              .setWorkgroupId(2050L)
              .setCasePoolId(2000408L)
              .setPermissionSetIds(ImmutableSet.of(30456L))
              .setFilters(FILTERS_RULE_2)
              .build());

  /** Users that are used to test the performance of rules. */
  private static final ImmutableSet<PoolAssignmentModel> EXPECTED_POOL_ASSIGNMENTS_USER_0 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543L).setPermissionSetId(1111L).build());

  private static final ImmutableSet<PoolAssignmentModel> EXPECTED_POOL_ASSIGNMENTS_USER_1 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000543L).setPermissionSetId(1111L).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000543L).setPermissionSetId(1156L).build(),
          PoolAssignmentModel.builder().setCasePoolId(2000543L).setPermissionSetId(2233L).build());

  private static final ImmutableSet<PoolAssignmentModel> EXPECTED_POOL_ASSIGNMENTS_USER_2 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000408L).setPermissionSetId(30456L).build());

  private static final ImmutableSet<PoolAssignmentModel> EXPECTED_POOL_ASSIGNMENTS_USER_3 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000408L).setPermissionSetId(30456L).build());

  private static final ImmutableSet<PoolAssignmentModel> EXPECTED_POOL_ASSIGNMENTS_USER_4 =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000555L).setPermissionSetId(1112L).build());

  private static final ImmutableList<UserModel> USERS =
      ImmutableList.of(
          UserModel.builder()
              .setUserId(0)
              .setWorkforceId(1024L)
              .setWorkgroupId(2048L)
              .setRoleIds(ImmutableList.of())
              .setSkillIds(ImmutableList.of())
              .setRoleSkillIds(ImmutableList.of())
              .setPoolAssignments(EXPECTED_POOL_ASSIGNMENTS_USER_0)
              .build(),
          UserModel.builder()
              .setUserId(1)
              .setWorkforceId(1024L)
              .setWorkgroupId(2048L)
              .setRoleIds(ImmutableList.of(1880L))
              .setSkillIds(ImmutableList.of(2020L))
              .setRoleSkillIds(ImmutableList.of())
              .setPoolAssignments(EXPECTED_POOL_ASSIGNMENTS_USER_1)
              .build(),
          UserModel.builder()
              .setUserId(2)
              .setWorkforceId(1024L)
              .setWorkgroupId(2050L)
              .setRoleIds(ImmutableList.of())
              .setSkillIds(ImmutableList.of())
              .setRoleSkillIds(ImmutableList.of(1997L))
              .setPoolAssignments(EXPECTED_POOL_ASSIGNMENTS_USER_2)
              .build(),
          UserModel.builder()
              .setUserId(3)
              .setWorkforceId(1024L)
              .setWorkgroupId(2050L)
              .setRoleIds(ImmutableList.of())
              .setSkillIds(ImmutableList.of(2088L))
              .setRoleSkillIds(ImmutableList.of())
              .setPoolAssignments(EXPECTED_POOL_ASSIGNMENTS_USER_3)
              .build(),
          UserModel.builder()
              .setUserId(4)
              .setWorkforceId(1024L)
              .setWorkgroupId(2048L)
              .setRoleIds(ImmutableList.of())
              .setSkillIds(ImmutableList.of())
              .setRoleSkillIds(ImmutableList.of())
              .setPoolAssignments(ImmutableSet.of())
              .build(),
          UserModel.builder()
              .setUserId(5)
              .setWorkforceId(1024L)
              .setWorkgroupId(2050L)
              .setRoleIds(ImmutableList.of())
              .setSkillIds(ImmutableList.of())
              .setRoleSkillIds(ImmutableList.of())
              .setPoolAssignments(EXPECTED_POOL_ASSIGNMENTS_USER_4)
              .build(),
          UserModel.builder()
              .setUserId(6)
              .setWorkforceId(1024L)
              .setWorkgroupId(2060L)
              .setRoleIds(ImmutableList.of())
              .setSkillIds(ImmutableList.of())
              .setRoleSkillIds(ImmutableList.of())
              .setPoolAssignments(ImmutableSet.of())
              .build());

  private static final ImmutableSet<UserModel> USERS_WITH_LESS_ASSIGNED_PERMISSIONS =
      ImmutableSet.of(USERS.get(5));

  private static final ImmutableSet<UserModel> USERS_WITH_MORE_ASSIGNED_PERMISSIONS =
      ImmutableSet.of(USERS.get(4));

  private static final double EXPECTED_RULES_COVERAGE = (double) 5 / 7;

  private static final ImmutableSet<PoolAssignmentModel> EXPECTED_UNCOVERED_POOL_ASSIGNMENTS =
      ImmutableSet.of(
          PoolAssignmentModel.builder().setCasePoolId(2000555L).setPermissionSetId(1112L).build());

  @Test
  public void calculateRulesCoverageTest() {
    RuleValidation ruleValidation = new RuleValidation(USERS);
    RuleValidationReport ruleValidationReport = ruleValidation.validate(RULES);
    Assert.assertEquals(
        Double.toString(EXPECTED_RULES_COVERAGE),
        Double.toString(ruleValidationReport.ruleCoverage()));
  }

  @Test
  public void findUncoveredPoolAssignmentsTest() {
    RuleValidation ruleValidation = new RuleValidation(USERS);
    RuleValidationReport ruleValidationReport = ruleValidation.validate(RULES);
    Assert.assertEquals(
        EXPECTED_UNCOVERED_POOL_ASSIGNMENTS, ruleValidationReport.uncoveredPoolAssignments());
  }

  @Test
  public void findUsersWithLessAssignedPermissionsTest() {
    RuleValidation ruleValidation = new RuleValidation(USERS);
    RuleValidationReport ruleValidationReport = ruleValidation.validate(RULES);
    Assert.assertEquals(
        USERS_WITH_LESS_ASSIGNED_PERMISSIONS,
        ruleValidationReport.usersWithLessAssignedPermissions());
  }

  @Test
  public void findWrongUsersWithMoreAssignedPermissionsTest() {
    RuleValidation ruleValidation = new RuleValidation(USERS);
    RuleValidationReport ruleValidationReport = ruleValidation.validate(RULES);
    Assert.assertEquals(
        USERS_WITH_MORE_ASSIGNED_PERMISSIONS,
        ruleValidationReport.usersWithMoreAssignedPermissions());
  }
}
