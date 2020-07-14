package src.main.java.com.googleintern.wfm.ruleengine.action.generator;

/**
 * RuleIdGenerator class is used to generate a valid rule ID for {@link
 * src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel}.
 */
public class RuleIdGenerator {
  public long ruleId;

  public RuleIdGenerator() {
    this.ruleId = 0L;
  }

  public long getRuleId(){
      return ruleId++;
  }
}
