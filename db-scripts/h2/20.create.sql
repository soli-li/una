CREATE TABLE IF NOT EXISTS S_PW_POLICY (
  ID VARCHAR(64) NOT NULL,
  LABEL VARCHAR(64),
  DESCRIPTION VARCHAR(1024),
  LETTERS VARCHAR(8) DEFAULT 'N',
  CASE_SENSITIVE VARCHAR(8) DEFAULT 'N',
  DIGITALS VARCHAR(8) DEFAULT 'N',
  NON_ALPHANUMERIC VARCHAR(8) DEFAULT 'N',
  LENGTH INT DEFAULT 0,
  MAXIMUM_AGE INT DEFAULT 0,
  REPEAT_COUNT INT DEFAULT 0,
  TRIES_COUNT INT DEFAULT 0,
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP,
  UPDATED_USER_ID VARCHAR(64),
  UPDATED_DATE TIMESTAMP
);
ALTER TABLE S_PW_POLICY ADD CONSTRAINT IF NOT EXISTS S_PW_POLICY_PK PRIMARY KEY(ID);

CREATE TABLE IF NOT EXISTS S_CP_RECORD (
  ID VARCHAR(64) NOT NULL,
  USERNAME VARCHAR(64) NOT NULL,
  PASSWORD VARCHAR(256) NOT NULL,
  CHANGED_DATE TIMESTAMP
);
ALTER TABLE S_CP_RECORD ADD CONSTRAINT IF NOT EXISTS S_CP_RECORD_PK PRIMARY KEY(ID);
CREATE INDEX IF NOT EXISTS S_CP_RECORD_IX ON S_CP_RECORD(USERNAME);

CREATE TABLE IF NOT EXISTS S_COMPANY (
  ID VARCHAR(64) NOT NULL,
  NAME VARCHAR(512) NOT NULL,
  SHORT_NAME VARCHAR(128),
  LEGAL_PERSON VARCHAR(128),
  ADDRESS VARCHAR(128),
  REMARK VARCHAR(512),
  PW_POLICY_ID VARCHAR(64),
  STATUS VARCHAR(8) DEFAULT 'E',
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP,
  UPDATED_USER_ID VARCHAR(64),
  UPDATED_DATE TIMESTAMP
);
ALTER TABLE S_COMPANY ADD CONSTRAINT IF NOT EXISTS S_COMPANY_PK PRIMARY KEY(ID);
CREATE UNIQUE INDEX IF NOT EXISTS S_COMPANY_NAME_UX ON S_COMPANY(NAME);
CREATE UNIQUE INDEX IF NOT EXISTS S_COMPANY_SHORT_NAME_UX ON S_COMPANY(SHORT_NAME);

CREATE TABLE IF NOT EXISTS S_USER (
  ID VARCHAR(64) NOT NULL,
  COMPANY_ID VARCHAR(64),
  USERNAME VARCHAR(64) NOT NULL,
  PASSWORD VARCHAR(256) NOT NULL,
  ACCOUNT_NON_EXPIRED VARCHAR(8) DEFAULT 'N',
  ACCOUNT_NON_LOCKED VARCHAR(8) DEFAULT 'N',
  CREDENTIALS_NON_EXPIRED VARCHAR(8) DEFAULT 'N',
  DEFAULT_GROUP_ID VARCHAR(64),
  LAST_LOGIN_DATE TIMESTAMP,
  LAST_CHANGE_PW_DATE TIMESTAMP,
  FAILURE_COUNT INT DEFAULT 0,
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP,
  UPDATED_USER_ID VARCHAR(64),
  UPDATED_DATE TIMESTAMP
);
ALTER TABLE S_USER ADD CONSTRAINT IF NOT EXISTS S_USER_PK PRIMARY KEY(ID);
CREATE UNIQUE INDEX IF NOT EXISTS S_USER_NAME_UX ON S_USER(USERNAME);
CREATE INDEX IF NOT EXISTS S_USER_COMPANY_IX ON S_USER(COMPANY_ID);

CREATE TABLE IF NOT EXISTS S_USER_GROUP (
  ID VARCHAR(64) NOT NULL,
  USER_ID VARCHAR(64) NOT NULL,
  GROUP_ID VARCHAR(64) NOT NULL,
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP
);
ALTER TABLE S_USER_GROUP ADD CONSTRAINT IF NOT EXISTS S_USER_GROUP_PK PRIMARY KEY(ID);
CREATE UNIQUE INDEX IF NOT EXISTS S_USER_GROUP_UX ON S_USER_GROUP(USER_ID, GROUP_ID);
CREATE INDEX IF NOT EXISTS S_USER_GROUP_USER_IX ON S_USER_GROUP(USER_ID);
CREATE INDEX IF NOT EXISTS S_USER_GROUP_GROUP_IX ON S_USER_GROUP(GROUP_ID);

