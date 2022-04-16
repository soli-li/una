package com.una.system.manager.dao;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.una.system.manager.model.Menu;
import com.una.system.manager.pojo.req.SearchMenu;

@Repository
public interface MenuDao extends BaseRepository<Menu, String> {
  @Query(nativeQuery = true, value = """
    SELECT M.ID, M.NAME, M.ICON, M.SORT, M.FRONT_END_URI, M.PARENT_ID, M.PERMISSIONS_ID, M.STATUS, M.REMARK, M.CREATED_USER_ID, M.CREATED_DATE
    FROM S_MENU M
    WHERE M.PERMISSIONS_ID IN(?1) AND (?2 IS NULL OR M.STATUS = ?2)
    ORDER BY M.SORT
    """)
  Set<Menu> findByPermissionsId(Collection<String> permIds, String status);

  @Query(nativeQuery = false, value = """
    SELECT m, pm, p, cu
    FROM Menu m LEFT JOIN User cu ON cu.id = m.createdUserId
                LEFT JOIN Permissions p ON m.permissionsId = p.id
                LEFT JOIN Menu pm ON m.parentId = pm.id
    WHERE 1 = 1
          AND (:#{#sm.name} IS NULL OR UPPER(m.name) LIKE UPPER(:#{#sm.name}))
          AND (:#{#sm.id} IS NULL OR m.id = :#{#sm.id})
          AND (:#{#sm.eqFrontEndUri} IS NULL OR UPPER(m.frontEndUri) = UPPER(:#{#sm.eqFrontEndUri}))
    """)
  Page<Object[]> search(@Param("sm") SearchMenu searchMenu, Pageable pageable);

}
