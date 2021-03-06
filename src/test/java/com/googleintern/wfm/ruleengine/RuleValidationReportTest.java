package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.RuleValidation;
import src.main.java.com.googleintern.wfm.ruleengine.model.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * RuleValidationReportTest class is used to test the functionality of the RuleValidationReport
 * class.
 */
public class RuleValidationReportTest {

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
              .setRuleId(0L)
              .setWorkforceId(1024L)
              .setWorkgroupId(2048L)
              .setCasePoolId(2000543L)
              .setPermissionSetIds(ImmutableSet.of(1111L))
              .setFilters(ImmutableList.of())
              .build(),
          RuleModel.builder()
              .setRuleId(1L)
              .setWorkforceId(1024L)
              .setWorkgroupId(2048L)
              .setCasePoolId(2000543L)
              .setPermissionSetIds(ImmutableSet.of(1156L, 2233L))
              .setFilters(FILTERS_RULE_1)
              .build(),
          RuleModel.builder()
              .setRuleId(2L)
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

  private static final String TEST_CSV_FILE_OUTPUT_PATH = "rule_validation_test_output.csv";

  private static final String EXPECTED_CSV_FILE_OUTPUT_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/src/"
          + "test/resources/com/googleintern/wfm/ruleengine/csv_rule_validation_expected_results.csv";

  @Test
  public void writeToCsvFileTest() throws IOException, CsvException {
    Reader readerForExpectedWrittenResults =
        Files.newBufferedReader(Paths.get(EXPECTED_CSV_FILE_OUTPUT_PATH));
    CSVReader csvReaderForExpectedWrittenResults =
        new CSVReaderBuilder(readerForExpectedWrittenResults).build();
    List<String[]> expectedWrittenResults = csvReaderForExpectedWrittenResults.readAll();

    RuleValidation ruleValidation = new RuleValidation(USERS);
    RuleValidationReport ruleValidationReport = ruleValidation.validate(RULES);
    ruleValidationReport.writeToCsvFile(TEST_CSV_FILE_OUTPUT_PATH);

    Reader readerForActualWrittenResults =
        Files.newBufferedReader(Paths.get(TEST_CSV_FILE_OUTPUT_PATH));
    CSVReader csvReaderForActualWrittenResults =
        new CSVReaderBuilder(readerForActualWrittenResults).build();
    List<String[]> actualWrittenResults = csvReaderForActualWrittenResults.readAll();

    Assert.assertEquals(expectedWrittenResults.size(), actualWrittenResults.size());
    for (int i = 0; i < actualWrittenResults.size(); i++) {
      Assert.assertEquals(
          Arrays.toString(expectedWrittenResults.get(i)),
          Arrays.toString(actualWrittenResults.get(i)));
    }
  }
}
