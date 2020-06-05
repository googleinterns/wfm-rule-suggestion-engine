package java.src.com.googleintern.wfm.ruleengine.module;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Filter {
    public abstract int filterType();
    public abstract int idValue();

    public static Builder builder() {
        return new AutoValue_Filter.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setFilterType(int filterType);
        public abstract Builder setIdValue(int idValue);
        public abstract Filter build();
    }
}
