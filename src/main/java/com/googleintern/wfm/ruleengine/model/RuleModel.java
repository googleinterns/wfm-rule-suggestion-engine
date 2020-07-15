package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

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

  public boolean isUserCoveredByRules(UserModel user) {
    if ((workforceId() != user.workforceId()) || (workgroupId() != user.workgroupId())) {
      return false;
    }
    for (ImmutableSet<FilterModel> orFilters : filters()) {
      if (Sets.intersection(ImmutableSet.copyOf(user.skillIds()), getSkillIdsFromFilters(orFilters))
              .isEmpty()
          && Sets.intersection(
                  ImmutableSet.copyOf(user.roleSkillIds()), getSkillIdsFromFilters(orFilters))
              .isEmpty()
          && Sets.intersection(
                  ImmutableSet.copyOf(user.roleIds()), getRoleIdsFromFilters(orFilters))
              .isEmpty()) {
        return false;
      }
    }
    return true;
  }

  private static ImmutableSet<Long> getSkillIdsFromFilters(ImmutableSet<FilterModel> filters) {
    return filters.stream()
        .filter(filer -> filer.type() == FilterModel.FilterType.SKILL)
        .map(filter -> filter.value())
        .collect(toImmutableSet());
  }

  private static ImmutableSet<Long> getRoleIdsFromFilters(ImmutableSet<FilterModel> filters) {
    return filters.stream()
        .filter(filer -> filer.type() == FilterModel.FilterType.ROLE)
        .map(filter -> filter.value())
        .collect(toImmutableSet());
  }
}
