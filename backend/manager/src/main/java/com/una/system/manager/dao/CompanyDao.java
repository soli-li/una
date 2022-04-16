package com.una.system.manager.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.una.system.manager.model.Company;
import com.una.system.manager.pojo.req.SearchCompany;

@Repository
public interface CompanyDao extends BaseRepository<Company, String> {
  @Query(nativeQuery = false, value = """
    SELECT c, pp, cu, uu
    FROM Company c LEFT JOIN PasswordPolicy pp ON pp.id = c.pwPolicyId
                   LEFT JOIN User cu ON cu.id = c.createdUserId
                   LEFT JOIN User uu ON uu.id = c.updatedUserId
    WHERE (:allow = TRUE OR c.id = :id)
          AND (:#{#sc.name} IS NULL OR UPPER(c.name) LIKE UPPER(:#{#sc.name}))
          AND (:#{#sc.shortName} IS NULL OR UPPER(c.shortName) LIKE UPPER(:#{#sc.shortName}))
          AND (:#{#sc.legalPerson} IS NULL OR UPPER(c.legalPerson) LIKE UPPER(:#{#sc.legalPerson}))
          AND (:#{#sc.status} IS NULL OR c.status = :#{#sc.status})
          AND (:#{#sc.id} IS NULL OR c.id = :#{#sc.id})
          AND (:#{#sc.eqName} IS NULL OR UPPER(c.name) = UPPER(:#{#sc.eqName}))
          AND (:#{#sc.eqSNamd} IS NULL OR UPPER(c.shortName) = UPPER(:#{#sc.eqSNamd}))
    """)
  Page<Object[]> search(@Param("sc") SearchCompany searchCompany, @Param("id") String companyId, @Param("allow") boolean allowNotCompanyCond,
    Pageable pageable);
}
