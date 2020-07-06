package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;

/**
 * Store information about a filter for a Rule object.
 *
 * <p>Enum variable type represents the type of filter(ROLE, SKILL, ROLESKILL). Long variable value
 * represents the value of ID for a filter.
 */
@AutoValue
public abstract class FilterModel {
  public enum FilterType {
    ROLE,
    SKILL,
  }

  public abstract FilterType type();

  public abstract long value();

  public static Builder builder() {
    return new AutoValue_FilterModel.Builder();
  }

  /** Builder class is used to set variables and create an instance of FilterModel class. */
  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setType(FilterType filterType);

    public abstract Builder setValue(long value);

    public abstract FilterModel build();
  }
}
