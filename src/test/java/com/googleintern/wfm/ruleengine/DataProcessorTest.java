package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.opencsv.exceptions.CsvException;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.CsvParser;
import src.main.java.com.googleintern.wfm.ruleengine.action.DataProcessor;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

import java.io.IOException;

/** DataProcessorTest class is used to test the functionality of DataProcessing class. */
public class DataProcessorTest {
  private static final String TEST_CSV_FILE_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/src/"
          + "test/resources/com/googleintern/wfm/ruleengine/csv_parser_test_data.csv";
  private static final int EXPECTED_VALID_DATA_SIZE = 5;
  private static final long INVALID_WORKGROUP_ID = 0L;

  @Test
  public void filterInvalidTest() throws IOException, CsvException {
    ImmutableList<UserModel> userPoolAssignments =
        CsvParser.readFromCSVFile(TEST_CSV_FILE_PATH);
    ImmutableList<UserModel> validUserPoolAssignments =
        DataProcessor.filterValidData(userPoolAssignments);
    Assert.assertEquals(EXPECTED_VALID_DATA_SIZE, 5);
    for (UserModel userData : validUserPoolAssignments) {
      Assert.assertNotEquals(INVALID_WORKGROUP_ID, userData.workgroupId());
    }
  }
}
