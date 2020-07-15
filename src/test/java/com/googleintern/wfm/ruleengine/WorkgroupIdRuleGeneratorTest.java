package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.opencsv.exceptions.CsvException;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.CsvParser;
import src.main.java.com.googleintern.wfm.ruleengine.action.generator.WorkgroupIdRuleGenerator;
import src.main.java.com.googleintern.wfm.ruleengine.action.WorkgroupIdGroupingUtil;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

import java.io.IOException;

/**
 * WorkgroupIdRuleGeneratorTest is used to test the functionality of the
 * WorkgroupIdRuleGenerator class.
 */
public class WorkgroupIdRuleGeneratorTest {
  private static final String TEST_CSV_FILE_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/src/"
          + "test/resources/com/googleintern/wfm/ruleengine/csv_grouping_test_data.csv";
  private static final Long EXPECTED_FIRST_WORKGROUP_ID = 1122L;
  private static final Long EXPECTED_SECOND_WORKGROUP_ID = 2233L;
  private static final ImmutableList<ImmutableSet<FilterModel>> emptyFilters =
      ImmutableList.<ImmutableSet<FilterModel>>builder().build();

  /**
   * Expected general rules for Workgroup ID = 1122.
   *
   * Permissions have different case pool ID values.
   */
  private static final ImmutableSet<RuleModel> EXPECTED_FIRST_GENERATED_RULES =
      ImmutableSet.of(
          RuleModel.builder()
              .setWorkforceId(1024L)
              .setWorkgroupId(1122L)
              .setCasePoolId(2000543L)
              .setPermissionSetIds(ImmutableSet.of(2048L))
              .setFilters(emptyFilters)
              .build(),
          RuleModel.builder()
              .setWorkforceId(1024L)
              .setWorkgroupId(1122L)
              .setCasePoolId(2000555L)
              .setPermissionSetIds(ImmutableSet.of(2048L))
              .setFilters(emptyFilters)
              .build());

  /**
   * Expected general rules for Workgroup ID = 2233.
   *
   * Permissions have the same case pool ID, but different permission set IDs.
   */
  private static final ImmutableSet<RuleModel> EXPECTED_SECOND_GENERATED_RULES =
      ImmutableSet.of(
          RuleModel.builder()
              .setWorkforceId(1024L)
              .setWorkgroupId(2233L)
              .setCasePoolId(2000543L)
              .setPermissionSetIds(ImmutableSet.of(2048L, 2051L))
              .setFilters(emptyFilters)
              .build());

  @Test
  public void findGeneralRuleForWorkGroupIdTest() throws IOException, CsvException {
    ImmutableList<UserModel> userPoolAssignments =
        CsvParser.readFromCSVFile(TEST_CSV_FILE_PATH);
    ImmutableListMultimap<Long, UserModel> mapByWorkGroupId =
        WorkgroupIdGroupingUtil.groupByWorkGroupId(userPoolAssignments);

    ImmutableSet<RuleModel> firstGeneratedRules =
        WorkgroupIdRuleGenerator.generateWorkgroupIdRules(
            mapByWorkGroupId.get(EXPECTED_FIRST_WORKGROUP_ID));
    Assert.assertEquals(EXPECTED_FIRST_GENERATED_RULES, firstGeneratedRules);

    ImmutableSet<RuleModel> secondGeneratedRules =
        WorkgroupIdRuleGenerator.generateWorkgroupIdRules(
            mapByWorkGroupId.get(EXPECTED_SECOND_WORKGROUP_ID));
    Assert.assertEquals(EXPECTED_SECOND_GENERATED_RULES, secondGeneratedRules);
  }
}
