<<<<<<< HEAD:src/main/java/com/googleintern/wfm/ruleengine/action/RuleSuggestionService.java
package src.main.java.com.googleintern.wfm.ruleengine.action;
=======
package java.src.com.googleintern.wfm.ruleengine.action;
>>>>>>> 8be7a5e... Initial Contribution:java/src/com/googleintern/wfm/ruleengine/action/RuleSuggestionService.java

public interface RuleSuggestionService {

  //Returns a string style CSV.
  String suggestRules (String csvFilePath);

  String suggestRules (String csvFilePath, int percentage);
}
