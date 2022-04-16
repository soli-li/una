package com.una.system.mananger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class InitDB {

  private final JdbcTemplate jdbcTemplate;
  private final PlatformTransactionManager transactionManager;
  private final String type;

  public InitDB(final JdbcTemplate jdbcTemplate, final PlatformTransactionManager transactionManager, final String type) {
    this.jdbcTemplate = jdbcTemplate;
    this.type = type;
    this.transactionManager = transactionManager;
  }

  private void executeSql(final String fileName) throws Exception {
    final File createFile = this.getFileFromScriptFolder(fileName);
    if (!createFile.exists()) {
      return;
    }
    final String context = this.readFileContext(createFile);
    this.jdbcTemplate.execute(context);
  }

  private File getFileFromScriptFolder(final String fileName) throws URISyntaxException, MalformedURLException {
    final URI uri = this.getClass().getResource("/").toURI();
    final Path path = Paths.get(new File(uri).getAbsolutePath(), "..", "..", "..", "..", "db-scripts", this.type, fileName);
    final File file = path.toFile();
    return file;
  }

  public void init() throws Exception {
    final DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
    defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    final TransactionStatus transaction = this.transactionManager.getTransaction(defaultTransactionDefinition);
    //this.executeSql("5.database.sql");
    this.executeSql("10.delete.sql");
    this.executeSql("20.create.sql");
    this.executeSql("30.init-data.sql");
    this.executeSql("100.nacos.sql");
    this.transactionManager.commit(transaction);
  }

  private String readFileContext(final File file) throws IOException {
    final StringBuilder buff = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
      String line = reader.readLine();
      while (line != null) {
        buff.append(line);
        line = reader.readLine();
      }
    }
    return buff.toString();
  }
}
