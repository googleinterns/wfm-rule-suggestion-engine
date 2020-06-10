package main.java.src.com.googleintern.wfm.ruleengine.action;

import java.io.IOException;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import main.java.src.com.googleintern.wfm.ruleengine.module.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList;
import com.google.common.base.Strings;

public class CsvParser {

    public static String csvFileUrl;
    public static List<UserPoolAssignment> userPoolAssignmentList;

    public CsvParser(String csvFileUrl){
        this.csvFileUrl = csvFileUrl;
        userPoolAssignmentList = new ArrayList<UserPoolAssignment>();
    }

    public static void ReadFromCSVFile() throws IOException, CsvException {
        Reader reader = Files.newBufferedReader(Paths.get(csvFileUrl));
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        List<String[]> userRecords = csvReader.readAll();
        int i = 250;
        for (String[] record : userRecords) {
            int userId = Integer.parseInt(record[0]);
            int workforceId = Integer.parseInt(record[1]);
            int workgroupId = Integer.parseInt(record[6]);
            System.out.println("userId: " + userId + " workforce id: " + workforceId + " workgroup id: " + workgroupId);
            Pattern pattern = Pattern.compile("(\\d+)");

            // reading role ids
            Matcher roleIdsMatcher = pattern.matcher(record[2]);
            Set<Filter> filterSet = new HashSet<Filter>();
            while(roleIdsMatcher.find()){
                int idValue = Integer.parseInt(roleIdsMatcher.group());
                Filter filter = Filter.builder().setFilterType(1).setIdValue(idValue).build();
                filterSet.add(filter);
                System.out.println("role_ids: " + idValue);

            }

            // reading skill ids
            Matcher skillIdsMatcher = pattern.matcher(record[3]);
            while(skillIdsMatcher.find()) {
                int idValue = Integer.parseInt(skillIdsMatcher.group());
                Filter filter = Filter.builder().setFilterType(2).setIdValue(idValue).build();
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
                Filter filter = Filter.builder().setFilterType(3).setIdValue(idValue).build();
                filterSet.add(filter);
                System.out.println("role_skill_ids: " + idValue);
            }

            Set<PoolAssignment> poolAssignmentsSet = new HashSet<PoolAssignment>();
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
                PoolAssignment poolAssignment = PoolAssignment.builder().setCasePoolId(poolId)
                        .setPermissionSetId(permissionId).build();
                poolAssignmentsSet.add(poolAssignment);
                System.out.println("pool id: " + poolId + "  permission id: " + permissionId);
            }

            UserPoolAssignment user = UserPoolAssignment.builder().setUserId(userId).setWorkforceId(workforceId)
                    .setWorkgroupId(workgroupId).setFilters(filterSet).setPoolAssignments(poolAssignmentsSet).build();
            System.out.println(userPoolAssignmentList.size());
            userPoolAssignmentList.add(user);

            i--;
            if(i<=0) break;
            System.out.println("---------------------------");
        }
    }

    public static void main(String[] args) throws IOException, CsvException {
        System.out.println("Start Reading");
        CsvParser csvParser = new CsvParser("/usr/local/google/home/qintonghan/Project/wfm-rule-suggestion-engine/data/support_test_agents_anonymized.csv");
        csvParser.ReadFromCSVFile();
        System.out.println("End Reading");
    }
}
