package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.opencsv.exceptions.CsvException;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.CsvParser;
import src.main.java.com.googleintern.wfm.ruleengine.action.GroupByCasePoolIdAndPermissionId;
import src.main.java.com.googleintern.wfm.ruleengine.action.GroupByWorkgroupId;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;

import java.io.IOException;

public class GroupingTest {
  private static final String TEST_CSV_FILE_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/src/"
          + "test/resources/com/googleintern/wfm/ruleengine/csv_parser_test_data.csv";

  @Test
  public void groupByWorkGroupIdTest() throws IOException, CsvException {
    ImmutableList<UserPoolAssignmentModel> userPoolAssignments =
        CsvParser.readFromCSVFile(TEST_CSV_FILE_PATH);
    ImmutableListMultimap<Long, UserPoolAssignmentModel> mapByWorkGroupId =
        GroupByWorkgroupId.groupByWorkGroupId(userPoolAssignments);
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
        GroupByWorkgroupId.groupByWorkGroupId(userPoolAssignments);
    ImmutableSet<Long> workgroupIds = mapByWorkGroupId.keySet();
    for (Long workgroupId : workgroupIds) {
      ImmutableListMultimap<PoolAssignmentModel, FilterModel> mapByCasePoolIdAndPermissionSetId =
          GroupByCasePoolIdAndPermissionId.groupByCasePoolIdAndPermissionSetId(
              mapByWorkGroupId.get(workgroupId));
      for (PoolAssignmentModel poolAssignment : mapByCasePoolIdAndPermissionSetId.keySet()) {
          System.out.println(poolAssignment.casePoolId() + "   " + poolAssignment.permissionSetId());
      }
    }
  }
}
