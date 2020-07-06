package src.main.java.com.googleintern.wfm.ruleengine.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

/**
 * KarnaughMapComparisionResultModel class is used to store information about minimized results from
 * the KarnaughMapReduction class.
 */
@AutoValue
public abstract class KarnaughMapComparisionResultModel {
  public abstract ImmutableList<ImmutableList<Integer>> minimizedResults();

  public abstract ImmutableSet<ImmutableList<Integer>> minimizedTerms();

  public static Builder builder() {
    return new AutoValue_KarnaughMapComparisionResultModel.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setMinimizedResults(List<ImmutableList<Integer>> minimizedResults);

    public abstract Builder setMinimizedTerms(Set<ImmutableList<Integer>> minimizedTerms);

    public abstract KarnaughMapComparisionResultModel build();
  }
}
