package java.src.com.googleintern.wfm.ruleengine.module;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Set;

@AutoValue
public abstract class Rule {
    public abstract int workgroupId();
    public abstract int casePoolId();
    public abstract ImmutableSet<Integer> permissionSetIds();
    public abstract ImmutableList<ImmutableSet<Filter>> filters();

    public static Builder builder() {
        return new AutoValue_Rule.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder{
        public abstract Builder setWorkgroupId(int workgroupIdValue);
        public abstract Builder setCasePoolId(int casePoolIdValue);
        public abstract Builder setPermissionSetIds(Set<Integer> permissionSetIdsValue);
        public abstract Builder setFilters(List<ImmutableSet<Filter>> filtersValue);
        public abstract Rule build();
    }
}