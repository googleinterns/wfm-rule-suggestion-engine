package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVWriter;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;


public class CsvWriter {
  enum Header {
    RULE_ID(0),
    WORKFORCE_ID(1),
    WORKGROUP_ID(2),
    CASE_POOL_ID(3),
    PERMISSION_SET_ID(4),
    FILTER_ID(5);

    final int column;

    Header(int column) {
      this.column = column;
    }
  }

  private static final String OUTPUT_CSV_FILE_NAME = "Generated Rules" + ".csv";
  private static final String[] CSV_FILE_HEADER =
      new String[] {
        "Workforce ID", "Workgroup ID", "Case Pool ID", "Permission Set IDs", "Filters"
      };

  public static void writeDataIntoCsvFile(
      String outputCsvFileLocation, ImmutableList<RuleModel> rules) throws IOException {
    String outputCsvFilePath = outputCsvFileLocation + OUTPUT_CSV_FILE_NAME;
    File file = new File(outputCsvFilePath);
    Files.deleteIfExists(file.toPath());

    // Create FileWriter object
    FileWriter outputFile = new FileWriter(file);

    // Create CSVWriter object
    CSVWriter writer = new CSVWriter(outputFile);

    writer.writeNext(CSV_FILE_HEADER);

    ImmutableList<String[]> data =
        rules.stream().map(CsvWriter::writeData).collect(ImmutableList.toImmutableList());

    // Write all rules into csv file
    writer.writeAll(data);

    // Close writer connection
    writer.close();
  }

  private static String[] writeData(final RuleModel rule) {
    String workforceId = Long.toString(rule.workforceId());
    String workgroupId = Long.toString(rule.workgroupId());
    String casePoolId = Long.toString(rule.casePoolId());

    String permissionIds = "[";
    for (final Long permissionId : rule.permissionSetIds()) {
      if (permissionIds.length() > 1) permissionIds = permissionIds + ",";
      permissionIds = permissionIds + Long.toString(permissionId);
    }
    permissionIds = permissionIds + "]";

    String filterIds = "[";
    for (final ImmutableSet<FilterModel> filterSet : rule.filters()) {
      if (filterIds.length() > 1) filterIds = filterIds + ";";
      String currFilterIds = "";
      for (final FilterModel filter : filterSet) {
        currFilterIds = currFilterIds.length() > 0? currFilterIds + "," : currFilterIds + "{";
        if (filter.type() == FilterModel.FilterType.SKILL
            || filter.type() == FilterModel.FilterType.ROLESKILL)
          currFilterIds = currFilterIds + "skill_id:";
        else if (filter.type() == FilterModel.FilterType.ROLE)
          currFilterIds = currFilterIds + "role_id:";
        currFilterIds = currFilterIds + Long.toString(filter.value());
      }
      filterIds = currFilterIds.length() > 0? filterIds + currFilterIds + "}" : filterIds;
    }
    filterIds = filterIds + "]";

    return new String[] {workforceId, workgroupId, casePoolId, permissionIds, filterIds};
  }

  public static void main(String[] args) throws IOException {
    String outputCsvFileLocation =
        "/usr/local/google/home/qintonghan/Project/wfm-rule-suggestion-engine/output/";
    ImmutableList.Builder<RuleModel> rulesBuilder = ImmutableList.<RuleModel>builder();
    RuleModel.Builder ruleBuilder = RuleModel.builder();
    ruleBuilder
        .setWorkforceId(1024L)
        .setWorkgroupId(2048L)
        .setCasePoolId(334L)
        .setPermissionSetIds(ImmutableSet.of(4456L, 5678L));

    ImmutableSet.Builder<FilterModel> filterBuilder = ImmutableSet.builder();
    filterBuilder.add(
        FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(7689L).build());
    filterBuilder.add(
            FilterModel.builder().setType(FilterModel.FilterType.ROLESKILL).setValue(7689L).build());
    filterBuilder.add(
            FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(7689L).build());
    ImmutableSet<FilterModel> filter = filterBuilder.build();

    ImmutableSet.Builder<FilterModel> filterBuilder1 = ImmutableSet.builder();
    filterBuilder1.add(
            FilterModel.builder().setType(FilterModel.FilterType.SKILL).setValue(7689L).build());
    filterBuilder1.add(
            FilterModel.builder().setType(FilterModel.FilterType.ROLESKILL).setValue(7689L).build());
    filterBuilder1.add(
            FilterModel.builder().setType(FilterModel.FilterType.ROLE).setValue(7689L).build());
    ImmutableSet<FilterModel> filter1 = filterBuilder.build();

    ImmutableList<ImmutableSet<FilterModel>> filters =
        ImmutableList.<ImmutableSet<FilterModel>>builder().add(filter).add(filter1).build();

    rulesBuilder.add(ruleBuilder.setFilters(filters).build());
    writeDataIntoCsvFile(outputCsvFileLocation, rulesBuilder.build());
  }
}
