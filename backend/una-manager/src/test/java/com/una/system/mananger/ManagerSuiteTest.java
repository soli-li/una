package com.una.system.mananger;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ PasswordPolicyControllerTest.class, CompanyControllerTest.class,
    UserControllerTest.class, GroupControllerTest.class, RoleControllerTest.class,
    PermissionsControllerTest.class, MenuControllerTest.class, UrlPermControllerTest.class })
public class ManagerSuiteTest {

}
