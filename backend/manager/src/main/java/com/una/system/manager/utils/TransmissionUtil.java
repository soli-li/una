package com.una.system.manager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClient.Attributes;
import org.apache.sshd.sftp.client.SftpClient.DirEntry;
import org.apache.sshd.sftp.client.SftpClientFactory;

public final class TransmissionUtil {
  public static class Client {
    private Client() {
    }
  }

  public static final class FileAttributes {
    private long modification;
    private long size;
    private int type;
    private String user;
    private String name;

    private FileAttributes() {
    }

    public long getModification() {
      return this.modification;
    }

    public String getName() {
      return this.name;
    }

    public long getSize() {
      return this.size;
    }

    public int getType() {
      return this.type;
    }

    public String getUser() {
      return this.user;
    }

    public void setModification(final long modification) {
      this.modification = modification;
    }

    public void setName(final String name) {
      this.name = name;
    }

    public void setSize(final long size) {
      this.size = size;
    }

    public void setType(final int type) {
      this.type = type;
    }

    public void setUser(final String user) {
      this.user = user;
    }
  }

  private static class FileImpl implements Transmission {

    private Integer bufferSize;

    @Override
    public void close() throws Exception {
      this.logout();
    }

    @Override
    public boolean closed() {
      return false;
    }

    @Override
    public long downloadFile(final String remoteFilePath, final String filename, final OutputStream outputStream) throws TransmissionException {
      TransmissionUtil.hasLength(remoteFilePath, "parameter 'remoteFilePath' must not be empty");
      TransmissionUtil.hasLength(filename, "parameter 'filename' must not be empty");
      Objects.requireNonNull(outputStream, "parameter 'outputStream' must not be null");
      final String filePath = Path.of(remoteFilePath, filename).toString();
      try (FileInputStream inputStream = new FileInputStream(new File(filePath))) {
        if (Objects.isNull(this.bufferSize) || this.bufferSize <= 0) {
          return IOUtils.copyLarge(inputStream, outputStream);
        }
        else {
          return IOUtils.copyLarge(inputStream, outputStream, new byte[this.bufferSize]);
        }
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("download file exception, file name %s", filePath), e);
      }
    }

    @Override
    public boolean isLogin() {
      return true;
    }

    @Override
    public List<FileAttributes> list(final String directory, final FilterType type) throws TransmissionException {
      TransmissionUtil.hasLength(directory, "parameter 'directory' must not be empty");

      final File file = new File(directory);
      File[] files = {};
      if (type == FilterType.DIRECTORY) {
        files = file.listFiles(File::isDirectory);
      }
      else if (type == FilterType.FILE) {
        files = file.listFiles(File::isFile);
      }
      else {
        files = file.listFiles();
      }

      final List<FileAttributes> attrList = new ArrayList<>();
      for (final File targetFile : files) {
        final FileAttributes attributes = new FileAttributes();
        attributes.setName(targetFile.getName());
        attributes.setSize(targetFile.length());
        attributes.setModification(targetFile.lastModified());
        //        attributes.setType();
        //        attributes.setUser();

        attrList.add(attributes);
      }
      return attrList;
    }

    @Override
    public void login(final String address, final int port, final String username, final String password) throws TransmissionException {
    }

    @Override
    public void logout() throws TransmissionException {
    }

    @Override
    public void mkdirs(final String directory) {
      TransmissionUtil.hasLength(directory, "parameter 'directory' must not be empty");
      new File(directory).mkdirs();
    }

    @Override
    public void remove(final String remotePath) {
      TransmissionUtil.hasLength(remotePath, "parameter 'remotePath' must not be empty");
      new File(remotePath).delete();
    }

    @Override
    public void rmdir(final String directory) {
      TransmissionUtil.hasLength(directory, "parameter 'directory' must not be empty");
      new File(directory).delete();
    }

    @Override
    public void setBufferSize(final Integer bufferSize) {
      this.bufferSize = bufferSize;
    }

