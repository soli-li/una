package com.una.system.manager.service;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GenerateService {

  public String generateId() {
    return UUID.randomUUID().toString();
  }
}
