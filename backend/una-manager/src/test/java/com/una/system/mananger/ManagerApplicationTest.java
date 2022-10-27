package com.una.system.mananger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.una.system.manager.ManagerApplication;
import java.io.File;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest(classes = ManagerApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = { "unit-test" })
@AutoConfigureMockMvc
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class ManagerApplicationTest {

  @BeforeAll
  public static void initActive() {
  }

  @Value("${app.test.db.init-enable:false}")
  private boolean initDataBaseEnable;
  @Value("${app.test.db.platform:}")
  private String dbPlatform;
  @Value("${app.test.db.script-root:}")
  private String dbScriptRoot;
  @Value("${app.test.db.execute-scope.min:10}")
  private int dbMinScript;
  @Value("${app.test.db.execute-scope.max:100}")
  private int dbMaxScript;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private PlatformTransactionManager platformTransactionManager;
  @Autowired
  private ObjectMapper objectMapper;

  @Value("${spring.datasource.url}")
  private String jdbcName;
  private final String companyId = "c808faa6-6721-42a1-bdd7-3ca88141a67e";
  private final String userId = "8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6";
  private final String groupId = "97c6a622-8c1b-43a5-b0df-d03e373f67c0";
  private final String roleId = "4aa6350b-1cdb-4fba-99be-b2645b7276a2";
  private final String basePermId = "global-base-function";

  private final String configMenuId = "578c3196-c543-4292-ba26-0d037dec84fb";

  public String getBasePermId() {
    return this.basePermId;
  }

  public String getCompanyId() {
    return this.companyId;
  }

  public String getConfigMenuId() {
    return this.configMenuId;
  }

  public String getGroupId() {
    return this.groupId;
  }

  public JdbcTemplate getJdbcTemplate() {
    return this.jdbcTemplate;
  }

  public MockMvc getMockMvc() {
    return this.mockMvc;
  }

  public ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  public String getRoleId() {
    return this.roleId;
  }

  public String getUserId() {
    return this.userId;
  }

  @Test
  @Order(1)
  public void init() {
    try {
      this.initDataBase();
    } catch (final Exception e) {
      Assertions.assertTrue(false, e.getMessage());
    }
  }

  public void initDataBase() throws Exception {
    if (!this.initDataBaseEnable || StringUtils.isBlank(this.dbScriptRoot)) {
      return;
    }
    File file = null;
    if (StringUtils.startsWith("file:/", this.dbScriptRoot)) {
      new File(new URI(this.dbScriptRoot));
    } else {
      file = new File(this.dbScriptRoot);
    }
    InitDataBase initDataBase = null;
    initDataBase = new InitDataBase(this.jdbcTemplate, this.platformTransactionManager,
        this.dbPlatform, file);
    initDataBase.init(this.dbMinScript, this.dbMaxScript);
  }

}