CREATE TABLE IF NOT EXISTS S_USER_ROLE (
  ID VARCHAR(64) NOT NULL,
  USER_ID VARCHAR(64) NOT NULL,
  ROLE_ID VARCHAR(64) NOT NULL,
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP
);
ALTER TABLE S_USER_ROLE ADD CONSTRAINT IF NOT EXISTS S_USER_ROLE_PK PRIMARY KEY(ID);
CREATE UNIQUE INDEX IF NOT EXISTS S_USER_ROLE_UX ON S_USER_ROLE(USER_ID, ROLE_ID);
CREATE INDEX IF NOT EXISTS S_USER_ROLE_USER_IX ON S_USER_ROLE(USER_ID);
CREATE INDEX IF NOT EXISTS S_USER_ROLE_ROLE_IX ON S_USER_ROLE(ROLE_ID);

CREATE TABLE IF NOT EXISTS S_GROUP (
  ID VARCHAR(64) NOT NULL,
  COMPANY_ID VARCHAR(64),
  PARENT_ID VARCHAR(64),
  NAME VARCHAR(64) NOT NULL,
  STATUS VARCHAR(8) DEFAULT 'E',
  REMARK VARCHAR(512),
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP,
  UPDATED_USER_ID VARCHAR(64),
  UPDATED_DATE TIMESTAMP
);
ALTER TABLE S_GROUP ADD CONSTRAINT IF NOT EXISTS S_GROUP_PK PRIMARY KEY(ID); 
CREATE INDEX IF NOT EXISTS S_GROUP_COMPANY_IX ON S_GROUP(COMPANY_ID);
CREATE UNIQUE INDEX IF NOT EXISTS S_GROUP_NAME_UX ON S_GROUP(COMPANY_ID, NAME);

CREATE TABLE IF NOT EXISTS S_GROUP_ROLE (
  ID VARCHAR(64) NOT NULL,
  GROUP_ID VARCHAR(64) NOT NULL,
  ROLE_ID VARCHAR(64) NOT NULL,
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP
);
ALTER TABLE S_GROUP_ROLE ADD CONSTRAINT IF NOT EXISTS S_GROUP_ROLE_PK PRIMARY KEY(ID);
CREATE UNIQUE INDEX IF NOT EXISTS S_GROUP_ROLE_UX ON S_GROUP_ROLE(GROUP_ID, ROLE_ID);
CREATE INDEX IF NOT EXISTS S_GROUP_ROLE_GROUP_IX ON S_GROUP_ROLE(GROUP_ID);
CREATE INDEX IF NOT EXISTS S_GROUP_ROLE_ROLE_IX ON S_GROUP_ROLE(ROLE_ID);

CREATE TABLE IF NOT EXISTS S_ROLE (
  ID VARCHAR(64) NOT NULL,
  COMPANY_ID VARCHAR(64),
  AUTHORITY VARCHAR(64) NOT NULL,
  STATUS VARCHAR(8) DEFAULT 'E',
  REMARK VARCHAR(512),
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP,
  UPDATED_USER_ID VARCHAR(64),
  UPDATED_DATE TIMESTAMP
);
ALTER TABLE S_ROLE ADD CONSTRAINT IF NOT EXISTS S_ROLE_PK PRIMARY KEY(ID);
CREATE INDEX IF NOT EXISTS S_ROLE_COMPANY_IX ON S_ROLE(COMPANY_ID);
CREATE UNIQUE INDEX IF NOT EXISTS S_ROLE_NAME_UX ON S_ROLE(COMPANY_ID, AUTHORITY);

CREATE TABLE IF NOT EXISTS S_USER_PROFILE (
  ID VARCHAR(64) NOT NULL,
  USER_ID VARCHAR(64) NOT NULL,
  REAL_NAME VARCHAR(64),
  PHONE VARCHAR(512),
  EMAIL VARCHAR(128),
  AVATAR VARCHAR(128),
  AVATAR_TYPE VARCHAR(32),
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP,
  UPDATED_USER_ID VARCHAR(64),
  UPDATED_DATE TIMESTAMP
);
ALTER TABLE S_USER_PROFILE ADD CONSTRAINT IF NOT EXISTS S_USER_PROFILE_PK PRIMARY KEY(ID);
CREATE UNIQUE INDEX IF NOT EXISTS S_USER_PROFILE_UX ON S_USER_PROFILE(USER_ID);

