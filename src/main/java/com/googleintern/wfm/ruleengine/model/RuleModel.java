package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Set;

@AutoValue
public abstract class RuleModel {
    public abstract long workforceId();
    public abstract long workgroupId();
    public abstract long casePoolId();
    public abstract ImmutableSet<Long> permissionSetIds();
    public abstract ImmutableList<ImmutableSet<FilterModel>> filters();

    public static Builder builder() {
        return new AutoValue_RuleModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder{
        public abstract Builder setWorkforceId(long workforceIdValue);
        public abstract Builder setWorkgroupId(long workgroupIdValue);
        public abstract Builder setCasePoolId(long casePoolIdValue);
        public abstract Builder setPermissionSetIds(Set<Long> permissionSetIdsValue);
        public abstract Builder setFilters(List<ImmutableSet<FilterModel>> filtersValue);
        public abstract RuleModel build();
    }
}