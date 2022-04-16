package com.una.system.manager.dao;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.una.system.manager.model.Role;
import com.una.system.manager.pojo.req.SearchRole;

@Repository
public interface RoleDao extends BaseRepository<Role, String> {
  @Modifying
  @Query(nativeQuery = true, value = """
    DELETE FROM S_GROUP_ROLE WHERE ROLE_ID = ?1
    """)
  void deleteGroupRelation(String roleId);

  @Modifying
  @Query(nativeQuery = true, value = """
  DELETE FROM S_ROLE_PERM WHERE ROLE_ID = ?1
    """)
  void deletePermRelation(String roleId);

  @Modifying
  @Query(nativeQuery = true, value = """
    DELETE FROM S_USER_ROLE WHERE ROLE_ID = ?1
    """)
  void deleteUserRelation(String roleId);

  @Query(nativeQuery = true, value = """
    SELECT R.ID, R.COMPANY_ID, R.AUTHORITY, R.STATUS, R.REMARK, R.CREATED_USER_ID, R.CREATED_DATE, R.UPDATED_USER_ID, R.UPDATED_DATE
    FROM S_ROLE R INNER JOIN S_GROUP_ROLE GR ON GR.ROLE_ID = R.ID
                  INNER JOIN S_GROUP G ON G.ID = GR.GROUP_ID
    WHERE G.ID IN (?1) AND (?2 IS NULL OR R.STATUS = ?2) AND (?3 IS NULL OR R.COMPANY_ID = ?3)
    ORDER BY R.AUTHORITY
    """)
  Set<Role> findByGroup(Collection<String> groupIds, String roleStatus, String company);

  @Query(nativeQuery = true, value = """
    SELECT R.ID, R.COMPANY_ID, R.AUTHORITY, R.STATUS, R.REMARK, R.CREATED_USER_ID, R.CREATED_DATE, R.UPDATED_USER_ID, R.UPDATED_DATE
    FROM S_ROLE R INNER JOIN S_ROLE_PERM RP ON RP.ROLE_ID = R.ID
                  INNER JOIN S_PERMISSIONS P ON P.ID = RP.PERMISSIONS_ID
    WHERE P.ID IN (?1) AND R.COMPANY_ID = ?2
    ORDER BY R.AUTHORITY
    """)
  Set<Role> findByPerm(Collection<String> permIds, String companyId);

  @Query(nativeQuery = true, value = """
    SELECT R.ID, R.COMPANY_ID, R.AUTHORITY, R.STATUS, R.REMARK, R.CREATED_USER_ID, R.CREATED_DATE, R.UPDATED_USER_ID, R.UPDATED_DATE
    FROM S_ROLE R INNER JOIN S_USER_ROLE UR ON UR.ROLE_ID = R.ID
                  INNER JOIN S_USER U ON U.ID = UR.USER_ID
    WHERE U.ID IN (?1) AND (?2 IS NULL OR R.STATUS = ?2) AND (?3 IS NULL OR R.COMPANY_ID = ?3)
    ORDER BY R.AUTHORITY
    """)
  Set<Role> findByUser(Collection<String> userIds, String roleStatus, String companyId);

  @Modifying
  @Query(nativeQuery = true, value = """
  INSERT INTO S_GROUP_ROLE(ID, GROUP_ID, ROLE_ID, CREATED_USER_ID, CREATED_DATE)
  VALUES(?1, ?2, ?3, ?4, ?5);
    """)
  void saveGroupRelation(String id, String groupId, String roleId, String createdUserId, LocalDateTime dateTime);

  @Modifying
  @Query(nativeQuery = true, value = """
  INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
  VALUES(?1, ?2, ?3, ?4, ?5);
    """)
  void savePermRelation(String id, String groupId, String roleId, String createdUserId, LocalDateTime dateTime);

  @Modifying
  @Query(nativeQuery = true, value = """
    INSERT INTO S_USER_ROLE(ID, USER_ID, ROLE_ID, CREATED_USER_ID, CREATED_DATE)
    VALUES(?1, ?2, ?3, ?4, ?5);
    """)
  void saveUserRelation(String id, String userId, String roleId, String createdUserId, LocalDateTime dateTime);

  @Query(nativeQuery = false, value = """
    SELECT r, c, cu, uu
    FROM Role r INNER JOIN Company c ON r.companyId = c.id
                LEFT JOIN User cu ON cu.id = r.createdUserId
                LEFT JOIN User uu ON uu.id = r.updatedUserId
    WHERE (:allow = TRUE OR r.companyId = :id)
          AND (:#{#sr.authority} IS NULL OR UPPER(r.authority) LIKE UPPER(:#{#sr.authority}))
          AND (:#{#sr.companyName} IS NULL OR UPPER(c.name) LIKE UPPER(:#{#sr.companyName}))
          AND (:#{#sr.status} IS NULL OR r.status = :#{#sr.status})
          AND (:#{#sr.companyId} IS NULL OR r.companyId = :#{#sr.companyId})
          AND (:#{#sr.eqAuthority} IS NULL OR (UPPER(r.authority) = UPPER(:#{#sr.eqAuthority}) AND :#{#sr.companyId} = r.companyId))
    """)
  Page<Object[]> search(@Param("sr") SearchRole searchRole, @Param("id") String companyId, @Param("allow") boolean allowNotCompanyCond, Pageable pageable);

}
