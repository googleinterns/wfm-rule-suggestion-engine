package main.java.src.com.googleintern.wfm.ruleengine.action;

public interface RuleSuggestionService {

  //Returns a string style CSV.
  String suggestRules (String csvFilePath);

  String suggestRules (String csvFilePath, int percentage);
}
