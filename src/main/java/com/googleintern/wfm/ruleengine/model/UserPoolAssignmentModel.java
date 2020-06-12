package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

/**
 * UserPoolAssignmentModel class is used to store a row of data from input file.
 */
@AutoValue
public abstract class UserPoolAssignmentModel {
    public abstract long userId();

    public abstract long workforceId();

    public abstract long workgroupId();

    public abstract ImmutableList<Long> roleIds();

    public abstract ImmutableList<Long> skillIds();

    public abstract ImmutableList<Long> roleSkillIds();

    public abstract ImmutableSet<PoolAssignmentModel> poolAssignments();

    public static Builder builder() {
        return new AutoValue_UserPoolAssignmentModel.Builder();
    }

    /**
     * Builder class is used to set variables and create an instance for UserPoolAssignmentModel class.
     */
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setUserId(long userId);

        public abstract Builder setWorkforceId(long workforceId);

        public abstract Builder setWorkgroupId(long workgroupId);

        public abstract Builder setRoleIds(List<Long> roleIds);

        public abstract Builder setSkillIds(List<Long> skillIds);

        public abstract Builder setRoleSkillIds(List<Long> roleSkillIds);

        public abstract Builder setPoolAssignments(Set<PoolAssignmentModel> poolAssignments);

        public abstract UserPoolAssignmentModel build();
    }
}
