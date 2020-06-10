package src.test.java.com.googleintern.wfm.ruleengine;

import com.opencsv.exceptions.CsvException;
import org.junit.Assert;
import src.main.java.com.googleintern.wfm.ruleengine.action.CsvParser;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CsvParserTest {
    public static void main(String[] args) throws IOException, CsvException {
        final String testCsvFilePath = "/usr/local/google/home/qintonghan/Project/wfm-rule-suggestion-engine/src/" +
                "test/resources/com/googleintern/wfm/ruleengine/csv_parser_test_data.csv";
        CsvParser csvParser = new CsvParser(testCsvFilePath);
        System.out.println("Start Reading");
        CsvParser.ReadFromCSVFile();
        System.out.println("Finish Reading");

        List<UserPoolAssignmentModel> userPoolAssignmentList = csvParser.userPoolAssignmentList;

        System.out.println("Start Testing");

        // Test for case 0
        Set<FilterModel> expectedFilterSet = new HashSet<>();
        Set<PoolAssignmentModel> expectedPoolAssignmentSet = new HashSet<>();
        UserPoolAssignmentModel expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(0).
                setWorkforceId(1024).setWorkgroupId(0).setFilters(expectedFilterSet).
                setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(0));

        // Test for case 1
        expectedFilterSet = new HashSet<>();
        expectedFilterSet.add(FilterModel.builder().setFilterType(1).setIdValue(2020).build());
        expectedPoolAssignmentSet = new HashSet<>();
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(1).
                setWorkforceId(1024).setWorkgroupId(0).setFilters(expectedFilterSet).
                setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(1));

        // Test for case 2
        expectedFilterSet = new HashSet<>();
        expectedFilterSet.add(FilterModel.builder().setFilterType(1).setIdValue(2020).build());
        expectedFilterSet.add(FilterModel.builder().setFilterType(1).setIdValue(2019).build());
        expectedFilterSet.add(FilterModel.builder().setFilterType(1).setIdValue(2018).build());
        expectedFilterSet.add(FilterModel.builder().setFilterType(2).setIdValue(2020).build());
        expectedFilterSet.add(FilterModel.builder().setFilterType(3).setIdValue(1990).build());
        expectedPoolAssignmentSet = new HashSet<>();
        expectedPoolAssignmentSet.add(PoolAssignmentModel.builder().setCasePoolId(2000543).setPermissionSetId(2048).build());
        expectedUserPoolAssignmentValue = UserPoolAssignmentModel.builder().setUserId(2).
                setWorkforceId(1024).setWorkgroupId(0).setFilters(expectedFilterSet).
                setPoolAssignments(expectedPoolAssignmentSet).build();
        Assert.assertEquals(expectedUserPoolAssignmentValue, userPoolAssignmentList.get(2));

        System.out.println("End Testing");
    }

}
