package src.main.java.com.googleintern.wfm.ruleengine.action;

import java.io.IOException;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import src.main.java.com.googleintern.wfm.ruleengine.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * CsvParser class is used to retrieve information from the input csv file ans store these data as instances of
 * UserPoolAssignmentModel.
 * String variable csvFilePath represents the path to the csv file, including the file name.
 * List variable userPoolAssignmentList stores the results reading from the input csv file.
 */
public class CsvParser {

    public static String csvFilePath;
    public static List<UserPoolAssignmentModel> userPoolAssignmentList;

    /**
     * CsvParser class constructor
     * @param csvFilePath path to the csv file, including the file name.
     */
    public CsvParser(String csvFilePath){
        this.csvFilePath = csvFilePath;
        userPoolAssignmentList = new ArrayList<>();
    }

    /**
     * Read all data using csvFilePath. Parse useful information out and store it in userPoolAssignmentList.
     * @throws IOException
     * @throws CsvException
     */
    public static void ReadFromCSVFile() throws IOException, CsvException {
        // Read all data from input csv file located at given path
        Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        List<String[]> userRecords = csvReader.readAll();

        // Input patterns that are used to match target information
        Pattern numberPattern = Pattern.compile("(\\d+)");
        Pattern roleSkillPattern = Pattern.compile("\"skill_id\":\"(\\d+)\"");
        Pattern permissionPattern =
                Pattern.compile("\"cases_pool_id\":\"(\\d+)\",\"permission_set_id\":\"(\\d+)\"");

        // Parse data line by line
        for (String[] record : userRecords){
            long userId = Long.parseLong(record[0]);
            long workforceId = Long.parseLong(record[4]);
            long workgroupId = Long.parseLong(record[5]);

            // Parse role ids
            Matcher roleIdsMatcher = numberPattern.matcher(record[1]);
            List<Long> roleIds = new ArrayList<>();
            while(roleIdsMatcher.find()){
                long idValue = Long.parseLong(roleIdsMatcher.group());
                roleIds.add(idValue);
            }

            // Parse skill ids
            Matcher skillIdsMatcher = numberPattern.matcher(record[2]);
            List<Long> skillIds = new ArrayList<>();
            while(skillIdsMatcher.find()) {
                long idValue = Long.parseLong(skillIdsMatcher.group());
                skillIds.add(idValue);
            }

            // Parse role_skills ids
            Matcher roleSKillsMatcher = roleSkillPattern.matcher(record[3]);
            List<Long> roleSkillIds = new ArrayList<>();
            while(roleSKillsMatcher.find()) {
                String skillId = roleSKillsMatcher.group();
                Matcher roleSkillIdMatcher = numberPattern.matcher(skillId);
                roleSkillIdMatcher.find();
                long idValue = Long.parseLong(roleSkillIdMatcher.group());
                roleSkillIds.add(idValue);
            }

            // Parse pool ids and permission ids
            Matcher permissionsMatcher = permissionPattern.matcher(record[6]);
            Set<PoolAssignmentModel> poolAssignmentsSet = new HashSet<PoolAssignmentModel>();
            while(permissionsMatcher.find()){
                String permission = permissionsMatcher.group();
                Matcher idsForPermission = numberPattern.matcher(permission);
                idsForPermission.find();
                int poolId = Integer.parseInt(idsForPermission.group());
                idsForPermission.find();
                int permissionId = Integer.parseInt(idsForPermission.group());
                PoolAssignmentModel poolAssignment = PoolAssignmentModel.builder().setCasePoolId(poolId)
                        .setPermissionSetId(permissionId).build();
                poolAssignmentsSet.add(poolAssignment);
            }

            // Save current data in userPoolAssignmentList
            UserPoolAssignmentModel user = UserPoolAssignmentModel.builder().setUserId(userId).setWorkforceId(workforceId)
                    .setWorkgroupId(workgroupId).setRoleIds(roleIds).setSkillIds(skillIds).setRoleSkillIds(roleSkillIds)
                    .setPoolAssignments(poolAssignmentsSet).build();
            userPoolAssignmentList.add(user);
        }
    }
}
