package com.una.common;

import com.una.common.utils.LogUtil;

public interface Constants {
  int PAGE_NUMBER = 1;
  int PAGE_SIZE = 10;
  String SORT_ASC = "ascend,asc";
  String SORT_DESC = "descend,desc";

  String ENABLE = "E";
  String DISABLE = "D";
  String TRUE = "Y";
  String FALSE = "N";

  String LOG_MARK = LogUtil.getDefaultLogMark();
  String MEDIA_TYPE_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
  String MEDIA_TYPE_MULTIPART_FORM_DATA = "multipart/form-data";

  String SESSION_KEY = "sessionId";
  String TEMP_ACTION_EXPIRED = "expired";
  String REMOTE_ADDRESS = "remote-address";

  String RESP_SUCCESS_CODE = "000000";
  String RESP_FAILURE_CODE_NO_SESSION = "999998";
  String RESP_FAILURE_CODE = "999999";

}
