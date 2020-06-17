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

/**
 * 
 */
public class CsvWriter {
  enum Separator {
    SEMICOLON(";"),
    COMMA(","),
    SQUARE_BRACKET_LEFT("["),
    SQUARE_BRACKET_RIGHT("]"),
    CURLY_BRACKET_LEFT("{"),
    CURLY_BRACKET_RIGHT("}");

    final String symbol;

    Separator(String symbol) {
      this.symbol = symbol;
    }
  }
  private static final String SKILL_ID_FORMAT = "skill_id:";
  private static final String ROLE_ID_FORMAT = "role_id:";
  private static final String OUTPUT_CSV_FILE_NAME = "Generated Rules" + ".csv";
  private static final String[] CSV_FILE_HEADER =
      new String[] {
        "Workforce ID", "Workgroup ID", "Case Pool ID", "Permission Set IDs", "Filters"
      };

  public static void writeDataIntoCsvFile(
      String outputCsvFileLocation, ImmutableList<RuleModel> rules) throws IOException {
    String outputCsvFilePath = outputCsvFileLocation + OUTPUT_CSV_FILE_NAME;

    File outputFile = new File(outputCsvFilePath);
    Files.deleteIfExists(outputFile.toPath());

    FileWriter outputFileWriter = new FileWriter(outputFile);

    CSVWriter csvWriter = new CSVWriter(outputFileWriter);

    csvWriter.writeNext(CSV_FILE_HEADER);
    ImmutableList<String[]> data =
        rules.stream().map(CsvWriter::writeData).collect(ImmutableList.toImmutableList());

    csvWriter.writeAll(data);
    csvWriter.close();
  }

  private static String[] writeData(final RuleModel rule) {
    String workforceId = Long.toString(rule.workforceId());
    String workgroupId = Long.toString(rule.workgroupId());
    String casePoolId = Long.toString(rule.casePoolId());

    String permissionIds = Separator.SQUARE_BRACKET_LEFT.symbol;
    for (final Long permissionId : rule.permissionSetIds()) {
      if (permissionIds.length() > 1) permissionIds = permissionIds + Separator.COMMA.symbol;
      permissionIds = permissionIds + permissionId;
    }
    permissionIds = permissionIds + Separator.SQUARE_BRACKET_RIGHT.symbol;

    String filterIds = Separator.SQUARE_BRACKET_LEFT.symbol;
    for (final ImmutableSet<FilterModel> filterSet : rule.filters()) {
      if (filterIds.length() > 1) filterIds = filterIds + Separator.SEMICOLON.symbol;
      String currFilterIds = "";
      for (final FilterModel filter : filterSet) {
        currFilterIds =
            currFilterIds.length() > 0
                ? currFilterIds + Separator.COMMA.symbol
                : currFilterIds + Separator.CURLY_BRACKET_LEFT.symbol;
        if (filter.type() == FilterModel.FilterType.SKILL
            || filter.type() == FilterModel.FilterType.ROLESKILL)
          currFilterIds = currFilterIds + SKILL_ID_FORMAT;
        else if (filter.type() == FilterModel.FilterType.ROLE)
          currFilterIds = currFilterIds + ROLE_ID_FORMAT;
        currFilterIds = currFilterIds + filter.value();
      }
      filterIds =
          currFilterIds.length() > 0
              ? filterIds + currFilterIds + Separator.CURLY_BRACKET_RIGHT.symbol
              : filterIds;
    }
    filterIds = filterIds + Separator.SQUARE_BRACKET_RIGHT.symbol;

    return new String[] {workforceId, workgroupId, casePoolId, permissionIds, filterIds};
  }

  public static void main(String[] args) throws IOException {
    String outputCsvFileLocation =
        "/usr/local/google/home/qintonghan/Project/wfm-rule-suggestion-engine/output/";
    ImmutableList.Builder<RuleModel> rulesBuilder = ImmutableList.builder();
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
