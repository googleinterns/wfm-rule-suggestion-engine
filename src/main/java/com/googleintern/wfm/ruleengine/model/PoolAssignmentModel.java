package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;

/** PoolAssignment class is used to store information about one pool assignment from input. */
@AutoValue
public abstract class PoolAssignmentModel {
  public abstract long casePoolId();

  public abstract long permissionSetId();

  public static Builder builder() {
    return new AutoValue_PoolAssignmentModel.Builder();
  }

  /** Builder class is used to set variables and create an instance of PoolAssignment class. */
  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setCasePoolId(long casePoolId);

    public abstract Builder setPermissionSetId(long permissionSetId);

    public abstract PoolAssignmentModel build();
  }
}
