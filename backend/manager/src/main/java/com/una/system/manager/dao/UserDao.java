package com.una.system.manager.dao;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.una.system.manager.model.User;
import com.una.system.manager.pojo.req.SearchUser;

@Repository("userDao")
public interface UserDao extends BaseRepository<User, String> {
  @Modifying
  @Query(nativeQuery = true, value = """
    DELETE FROM S_USER_GROUP WHERE USER_ID = ?1
    """)
  void deleteGroupRelation(String userId);

  @Modifying
  @Query(nativeQuery = true, value = """
    DELETE FROM S_USER_ROLE WHERE USER_ID = ?1
    """)
  void deleteRoleRelation(String userId);

  @Query(nativeQuery = true, value = """
    SELECT U.ID, U.COMPANY_ID, U.USERNAME, U.PASSWORD, U.ACCOUNT_NON_EXPIRED, U.ACCOUNT_NON_LOCKED, U.CREDENTIALS_NON_EXPIRED, U.DEFAULT_GROUP_ID, U.LAST_CHANGE_PW_DATE, U.LAST_LOGIN_DATE, U.FAILURE_COUNT, U.CREATED_USER_ID, U.CREATED_DATE, U.UPDATED_USER_ID, U.UPDATED_DATE
    FROM S_USER U INNER JOIN S_USER_GROUP UG ON UG.USER_ID = U.ID
                  INNER JOIN S_GROUP G ON G.ID = UG.GROUP_ID
    WHERE G.ID IN (?1) AND (?2 IS NULL OR U.COMPANY_ID = ?2)
    ORDER BY U.USERNAME
    """)
  Set<User> findByGroup(Collection<String> groupIds, String companyId);

  @Query(nativeQuery = true, value = """
    SELECT U.ID, U.COMPANY_ID, U.USERNAME, U.PASSWORD, U.ACCOUNT_NON_EXPIRED, U.ACCOUNT_NON_LOCKED, U.CREDENTIALS_NON_EXPIRED, U.DEFAULT_GROUP_ID, U.LAST_CHANGE_PW_DATE, U.LAST_LOGIN_DATE, U.FAILURE_COUNT, U.CREATED_USER_ID, U.CREATED_DATE, U.UPDATED_USER_ID, U.UPDATED_DATE
    FROM S_USER U INNER JOIN S_USER_ROLE UR ON UR.USER_ID = U.ID
                  INNER JOIN S_ROLE R ON R.ID = UR.ROLE_ID
    WHERE R.ID IN (?1) AND (?2 IS NULL OR U.COMPANY_ID = ?2)
    ORDER BY U.USERNAME
    """)
  Set<User> findByRole(Collection<String> userIds, String companyId);

  //  @Query(nativeQuery = false, value = "SELECT U FROM User U WHERE UPPER(U.username) = UPPER(?1)")
  Optional<User> findByUsername(String name);

  @Modifying
  @Query(nativeQuery = true, value = """
    INSERT INTO S_USER_GROUP(ID, USER_ID, GROUP_ID, CREATED_USER_ID, CREATED_DATE)
    VALUES(?1, ?2, ?3, ?4, ?5);
    """)
  void saveGroupRelation(String id, String userId, String groupId, String createdUserId, LocalDateTime dateTime);

  @Modifying
  @Query(nativeQuery = true, value = """
  INSERT INTO S_USER_ROLE(ID, USER_ID, ROLE_ID, CREATED_USER_ID, CREATED_DATE)
  VALUES(?1, ?2, ?3, ?4, ?5);
    """)
  void saveRoleRelation(String id, String userId, String roleId, String createdUserId, LocalDateTime dateTime);

  @Modifying
  @Query(nativeQuery = true, value = """
    INSERT INTO S_USER(ID, COMPANY_ID, USERNAME, PASSWORD, ACCOUNT_NON_EXPIRED, ACCOUNT_NON_LOCKED, CREDENTIALS_NON_EXPIRED, DEFAULT_GROUP_ID, LAST_LOGIN_DATE, LAST_CHANGE_PW_DATE,
                      FAILURE_COUNT, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE)
    VALUES(:#{#u.id}, :#{#u.companyId}, :#{#u.username}, '', :#{#u.accountNonExpired ? 'Y' : 'N'}, :#{#u.accountNonLocked ? 'Y' : 'N'}, :#{#u.credentialsNonExpired ? 'Y' : 'N'},
           :#{#u.defaultGroupId}, NULL, NULL, :#{#u.failureCount}, :#{#u.createdUserId}, :#{#u.createdDate}, :#{#u.updatedUserId}, :#{#u.updatedDate})
    """)
  void saveUser(@Param("u") User user);

  @Query(nativeQuery = false, value = """
    SELECT u, c, p, g, cu, uu
    FROM User u INNER JOIN Company c ON u.companyId = c.id
                LEFT JOIN UserProfile p ON u.id = p.userId
                LEFT JOIN Group g ON u.defaultGroupId = g.id
                LEFT JOIN User cu ON cu.id = u.createdUserId
                LEFT JOIN User uu ON uu.id = u.updatedUserId
    WHERE (:allow = TRUE OR u.companyId = :id)
          AND (:#{#su.name} IS NULL OR UPPER(u.username) LIKE UPPER(:#{#su.name}))
          AND (:#{#su.companyName} IS NULL OR UPPER(c.name) LIKE UPPER(:#{#su.companyName}))
          AND (:#{#su.realName} IS NULL OR UPPER(p.realName) LIKE UPPER(:#{#su.realName}))
          AND (:#{#su.eqName} IS NULL OR UPPER(u.username) = UPPER(:#{#su.eqName}))
          AND (:#{#su.companyId} IS NULL OR u.companyId = :#{#su.companyId})
    """)
  Page<Object[]> search(@Param("su") SearchUser searchUser, @Param("id") String companyId, @Param("allow") boolean allowNotCompanyCond, Pageable pageable);
}
