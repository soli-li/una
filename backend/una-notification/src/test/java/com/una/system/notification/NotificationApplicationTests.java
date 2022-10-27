package com.una.system.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = NotificationApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback(value = true)
@ActiveProfiles(profiles = { "unit-test" })
@AutoConfigureMockMvc
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class NotificationApplicationTests {

  @BeforeAll
  public static void initActive() {
  }

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  public MockMvc getMockMvc() {
    return this.mockMvc;
  }

  public ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  public void setMockMvc(final MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  public void setObjectMapper(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

}
