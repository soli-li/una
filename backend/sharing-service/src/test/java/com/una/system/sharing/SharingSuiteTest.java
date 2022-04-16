package com.una.system.sharing;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ ConfigurationHandlerTest.class, ShortTermHandlerTest.class, UserHandlerTest.class })
public class SharingSuiteTest {

}
