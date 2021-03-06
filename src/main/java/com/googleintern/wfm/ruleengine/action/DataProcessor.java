package src.main.java.com.googleintern.wfm.ruleengine.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import src.main.java.com.googleintern.wfm.ruleengine.model.UserModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.toSet;

/** DataProcessor class is used to filter out conflict data. */
public class DataProcessor {
  /**
   * Filter out conflict user data. Conflict users are users with less role/skill ids but more
   * assigned permissions. This function is used when we want final generated rules to assign less
   * permissions in conflict cases.
   *
   * <p>Example for conflict users: User0, 1, 2 are from the same workforce and the same workgroup.
   * User 0 has {role id = 1111, skill ids = 2222, 3333} and is assigned permissions = {AAAA, BBBB}.
   * User 1 has {role id = 1111, skill ids = 2222} and is assigned permissions = {AAAA, BBBB, CCCC}.
   * User 2 has {role id = 1111, skill ids = 2222} and is assigned permissions = {AAAA, CCCC}. Both
   * User 1 and 2 are conflict users with respect to User 0. Types of Skill/role ids for User 1 and
   * 2 are included in the types for User 0. However, User 1 and 2 have a different permission CCCC
   * that is not included in User 0.
   */
  public static ImmutableList<UserModel> removeConflictUsers(List<UserModel> rawUserData) {
    ImmutableSet<Long> coveredConflictUsers = findConflictUserIdPairs(rawUserData);
    return rawUserData.stream()
        .filter(user -> !coveredConflictUsers.contains(user.userId()))
        .collect(toImmutableList());
  }

  private static ImmutableSet<Long> findConflictUserIdPairs(List<UserModel> rawUserData) {
    Set<Long> dirtyUsers = new HashSet<Long>();

    for (UserModel currentUser : rawUserData) {
      if (dirtyUsers.contains(currentUser.userId())) {
        continue;
      }
      dirtyUsers.addAll(
          rawUserData.stream()
              .filter(
                  comparedUser ->
                      !dirtyUsers.contains(comparedUser.userId())
                          && currentUser.isAConflictUser(comparedUser))
              .map(UserModel::userId)
              .collect(toSet()));
    }
    return ImmutableSet.copyOf(dirtyUsers);
  }
}
