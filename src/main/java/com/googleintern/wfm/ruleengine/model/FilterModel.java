package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class FilterModel {
    enum FilterTypes{
        ROLEID,
        SKILL,
        ROLESKILL,
    }
    public abstract FilterTypes type();
    public abstract long value();

    public static Builder builder() {
        return new AutoValue_FilterModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setType(FilterTypes filterType);
        public abstract Builder setValue(long idValue);
        public abstract FilterModel build();
    }
}
