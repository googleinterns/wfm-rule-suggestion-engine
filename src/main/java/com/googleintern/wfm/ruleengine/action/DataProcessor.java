package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

import static com.google.common.collect.ImmutableList.toImmutableList;

/** DataProcessor class is used to filter out invalid or conflict data. */
public class DataProcessor {

  /** Filter out user data with invalid workgroup Id values(< 0). */
  public static ImmutableList<UserModel> filterUsersWithValidWorkgroupId(
      ImmutableList<UserModel> rawData) {
    ImmutableList.Builder<UserModel> validDataBuilder = ImmutableList.<UserModel>builder();

    for (final UserModel data : rawData) {
      if (data.workgroupId() > 0) validDataBuilder.add(data);
    }
    return validDataBuilder.build();
  }

  public static void printConflictUserPairs(ImmutableSetMultimap<Long, Long> conflictUserPairs) {
    for (Long userId : conflictUserPairs.keySet()) {
      StringBuilder conflictRow = new StringBuilder();
      conflictRow.append(userId + "  [");
      ImmutableSet<Long> conflictUserIds = conflictUserPairs.get(userId);
      for (Long conflictId : conflictUserIds) {
        conflictRow.append(conflictId + "   ");
      }
      conflictRow.append("]");
      System.out.println(conflictRow.toString());
    }
  }

  public static ImmutableList<UserModel> removeConflictUsers(
      ImmutableList<UserModel> rawUserData) {
    ImmutableSet<Long> coveredConflictUsers =
        collectAllCoveredConflictUserIds(findConflictUserIdPairs(rawUserData));
    return rawUserData.stream()
        .filter(user -> !coveredConflictUsers.contains(user.userId()))
        .collect(toImmutableList());
  }

  private static ImmutableSet<Long> collectAllCoveredConflictUserIds(
      ImmutableSetMultimap<Long, Long> conflictUserPairs) {
    ImmutableSet.Builder<Long> coveredConflictUsers = ImmutableSet.builder();
    conflictUserPairs
        .keySet()
        .forEach(key -> coveredConflictUsers.addAll(conflictUserPairs.get(key)));
    return coveredConflictUsers.build();
  }

  /**
   * key userId: filter{a, b, c}, permission{1, 2} value userId: filter{a, b}, permission{1, 2, 3}
   */
  private static ImmutableSetMultimap<Long, Long> findConflictUserIdPairs(
      ImmutableList<UserModel> rawUserData) {
    ImmutableSetMultimap.Builder<Long, Long> conflictUserPairsBuilder =
        ImmutableSetMultimap.builder();
    rawUserData.forEach(
        currentUser ->
            conflictUserPairsBuilder.putAll(
                currentUser.userId(), currentUser.findConflictUsers(rawUserData)));
    return conflictUserPairsBuilder.build();
  }
}
