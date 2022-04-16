package com.una.system.manager.dao;

import org.springframework.stereotype.Repository;

import com.una.system.manager.model.PasswordPolicy;

@Repository
public interface PasswordPolicyDao extends BaseRepository<PasswordPolicy, String> {
}
