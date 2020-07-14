package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

/** RuleModel class is used to store detailed information about a newly generated rule. */
@AutoValue
public abstract class RuleModel {
  public abstract long ruleId();

  public abstract long workforceId();

  public abstract long workgroupId();

  public abstract long casePoolId();

  public abstract ImmutableSet<Long> permissionSetIds();

  public abstract ImmutableList<ImmutableSet<FilterModel>> filters();

  public static Builder builder() {
    return new AutoValue_RuleModel.Builder();
  }

  /** Builder class is used to set variables and create an instance for RuleModel class. */
  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setRuleId(long ruleId);

    public abstract Builder setWorkforceId(long workforceId);

    public abstract Builder setWorkgroupId(long workgroupId);

    public abstract Builder setCasePoolId(long casePoolId);

    public abstract Builder setPermissionSetIds(Set<Long> permissionSetIds);

    public abstract Builder setFilters(List<ImmutableSet<FilterModel>> filters);

    public abstract RuleModel build();
  }
}
