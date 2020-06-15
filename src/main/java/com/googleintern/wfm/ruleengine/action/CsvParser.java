package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CsvParser class is used to retrieve information from the input csv file ans store these data as
 * instances of UserPoolAssignmentModel.
 *
 * <p>enum variable Header represents the relationship between column position and the data stored
 * inside. Pattern variable NUMBER_PATTERN finds numbers from strings. Pattern variable
 * ROLESKILL_PATTERN finds skill_id elements from strings. Pattern variable PERMISSION_PATTERN finds
 * pool_permission elements from strings.
 */
public class CsvParser {
  enum Header {
    USER_ID(0),
    ROLE_ID(1),
    SKILL_ID(2),
    ROLESKILL_ID(3),
    WORKFORCE_ID(4),
    WORKGROUP_ID(5);

    final int column;

    Header(int column) {
      this.column = column;
    }
  }

  private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");
  private static final Pattern ROLESKILL_PATTERN = Pattern.compile("\"skill_id\":\"(\\d+)\"");
  private static final Pattern PERMISSION_PATTERN =
      Pattern.compile("\"cases_pool_id\":\"(\\d+)\",\"permission_set_id\":\"(\\d+)\"");

  /**
   * Read all data using csvFilePath. Parse useful information out and store it in *
   * userPoolAssignmentList.
   *
   * @param csvFilePath: represents the path to the csv file, including the file name.
   * @return
   * @throws IOException
   * @throws CsvException
   */
  public static ImmutableList<UserPoolAssignmentModel> readFromCSVFile(String csvFilePath)
      throws IOException, CsvException {
    // Read all data from input csv file located at given path
    Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
    CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
    List<String[]> userRecords = csvReader.readAll();

    // List variable userPoolAssignmentList stores the results reading from the input csv file
    ImmutableList.Builder<UserPoolAssignmentModel> userPoolAssignmentList =
        new ImmutableList.Builder<>();

    // Parse data line by line
    for (String[] record : userRecords) {
      long userId = Long.parseLong(record[Header.USER_ID.column]);
      long workforceId = Long.parseLong(record[Header.WORKFORCE_ID.column]);
      long workgroupId = Long.parseLong(record[Header.WORKGROUP_ID.column]);

      // Parse role ids
      Matcher roleIdsMatcher = NUMBER_PATTERN.matcher(record[Header.ROLE_ID.column]);
      List<Long> roleIds = new ArrayList<>();
      while (roleIdsMatcher.find()) {
        long idValue = Long.parseLong(roleIdsMatcher.group());
        roleIds.add(idValue);
      }

      // Parse skill ids
      Matcher skillIdsMatcher = NUMBER_PATTERN.matcher(record[Header.SKILL_ID.column]);
      List<Long> skillIds = new ArrayList<>();
      while (skillIdsMatcher.find()) {
        long idValue = Long.parseLong(skillIdsMatcher.group());
        skillIds.add(idValue);
      }

      // Parse role_skills ids
      Matcher roleSKillsMatcher = ROLESKILL_PATTERN.matcher(record[Header.ROLESKILL_ID.column]);
      List<Long> roleSkillIds = new ArrayList<>();
      while (roleSKillsMatcher.find()) {
        String skillId = roleSKillsMatcher.group();
        Matcher roleSkillIdMatcher = NUMBER_PATTERN.matcher(skillId);
        roleSkillIdMatcher.find();
        long idValue = Long.parseLong(roleSkillIdMatcher.group());
        roleSkillIds.add(idValue);
      }

      // Parse pool ids and permission ids
      Matcher permissionsMatcher = PERMISSION_PATTERN.matcher(record[6]);
      Set<PoolAssignmentModel> poolAssignmentsSet = new HashSet<PoolAssignmentModel>();
      while (permissionsMatcher.find()) {
        String permission = permissionsMatcher.group();
        Matcher idsForPermission = NUMBER_PATTERN.matcher(permission);
        idsForPermission.find();
        int poolId = Integer.parseInt(idsForPermission.group());
        idsForPermission.find();
        int permissionId = Integer.parseInt(idsForPermission.group());
        PoolAssignmentModel poolAssignment =
            PoolAssignmentModel.builder()
                .setCasePoolId(poolId)
                .setPermissionSetId(permissionId)
                .build();
        poolAssignmentsSet.add(poolAssignment);
      }

      // Save current data in userPoolAssignmentList
      UserPoolAssignmentModel user =
          UserPoolAssignmentModel.builder()
              .setUserId(userId)
              .setWorkforceId(workforceId)
              .setWorkgroupId(workgroupId)
              .setRoleIds(roleIds)
              .setSkillIds(skillIds)
              .setRoleSkillIds(roleSkillIds)
              .setPoolAssignments(poolAssignmentsSet)
              .build();
      userPoolAssignmentList.add(user);
    }
    return userPoolAssignmentList.build();
  }
}
