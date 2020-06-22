package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.*;
import com.opencsv.exceptions.CsvException;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;

import java.io.IOException;
import java.util.*;

public class GroupByWorkgroupId {

  public static ImmutableListMultimap<Long, UserPoolAssignmentModel> groupByWorkGroupId(
      ImmutableList<UserPoolAssignmentModel> validData) {
    ImmutableListMultimap.Builder<Long, UserPoolAssignmentModel> mapByWorkGroupIdBuilder =
        ImmutableListMultimap.builder();
    for (UserPoolAssignmentModel data : validData) {
      mapByWorkGroupIdBuilder.put(data.workgroupId(), data);
    }
    return mapByWorkGroupIdBuilder.build();
  }

  public static ImmutableSet<RuleModel> generalRuleByWorkgroupId(
      ImmutableListMultimap<Long, UserPoolAssignmentModel> mapByWorkgroupId, Long workgroupId) {
    ImmutableList<UserPoolAssignmentModel> userFromSameWorkGroupId =
        mapByWorkgroupId.get(workgroupId);
    ImmutableSet<PoolAssignmentModel> permissionIntersections =
        userFromSameWorkGroupId.get(0).poolAssignments();
    for (UserPoolAssignmentModel user : userFromSameWorkGroupId) {
      permissionIntersections =
          Sets.intersection(permissionIntersections, user.poolAssignments()).immutableCopy();
      if (permissionIntersections.size() == 0) return null;
    }

    SetMultimap<Long, Long> permissionGroup = HashMultimap.create();
    for (PoolAssignmentModel permission : permissionIntersections) {
      Long casePoolId = permission.casePoolId();
      Long permissionSetId = permission.permissionSetId();
      if (permissionGroup.containsKey(casePoolId))
        permissionGroup.get(casePoolId).add(permissionSetId);
      else permissionGroup.put(casePoolId, permissionSetId);
    }
    ImmutableSet.Builder<RuleModel> generalRulesForWorkgroupBuilder =
        ImmutableSet.<RuleModel>builder();
    Long workforceId = userFromSameWorkGroupId.get(0).workforceId();
    ImmutableList<ImmutableSet<FilterModel>> emptyFilters =
        ImmutableList.<ImmutableSet<FilterModel>>builder().build();
    for (Map.Entry<Long, Collection<Long>> entry : permissionGroup.asMap().entrySet()) {
      Long casePoolId = entry.getKey();
      Collection<Long> permissionSetIds = entry.getValue();
      RuleModel rule =
          RuleModel.builder()
              .setWorkforceId(workforceId)
              .setWorkgroupId(workgroupId)
              .setCasePoolId(casePoolId)
              .setFilters(emptyFilters)
              .setPermissionSetIds(Collections.unmodifiableSet((Set<Long>) permissionSetIds))
              .build();
      generalRulesForWorkgroupBuilder.add(rule);
    }
    return generalRulesForWorkgroupBuilder.build();
  }

  private static final String TEST_CSV_FILE_PATH =
      System.getProperty("user.home")
          + "/Project/wfm-rule-suggestion-engine/src/"
          + "test/resources/com/googleintern/wfm/ruleengine/csv_parser_test_data.csv";

  public static void main(String[] args) throws IOException, CsvException {
    ImmutableList<UserPoolAssignmentModel> userPoolAssignments =
        CsvParser.readFromCSVFile(TEST_CSV_FILE_PATH);
    ImmutableListMultimap<Long, UserPoolAssignmentModel> mapByWorkGroupId =
        GroupByWorkgroupId.groupByWorkGroupId(userPoolAssignments);
    mapByWorkGroupId.forEach((key, value) -> System.out.println(key));
  }
}
