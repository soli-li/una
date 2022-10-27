package com.una.system.manager.dao;

import com.una.system.manager.model.Permissions;
import com.una.system.manager.pojo.req.SearchPermissions;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionsDao extends BaseRepository<Permissions, String> {
  @Modifying
  @Query(nativeQuery = true,
      value = """
          DELETE FROM S_ROLE_PERM WHERE ROLE_ID = ?1 AND PERMISSIONS_ID = ?2
          """)
  void deleteRoleRelation(String roleId, String permId);

  @Query(nativeQuery = true,
      value = """
          SELECT P.ID, P.NAME, P.STATUS, P.REMARK, P.CREATED_USER_ID, P.CREATED_DATE,
                 P.UPDATED_USER_ID, P.UPDATED_DATE
          FROM S_PERMISSIONS P INNER JOIN S_ROLE_PERM RP ON RP.PERMISSIONS_ID = P.ID
                               INNER JOIN S_ROLE R ON R.ID = RP.ROLE_ID
          WHERE R.ID IN(?1) AND (?2 IS NULL OR P.STATUS = ?2)
          ORDER BY P.NAME
          """)
  Set<Permissions> findByRoleId(Collection<String> roleIds, String permStatus);

  @Modifying
  @Query(nativeQuery = true,
      value = """
          INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
          VALUES(?1, ?2, ?3, ?4, ?5);
            """)
  void saveRoleRelation(String id, String roleId, String permId, String createdUserId,
      LocalDateTime dateTime);

  @Query(nativeQuery = false,
      value = """
          SELECT p, cu, uu
          FROM Permissions p LEFT JOIN User cu ON cu.id = p.createdUserId
                             LEFT JOIN User uu ON uu.id = p.updatedUserId
          WHERE 1 = 1
                AND (:#{#sp.name} IS NULL OR UPPER(p.name) LIKE UPPER(:#{#sp.name}))
                AND (:#{#sp.status} IS NULL OR p.status = :#{#sp.status})
          """)
  Page<Object[]> search(@Param("sp") SearchPermissions searchPermissions, Pageable pageable);
}
