package com.una.system.manager.dao;

import com.una.system.manager.model.Configuration;
import com.una.system.manager.pojo.req.SearchConf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationDao extends BaseRepository<Configuration, String> {

  @Query(nativeQuery = false,
      value = """
          SELECT c, cp, cu
          FROM Configuration c LEFT JOIN Company cp ON c.companyId = cp.id
                               LEFT JOIN User cu ON cu.id = c.createdUserId
          WHERE (:allow = TRUE OR c.companyId = :id)
                AND (:#{#sc.name} IS NULL OR UPPER(c.name) LIKE UPPER(:#{#sc.name}))
                AND (:#{#sc.companyName} IS NULL OR UPPER(cp.name) LIKE UPPER(:#{#sc.companyName}))
                AND (:#{#sc.companyId} IS NULL OR c.companyId = :#{#sc.companyId})
                AND (:#{#sc.confKey} IS NULL OR UPPER(c.confKey) LIKE UPPER(:#{#sc.confKey}))
                AND (:#{#sc.status} IS NULL OR c.status = :#{#sc.status})
                AND (:#{#sc.eqConfKey} IS NULL OR c.confKey = :#{#sc.eqConfKey})
          """)
  Page<Object[]> search(@Param("sc") SearchConf searchConf, @Param("id") String companyId,
      @Param("allow") boolean allowNotCompanyCond, Pageable pageable);
}
