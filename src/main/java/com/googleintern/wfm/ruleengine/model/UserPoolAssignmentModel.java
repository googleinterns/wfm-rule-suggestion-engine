package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

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

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setUserId(long userIdValue);

        public abstract Builder setWorkforceId(long workforceIdValue);

        public abstract Builder setWorkgroupId(long workgroupIdValue);

        public abstract Builder setRoleIds(List<Long> roleIdValues);

        public abstract Builder setSkillIds(List<Long> skillIdValues);

        public abstract Builder setRoleSkillIds(List<Long> roleSkillIdValues);

        public abstract Builder setPoolAssignments(Set<PoolAssignmentModel> poolAssignmentSetValue);

        public abstract UserPoolAssignmentModel build();
    }
}
