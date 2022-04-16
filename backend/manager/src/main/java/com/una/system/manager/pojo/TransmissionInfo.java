package com.una.system.manager.pojo;

import org.apache.commons.lang3.StringUtils;

public class TransmissionInfo {
  private static final String STORE_TYPE_KEY = "storeType";
  private static final String IP_KEY = "ip";
  private static final String PORT_KEY = "port";
  private static final String USERNAME_KEY = "username";
  private static final String PASSWORD_KEY = "password";
  private static final String FOLDER_KEY = "folder";
  private static final String FILENAME_LENGTH_KEY = "filenameLength";
  private static final String FILE_LENGTH_KEY = "fileLength";

  private String prefix = "";
  private String storeType;
  private String ip;
  private Integer port;
  private String username;
  private String password;
  private String folder;
  private Integer filenameLength;
  private Long fileLength;

  public Long getFileLength() {
    return this.fileLength;
  }

  public String getFileLengthKey() {
    if (StringUtils.endsWith(this.prefix, ".")) {
      return this.prefix + TransmissionInfo.FILE_LENGTH_KEY;
    }
    return String.join(".", this.prefix, TransmissionInfo.FILE_LENGTH_KEY);
  }

  public Integer getFilenameLength() {
    return this.filenameLength;
  }

  public String getFilenameLengthKey() {
    if (StringUtils.endsWith(this.prefix, ".")) {
      return this.prefix + TransmissionInfo.FILENAME_LENGTH_KEY;
    }
    return String.join(".", this.prefix, TransmissionInfo.FILENAME_LENGTH_KEY);
  }

  public String getFolder() {
    return this.folder;
  }

  public String getFolderKey() {
    if (StringUtils.endsWith(this.prefix, ".")) {
      return this.prefix + TransmissionInfo.FOLDER_KEY;
    }
    return String.join(".", this.prefix, TransmissionInfo.FOLDER_KEY);
  }

  public String getIp() {
    return this.ip;
  }

  public String getIpKey() {
    if (StringUtils.endsWith(this.prefix, ".")) {
      return this.prefix + TransmissionInfo.IP_KEY;
    }
    return String.join(".", this.prefix, TransmissionInfo.IP_KEY);
  }

  public String getPassword() {
    return this.password;
  }

  public String getPasswordKey() {
    if (StringUtils.endsWith(this.prefix, ".")) {
      return this.prefix + TransmissionInfo.PASSWORD_KEY;
    }
    return String.join(".", this.prefix, TransmissionInfo.PASSWORD_KEY);
  }

  public Integer getPort() {
    return this.port;
  }

  public String getPortKey() {
    if (StringUtils.endsWith(this.prefix, ".")) {
      return this.prefix + TransmissionInfo.PORT_KEY;
    }
    return String.join(".", this.prefix, TransmissionInfo.PORT_KEY);
  }

  public String getPrefix() {
    return this.prefix;
  }

  public String getStoreType() {
    return this.storeType;
  }

  public String getStoreTypeKey() {
    if (StringUtils.endsWith(this.prefix, ".")) {
      return this.prefix + TransmissionInfo.STORE_TYPE_KEY;
    }
    return String.join(".", this.prefix, TransmissionInfo.STORE_TYPE_KEY);
  }

  public String getUsername() {
    return this.username;
  }

  public String getUsernameKey() {
    if (StringUtils.endsWith(this.prefix, ".")) {
      return this.prefix + TransmissionInfo.USERNAME_KEY;
    }
    return String.join(".", this.prefix, TransmissionInfo.USERNAME_KEY);
  }

  public void setFileLength(final Long fileLength) {
    this.fileLength = fileLength;
  }

  public void setFilenameLength(final Integer filenameLength) {
    this.filenameLength = filenameLength;
  }

  public void setFolder(final String folder) {
    this.folder = folder;
  }

  public void setIp(final String ip) {
    this.ip = ip;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public void setPort(final Integer port) {
    this.port = port;
  }

  public void setPrefix(final String prefix) {
    this.prefix = prefix;
  }

  public void setStoreType(final String storeType) {
    this.storeType = storeType;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

}