    @Override
    public long uploadFile(final InputStream inputStream, final String remoteFilePath, final String filename) throws TransmissionException {
      TransmissionUtil.hasLength(remoteFilePath, "parameter 'remoteFilePath' must not be empty");
      TransmissionUtil.hasLength(remoteFilePath, "filename 'filename' must not be empty");
      Objects.requireNonNull(inputStream, "parameter 'inputStream' must not be null");

      final String filePath = Path.of(remoteFilePath, filename).toString();
      try (FileOutputStream outputStream = new FileOutputStream(new File(filePath))) {
        if (Objects.isNull(this.bufferSize) || this.bufferSize <= 0) {
          return IOUtils.copyLarge(inputStream, outputStream);
        }
        else {
          return IOUtils.copyLarge(inputStream, outputStream, new byte[this.bufferSize]);
        }
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("upload file exception, file name %s", filePath), e);
      }
    }

  }

  public enum FilterType {
    DIRECTORY,
    FILE,
    ALL;
  }

  private static class FTPImpl implements Transmission {
    private boolean pasv = false;
    private boolean ssl = false;
    private Integer bufferSize;
    private FTPClient ftpClient;

    public FTPImpl(final boolean pasv, final boolean ssl) {
      this.pasv = pasv;
      this.ssl = ssl;
    }

    @Override
    public void close() throws Exception {
      this.logout();
    }

    @Override
    public boolean closed() {
      if (this.isLogin(false)) {
        return this.ftpClient.isConnected();
      }
      return true;
    }

    @Override
    public long downloadFile(final String remoteFilePath, final String filename, final OutputStream outputStream) throws TransmissionException {
      TransmissionUtil.hasLength(remoteFilePath, "parameter 'remoteFilePath' must not be empty");
      TransmissionUtil.hasLength(filename, "parameter 'filename' must not be empty");
      Objects.requireNonNull(outputStream, "parameter 'outputStream' must not be null");
      this.isLogin(true);
      final String filePath = String.join(TransmissionUtil.ftpSeparator, remoteFilePath, filename);
      try {
        this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        this.ftpClient.changeWorkingDirectory(remoteFilePath);
        long size = 0;
        try (InputStream inputStream = this.ftpClient.retrieveFileStream(filename);) {
          if (Objects.isNull(this.bufferSize) || this.bufferSize <= 0) {
            size = IOUtils.copyLarge(inputStream, outputStream);
          }
          else {
            size = IOUtils.copyLarge(inputStream, outputStream, new byte[this.bufferSize]);
          }
          this.ftpClient.getReply();
        }
        return size;
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("download file exception, file name %s", filePath), e);
      }
    }

    private FTPClient getFtpClient(final FTPClientConfig config, final boolean isFtps) {
      FTPClient ftp = null;
      if (isFtps) {
        ftp = new FTPSClient();
      }
      else {
        ftp = new FTPClient();
      }
      FTPClientConfig ftpConfig = config;
      if (ftpConfig == null) {
        ftpConfig = new FTPClientConfig();
      }
      ftp.configure(ftpConfig);
      return ftp;
    }

    @Override
    public boolean isLogin() {
      return this.isLogin(false);
    }

    private boolean isLogin(final boolean throwException) {
      if (this.ftpClient == null || !this.ftpClient.isConnected()) {
        if (throwException) {
          throw new TransmissionException("please first login ftp");
        }
        return false;
      }
      return true;
    }

    @Override
    public List<FileAttributes> list(final String directory, final FilterType type) throws TransmissionException {
      TransmissionUtil.hasLength(directory, "parameter 'directory' must not be empty");
      this.isLogin(true);
      final List<FileAttributes> list = new ArrayList<>();
      try {
        FTPFile[] ftpFiles = null;

        if (FilterType.FILE == type) {
          ftpFiles = this.ftpClient.listFiles(directory);
        }
        else if (FilterType.DIRECTORY == type) {
          ftpFiles = this.ftpClient.listDirectories(directory);
        }
        else {
          final FTPFile[] fileList = this.ftpClient.listFiles(directory);
          final FTPFile[] dirList = this.ftpClient.listDirectories(directory);
          ftpFiles = new FTPFile[fileList.length + dirList.length];
          System.arraycopy(dirList, 0, dirList, 0, dirList.length);
          System.arraycopy(fileList, 0, ftpFiles, dirList.length, fileList.length);
        }

        for (final FTPFile ftpFile : ftpFiles) {
          if (Objects.isNull(ftpFile) || StringUtils.equalsAnyIgnoreCase(ftpFile.getName(), ".", "..")) {
            continue;
          }
          final FileAttributes fileAttributes = new FileAttributes();
          fileAttributes.setType(ftpFile.getType());
          fileAttributes.setSize(ftpFile.getSize());
          fileAttributes.setUser(ftpFile.getUser());
          fileAttributes.setName(ftpFile.getName());
          fileAttributes.setModification(ftpFile.getTimestamp().getTimeInMillis());
          list.add(fileAttributes);
        }
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("list directory path exception, directory is %s", directory), e);
      }
      return list;
    }

    @Override
    public void login(final String address, final int port, final String username, final String password) throws TransmissionException {
      TransmissionUtil.hasLength(address, "parameter 'address' must not be empty");
      this.ftpClient = this.getFtpClient(null, this.ssl);
      try {
        this.ftpClient.connect(address, port);
        if (!this.ftpClient.login(username, password)) {
          throw new TransmissionException("username or password incorrect");
        }
        this.setDataConnectionMode();
      }
      catch (final Exception e) {
        throw new TransmissionException("login exception", e);
      }
    }

    @Override
    public void logout() throws TransmissionException {
      this.isLogin(true);
      try {
        this.ftpClient.logout();
        this.ftpClient.disconnect();
      }
      catch (final Exception e) {
        throw new TransmissionException("logout exception", e);
      }
    }

    private void mkdir(final String path) {
      try {
        this.ftpClient.makeDirectory(path);
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("mkdir exception, directory is %s", path), e);
      }
    }

    @Override
    public void mkdirs(final String directory) throws TransmissionException {
      TransmissionUtil.hasLength(directory, "parameter 'directory' must not be empty");
      this.isLogin(true);
      final String[] directorys = directory.split(TransmissionUtil.ftpSeparator);
      final StringBuilder buff = new StringBuilder();
      final List<String> dirList = new ArrayList<>();
      for (final String s : directorys) {
        if (StringUtils.isBlank(s)) {
          continue;
        }
        dirList.add(s);
      }
      for (final String s : dirList) {
        buff.append(TransmissionUtil.ftpSeparator).append(s);
        this.mkdir(buff.toString());
      }
    }

    @Override
    public void remove(final String remotePath) {
      TransmissionUtil.hasLength(remotePath, "parameter 'remotePath' must not be empty");
      this.isLogin(true);
      try {
        this.ftpClient.deleteFile(remotePath);
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("remove file exception, file name %s", remotePath), e);
      }
    }

    @Override
    public void rmdir(final String directory) {
      TransmissionUtil.hasLength(directory, "parameter 'remotePath' must not be empty");
      this.isLogin(true);
      try {
        this.ftpClient.removeDirectory(directory);
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("remove directory exception, directory path %s", directory), e);
      }
    }

    @Override
    public void setBufferSize(final Integer bufferSize) {
      this.bufferSize = bufferSize;
    }

    private void setDataConnectionMode() {
      String mode = "";
      if (this.pasv) {
        this.ftpClient.enterLocalPassiveMode();
      }
      else {
        this.ftpClient.enterLocalActiveMode();
      }
      switch (this.ftpClient.getDataConnectionMode()) {
        case FTPClient.ACTIVE_LOCAL_DATA_CONNECTION_MODE:
          mode = "'ACTIVE LOCAL'";
          break;
        case FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE:
          mode = "'PASSIVE LOCAL'";
          break;
        default:
          throw new RuntimeException(String.format("unknown data connection mode, mode: %s", mode));
      }
    }

    @Override
    public long uploadFile(final InputStream inputStream, final String remoteFilePath, final String filename) throws TransmissionException {
      TransmissionUtil.hasLength(remoteFilePath, "parameter 'remoteFilePath' must not be empty");
      TransmissionUtil.hasLength(filename, "parameter 'filename' must not be empty");
      Objects.requireNonNull(inputStream, "parameter 'inputStream' must not be null");
      this.isLogin(true);
      final String filePath = String.join(TransmissionUtil.ftpSeparator, remoteFilePath, filename);
      try {
        this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        this.ftpClient.changeWorkingDirectory(remoteFilePath);
        long size = 0;
        try (OutputStream outputStream = this.ftpClient.storeFileStream(filename);) {
          if (Objects.isNull(this.bufferSize) || this.bufferSize <= 0) {
            size = IOUtils.copyLarge(inputStream, outputStream);
          }
          else {
            size = IOUtils.copyLarge(inputStream, outputStream, new byte[this.bufferSize]);
          }
          this.ftpClient.getReply();
        }
        return size;
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("upload file exception, file name %s", filePath), e);
      }
    }
  }

  private static class SFTPImpl implements Transmission {
    private SshClient client;
    private ClientSession session;
    private SftpClient sftp;
    private Integer bufferSize;

    @Override
    public void close() throws Exception {
      this.logout();
    }

    @Override
    public boolean closed() {
      return this.isLogin(false);
    }

    @Override
    public long downloadFile(final String remoteFilePath, final String filename, final OutputStream outputStream) throws TransmissionException {
      TransmissionUtil.hasLength(remoteFilePath, "parameter 'remoteFilePath' must not be empty");
      TransmissionUtil.hasLength(filename, "parameter 'filename' must not be empty");
      Objects.requireNonNull(outputStream, "parameter 'inputStream' must not be null");
      final String filePath = String.join(TransmissionUtil.ftpSeparator, remoteFilePath, filename);
      this.isLogin(true);
      try {
        this.sftp.open(filePath);
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("%s is incorrect path", filePath), e);
      }
      try {
        try (InputStream inputStream = this.sftp.read(filePath);) {
          if (Objects.isNull(this.bufferSize) || this.bufferSize <= 0) {
            return IOUtils.copyLarge(inputStream, outputStream);
          }
          else {
            return IOUtils.copyLarge(inputStream, outputStream, new byte[this.bufferSize]);
          }
        }
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("download file exception, file name %s", filePath), e);
      }
    }

    @Override
    public boolean isLogin() {
      return this.isLogin(false);
    }

    private boolean isLogin(final boolean throwException) {
      if (this.sftp == null || this.sftp.isClosing()) {
        if (throwException) {
          throw new TransmissionException("please first login sftp");
        }
        return false;
      }
      return true;
    }

    @Override
    public List<FileAttributes> list(final String directory, final FilterType type) throws TransmissionException {
      TransmissionUtil.hasLength(directory, "parameter 'directory' must not be empty");
      this.isLogin(true);
      final List<FileAttributes> fileList = new ArrayList<>();
      try {
        final Iterable<DirEntry> iterable = this.sftp.readDir(directory);
        for (final DirEntry dirEntry : iterable) {
          final String name = dirEntry.getFilename();
          if (".".equals(name) || "..".equals(name)) {
            continue;
          }
          final Attributes attributes = dirEntry.getAttributes();
          final FileAttributes fileAttributes = new FileAttributes();
          fileAttributes.setType(attributes.getType());
          fileAttributes.setSize(attributes.getSize());
          fileAttributes.setUser(attributes.getOwner());
          fileAttributes.setModification(attributes.getModifyTime().toMillis());
          fileAttributes.setName(name);
          if (FilterType.DIRECTORY == type && attributes.isDirectory() || FilterType.FILE == type && !attributes.isDirectory()) {
          }
          fileList.add(fileAttributes);
        }
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("list directory path exception, directory is %s", directory), e);
      }
      return fileList;
    }

    @Override
    public void login(final String address, final int port, final String username, final String password) throws TransmissionException {
      TransmissionUtil.hasLength(address, "parameter 'address' must not be empty");
      TransmissionUtil.hasLength(username, "parameter 'username' must not be empty");
      this.client = SshClient.setUpDefaultClient();
      try {
        this.client.start();
        final ConnectFuture connect = this.client.connect(username, address, port);
        connect.await();
        this.session = connect.getSession();
        this.session.addPasswordIdentity(password);

        final AuthFuture authFuture = this.session.auth();
        authFuture.await();
        if (!this.session.isAuthenticated()) {
          throw new TransmissionException("username or password incorrect");
        }
        final SftpClientFactory factory = SftpClientFactory.instance();
        this.sftp = factory.createSftpClient(this.session);
      }
      catch (final Exception e) {
        throw new TransmissionException("login exception", e);
      }
    }

    @Override
    public void logout() throws TransmissionException {
      this.isLogin(true);
      boolean status = true;
      Exception ex = null;
      try {
        this.sftp.close();
        this.session.close();
      }
      catch (final Exception e) {
        ex = e;
        status = false;
      }

      try {
        this.client.stop();
      }
      catch (final Exception e) {
        status = false;
        ex = e;
      }

      if (!status) {
        throw new TransmissionException("logout exception", ex);
      }
    }

    private void mkdir(final String path) {
      boolean hasDir = true;
      try {
        this.sftp.openDir(path);
      }
      catch (final Exception e1) {
        hasDir = false;
      }
      if (!hasDir) {
        try {
          this.sftp.mkdir(path);
        }
        catch (final Exception e) {
          throw new TransmissionException(String.format("mkdir exception, directory is %s", path), e);
        }
      }
    }

    @Override
    public void mkdirs(final String directory) throws TransmissionException {
      TransmissionUtil.hasLength(directory, "parameter 'directory' must not be empty");
      this.isLogin(true);
      final String[] directorys = directory.split(TransmissionUtil.ftpSeparator);
      final StringBuilder buff = new StringBuilder();
      final List<String> dirList = new ArrayList<>();
      for (final String s : directorys) {
        if (StringUtils.isBlank(s)) {
          continue;
        }
        dirList.add(s);
      }

      for (final String path : dirList) {
        buff.append(TransmissionUtil.ftpSeparator).append(path);
        this.mkdir(buff.toString());
      }
    }

    @Override
    public void remove(final String remotePath) {
      TransmissionUtil.hasLength(remotePath, "parameter 'remotePath' must not be empty");
      this.isLogin(true);
      try {
        this.sftp.remove(remotePath);
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("remove file exception, file name %s", remotePath), e);
      }
    }

    @Override
    public void rmdir(final String directory) {
      TransmissionUtil.hasLength(directory, "parameter 'directory' must not be empty");
      this.isLogin(true);
      try {
        this.sftp.rmdir(directory);
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("remove directory exception, directory path %s", directory), e);
      }

    }

    @Override
    public void setBufferSize(final Integer bufferSize) {
      this.bufferSize = bufferSize;
    }

    @Override
    public long uploadFile(final InputStream inputStream, final String remoteFilePath, final String filename) throws TransmissionException {
      TransmissionUtil.hasLength(remoteFilePath, "parameter 'remoteFilePath' must not be empty");
      TransmissionUtil.hasLength(filename, "parameter 'filename' must not be empty");
      Objects.requireNonNull(inputStream, "parameter 'inputStream' must not be null");
      this.isLogin(true);
      final String filePath = String.join(TransmissionUtil.ftpSeparator, remoteFilePath, filename);
      try {
        try (OutputStream outputStream = this.sftp.write(filePath);) {
          if (Objects.isNull(this.bufferSize) || this.bufferSize <= 0) {
            return IOUtils.copyLarge(inputStream, outputStream);
          }
          else {
            return IOUtils.copyLarge(inputStream, outputStream, new byte[this.bufferSize]);
          }
        }
      }
      catch (final Exception e) {
        throw new TransmissionException(String.format("upload file exception, file name %s", filePath), e);
      }
    }
  }

  public interface Transmission extends AutoCloseable {
    boolean closed();

    long downloadFile(String remoteFilePath, String filename, OutputStream outputStream) throws TransmissionException;

    boolean isLogin();

    List<FileAttributes> list(String directory, FilterType type) throws TransmissionException;

    void login(String address, int port, String username, String password) throws TransmissionException;

    void logout() throws TransmissionException;

    void mkdirs(String directory) throws TransmissionException;

    void remove(String remotePath);

    void rmdir(String directory);

    void setBufferSize(Integer bufferSize);

    long uploadFile(InputStream inputStream, String remoteFilePath, String filename) throws TransmissionException;
  }

  public static class TransmissionException extends RuntimeException {
    private static final long serialVersionUID = -3415178653073798279L;

    public TransmissionException() {
    }

    public TransmissionException(final String message) {
      super(message);
    }

    public TransmissionException(final String message, final Throwable cause) {
      super(message, cause);
    }

    public TransmissionException(final Throwable cause) {
      super(cause);
    }

  }

  public enum Type {
    FTP_PASV("ftp_pasv") {
      @Override
      public Transmission instance() {
        return new FTPImpl(true, false);
      }
    },
    FTP_PORT("ftp", "ftp_port") {
      @Override
      public Transmission instance() {
        return new FTPImpl(false, false);
      }
    },
    FTPS_PASV("ftps_pasv") {
      @Override
      public Transmission instance() {
        return new FTPImpl(true, false);
      }
    },
    FTPS_PORT("ftps", "ftps_port") {
      @Override
      public Transmission instance() {
        return new FTPImpl(true, true);
      }
    },
    SFTP("sftp") {
      @Override
      public Transmission instance() {
        return new SFTPImpl();
      }
    },
    FILE("file") {
      @Override
      public Transmission instance() {
        return new FileImpl();
      }
    };

    public static Transmission getInstance(final String alias) {
      for (final Type type : Type.values()) {
        if (StringUtils.equalsAnyIgnoreCase(StringUtils.trim(alias), type.aliases)) {
          return type.instance();
        }
      }
      throw new TransmissionException("not match transform type, alias: " + alias);
    }

    private final String[] aliases;

    Type(final String... aliases) {
      this.aliases = aliases;
    }

    public abstract Transmission instance();
  }

  private static final String ftpSeparator = "/";

  private static void hasLength(final String str, final String message) {
    if (StringUtils.isBlank(str)) {
      throw new TransmissionException(message);
    }
  }
}
