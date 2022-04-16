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

import com.una.system.manager.model.Group;
import com.una.system.manager.pojo.req.SearchGroup;

@Repository
public interface GroupDao extends BaseRepository<Group, String> {
  @Modifying
  @Query(nativeQuery = true, value = """
    DELETE FROM S_GROUP_ROLE WHERE GROUP_ID = ?1
    """)
  void deleteRoleRelation(String groupId);

  @Modifying
  @Query(nativeQuery = true, value = """
    DELETE FROM S_USER_GROUP WHERE GROUP_ID = ?1
    """)
  void deleteUserRelation(String groupId);

  @Query(nativeQuery = true, value = """
    SELECT G.ID, G.COMPANY_ID, G.PARENT_ID, G.NAME, G.STATUS, G.REMARK, G.CREATED_USER_ID, G.CREATED_DATE, G.UPDATED_USER_ID, G.UPDATED_DATE
    FROM S_GROUP G INNER JOIN S_GROUP_ROLE GR ON GR.GROUP_ID = G.ID
                   INNER JOIN S_ROLE R ON R.ID = GR.ROLE_ID
    WHERE R.ID IN (?1) AND (?2 IS NULL OR G.STATUS = ?2) AND (?3 IS NULL OR G.COMPANY_ID = ?3)
    ORDER BY G.NAME
    """)
  Set<Group> findByRole(Collection<String> roleIds, String groupStatus, String companyId);

  @Query(nativeQuery = true, value = """
    SELECT G.ID, G.COMPANY_ID, G.PARENT_ID, G.NAME, G.STATUS, G.REMARK, G.CREATED_USER_ID, G.CREATED_DATE, G.UPDATED_USER_ID, G.UPDATED_DATE
    FROM S_GROUP G INNER JOIN S_USER_GROUP UG ON UG.GROUP_ID = G.ID
                   INNER JOIN S_USER U ON U.ID = UG.USER_ID
    WHERE U.ID IN (?1) AND (?2 IS NULL OR G.STATUS = ?2) AND (?3 IS NULL OR G.COMPANY_ID = ?3)
    ORDER BY G.NAME
    """)
  Set<Group> findByUser(Collection<String> userIds, String groupStatus, String companyId);

  @Modifying
  @Query(nativeQuery = true, value = """
  INSERT INTO S_GROUP_ROLE(ID, GROUP_ID, ROLE_ID, CREATED_USER_ID, CREATED_DATE)
  VALUES(?1, ?2, ?3, ?4, ?5);
    """)
  void saveRoleRelation(String id, String groupId, String roleId, String createdUserId, LocalDateTime dateTime);

  @Modifying
  @Query(nativeQuery = true, value = """
    INSERT INTO S_USER_GROUP(ID, USER_ID, GROUP_ID, CREATED_USER_ID, CREATED_DATE)
    VALUES(?1, ?2, ?3, ?4, ?5);
    """)
  void saveUserRelation(String id, String userId, String groupId, String createdUserId, LocalDateTime dateTime);

  @Query(nativeQuery = false, value = """
    SELECT g, c, pg, cu, uu
    FROM Group g INNER JOIN Company c ON g.companyId = c.id
                 LEFT JOIN User cu ON cu.id = g.createdUserId
                 LEFT JOIN User uu ON uu.id = g.updatedUserId
                 LEFT JOIN Group pg ON pg.id = g.parentId
    WHERE (:allow = TRUE OR g.companyId = :id)
          AND (:#{#sg.name} IS NULL OR UPPER(g.name) LIKE UPPER(:#{#sg.name}))
          AND (:#{#sg.companyName} IS NULL OR UPPER(c.name) LIKE UPPER(:#{#sg.companyName}))
          AND (:#{#sg.status} IS NULL OR g.status = :#{#sg.status})
          AND (:#{#sg.parentId} IS NULL OR g.parentId = :#{#sg.parentId})
          AND (:#{#sg.companyId} IS NULL OR g.companyId = :#{#sg.companyId})
          AND (:#{#sg.eqName} IS NULL OR (UPPER(g.name) = UPPER(:#{#sg.eqName}) AND :#{#sg.companyId} = g.companyId))
    """)
  Page<Object[]> search(@Param("sg") SearchGroup searchGroup, @Param("id") String companyId, @Param("allow") boolean allowNotCompanyCond, Pageable pageable);
}
