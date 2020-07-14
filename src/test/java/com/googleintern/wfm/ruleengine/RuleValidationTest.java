package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
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

  private static final ImmutableSetMultimap<UserModel, PoolAssignmentModel>
      EXPECTED_ASSIGNED_POOL_ASSIGNMENTS_BY_USERS =
          ImmutableSetMultimap.<UserModel, PoolAssignmentModel>builder()
              .putAll(USERS.get(0), EXPECTED_POOL_ASSIGNMENTS_USER_0)
              .putAll(USERS.get(1), EXPECTED_POOL_ASSIGNMENTS_USER_1)
              .putAll(USERS.get(2), EXPECTED_POOL_ASSIGNMENTS_USER_2)
              .putAll(USERS.get(3), EXPECTED_POOL_ASSIGNMENTS_USER_3)
              .putAll(USERS.get(4), EXPECTED_POOL_ASSIGNMENTS_USER_0)
              .putAll(USERS.get(5), ImmutableSet.of())
              .putAll(USERS.get(6), ImmutableSet.of())
              .build();

  private static final RuleValidationReport EXPECTED_RULE_VALIDATION_REPORT =
      RuleValidationReport.builder()
          .setGeneratedRules(RULES)
          .setAssignedPoolAssignmentsByUsers(EXPECTED_ASSIGNED_POOL_ASSIGNMENTS_BY_USERS)
          .setRuleCoverage(EXPECTED_RULES_COVERAGE)
          .setUsersWithLessAssignedPermissions(USERS_WITH_LESS_ASSIGNED_PERMISSIONS)
          .setUsersWithMoreAssignedPermissions(USERS_WITH_MORE_ASSIGNED_PERMISSIONS)
          .setUncoveredPoolAssignments(EXPECTED_UNCOVERED_POOL_ASSIGNMENTS)
          .build();

  private static final RuleValidationReport EXPECTED_RULE_VALIDATION_REPORT_WITH_EMPTY_USERS =
      RuleValidationReport.builder()
          .setGeneratedRules(RULES)
          .setAssignedPoolAssignmentsByUsers(
              ImmutableSetMultimap.<UserModel, PoolAssignmentModel>builder().build())
          .setRuleCoverage(0d / 0d)
          .setUsersWithLessAssignedPermissions(ImmutableSet.of())
          .setUsersWithMoreAssignedPermissions(ImmutableSet.of())
          .setUncoveredPoolAssignments(ImmutableSet.of())
          .build();

  private static final RuleValidationReport EXPECTED_RULE_VALIDATION_REPORT_WITH_EMPTY_RULES =
      RuleValidationReport.builder()
          .setGeneratedRules(ImmutableSet.of())
          .setAssignedPoolAssignmentsByUsers(
              ImmutableSetMultimap.<UserModel, PoolAssignmentModel>builder().build())
          .setRuleCoverage((double) 2 / 7)
          .setUsersWithLessAssignedPermissions(
              ImmutableSet.of(USERS.get(0), USERS.get(1), USERS.get(2), USERS.get(3), USERS.get(5)))
          .setUsersWithMoreAssignedPermissions(ImmutableSet.of())
          .setUncoveredPoolAssignments(
              ImmutableSet.<PoolAssignmentModel>builder()
                  .addAll(EXPECTED_POOL_ASSIGNMENTS_USER_0)
                  .addAll(EXPECTED_POOL_ASSIGNMENTS_USER_1)
                  .addAll(EXPECTED_POOL_ASSIGNMENTS_USER_2)
                  .addAll(EXPECTED_POOL_ASSIGNMENTS_USER_3)
                  .addAll(EXPECTED_POOL_ASSIGNMENTS_USER_4)
                  .build())
          .build();

  private static final RuleValidationReport
      EXPECTED_RULE_VALIDATION_REPORT_WITH_EMPTY_USERS_AND_EMPTY_RULES =
          RuleValidationReport.builder()
              .setGeneratedRules(ImmutableSet.of())
              .setAssignedPoolAssignmentsByUsers(
                  ImmutableSetMultimap.<UserModel, PoolAssignmentModel>builder().build())
              .setRuleCoverage(0d / 0d)
              .setUsersWithLessAssignedPermissions(ImmutableSet.of())
              .setUsersWithMoreAssignedPermissions(ImmutableSet.of())
              .setUncoveredPoolAssignments(ImmutableSet.of())
              .build();

  @Test
  public void validateTest() {
    RuleValidation ruleValidation = new RuleValidation(USERS);
    RuleValidationReport ruleValidationReport = ruleValidation.validate(RULES);
    Assert.assertEquals(EXPECTED_RULE_VALIDATION_REPORT, ruleValidationReport);
  }

  @Test
  public void validateTestWithEmptyUsers() {
    RuleValidation ruleValidation = new RuleValidation(ImmutableList.of());
    RuleValidationReport ruleValidationReport = ruleValidation.validate(RULES);
    Assert.assertEquals(EXPECTED_RULE_VALIDATION_REPORT_WITH_EMPTY_USERS, ruleValidationReport);
  }

  @Test
  public void validateTestWithEmptyRules() {
    RuleValidation ruleValidation = new RuleValidation(USERS);
    RuleValidationReport ruleValidationReport = ruleValidation.validate(ImmutableSet.of());
    Assert.assertEquals(EXPECTED_RULE_VALIDATION_REPORT_WITH_EMPTY_RULES, ruleValidationReport);
  }

  @Test
  public void validateTestWithEmptyRulesAndEmptyUsers() {
    RuleValidation ruleValidation = new RuleValidation(ImmutableList.of());
    RuleValidationReport ruleValidationReport = ruleValidation.validate(ImmutableSet.of());
    Assert.assertEquals(
        EXPECTED_RULE_VALIDATION_REPORT_WITH_EMPTY_USERS_AND_EMPTY_RULES, ruleValidationReport);
  }
}
