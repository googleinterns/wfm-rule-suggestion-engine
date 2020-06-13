package src.test.java.com.googleintern.wfm.ruleengine;

import com.opencsv.exceptions.CsvException;
import org.junit.*;
import src.main.java.com.googleintern.wfm.ruleengine.action.CsvParser;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;
import java.io.IOException;
import java.util.*;

/***
 * CsvParserTest class is used to testing the functionality of CsvParser class.
 */
public class CsvParserTest {
    public static String testCsvFilePath = System.getProperty("user.home") + "/Project/wfm-rule-suggestion-engine/src/" +
            "test/resources/com/googleintern/wfm/ruleengine/csv_parser_test_data.csv";

    /**
     * Test CsvParser can read every row and every column from the input.
     * @throws IOException
     * @throws CsvException
     */
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

    /**
     * Test CsvParser read accurate information from the input file.
     * @throws IOException
     * @throws CsvException
     */
    @Test
    public void parserReadingTest() throws IOException, CsvException {
        CsvParser csvParser = new CsvParser(testCsvFilePath);
        CsvParser.ReadFromCSVFile();
        List<UserPoolAssignmentModel> userPoolAssignmentList = csvParser.userPoolAssignmentList;

        // Verify for Case 0 Readings
        List<Long> roleIds = new ArrayList<>();
        List<Long> skillIds = new ArrayList<>();
        List<Long> roleSkillIds = new ArrayList<>();
        Set<PoolAssignmentModel> expectedPoolAssignmentSet = new HashSet<>();
        UserPoolAssignmentModel expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(0).
                setWorkforceId(1024).setWorkgroupId(0).setRoleIds(roleIds).setSkillIds(skillIds).
                setRoleSkillIds(roleSkillIds).setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(0));

        // Verify for Case 1 Readings
        roleIds = Arrays.asList(2020L);
        skillIds = new ArrayList<>();
        roleSkillIds = new ArrayList<>();
        expectedPoolAssignmentSet = new HashSet<>();
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(1).
                setWorkforceId(1024).setWorkgroupId(0).setRoleIds(roleIds).setSkillIds(skillIds).
                setRoleSkillIds(roleSkillIds).setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(1));

        // Verify for Case 2 Readings
        roleIds = Arrays.asList(2020L, 2019L, 2018L);
        skillIds = Arrays.asList(2000L);
        roleSkillIds = Arrays.asList(1990L);
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(2048).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(2).
                setWorkforceId(1024).setWorkgroupId(0).setRoleIds(roleIds).setSkillIds(skillIds).
                setRoleSkillIds(roleSkillIds).setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(2));

        // Verify for Case 3 Readings
        roleIds = Arrays.asList(2020L, 2019L, 2018L, 2017L);
        skillIds = Arrays.asList(2000L, 2001L);
        roleSkillIds = Arrays.asList(1990L, 1989L, 1991L);
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1133).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(3).
                setWorkforceId(1024).setWorkgroupId(0).setRoleIds(roleIds).setSkillIds(skillIds).
                setRoleSkillIds(roleSkillIds).setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(3));

        // Verify for Case 4 Readings
        roleIds = new ArrayList<>();
        skillIds = Arrays.asList(1998L, 2038L, 2249L);
        roleSkillIds = Arrays.asList(1990L, 1991L);
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000516).setPermissionSetId(1111).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(4).
                setWorkforceId(1024).setWorkgroupId(2048).setRoleIds(roleIds).setSkillIds(skillIds).
                setRoleSkillIds(roleSkillIds).setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(4));

        // Verify for Case 5 Readings
        roleIds = new ArrayList<>();
        skillIds = new ArrayList<>();
        roleSkillIds = Arrays.asList(1990L, 1991L, 1992L, 1993L);
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000516).setPermissionSetId(1111).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2001052).setPermissionSetId(1111).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(5).
                setWorkforceId(1024).setWorkgroupId(2048).setRoleIds(roleIds).setSkillIds(skillIds).
                setRoleSkillIds(roleSkillIds).setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(5));

        // Verify for Case 6 Readings
        roleIds = new ArrayList<>();
        skillIds = new ArrayList<>();
        roleSkillIds = new ArrayList<>();
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1111).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1112).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1113).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(6).
                setWorkforceId(1024).setWorkgroupId(1024).setRoleIds(roleIds).setSkillIds(skillIds).
                setRoleSkillIds(roleSkillIds).setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(6));

        // Verify for Case 7 Readings
        roleIds = Arrays.asList(1998L, 2038L, 2249L);
        skillIds = Arrays.asList(1998L, 2038L, 2249L);
        roleSkillIds = Arrays.asList(1998L, 2038L, 2249L);
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1098).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(1112).build());
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(2249).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(7).
                setWorkforceId(1024).setWorkgroupId(2048).setRoleIds(roleIds).setSkillIds(skillIds).
                setRoleSkillIds(roleSkillIds).setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(7));

        // Verify for Case 8 Readings
        roleIds = Arrays.asList(1998L, 2038L, 2249L);
        skillIds = Arrays.asList(1998L, 2038L, 2249L);
        roleSkillIds = Arrays.asList(1990L, 1999L, 1991L);
        expectedPoolAssignmentSet = new HashSet<>();
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(8).
                setWorkforceId(1024).setWorkgroupId(2048).setRoleIds(roleIds).setSkillIds(skillIds).
                setRoleSkillIds(roleSkillIds).setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(8));
    }

}
