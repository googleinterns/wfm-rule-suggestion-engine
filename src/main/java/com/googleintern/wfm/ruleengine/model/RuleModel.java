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

  enum Separator {
    SEMICOLON(";"),
    COMMA(","),
    SQUARE_BRACKET_LEFT("["),
    SQUARE_BRACKET_RIGHT("]"),
    CURLY_BRACKET_LEFT("{"),
    CURLY_BRACKET_RIGHT("}");

    final String symbol;

    Separator(String symbol) {
      this.symbol = symbol;
    }
  }

  private static final String SKILL_ID_PREFIX = "skill_id:";
  private static final String ROLE_ID_PREFIX = "role_id:";

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

  public String[] convertRuleToCsvRow() {
    String ruleId = Long.toString(ruleId());
    String workforceId = Long.toString(workforceId());
    String workgroupId = Long.toString(workgroupId());
    String casePoolId = Long.toString(casePoolId());
    String permissionIds = convertPermissionSetIdsToCsvString(permissionSetIds());
    String filterIds = convertFilterIdsToCsvString(filters());
    return new String[] {ruleId, workforceId, workgroupId, casePoolId, permissionIds, filterIds};
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

  private static String convertPermissionSetIdsToCsvString(ImmutableSet<Long> permissionSetIds) {
    StringBuilder permissionIdsBuilder = new StringBuilder(Separator.SQUARE_BRACKET_LEFT.symbol);
    for (Long permissionSetId : permissionSetIds) {
      if (permissionIdsBuilder.length() > 1) {
        permissionIdsBuilder.append(Separator.COMMA.symbol);
      }
      permissionIdsBuilder.append(permissionSetId);
    }
    permissionIdsBuilder.append(Separator.SQUARE_BRACKET_RIGHT.symbol);
    return permissionIdsBuilder.toString();
  }

  private static String convertFilterIdsToCsvString(List<ImmutableSet<FilterModel>> filters) {
    StringBuilder filterIdsBuilder = new StringBuilder(Separator.SQUARE_BRACKET_LEFT.symbol);
    for (final ImmutableSet<FilterModel> filterSet : filters) {
      if (filterIdsBuilder.length() > 1) {
        filterIdsBuilder.append(Separator.SEMICOLON.symbol);
      }
      StringBuilder currFilterIdsBuilder = new StringBuilder();
      for (final FilterModel filter : filterSet) {
        currFilterIdsBuilder.append(
            currFilterIdsBuilder.length() > 0
                ? Separator.COMMA.symbol
                : Separator.CURLY_BRACKET_LEFT.symbol);
        if (filter.type() == FilterModel.FilterType.SKILL) {
          currFilterIdsBuilder.append(SKILL_ID_PREFIX);
        } else if (filter.type() == FilterModel.FilterType.ROLE) {
          currFilterIdsBuilder.append(ROLE_ID_PREFIX);
        }
        currFilterIdsBuilder.append(filter.value());
      }
      filterIdsBuilder.append(
          currFilterIdsBuilder.toString().isEmpty()
              ? ""
              : currFilterIdsBuilder.toString() + Separator.CURLY_BRACKET_RIGHT.symbol);
    }
    filterIdsBuilder.append(Separator.SQUARE_BRACKET_RIGHT.symbol);
    return filterIdsBuilder.toString();
  }
}
