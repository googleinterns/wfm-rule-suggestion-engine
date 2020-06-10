package src.main.java.com.googleintern.wfm.ruleengine.action;

import java.io.IOException;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.Reader;
import java.nio.file.Files;
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
        Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        List<String[]> userRecords = csvReader.readAll();
        for (String[] record : userRecords) {
            int userId = Integer.parseInt(record[0]);
            int workforceId = Integer.parseInt(record[1]);
            int workgroupId = Integer.parseInt(record[6]);
            System.out.println("userId: " + userId + " workforce id: " + workforceId + " workgroup id: " + workgroupId);
            Pattern pattern = Pattern.compile("(\\d+)");

            // reading role ids
            Matcher roleIdsMatcher = pattern.matcher(record[2]);
            Set<FilterModel> filterSet = new HashSet<FilterModel>();
            while(roleIdsMatcher.find()){
                int idValue = Integer.parseInt(roleIdsMatcher.group());
                FilterModel filter = FilterModel.builder().setFilterType(1).setIdValue(idValue).build();
                filterSet.add(filter);
                System.out.println("role_ids: " + idValue);

            }

            // reading skill ids
            Matcher skillIdsMatcher = pattern.matcher(record[3]);
            while(skillIdsMatcher.find()) {
                int idValue = Integer.parseInt(skillIdsMatcher.group());
                FilterModel filter = FilterModel.builder().setFilterType(2).setIdValue(idValue).build();
                filterSet.add(filter);
                System.out.println("skill_ids: " + idValue);
            }

            // reading role_skills ids
            Pattern roleSkillPattern = Pattern.compile("\"skill_id\":\"(\\d+)\"");
            Matcher roleSKillsMatcher = roleSkillPattern.matcher(record[4]);
            while(roleSKillsMatcher.find()) {
                String skillId = roleSKillsMatcher.group();
                Matcher roleSkillIdMatcher = pattern.matcher(skillId);
                roleSkillIdMatcher.find();
                int idValue = Integer.parseInt(roleSkillIdMatcher.group());
                if (idValue == 0) continue;
                FilterModel filter = FilterModel.builder().setFilterType(3).setIdValue(idValue).build();
                filterSet.add(filter);
                System.out.println("role_skill_ids: " + idValue);
            }

            Set<PoolAssignmentModel> poolAssignmentsSet = new HashSet<PoolAssignmentModel>();
            // reading pool ids and permission ids
            Pattern permissionPattern =
                    Pattern.compile("\"cases_pool_id\":\"(\\d+)\",\"permission_set_id\":\"(\\d+)\"");
            Matcher permissionsMatcher = permissionPattern.matcher(record[7]);
            while(permissionsMatcher.find()){
                String permission = permissionsMatcher.group();
                Matcher idsForPermission = pattern.matcher(permission);
                System.out.println(permission);
                idsForPermission.find();
                int poolId = Integer.parseInt(idsForPermission.group());
                idsForPermission.find();
                int permissionId = Integer.parseInt(idsForPermission.group());
                PoolAssignmentModel poolAssignment = PoolAssignmentModel.builder().setCasePoolId(poolId)
                        .setPermissionSetId(permissionId).build();
                poolAssignmentsSet.add(poolAssignment);
                System.out.println("pool id: " + poolId + "  permission id: " + permissionId);
            }

            UserPoolAssignmentModel user = UserPoolAssignmentModel.builder().setUserId(userId).setWorkforceId(workforceId)
                    .setWorkgroupId(workgroupId).setFilters(filterSet).setPoolAssignments(poolAssignmentsSet).build();
            System.out.println(userPoolAssignmentList.size());
            userPoolAssignmentList.add(user);

            System.out.println("---------------------------");
        }
    }
}
