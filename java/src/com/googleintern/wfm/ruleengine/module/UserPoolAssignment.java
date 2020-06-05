package java.src.com.googleintern.wfm.ruleengine.module;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

@AutoValue
public abstract class UserPoolAssignment {
    public abstract int userId();

    public abstract int workforceId();

    public abstract int workgroupId();

    public abstract ImmutableSet<Filter> filters();

    public abstract ImmutableSet<PoolAssignment> poolAssignments();

    public static Builder builder() {
        return new AutoValue_UserPoolAssignment.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setUserId(int userIdValue);

        public abstract Builder setWorkforceId(int workforceIdValue);

        public abstract Builder setWorkgroupId(int workgroupIdValue);

        public abstract Builder setFilters(Set<Filter> filtersValue);

        public abstract Builder setPoolAssignments(Set<PoolAssignment> poolAssignmentSetValue);

        public abstract UserPoolAssignment build();
    }
}
