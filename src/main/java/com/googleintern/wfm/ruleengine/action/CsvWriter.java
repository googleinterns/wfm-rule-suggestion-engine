package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.opencsv.CSVWriter;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvWriter {
    enum Header{
        RULE_ID(0),
        WORKFORCE_ID(1),
        WORKGROUP_ID(2),
        CASE_POOL_ID(3),
        PERMISSION_SET_ID(4),
        ROLE_ID(5),
        SKILL_ID(6),
       ROLE_SKILL_ID(7);

        final int column;
        Header(int column){
            this.column = column;
        }
    }

    private static final String OUTPUT_CSV_FILE_NAME = "Generated Rules" + LocalDateTime.now().toString() + ".csv";

    public static void writeDataIntoCsvFile(String outputCsvFileLocation, ImmutableList<RuleModel> rules) throws IOException {
        String filePath = outputCsvFileLocation + OUTPUT_CSV_FILE_NAME;
        File file = new File(filePath);

        // create FileWriter object with file as parameter
        FileWriter outputfile = new FileWriter(file);

        // create CSVWriter object filewriter object as parameter
        CSVWriter writer = new CSVWriter(outputfile);

        // create a List which contains String array
        List<String[]> data = new ArrayList<String[]>();
        data.add(new String[] { "Name", "Class", "Marks" });
        data.add(new String[] { "Aman", "10", "620" });
        data.add(new String[] { "Suraj", "10", "630" });
        writer.writeAll(data);

        // closing writer connection
        writer.close();
    }

  public static void main(String[] args) {
    writeDataIntoCsvFile();
  }
}
