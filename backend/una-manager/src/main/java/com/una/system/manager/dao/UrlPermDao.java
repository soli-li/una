package com.una.system.manager.dao;

import com.una.system.manager.model.UrlPerm;
import com.una.system.manager.pojo.req.SearchUrlPerm;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlPermDao extends BaseRepository<UrlPerm, String> {
  @Query(nativeQuery = true,
      value = """
          SELECT U.ID, U.NAME, U.URI, U.PERMISSIONS_ID, U.SORT, U.STATUS, U.REMARK,
                 U.CREATED_USER_ID, U.CREATED_DATE
          FROM S_URL_PERM U
          WHERE U.PERMISSIONS_ID IN(?1) AND (?2 IS NULL OR U.STATUS = ?2)
          ORDER BY U.SORT
          """)
  Set<UrlPerm> findByPermissionsId(Collection<String> permIds, String status);

  @Query(nativeQuery = false,
      value = """
          SELECT up, p, cu
          FROM UrlPerm up LEFT JOIN User cu ON cu.id = up.createdUserId
                          LEFT JOIN Permissions p ON up.permissionsId = p.id
          WHERE 1 = 1
                AND (:#{#sup.name} IS NULL OR UPPER(up.name) LIKE UPPER(:#{#sup.name}))
                AND (:#{#sup.uri} IS NULL OR UPPER(up.uri) LIKE UPPER(:#{#sup.uri}))
                AND (:#{#sup.perm} IS NULL OR UPPER(p.name) LIKE UPPER(:#{#sup.perm}))
                AND (:#{#sup.status} IS NULL OR up.status =:#{#sup.status})
          """)
  Page<Object[]> search(@Param("sup") SearchUrlPerm searchUrlPerm, Pageable pageable);

}
