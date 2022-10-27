package com.una.system.mananger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class InitDataBase {

  private static class ScriptFile {
    private Integer index;
    private File file;

    public File getFile() {
      return this.file;
    }

    public Integer getIndex() {
      return this.index;
    }

    public void setFile(final File file) {
      this.file = file;
    }

    public void setIndex(final Integer index) {
      this.index = index;
    }

  }

  private static final Pattern pattern = Pattern.compile("^(\\d+)\\..+\\.[sS][qQ][lL]$");
  private final JdbcTemplate jdbcTemplate;
  private final PlatformTransactionManager transactionManager;

  private final String platform;

  private final File scriptsRoot;

  public InitDataBase(final JdbcTemplate jdbcTemplate,
      final PlatformTransactionManager transactionManager, final String platform,
      final File scriptsRoot) {
    this.jdbcTemplate = jdbcTemplate;
    this.platform = platform;
    this.transactionManager = transactionManager;
    this.scriptsRoot = scriptsRoot;
  }

  private File getFileFromScriptFolder() throws URISyntaxException, MalformedURLException {
    final Path path = Paths.get(this.scriptsRoot.getAbsolutePath(), this.platform);
    final File file = path.toFile();
    return file;
  }

  public void init(final int min, final int max) throws Exception {
    final DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    final TransactionStatus transaction = this.transactionManager
        .getTransaction(transactionDefinition);

    final File root = this.getFileFromScriptFolder();
    if (!root.exists()) {
      return;
    }

    final List<ScriptFile> scriptFileList = new ArrayList<>();

    final List<File> files = Arrays.asList(root.listFiles());
    for (final File file : files) {
      if (!file.isFile()) {
        continue;
      }
      final String name = file.getName();
      final Matcher matcher = InitDataBase.pattern.matcher(name);
      if (!matcher.find()) {
        continue;
      }
      final int i = Integer.parseInt(matcher.group(1));
      if (min > i || i > max) {
        continue;
      }
      final ScriptFile scriptFile = new ScriptFile();
      scriptFile.setFile(file);
      scriptFile.setIndex(i);
      scriptFileList.add(scriptFile);
    }

    Collections.sort(scriptFileList, Comparator.comparing(ScriptFile::getIndex));
    for (final ScriptFile file : scriptFileList) {
      final String context = this.readFileContext(file.getFile());
      this.jdbcTemplate.execute(context);
    }
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
