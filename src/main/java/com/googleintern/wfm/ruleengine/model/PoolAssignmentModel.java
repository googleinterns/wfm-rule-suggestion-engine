package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PoolAssignmentModel {
    public abstract long casePoolId();
    public abstract long permissionSetId();

    public static Builder builder() {
        return new AutoValue_PoolAssignmentModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setCasePoolId(long casePoolIdValue);
        public abstract Builder setPermissionSetId(long permissionSetIdValue);
        public abstract PoolAssignmentModel build();
    }
}