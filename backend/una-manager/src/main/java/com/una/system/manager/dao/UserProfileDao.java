package com.una.system.manager.dao;

import com.una.system.manager.model.UserProfile;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileDao extends BaseRepository<UserProfile, String> {
  Optional<UserProfile> findByUserId(String userId);
}
