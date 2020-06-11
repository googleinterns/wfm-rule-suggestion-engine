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

public class CsvParser {

    public static String csvFilePath;
    public static List<UserPoolAssignmentModel> userPoolAssignmentList;

    public CsvParser(String csvFilePath){
        this.csvFilePath = csvFilePath;
        userPoolAssignmentList = new ArrayList<UserPoolAssignmentModel>();
    }

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
            int userId = Integer.parseInt(record[0]);
            int workforceId = Integer.parseInt(record[4]);
            int workgroupId = Integer.parseInt(record[5]);

            // Parse role ids
            Matcher roleIdsMatcher = numberPattern.matcher(record[1]);
            Set<FilterModel> filterSet = new HashSet<FilterModel>();
            while(roleIdsMatcher.find()){
                int idValue = Integer.parseInt(roleIdsMatcher.group());
                FilterModel filter = FilterModel.builder().setFilterType(1).setIdValue(idValue).build();
                filterSet.add(filter);
            }

            // Parse skill ids
            Matcher skillIdsMatcher = numberPattern.matcher(record[2]);
            while(skillIdsMatcher.find()) {
                int idValue = Integer.parseInt(skillIdsMatcher.group());
                FilterModel filter = FilterModel.builder().setFilterType(2).setIdValue(idValue).build();
                filterSet.add(filter);
            }

            // Parse role_skills ids
            Matcher roleSKillsMatcher = roleSkillPattern.matcher(record[3]);
            while(roleSKillsMatcher.find()) {
                String skillId = roleSKillsMatcher.group();
                Matcher roleSkillIdMatcher = numberPattern.matcher(skillId);
                roleSkillIdMatcher.find();
                int idValue = Integer.parseInt(roleSkillIdMatcher.group());
                if (idValue == 0) continue;
                FilterModel filter = FilterModel.builder().setFilterType(3).setIdValue(idValue).build();
                filterSet.add(filter);
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
                    .setWorkgroupId(workgroupId).setFilters(filterSet).setPoolAssignments(poolAssignmentsSet).build();
            userPoolAssignmentList.add(user);
        }
    }
}
