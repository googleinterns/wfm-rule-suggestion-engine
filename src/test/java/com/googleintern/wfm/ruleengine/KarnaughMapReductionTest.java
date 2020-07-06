package src.test.java.com.googleintern.wfm.ruleengine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import src.main.java.com.googleintern.wfm.ruleengine.action.KarnaughMapReduction;

/**
 * KarnaughMapReductionTest class is used to test the functionality of the KarnaughMapReduction
 * class.
 */
public class KarnaughMapReductionTest {

  /** Constant variables for Case 0: Have even number of variables in each term. */
  private static final ImmutableSet<ImmutableList<Integer>> ALL_ZERO_TERMS_Case_0 =
      ImmutableSet.of(
          ImmutableList.of(0, 0, 0, 0),
          ImmutableList.of(0, 0, 0, 1),
          ImmutableList.of(0, 0, 1, 1),
          ImmutableList.of(0, 1, 0, 0),
          ImmutableList.of(0, 1, 0, 1),
          ImmutableList.of(1, 0, 1, 1));

  private static final ImmutableSet<ImmutableList<Integer>> EXPECTED_MINIMIZED_TERMS_Case_0 =
      ImmutableSet.of(
          ImmutableList.of(0, -1, 0, -1),
          ImmutableList.of(0, 0, -1, 1),
          ImmutableList.of(-1, 0, 1, 1));

  /** Constant variables for Case 1: Have odd number of variables in each term. */
  private static final ImmutableSet<ImmutableList<Integer>> ALL_ZERO_TERMS_Case_1 =
      ImmutableSet.of(
          ImmutableList.of(0, 0, 0),
          ImmutableList.of(0, 0, 1),
          ImmutableList.of(0, 1, 0),
          ImmutableList.of(1, 0, 0),
          ImmutableList.of(0, 1, 1),
          ImmutableList.of(1, 1, 1));

  private static final ImmutableSet<ImmutableList<Integer>> EXPECTED_MINIMIZED_TERMS_Case_1 =
      ImmutableSet.of(
          ImmutableList.of(0, -1, -1), ImmutableList.of(-1, 0, 0), ImmutableList.of(-1, 1, 1));

  /** Constant variables for Case 2: Have terms with ten(large number) of variables. */
  private static final ImmutableSet<ImmutableList<Integer>> ALL_ZERO_TERMS_Case_2 =
      ImmutableSet.of(
          ImmutableList.of(0, 0, 0, 0, 0, 0, 0, 0, 1, 0),
          ImmutableList.of(0, 0, 0, 0, 0, 0, 0, 0, 1, 1),
          ImmutableList.of(0, 0, 1, 1, 0, 0, 0, 0, 0, 0),
          ImmutableList.of(0, 0, 1, 0, 0, 0, 0, 0, 0, 0),
          ImmutableList.of(1, 0, 0, 0, 0, 1, 1, 0, 0, 1),
          ImmutableList.of(1, 0, 0, 0, 0, 1, 0, 0, 0, 1),
          ImmutableList.of(1, 0, 0, 0, 0, 1, 1, 0, 0, 0),
          ImmutableList.of(1, 0, 0, 0, 0, 1, 0, 0, 0, 1),
          ImmutableList.of(0, 0, 0, 0, 0, 1, 1, 0, 0, 1));

  private static final ImmutableSet<ImmutableList<Integer>> EXPECTED_MINIMIZED_TERMS_Case_2 =
      ImmutableSet.of(
          ImmutableList.of(0, 0, 0, 0, 0, 0, 0, 0, 1, -1),
          ImmutableList.of(0, 0, 1, -1, 0, 0, 0, 0, 0, 0),
          ImmutableList.of(1, 0, 0, 0, 0, 1, -1, 0, 0, 1),
          ImmutableList.of(1, 0, 0, 0, 0, 1, 1, 0, 0, -1),
          ImmutableList.of(-1, 0, 0, 0, 0, 1, 1, 0, 0, 1));

  @Test
  public void minimizeKMapTermsWithEvenNumberOfVariablesTest() {
    ImmutableSet<ImmutableList<Integer>> minimizedTermsCase0 =
        KarnaughMapReduction.minimizeKMapTerms(ALL_ZERO_TERMS_Case_0);
    Assert.assertTrue(minimizedTermsCase0.equals(EXPECTED_MINIMIZED_TERMS_Case_0));
  }

  @Test
  public void minimizeKMapTermsWithOddNumberOfVariablesTest() {
    ImmutableSet<ImmutableList<Integer>> minimizedTermsCase1 =
        KarnaughMapReduction.minimizeKMapTerms(ALL_ZERO_TERMS_Case_1);
    Assert.assertTrue(minimizedTermsCase1.equals(EXPECTED_MINIMIZED_TERMS_Case_1));
  }

  @Test
  public void minimizeKMapTermsWithTenNumberOfVariablesTest() {
    ImmutableSet<ImmutableList<Integer>> minimizedTermsCase2 =
        KarnaughMapReduction.minimizeKMapTerms(ALL_ZERO_TERMS_Case_2);
    Assert.assertEquals(EXPECTED_MINIMIZED_TERMS_Case_2, minimizedTermsCase2);
    Assert.assertTrue(minimizedTermsCase2.equals(EXPECTED_MINIMIZED_TERMS_Case_2));
  }
}
