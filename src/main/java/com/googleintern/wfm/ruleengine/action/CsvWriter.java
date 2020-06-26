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
import java.util.List;

/** CsvWriter class is used to write newly generated rules into a csv file. */
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

  private static final String SKILL_ID_PREFIX = "skill_id:";
  private static final String ROLE_ID_PREFIX = "role_id:";
  private static final String[] CSV_FILE_HEADER =
      new String[] {
        "Workforce ID", "Workgroup ID", "Case Pool ID", "Permission Set IDs", "Filters"
      };

  /**
   * Create a new csv file using OUTPUT_CSV_FILE_NAME as name in outputCsvFileLocation and write
   * data into the newly created csv file.
   *
   * @throws IOException
   */
  public static void writeDataIntoCsvFile(String outputCsvFilePath, ImmutableList<RuleModel> rules)
      throws IOException {
    File outputFile = new File(outputCsvFilePath);
    Files.deleteIfExists(outputFile.toPath());
    outputFile.createNewFile();

    FileWriter outputFileWriter = new FileWriter(outputFile);
    CSVWriter csvWriter = new CSVWriter(outputFileWriter);

    csvWriter.writeNext(CSV_FILE_HEADER);
    ImmutableList<String[]> data =
        rules.stream().map(CsvWriter::convertRuleToRow).collect(ImmutableList.toImmutableList());

    csvWriter.writeAll(data);
    csvWriter.close();
  }

  private static String[] convertRuleToRow(final RuleModel rule) {
    String workforceId = Long.toString(rule.workforceId());
    String workgroupId = Long.toString(rule.workgroupId());
    String casePoolId = Long.toString(rule.casePoolId());
    String permissionIds = convertPermissionSetIdsToCell(rule.permissionSetIds());
    String filterIds = convertFilterIdsToCell(rule.filters());
    return new String[] {workforceId, workgroupId, casePoolId, permissionIds, filterIds};
  }

  private static String convertPermissionSetIdsToCell(ImmutableSet<Long> permissionSetIds) {
    StringBuilder permissionIdsBuilder = new StringBuilder(Separator.SQUARE_BRACKET_LEFT.symbol);
    for (Long permissionSetId : permissionSetIds) {
      if (permissionIdsBuilder.length() > 1) {
        permissionIdsBuilder.append(Separator.COMMA.symbol);
      }
      permissionIdsBuilder.append(permissionSetId);
    }
    permissionIdsBuilder.append(Separator.SQUARE_BRACKET_RIGHT.symbol);
    return permissionIdsBuilder.toString();
  }

  private static String convertFilterIdsToCell(List<ImmutableSet<FilterModel>> filters) {
    StringBuilder filterIdsBuilder = new StringBuilder(Separator.SQUARE_BRACKET_LEFT.symbol);
    for (final ImmutableSet<FilterModel> filterSet : filters) {
      if (filterIdsBuilder.length() > 1) {
        filterIdsBuilder.append(Separator.SEMICOLON.symbol);
      }
      StringBuilder currFilterIdsBuilder = new StringBuilder();
      for (final FilterModel filter : filterSet) {
        currFilterIdsBuilder.append(
            currFilterIdsBuilder.length() > 0
                ? Separator.COMMA.symbol
                : Separator.CURLY_BRACKET_LEFT.symbol);
        if (filter.type() == FilterModel.FilterType.SKILL) {
          currFilterIdsBuilder.append(SKILL_ID_PREFIX);
        } else if (filter.type() == FilterModel.FilterType.ROLE) {
          currFilterIdsBuilder.append(ROLE_ID_PREFIX);
        }
        currFilterIdsBuilder.append(filter.value());
      }
      filterIdsBuilder.append(
          currFilterIdsBuilder.toString().isEmpty()
              ? ""
              : currFilterIdsBuilder.toString() + Separator.CURLY_BRACKET_RIGHT.symbol);
    }
    filterIdsBuilder.append(Separator.SQUARE_BRACKET_RIGHT.symbol);
    return filterIdsBuilder.toString();
  }
}
