package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.CsvWriter;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/** * CsvWriterTest class is used to testing the functionality of CsvWriter class. */
public class CsvWriterTest {
  private static final String TEST_CSV_FILE_OUTPUT_PATH = "csv_writer_test_output.csv";

  private static final String EXPECTED_CSV_FILE_OUTPUT_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/src/"
          + "test/resources/com/googleintern/wfm/ruleengine/csv_writer_expected_output.csv";

  /** Rule 0: Empty for permission ids and filter ids. Invalid values for other variables. */
  private static final ImmutableSet<Long> PERMISSION_IDS_RULE_0 = ImmutableSet.of();

  private static final ImmutableSet<FilterModel> OR_FILTER_IDS_RULE_0 = ImmutableSet.of();
  private static final ImmutableList<ImmutableSet<FilterModel>> AND_FILTER_IDS_RULE_0 =
      ImmutableList.of(OR_FILTER_IDS_RULE_0);
  private static final RuleModel RULE_0 =
      RuleModel.builder()
          .setWorkforceId(0)
          .setWorkgroupId(0)
          .setCasePoolId(0)
          .setPermissionSetIds(PERMISSION_IDS_RULE_0)
          .setFilters(AND_FILTER_IDS_RULE_0)
          .build();

  /** Rule 1: Single element for permission ids and filter ids. Valid values for other variables. */
  private static final ImmutableSet<Long> PERMISSION_IDS_RULE_1 = ImmutableSet.of(3344L);

  private static final ImmutableSet<FilterModel> OR_FILTER_IDS_RULE_1 =
      ImmutableSet.of(
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(2020L).build());
  private static final ImmutableList<ImmutableSet<FilterModel>> AND_FILTER_IDS_RULE_1 =
      ImmutableList.of(OR_FILTER_IDS_RULE_1);
  private static final RuleModel RULE_1 =
      RuleModel.builder()
          .setWorkforceId(1024)
          .setWorkgroupId(2048)
          .setCasePoolId(200054)
          .setPermissionSetIds(PERMISSION_IDS_RULE_1)
          .setFilters(AND_FILTER_IDS_RULE_1)
          .build();

  /**
   * Rule 2: Multiple distinctive elements for permission ids and filter ids. Valid values for other
   * variables.
   */
  private static final ImmutableSet<Long> PERMISSION_IDS_RULE_2 =
          ImmutableSet.of(3344L, 2045L);

  private static final ImmutableSet<FilterModel> OR_FILTER_IDS_INDEX_1_RULE_2 =
      ImmutableSet.of(
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(2020L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(2011L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.ROLESKILL).setValue(2033L).build());
  private static final ImmutableSet<FilterModel> OR_FILTER_IDS_INDEX_2_RULE_2 =
      ImmutableSet.of(
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(1990L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(1995L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.ROLESKILL).setValue(1998L).build());
  private static final ImmutableList<ImmutableSet<FilterModel>> AND_FILTER_IDS_RULE_2 =
      ImmutableList.of(OR_FILTER_IDS_INDEX_1_RULE_2, OR_FILTER_IDS_INDEX_2_RULE_2);
  private static final RuleModel RULE_2 =
      RuleModel.builder()
          .setWorkforceId(1024)
          .setWorkgroupId(2048)
          .setCasePoolId(200054)
          .setPermissionSetIds(PERMISSION_IDS_RULE_2)
          .setFilters(AND_FILTER_IDS_RULE_2)
          .build();

  /**
   * Rule 3: Multiple elements for permission ids and filter ids. Permission ids and filter ids have
   * the same numerical values. Valid values for other variables.
   */
  private static final ImmutableSet<Long> PERMISSION_IDS_RULE_3 =
      ImmutableSet.of(2020L, 2011L, 2033L, 2056L);

  private static final ImmutableSet<FilterModel> OR_FILTER_IDS_INDEX_1_RULE_3 =
      ImmutableSet.of(
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(2020L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(2011L).build(),
          FilterModel.builder().setType(FilterModel.FilterType.ROLESKILL).setValue(2033L).build());
  private static final ImmutableSet<FilterModel> OR_FILTER_IDS_INDEX_2_RULE_3 =
      ImmutableSet.of(
          FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(2056L).build());
  private static final ImmutableList<ImmutableSet<FilterModel>> AND_FILTER_IDS_RULE_3 =
      ImmutableList.of(OR_FILTER_IDS_INDEX_1_RULE_3, OR_FILTER_IDS_INDEX_2_RULE_3);
  private static final RuleModel RULE_3 =
      RuleModel.builder()
          .setWorkforceId(1024)
          .setWorkgroupId(2048)
          .setCasePoolId(200054)
          .setPermissionSetIds(PERMISSION_IDS_RULE_3)
          .setFilters(AND_FILTER_IDS_RULE_3)
          .build();

  /** Test rule lists for CscWriter class. */
  private static final ImmutableList<RuleModel> RULES =
      ImmutableList.of(RULE_0, RULE_1, RULE_2, RULE_3);

  @Test
  public void correctlyWriteTest() throws IOException, CsvException {
    Reader readerForExpectedWrittenRules =
        Files.newBufferedReader(Paths.get(EXPECTED_CSV_FILE_OUTPUT_PATH));
    CSVReader csvReaderForExpectedWrittenRules =
        new CSVReaderBuilder(readerForExpectedWrittenRules).build();
    List<String[]> expectedWrittenRules = csvReaderForExpectedWrittenRules.readAll();

    CsvWriter.writeDataIntoCsvFile(TEST_CSV_FILE_OUTPUT_PATH, RULES);
    Reader readerForActualWrittenRules =
        Files.newBufferedReader(Paths.get(TEST_CSV_FILE_OUTPUT_PATH));
    CSVReader csvReaderForActualWrittenRules =
        new CSVReaderBuilder(readerForActualWrittenRules).build();
    List<String[]> actualWrittenRules = csvReaderForActualWrittenRules.readAll();

    Assert.assertEquals(expectedWrittenRules.size(), actualWrittenRules.size());
    for (int i = 0; i < actualWrittenRules.size(); i++) {
      Assert.assertEquals(
          Arrays.toString(expectedWrittenRules.get(i)), Arrays.toString(actualWrittenRules.get(i)));
    }
  }
}
