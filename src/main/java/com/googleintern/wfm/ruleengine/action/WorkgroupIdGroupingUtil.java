package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.*;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

import java.util.List;

/**
 * WorkgroupIdGroupingUtil class is used to group data by their workgroup Id value and generate
 * possible general rules for the same workgroup.
 */
public class WorkgroupIdGroupingUtil {
  /** Group data by workgroup Id. */
  public static ImmutableListMultimap<Long, UserModel> groupByWorkGroupId(
      List<UserModel> validData) {
    ImmutableListMultimap.Builder<Long, UserModel> userPoolAssignmentsByWorkGroupIdBuilder =
        ImmutableListMultimap.builder();
    for (UserModel data : validData) {
      userPoolAssignmentsByWorkGroupIdBuilder.put(data.workgroupId(), data);
    }
    return userPoolAssignmentsByWorkGroupIdBuilder.build();
  }
}
