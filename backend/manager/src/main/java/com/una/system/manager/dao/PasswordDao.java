package com.una.system.manager.dao;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.una.system.manager.model.PasswordPolicy;

@Repository
public interface PasswordDao extends BaseRepository<PasswordPolicy, String> {
  @Query(nativeQuery = true, value = "SELECT PASSWORD FROM S_USER U WHERE U.USERNAME = ?1")
  String getPasswordByUsername(String username);

  @Modifying
  @Query(nativeQuery = true, value = "UPDATE S_USER SET PASSWORD = ?2, LAST_CHANGE_PW_DATE = ?3 WHERE USERNAME = ?1")
  int upgradePassword(String username, String password, LocalDateTime lastChangePw);
}
