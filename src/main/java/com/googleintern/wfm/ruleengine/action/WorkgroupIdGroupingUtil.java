package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.*;
import src.main.java.com.googleintern.wfm.ruleengine.model.FilterModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.PoolAssignmentModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.RuleModel;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserPoolAssignmentModel;

import java.util.List;
import java.util.Set;

/**
 * GroupByWorkgroupId class is used to group data by their workgroup Id value and generate possible
 * general rules for the same workgroup.
 */
public class WorkgroupIdGroupingUtil {
  /** Group data by workgroup Id. */
  public static ImmutableListMultimap<Long, UserPoolAssignmentModel> groupByWorkGroupId(
      ImmutableList<UserPoolAssignmentModel> validData) {
    ImmutableListMultimap.Builder<Long, UserPoolAssignmentModel>
        userPoolAssignmentsByWorkGroupIdBuilder = ImmutableListMultimap.builder();
    for (UserPoolAssignmentModel data : validData) {
      userPoolAssignmentsByWorkGroupIdBuilder.put(data.workgroupId(), data);
    }
    return userPoolAssignmentsByWorkGroupIdBuilder.build();
  }
}
