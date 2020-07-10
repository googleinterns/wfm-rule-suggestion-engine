package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

/** DataProcessor class is used to filter out invalid or conflict data. */
public class DataProcessor {

  /** Filter out user data with invalid workgroup Id values(< 0). */
  public static ImmutableList<UserModel> filterValidData(
      ImmutableList<UserModel> rawData) {
    ImmutableList.Builder<UserModel> validDataBuilder =
        ImmutableList.<UserModel>builder();

    for (final UserModel data : rawData) {
      if (data.workgroupId() > 0) validDataBuilder.add(data);
    }
    return validDataBuilder.build();
  }
}
