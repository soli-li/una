package com.una.system.manager.dao;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.una.system.manager.model.UserProfile;

@Repository
public interface UserProfileDao extends BaseRepository<UserProfile, String> {
  Optional<UserProfile> findByUserId(String userId);
}
