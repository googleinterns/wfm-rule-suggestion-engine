package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    WORKGROUP_ID(5),
    POOL_ASSIGNMENT(6);

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
   * @throws IOException
   * @throws CsvException
   */
  public static ImmutableList<UserModel> readFromCSVFile(String csvFilePath)
      throws IOException, CsvException {
    // Read all data from input csv file located at given path
    Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
    CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
    List<String[]> userRecords = csvReader.readAll();

    return userRecords.stream()
        .map(record -> parseData(record))
        .collect(ImmutableList.toImmutableList());
  }

  private static UserModel parseData(String[] record) {
    long userId = Long.parseLong(record[Header.USER_ID.column]);
    long workforceId = Long.parseLong(record[Header.WORKFORCE_ID.column]);
    long workgroupId = Long.parseLong(record[Header.WORKGROUP_ID.column]);
    ImmutableList<Long> roleIds = parseRoleIds(record[Header.ROLE_ID.column]);
    ImmutableList<Long> skillIds = parseSkillIds(record[Header.SKILL_ID.column]);
    ImmutableList<Long> roleSkillIds = parseRoleSkillIds(record[Header.ROLESKILL_ID.column]);
    Set<PoolAssignmentModel> poolAssignments =
        parsePoolAssignments(record[Header.POOL_ASSIGNMENT.column]);

    return UserModel.builder()
        .setUserId(userId)
        .setWorkforceId(workforceId)
        .setWorkgroupId(workgroupId)
        .setRoleIds(roleIds)
        .setSkillIds(skillIds)
        .setRoleSkillIds(roleSkillIds)
        .setPoolAssignments(poolAssignments)
        .build();
  }

  private static ImmutableList<Long> parseRoleIds(String roleIdData) {
    Matcher roleIdsMatcher = NUMBER_PATTERN.matcher(roleIdData);
    ImmutableList.Builder<Long> roleIdsBuilder = ImmutableList.builder();
    while (roleIdsMatcher.find()) {
      long idValue = Long.parseLong(roleIdsMatcher.group());
      roleIdsBuilder.add(idValue);
    }
    return roleIdsBuilder.build();
  }

  private static ImmutableList<Long> parseSkillIds(String skillIdData) {
    Matcher skillIdsMatcher = NUMBER_PATTERN.matcher(skillIdData);
    ImmutableList.Builder<Long> skillIdsBuilder = ImmutableList.builder();
    while (skillIdsMatcher.find()) {
      long idValue = Long.parseLong(skillIdsMatcher.group());
      skillIdsBuilder.add(idValue);
    }
    return skillIdsBuilder.build();
  }

  private static ImmutableList<Long> parseRoleSkillIds(String roleSkillIdData) {
    Matcher roleSKillsMatcher = ROLESKILL_PATTERN.matcher(roleSkillIdData);
    ImmutableList.Builder<Long> roleSkillIdsBuilder = ImmutableList.builder();
    while (roleSKillsMatcher.find()) {
      String skillId = roleSKillsMatcher.group();
      Matcher roleSkillIdMatcher = NUMBER_PATTERN.matcher(skillId);
      roleSkillIdMatcher.find();
      long idValue = Long.parseLong(roleSkillIdMatcher.group());
      roleSkillIdsBuilder.add(idValue);
    }
    return roleSkillIdsBuilder.build();
  }

  private static ImmutableSet<PoolAssignmentModel> parsePoolAssignments(String poolAssignmentData) {
    Matcher permissionsMatcher = PERMISSION_PATTERN.matcher(poolAssignmentData);
    ImmutableSet.Builder<PoolAssignmentModel> poolAssignmentsBuilder = ImmutableSet.builder();
    while (permissionsMatcher.find()) {
      String permission = permissionsMatcher.group();
      Matcher idsForPermission = NUMBER_PATTERN.matcher(permission);
      if (idsForPermission.find()) {
        int poolId = Integer.parseInt(idsForPermission.group());
        if (idsForPermission.find()) {
          int permissionId = Integer.parseInt(idsForPermission.group());
          PoolAssignmentModel poolAssignment =
              PoolAssignmentModel.builder()
                  .setCasePoolId(poolId)
                  .setPermissionSetId(permissionId)
                  .build();
          poolAssignmentsBuilder.add(poolAssignment);
        }
      }
    }
    return poolAssignmentsBuilder.build();
  }
}
