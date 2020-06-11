package src.test.java.com.googleintern.wfm.ruleengine;

import com.opencsv.exceptions.CsvException;
import org.junit.*;
import src.main.java.com.googleintern.wfm.ruleengine.action.CsvParser;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class CsvParserTest {
    public static String testCsvFilePath = System.getProperty("user.home") + "/Project/wfm-rule-suggestion-engine/src/" +
            "test/resources/com/googleintern/wfm/ruleengine/csv_parser_test_data.csv";

    public Set<FilterModel> createExpectedFilterSet(List<Integer> roleIdsList, List<Integer> skillsList, List<Integer> roleSkillIdsList){
        Set<FilterModel> filterSet = new HashSet<>();
        for (final Integer roleId : roleIdsList)
            filterSet.add(FilterModel.builder().setFilterType(1).setIdValue(roleId).build());
        for (final Integer skill : skillsList)
            filterSet.add(FilterModel.builder().setFilterType(2).setIdValue(skill).build());
        for (final Integer roleSkillId : roleSkillIdsList)
            filterSet.add(FilterModel.builder().setFilterType(3).setIdValue(roleSkillId).build());
        return filterSet;
    }

    @Test
    public void parserFully() throws IOException, CsvException {
        CsvParser csvParser = new CsvParser(testCsvFilePath);
        CsvParser.ReadFromCSVFile();
        List<UserPoolAssignmentModel> userPoolAssignmentList = csvParser.userPoolAssignmentList;
        Assert.assertEquals(9, userPoolAssignmentList.size());
        for (final UserPoolAssignmentModel userPoolAssignment:userPoolAssignmentList) {
            Assert.assertNotNull(userPoolAssignment);
        }
    }

    @Test
    public void parserReadingTest() throws IOException, CsvException {
        CsvParser csvParser = new CsvParser(testCsvFilePath);
        CsvParser.ReadFromCSVFile();
        List<UserPoolAssignmentModel> userPoolAssignmentList = csvParser.userPoolAssignmentList;

        // Verify for Case 0 Readings
        Set<FilterModel> expectedFilterSet = new HashSet<>();
        Set<PoolAssignmentModel> expectedPoolAssignmentSet = new HashSet<>();
        UserPoolAssignmentModel expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(0).
                setWorkforceId(1024).setWorkgroupId(0).setFilters(expectedFilterSet).
                setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(0));

        // Verify for Case 1 Readings
        expectedFilterSet = new HashSet<>();
        expectedFilterSet.add(FilterModel.builder().setFilterType(1).setIdValue(2020).build());
        expectedPoolAssignmentSet = new HashSet<>();
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(1).
                setWorkforceId(1024).setWorkgroupId(0).setFilters(expectedFilterSet).
                setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(1));

        // Verify for Case 2 Readings
        expectedFilterSet = createExpectedFilterSet(Arrays.asList(2020, 2019, 2018), Arrays.asList(2000), Arrays.asList(1990));
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(2048).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(2).
                setWorkforceId(1024).setWorkgroupId(0).setFilters(expectedFilterSet).
                setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(2));

        // Verify for Case 3 Readings
        expectedFilterSet = createExpectedFilterSet(Arrays.asList(2020, 2019, 2018, 2017), Arrays.asList(2000, 2001), Arrays.asList(1990, 1989, 1991));
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1133).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(3).
                setWorkforceId(1024).setWorkgroupId(0).setFilters(expectedFilterSet).
                setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(3));

        // Verify for Case 4 Readings
        expectedFilterSet = createExpectedFilterSet(new ArrayList<>(), Arrays.asList(1998, 2038, 2249), Arrays.asList(1990, 1991));
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000516).setPermissionSetId(1111).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(4).
                setWorkforceId(1024).setWorkgroupId(2048).setFilters(expectedFilterSet).
                setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(4));

        // Verify for Case 5 Readings
        expectedFilterSet = createExpectedFilterSet(new ArrayList<>(), new ArrayList<>(), Arrays.asList(1990, 1991, 1992, 1993));
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000516).setPermissionSetId(1111).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2001052).setPermissionSetId(1111).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(5).
                setWorkforceId(1024).setWorkgroupId(2048).setFilters(expectedFilterSet).
                setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(5));

        // Verify for Case 6 Readings
        expectedFilterSet = createExpectedFilterSet(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1112).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1113).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(6).
                setWorkforceId(1024).setWorkgroupId(1024).setFilters(expectedFilterSet).
                setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(6));

        // Verify for Case 7 Readings
        expectedFilterSet = createExpectedFilterSet(Arrays.asList(1998, 2038, 2249), Arrays.asList(1998, 2038, 2249), Arrays.asList(1998, 2038, 2249));
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1098).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1112).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(2249).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(7).
                setWorkforceId(1024).setWorkgroupId(2048).setFilters(expectedFilterSet).
                setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(7));

        // Verify for Case 8 Readings
        expectedFilterSet = createExpectedFilterSet(Arrays.asList(1998, 2038, 2249), Arrays.asList(1998, 2038, 2249), Arrays.asList(1990, 1999, 1991));
        expectedPoolAssignmentSet = new HashSet<>();
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(8).
                setWorkforceId(1024).setWorkgroupId(2048).setFilters(expectedFilterSet).
                setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(8));
    }

}
