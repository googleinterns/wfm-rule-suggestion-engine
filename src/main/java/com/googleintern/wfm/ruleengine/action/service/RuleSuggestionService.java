package src.main.java.com.googleintern.wfm.ruleengine.action.service;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;

public interface RuleSuggestionService {

  //Returns a string style CSV.
  String suggestRules (String csvFilePath) throws IOException, CsvException;

  String suggestRules (String csvFilePath, int percentage);
}
