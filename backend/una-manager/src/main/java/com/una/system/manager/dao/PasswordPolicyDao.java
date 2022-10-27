package com.una.system.manager.dao;

import com.una.system.manager.model.PasswordPolicy;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordPolicyDao extends BaseRepository<PasswordPolicy, String> {
}
