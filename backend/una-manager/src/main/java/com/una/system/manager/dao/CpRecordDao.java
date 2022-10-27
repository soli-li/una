package com.una.system.manager.dao;

import com.una.system.manager.model.CpRecord;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CpRecordDao extends BaseRepository<CpRecord, String> {
  @Query(nativeQuery = false,
      value = """
          SELECT r
          FROM CpRecord r
          WHERE r.username = :username
          """)
  List<CpRecord> findByUsername(@Param("username") String username, Pageable pageable);
}
