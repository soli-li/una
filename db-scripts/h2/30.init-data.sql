INSERT INTO S_PW_POLICY(ID, LABEL, DESCRIPTION, LETTERS, CASE_SENSITIVE, DIGITALS, NON_ALPHANUMERIC, LENGTH, MAXIMUM_AGE, REPEAT_COUNT, TRIES_COUNT, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('8f4ffca8-72b2-491f-9c1c-fbd8807a6ba6', '无限制', '无限制', 'N', 'N', 'N', 'N', 0, 0, 0, 0, '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_PW_POLICY(ID, LABEL, DESCRIPTION, LETTERS, CASE_SENSITIVE, DIGITALS, NON_ALPHANUMERIC, LENGTH, MAXIMUM_AGE, REPEAT_COUNT, TRIES_COUNT, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('4045a2c5-2d9f-4db6-be02-9fe7db053087', '严格策略', '需要包含字母<br/>需要含有大小写的字母<br/>需要包含数字<br/>需要包含一个以上特定符号：`~!@#$%^&*()_-={}|[]><<br/>需要大于等于 20 位<br/>有效期为 90 天<br/>不能与前 5 次密码相同<br/>允许错误 3 次数', 'Y', 'Y', 'Y', 'Y', 20, 90, 5, 3, '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_COMPANY(ID, NAME, SHORT_NAME, LEGAL_PERSON, ADDRESS, REMARK, PW_POLICY_ID, STATUS, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('c808faa6-6721-42a1-bdd7-3ca88141a67e', 'company manager', 'CM', '', '', '', '4045a2c5-2d9f-4db6-be02-9fe7db053087', 'E', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_USER(ID, COMPANY_ID, USERNAME, PASSWORD, ACCOUNT_NON_EXPIRED, ACCOUNT_NON_LOCKED, CREDENTIALS_NON_EXPIRED, DEFAULT_GROUP_ID, LAST_CHANGE_PW_DATE, LAST_LOGIN_DATE, FAILURE_COUNT, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '00001', '{bcrypt}$2a$10$ETC2xXbgTAtOOq.tg9TSyOg3NO3kogE28ZD3XxEATaHFISKRVnBQW', 'Y', 'Y', 'Y', '', '2023-01-01 00:00:00.000', NULL, 0, '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_USER_PROFILE(ID, USER_ID, REAL_NAME, PHONE, EMAIL, AVATAR, AVATAR_TYPE, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('0248f905-aac2-412d-82de-fa31755824dd', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', 'Soli', '', '', '', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_GROUP(ID, COMPANY_ID, PARENT_ID, NAME, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('97c6a622-8c1b-43a5-b0df-d03e373f67c0', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', NULL, 'Default', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_ROLE(ID, COMPANY_ID, AUTHORITY, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('4aa6350b-1cdb-4fba-99be-b2645b7276a2', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', 'ADMIN', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_GROUP_ROLE(ID, GROUP_ID, ROLE_ID, CREATED_USER_ID, CREATED_DATE) 
VALUES('f883421a-2856-440a-877a-609a801847c4', '97c6a622-8c1b-43a5-b0df-d03e373f67c0', '4aa6350b-1cdb-4fba-99be-b2645b7276a2', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_USER_ROLE(ID, USER_ID, ROLE_ID, CREATED_USER_ID, CREATED_DATE) 
VALUES('9e7b2367-3608-4c23-bfb1-280bc6f4c925', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '4aa6350b-1cdb-4fba-99be-b2645b7276a2', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_COMPANY(ID, NAME, SHORT_NAME, LEGAL_PERSON, ADDRESS, REMARK, PW_POLICY_ID, STATUS, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('c9090c56-befe-4fdd-b5eb-7c54cde29ff4', 'Test', 'T', '', '', '', '8f4ffca8-72b2-491f-9c1c-fbd8807a6ba6', 'E', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_USER(ID, COMPANY_ID, USERNAME, PASSWORD, ACCOUNT_NON_EXPIRED, ACCOUNT_NON_LOCKED, CREDENTIALS_NON_EXPIRED, DEFAULT_GROUP_ID, LAST_CHANGE_PW_DATE, LAST_LOGIN_DATE, FAILURE_COUNT, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('a2c8aead-d869-467d-be14-c4bdd5183d72', 'c9090c56-befe-4fdd-b5eb-7c54cde29ff4', 'T01', '{bcrypt}$2a$10$ETC2xXbgTAtOOq.tg9TSyOg3NO3kogE28ZD3XxEATaHFISKRVnBQW', 'Y', 'Y', 'Y', '', NULL, NULL, 0, '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_USER(ID, COMPANY_ID, USERNAME, PASSWORD, ACCOUNT_NON_EXPIRED, ACCOUNT_NON_LOCKED, CREDENTIALS_NON_EXPIRED, DEFAULT_GROUP_ID, LAST_CHANGE_PW_DATE, LAST_LOGIN_DATE, FAILURE_COUNT, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('31c4caf8-9269-4eab-adaa-82b8cd7d8b4c', 'c9090c56-befe-4fdd-b5eb-7c54cde29ff4', 'T02', '{bcrypt}$2a$10$ETC2xXbgTAtOOq.tg9TSyOg3NO3kogE28ZD3XxEATaHFISKRVnBQW', 'Y', 'Y', 'Y', '3ba66877-46c0-4631-bff1-1a598c51c659', NULL, NULL, 0, '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_GROUP(ID, COMPANY_ID, PARENT_ID, NAME, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('3ba66877-46c0-4631-bff1-1a598c51c659', 'c9090c56-befe-4fdd-b5eb-7c54cde29ff4', NULL, 'Basic Group', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_ROLE(ID, COMPANY_ID, AUTHORITY, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('bd35b1e4-e9e1-46bf-b0cc-5a554b581d65', 'c9090c56-befe-4fdd-b5eb-7c54cde29ff4', 'ADMIN', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_ROLE(ID, COMPANY_ID, AUTHORITY, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE) 
VALUES('8d94a586-11b0-47e5-87d2-3c8ef8e154a8', 'c9090c56-befe-4fdd-b5eb-7c54cde29ff4', 'Basic Role', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_USER_ROLE(ID, USER_ID, ROLE_ID, CREATED_USER_ID, CREATED_DATE) 
VALUES('4348fb88-73aa-4d24-8080-2834a71a5fc2', 'a2c8aead-d869-467d-be14-c4bdd5183d72', 'bd35b1e4-e9e1-46bf-b0cc-5a554b581d65', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_USER_GROUP(ID, USER_ID, GROUP_ID, CREATED_USER_ID, CREATED_DATE) 
VALUES('161f6297-ed33-42b7-95be-f4b3cee207b5', '31c4caf8-9269-4eab-adaa-82b8cd7d8b4c', '3ba66877-46c0-4631-bff1-1a598c51c659', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_GROUP_ROLE(ID, GROUP_ID, ROLE_ID, CREATED_USER_ID, CREATED_DATE) 
VALUES('929a1786-b493-40c0-8550-a26b17e2d521', '3ba66877-46c0-4631-bff1-1a598c51c659', '8d94a586-11b0-47e5-87d2-3c8ef8e154a8', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');
/* ************************************************************************************************************************************************************************************************************************* */
/* special permissions */
/* handle cross-company data */
INSERT INTO S_PERMISSIONS(ID, NAME, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE)
VALUES('not-include-company', 'not include company', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

/* only root user can be add special data */
INSERT INTO S_PERMISSIONS(ID, NAME, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE)
VALUES('superuser-add-data', 'superuser add data', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

/* system basic permissions for all role */
INSERT INTO S_PERMISSIONS(ID, NAME, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE)
VALUES('global-base-function', 'global-base', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_PERMISSIONS(ID, NAME, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE)
VALUES('9f282321-68bd-4816-b913-acbfddf6bd11', 'super administrator''s permissions', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_PERMISSIONS(ID, NAME, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE)
VALUES('dc1985ea-3bf5-4d2e-9890-440780833a94', 'company administrator''s permissions', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_PERMISSIONS(ID, NAME, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE)
VALUES('468fb4d7-50be-4555-a05e-a6e3a2d9f75f', 'company administrator''s menu', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_PERMISSIONS(ID, NAME, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE)
VALUES('ed4af4ea-add9-4d25-9d48-1ef635d77bf0', 'query role/group/user ''s relation', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_PERMISSIONS(ID, NAME, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE)
VALUES('5de3c002-c50a-4142-a8ba-f89225fd61fb', 'user search', 'E', 'for search user''s url', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_PERMISSIONS(ID, NAME, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE)
VALUES('badcc479-9d69-4d76-94a6-450704f48148', 'group search', 'E', 'for search group''s url', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

INSERT INTO S_PERMISSIONS(ID, NAME, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE, UPDATED_USER_ID, UPDATED_DATE)
VALUES('3fbb1174-f4bb-4962-86b4-0c55631b8ad1', 'role search', 'E', 'for search role''s url', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000', NULL, NULL);

/* ************************************************************************************************************************************************************************************************************************* */
/* base function */
INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('10a96960-c3d0-4495-a5e5-ed7eabae55f0', 'user->current', '/user/current', 'global-base-function', '0', 'E', 'query user base info for current login user', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('06a06b00-2899-4efe-bf20-ed82a654b81e', 'menu->current', '/menu/current', 'global-base-function', '0', 'E', 'query menu data for current login user', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('6db88a64-2b9a-47e0-b99b-f0ae99b5957d', 'user->update self', '/user/update', 'global-base-function', '0', 'E', 'update for user self', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('d7d96fb3-88b3-479e-92a4-222ef0fc1bda', 'user->change password', '/user/changePassword', 'global-base-function', '0', 'E', 'user update self password', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('46cc414c-5c78-415f-8641-8bff4259499c', 'user->change group', '/user/changeGroup', 'global-base-function', '0', 'E', 'user change current group', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('0da64aa1-b34d-40e7-81dc-0245ab5011c9', 'user->current avatar', '/user/currentAvatar', 'global-base-function', '0', 'E', 'get current user avatar', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* password policy */
INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('54937058-077c-4328-9720-1eec2949350e', 'pwPolicy->all', '/pwPolicy/all', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'get all password policy data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('50c8164b-5c5c-4756-8874-116b22e7c63a', 'pwPolicy->save', '/pwPolicy/save', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'save password policy data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* company */
INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('8fd08d2b-f9fd-4a48-a93e-bcb1315ad6f3', 'company->search', '/company/search', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'query company data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('1113147e-8afa-4a0a-ad4d-78da546a8314', 'company->exist', '/company/exist', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'query company exist', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('47c54679-64b7-4ac3-9bcc-b3c07bd53166', 'company->save', '/company/save', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'save company data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* user */
INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('20789c54-5cdc-4ceb-a003-36ec8d6d443b', 'user->search', '/user/search', '5de3c002-c50a-4142-a8ba-f89225fd61fb', '0', 'E', 'query user data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('d6be54bb-d842-4b25-a5e2-6f1bdc448660', 'user->exist', '/user/exist', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'query user exist', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('3dcb6b61-3e3d-45d7-88c9-e443b5066753', 'user->save', '/user/save', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'save user data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('13e8f452-608f-49ca-97c8-f2523139a429', 'user->resetPassword', '/user/resetPassword', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'manager reset user''s password', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('6dbac7e5-b2cc-4462-990e-fcb007cd516c', 'user->get online session', '/user/getOnlineUserSession/*/*', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '10', 'E', 'get online user session list', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('2f323f0d-964a-48e9-ba9e-92c0206492d7', 'user->take out session', '/user/takeOutUser', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'take out session for logined user', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('897b0280-47be-4d2c-85ce-7951537a94af', 'user->getAvatar', '/user/getAvatar', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'get user avatar', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* group */
INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('f2c3709d-2186-4272-8be9-fd5b24bf4596', 'group->search', '/group/search', 'badcc479-9d69-4d76-94a6-450704f48148', '0', 'E', 'query group data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('8e56d12b-6b98-4b9e-ad83-df9d467a26bf', 'group->exist', '/group/exist', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'query group exist', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('8c9088e9-01fc-44de-89e0-15703e6139ef', 'group->save', '/group/save', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'save group data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* role */
INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('2c44a0cb-52fe-4628-a3db-718e47ad7594', 'role->search', '/role/search', '3fbb1174-f4bb-4962-86b4-0c55631b8ad1', '0', 'E', 'query role data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('1fa9ea11-159c-437b-be49-a81382a47a99', 'role->exist', '/role/exist', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'query role exist', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('5ac84397-29ef-42ac-b99e-77b0add327cf', 'role->save', '/role/save', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'save role data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* permissions */
INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('1ae43e37-0c37-46a7-b776-3bca10ef42c4', 'permissions->search', '/perm/search', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'query permissions data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('1abaf7cc-8db3-4ec8-bfdb-469de5c0bc02', 'permissions->save', '/perm/save', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'save permissions data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* menu */
INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('e0bdd6f2-bb2b-44ec-b351-1faa7e8b0a90', 'menu->search', '/menu/search', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'query menu data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('119d7cd2-d27c-42b3-9147-0e5882cabd80', 'menu->exist', '/menu/exist', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'query menu exist', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('4f7b25e5-ca1c-4768-bab5-a74ad686653d', 'menu->save', '/menu/save', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'save menu data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* url permissions */
INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('c5cc9f14-39be-461e-bc97-e826bde7a016', 'urlPerm->search', '/url/search', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'query url permissions data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('6170adc3-f1c7-4590-9fe3-9ce584d85941', 'urlPerm->save', '/url/save', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'save url permissions data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* configuration */
INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('5c41408c-1307-4a3e-bcde-c6798da3fbfd', 'conf->search', '/conf/search', 'dc1985ea-3bf5-4d2e-9890-440780833a94', 0, 'E', 'query configuration data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('0e578e1e-7f72-4fa2-a4ac-5eb73ba33bab', 'conf->exist', '/conf/exist', 'dc1985ea-3bf5-4d2e-9890-440780833a94', 0, 'E', 'query configuration exist', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('e202ca54-a31e-47f1-abb3-9c06cca1f1f6', 'conf->save', '/conf/save', 'dc1985ea-3bf5-4d2e-9890-440780833a94', 0, 'E', 'save configuration data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* user/group/role relation */
INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('70fa0471-4cb9-4729-87db-c5820b090d65', 'relation->user''s group', '/group/findByUser', 'ed4af4ea-add9-4d25-9d48-1ef635d77bf0', '0', 'E', 'query user''s group data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('de62e425-d9a4-4a9d-8b8c-7b666c03ecf5', 'relation->user''s role', '/role/findByUser', 'ed4af4ea-add9-4d25-9d48-1ef635d77bf0', '0', 'E', 'query user''s role data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('49a5a7ba-2178-4591-ab7d-2a997b011a62', 'relation->group''s role', '/role/findByGroup', 'ed4af4ea-add9-4d25-9d48-1ef635d77bf0', '0', 'E', 'query group''s role data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('f4e6414b-6cd0-4e3f-bc8e-064729894c00', 'relation->group''s user', '/user/findByGroup', 'ed4af4ea-add9-4d25-9d48-1ef635d77bf0', '0', 'E', 'query group''s user data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('806c9879-d305-4b4b-9b6a-e048a768db71', 'relation->role''s user', '/user/findByRole', 'ed4af4ea-add9-4d25-9d48-1ef635d77bf0', '0', 'E', 'query role''s user data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('8fe1347b-8bc4-4b2c-80e6-bbd1a3f84734', 'relation->role''s group', '/group/findByRole', 'ed4af4ea-add9-4d25-9d48-1ef635d77bf0', '0', 'E', 'query role''s group data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('a111afbe-7ea3-44a0-b9d6-355507c3abfa', 'relation->role''s function', '/perm/findFunctionByRole', 'ed4af4ea-add9-4d25-9d48-1ef635d77bf0', '0', 'E', 'query role''s relation function data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('1ae0877b-006d-4881-b8ea-f2f7cd68d354', 'relation->save user relation', '/user/updateRelation', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'save user relation data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('e5ffd5c4-eccf-412b-ba1c-043acdb6b6a1', 'relation->save group relation', '/group/updateRelation', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'save group relation data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('c43ca1a5-cbc0-4383-b984-00118f7bca68', 'relation->save role relation', '/role/updateRelation', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '0', 'E', 'save role relation data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('32d1e2fc-505b-4976-9922-7bf2a9b65377', 'relation->permissions''s role', '/role/findByPerm', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'query permissions''s role data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('e2bd2278-31a3-4892-b366-4c877d3fd5c8', 'relation->save permissions''s role', '/perm/updateRelation', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'save permissions''s role data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('713ec82f-5227-45df-8f90-4aeaff8645b8', 'permissions->findByRole', '/perm/findByRole', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'query permissions data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('10734cde-e1df-4108-aaa1-5fc8386be392', 'role->save role''s permissions', '/role/updateRelationForPerm', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'save role''s permissions data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_URL_PERM(ID, NAME, URI, PERMISSIONS_ID, SORT, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('b5f16cb5-3bfd-45ff-96dc-394d3ba86b99', 'configuration->refresh', '/conf/refresh', '9f282321-68bd-4816-b913-acbfddf6bd11', '0', 'E', 'null', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* ************************************************************************************************************************************************************************************************************************* */
/* for root administrator*/
INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('578c3196-c543-4292-ba26-0d037dec84fb', 'root-system-manage', 'bars|outline', 0, '/root-system-manage', '', '9f282321-68bd-4816-b913-acbfddf6bd11', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('985e4e63-6c18-42d1-a5de-0251524bf49f', 'pwPolicy-manage', 'insurance|outline', 10, '/pwPolicy-manage-for-manager', '578c3196-c543-4292-ba26-0d037dec84fb', '9f282321-68bd-4816-b913-acbfddf6bd11', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('8bbce648-e118-41ed-80df-458caad09e92', 'company-manage', 'home|outline', 20, '/company-manage-for-manager', '578c3196-c543-4292-ba26-0d037dec84fb', '9f282321-68bd-4816-b913-acbfddf6bd11', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('76c6b2ff-9564-4a9c-9358-00e6a5b0bc2e', 'user-manage', 'user|outline', 30, '/user-manage-for-manager', '578c3196-c543-4292-ba26-0d037dec84fb', '9f282321-68bd-4816-b913-acbfddf6bd11', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('3b9bceb9-602f-4d9a-866c-cf2f005b5a99', 'group-manage', 'team|outline', 40, '/group-manage-for-manager', '578c3196-c543-4292-ba26-0d037dec84fb', '9f282321-68bd-4816-b913-acbfddf6bd11', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('30265178-953d-4497-908e-c8bae9412f7e', 'role-manage', 'audit|outline', 50, '/role-manage-for-manager', '578c3196-c543-4292-ba26-0d037dec84fb', '9f282321-68bd-4816-b913-acbfddf6bd11', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('191bf1de-8a59-4681-b762-51d04d95e1ff', 'perm-manage', 'safety-certificate|outline', 60, '/perm-manage', '578c3196-c543-4292-ba26-0d037dec84fb', '9f282321-68bd-4816-b913-acbfddf6bd11', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('9804e3d1-88d5-4718-8b9e-d69ccc2fcb09', 'menu-manage', 'menu|outline', 70, '/menu-manage', '578c3196-c543-4292-ba26-0d037dec84fb', '9f282321-68bd-4816-b913-acbfddf6bd11', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('b591227d-0868-4c1a-94aa-c899509c93ab', 'url-manage', 'link|outline', 80, '/url-manage', '578c3196-c543-4292-ba26-0d037dec84fb', '9f282321-68bd-4816-b913-acbfddf6bd11', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* for company administrator*/
INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('7b72a151-d03c-4149-8e91-4774a49b5697', 'system-manage', 'bars|outline', 0, '/system-manage', '', '468fb4d7-50be-4555-a05e-a6e3a2d9f75f', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('e0424035-f763-4e7c-9727-b3b47e3ffe01', 'pwPolicy-manage', 'insurance|outline', 10, '/pwPolicy-manage', '7b72a151-d03c-4149-8e91-4774a49b5697', '468fb4d7-50be-4555-a05e-a6e3a2d9f75f', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('5ebfbd8b-b278-43bc-9109-ee152620481e', 'company-manage', 'home|outline', 20, '/company-manage', '7b72a151-d03c-4149-8e91-4774a49b5697', '468fb4d7-50be-4555-a05e-a6e3a2d9f75f', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('099050e4-69de-4236-b70e-3ad8ea24bc89', 'user-manage', 'user|outline', 30, '/user-manage', '7b72a151-d03c-4149-8e91-4774a49b5697', '468fb4d7-50be-4555-a05e-a6e3a2d9f75f', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('5687860e-f1e0-4989-b5ac-4638652c6cb6', 'group-manage', 'team|outline', 40, '/group-manage', '7b72a151-d03c-4149-8e91-4774a49b5697', '468fb4d7-50be-4555-a05e-a6e3a2d9f75f', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('5d46af96-0ffe-404f-9c16-0975bf3e40c4', 'role-manage', 'audit|outline', 50, '/role-manage', '7b72a151-d03c-4149-8e91-4774a49b5697', '468fb4d7-50be-4555-a05e-a6e3a2d9f75f', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('98f86723-4ded-4704-b192-5ffe3f4a76fa', 'configuration-manage', 'setting|outline', 5, '/config-manage-for-manager', '578c3196-c543-4292-ba26-0d037dec84fb', '9f282321-68bd-4816-b913-acbfddf6bd11', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_MENU(ID, NAME, ICON, SORT, FRONT_END_URI, PARENT_ID, PERMISSIONS_ID, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('1a7b5915-2dc4-4b5a-bbf1-5fd53dda778b', 'configuration-manage', 'setting|outline', 5, '/config-manage', '7b72a151-d03c-4149-8e91-4774a49b5697', '468fb4d7-50be-4555-a05e-a6e3a2d9f75f', 'E', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

/* ************************************************************************************************************************************************************************************************************************* */
INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('1ab8d009-172c-4713-b037-628ae73f5e13', '4aa6350b-1cdb-4fba-99be-b2645b7276a2', 'not-include-company', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('6dd799fb-f6b7-4303-a2f3-2ebe4ee1ddac', '4aa6350b-1cdb-4fba-99be-b2645b7276a2', 'superuser-add-data', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('9493f3de-0234-49ba-bdb3-eac9be6f68b8', '4aa6350b-1cdb-4fba-99be-b2645b7276a2', 'global-base-function', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('88738167-7b61-42d4-b690-480515187c9a', '4aa6350b-1cdb-4fba-99be-b2645b7276a2', '9f282321-68bd-4816-b913-acbfddf6bd11', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('cfad5ad0-11f4-4a13-9378-b63a1db1e9a3', '4aa6350b-1cdb-4fba-99be-b2645b7276a2', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('e6a9c0e1-a05b-4ec8-9d89-40c360bb657d', '4aa6350b-1cdb-4fba-99be-b2645b7276a2', 'badcc479-9d69-4d76-94a6-450704f48148', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('db844c74-6c2a-4864-b354-c2db7f5c69ec', '4aa6350b-1cdb-4fba-99be-b2645b7276a2', 'ed4af4ea-add9-4d25-9d48-1ef635d77bf0', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('62a8d1a5-6bec-4a56-aa86-48eec2750ef3', '4aa6350b-1cdb-4fba-99be-b2645b7276a2', '5de3c002-c50a-4142-a8ba-f89225fd61fb', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('2336a786-82a5-4d8a-a31b-20c3f5d690b7', '4aa6350b-1cdb-4fba-99be-b2645b7276a2', '3fbb1174-f4bb-4962-86b4-0c55631b8ad1', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('31b0c36c-f5ff-4adb-beec-0c14d97d11c7', 'bd35b1e4-e9e1-46bf-b0cc-5a554b581d65', 'global-base-function', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('9f04edcf-4cc4-40d1-bb5b-cf9272be8b66', 'bd35b1e4-e9e1-46bf-b0cc-5a554b581d65', 'dc1985ea-3bf5-4d2e-9890-440780833a94', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('eb720a12-b77e-495f-ae92-0ac20bf0a84e', 'bd35b1e4-e9e1-46bf-b0cc-5a554b581d65', '468fb4d7-50be-4555-a05e-a6e3a2d9f75f', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('2e36403d-b948-4256-8874-f9e18ad9a261', 'bd35b1e4-e9e1-46bf-b0cc-5a554b581d65', 'ed4af4ea-add9-4d25-9d48-1ef635d77bf0', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('b966bfe6-5be5-407b-b243-ba5d1329cdea', 'bd35b1e4-e9e1-46bf-b0cc-5a554b581d65', '5de3c002-c50a-4142-a8ba-f89225fd61fb', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('88565913-2d26-411b-a649-268f30d4bb32', 'bd35b1e4-e9e1-46bf-b0cc-5a554b581d65', '3fbb1174-f4bb-4962-86b4-0c55631b8ad1', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('abf45fae-9881-4544-826f-1d3daf4ba3a4', 'bd35b1e4-e9e1-46bf-b0cc-5a554b581d65', 'badcc479-9d69-4d76-94a6-450704f48148', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_ROLE_PERM(ID, ROLE_ID, PERMISSIONS_ID, CREATED_USER_ID, CREATED_DATE)
VALUES('488a4d2e-fecc-41dc-81d0-1e1e5f31eb17', '8d94a586-11b0-47e5-87d2-3c8ef8e154a8', 'global-base-function', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00.000');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('49e3fbd2-a3d8-4416-96a5-e168466a93e7', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '账号最大会话数', 'system-config.security.session.maximumSessions', '1', 'number', 'E', '小于0则无限制', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('8e8bfb0a-04ad-46dd-b1ce-eb1421f88fcd', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '登录踢会话机制', 'system-config.security.session.maxSessionsPreventsLogin', 'N', 'boolean', 'E', '为N时，会踢掉最早登录的session，为Y时，此次登录失败', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('369ff443-57da-485a-a214-6eda363afc0b', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '头像->文件名长度', 'system-config.user.avatar.filenameLength', '128', 'number', 'D', '文件名最长限制', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('1a213451-bc1c-4b04-873a-48ed779445db', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '头像->文件大小', 'system-config.user.avatar.fileLength', '2097152', 'number', 'D', '文件最大2MB', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('bf5901f6-ce60-4ab3-a937-ba28bae002d6', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '头像->存储方式', 'system-config.user.avatar.storeType', 'file', 'string', 'D', '支持file/ftp/ftp_pasv/ftps/ftps_pasv/sftp', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('6775c182-2ab2-4af3-8088-6dc1a0923308', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '头像->账号', 'system-config.user.avatar.username', '', 'string', 'D', '文件服务器账号', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('390d8a2e-82da-4219-be37-83dfe5fa148a', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '头像->密码', 'system-config.user.avatar.password', '', 'string', 'D', '文件服务器密码', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('db1c3556-ec17-447f-a8d3-0737737bb896', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '头像->存放目录', 'system-config.user.avatar.folder', '', 'string', 'D', '', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('ed453c82-8f44-4a67-a905-15327920a797', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '头像->ip地址', 'system-config.user.avatar.ip', '', 'string', 'D', '文件服务器地址', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('aa7a0d87-7db3-48d6-b721-f23922107421', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '头像->端口', 'system-config.user.avatar.port', '0', 'number', 'D', '文件服务器端口', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('77b998bc-cc71-4fcb-9933-255492b83903', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '密码->密码长度', 'system-config.new-password.length', '8', 'number', 'E', '初始化及重置时的密码长度', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('32b9c411-c86d-4b82-9a1c-f2857c9416bc', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '密码->包含字母', 'system-config.new-password.letters', 'Y', 'boolean', 'E', '初始化及重置时的密码是否包含字母', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('6c6ae3fd-d75d-4813-a5b0-0fcb03fe4eb8', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '密码->包含数字', 'system-config.new-password.digitals', 'Y', 'boolean', 'E', '初始化及重置时的密码是否包含数字', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('af4e3fa2-7fa5-4aa3-9de2-69d762fa445f', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '密码->区分大小写', 'system-config.new-password.case-sensitive', 'Y', 'boolean', 'E', '初始化及重置时的密码是否区分大小写', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');

INSERT INTO S_CONF(ID, COMPANY_ID, NAME, CONF_KEY, CONF_VALUE, VALUE_TYPE, STATUS, REMARK, CREATED_USER_ID, CREATED_DATE)
VALUES('516014da-6b27-4f76-9806-fe462c7af116', 'c808faa6-6721-42a1-bdd7-3ca88141a67e', '密码->包含符号', 'system-config.new-password.non-alphanumeric', 'Y', 'boolean', 'E', '初始化及重置时的密码是否包含符号', '8ce4f1e7-0cc8-44d4-ab65-d946e9be1ad6', '2022-01-01 00:00:00');