CREATE TABLE IF NOT EXISTS S_ROLE_PERM (
  ID VARCHAR(64) NOT NULL,
  ROLE_ID VARCHAR(64) NOT NULL,
  PERMISSIONS_ID VARCHAR(64) NOT NULL,
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP
);
ALTER TABLE S_ROLE_PERM ADD CONSTRAINT IF NOT EXISTS S_ROLE_PERM_PK PRIMARY KEY(ID);
CREATE UNIQUE INDEX IF NOT EXISTS S_ROLE_PERM_UX ON S_ROLE_PERM(ROLE_ID, PERMISSIONS_ID);
CREATE INDEX IF NOT EXISTS S_ROLE_PERM_ROLE_IX ON S_ROLE_PERM(ROLE_ID);
CREATE INDEX IF NOT EXISTS S_ROLE_PERM_PERM_IX ON S_ROLE_PERM(PERMISSIONS_ID);

CREATE TABLE IF NOT EXISTS S_PERMISSIONS (
  ID VARCHAR(64) NOT NULL,
  NAME VARCHAR(64) NOT NULL,
  STATUS VARCHAR(8) DEFAULT 'E',
  REMARK VARCHAR(512),
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP,
  UPDATED_USER_ID VARCHAR(64),
  UPDATED_DATE TIMESTAMP
);
ALTER TABLE S_PERMISSIONS ADD CONSTRAINT IF NOT EXISTS S_PERMISSIONS_PK PRIMARY KEY(ID);

CREATE TABLE IF NOT EXISTS S_MENU (
  ID VARCHAR(64) NOT NULL,
  NAME VARCHAR(64) NOT NULL,
  ICON VARCHAR(128),
  SORT INT DEFAULT 0,
  FRONT_END_URI VARCHAR(64) NOT NULL,
  PARENT_ID VARCHAR(64),
  PERMISSIONS_ID VARCHAR(64),
  STATUS VARCHAR(8) DEFAULT 'E',
  REMARK VARCHAR(512),
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP
);
ALTER TABLE S_MENU ADD CONSTRAINT IF NOT EXISTS S_MENU_PK PRIMARY KEY(ID);
CREATE INDEX IF NOT EXISTS S_MENU_IX ON S_MENU(PERMISSIONS_ID);
CREATE UNIQUE INDEX IF NOT EXISTS S_MENU_UX ON S_MENU(FRONT_END_URI);
 
CREATE TABLE IF NOT EXISTS S_URL_PERM (
  ID VARCHAR(64) NOT NULL,
  NAME VARCHAR(64) NOT NULL,
  URI VARCHAR(128) NOT NULL,
  PERMISSIONS_ID VARCHAR(64),
  SORT INT DEFAULT 0,
  STATUS VARCHAR(8) DEFAULT 'E',
  REMARK VARCHAR(512),
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP
);
ALTER TABLE S_URL_PERM ADD CONSTRAINT IF NOT EXISTS S_URL_PERM_PK PRIMARY KEY(ID);
CREATE INDEX IF NOT EXISTS S_URL_PERM_IX ON S_URL_PERM(PERMISSIONS_ID);

CREATE TABLE IF NOT EXISTS S_CONF (
  ID VARCHAR(64) NOT NULL,
  COMPANY_ID VARCHAR(64) NOT NULL,
  NAME VARCHAR(128),
  CONF_KEY VARCHAR(128) NOT NULL,
  CONF_VALUE VARCHAR(2048),
  VALUE_TYPE VARCHAR(64),
  STATUS VARCHAR(8) DEFAULT 'E',
  REMARK VARCHAR(512),
  CREATED_USER_ID VARCHAR(64),
  CREATED_DATE TIMESTAMP
);
ALTER TABLE S_CONF ADD CONSTRAINT IF NOT EXISTS S_CONF_PK PRIMARY KEY(ID);
CREATE INDEX IF NOT EXISTS S_CONF_IX ON S_CONF(COMPANY_ID);
CREATE UNIQUE INDEX IF NOT EXISTS S_CONF_UX ON S_CONF(COMPANY_ID, CONF_KEY);