package main.java.src.com.googleintern.wfm.ruleengine.module;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PoolAssignment {
    public abstract int casePoolId();
    public abstract int permissionSetId();

    public static Builder builder() {
        return new AutoValue_PoolAssignment.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setCasePoolId(int casePoolIdValue);
        public abstract Builder setPermissionSetId(int permissionSetIdValue);
        public abstract PoolAssignment build();
    }
}