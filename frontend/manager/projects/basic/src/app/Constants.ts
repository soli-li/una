import { environment } from '../environments/environment';

const BASIC_TITLE = 'Manager';

const TRUE = 'Y';
const FALSE = 'N';
const ENABLE = 'E';
const DISABLE = 'D';
const PAGE_SIZE = 10;
const UTF_8_ENCODING = 'UTF-8';
const MEDIA_TYPE_X_WWW_FORM_URLENCODED = 'application/x-www-form-urlencoded';
const MEDIA_TYPE_JSON = 'application/json';
const MEDIA_TYPE_TEXT_PLAIN = 'text/plain';
const SERVER_API_URL = environment.SERVER_API_URL;
const SESSION_ID = 'sessionId';
const ROUTER_LOGIN = '';
const ROUTER_MAIN = 'main';
const HEAD_TOKEN = 'x-auth-token';

const EnableDisableMap = new Map<string, string>();
EnableDisableMap.set(ENABLE, '可用');
EnableDisableMap.set(DISABLE, '不可用');

export const Constants = {
  BASIC_TITLE,
  TRUE,
  FALSE,
  EnableDisableMap,
  ENABLE,
  DISABLE,
  PAGE_SIZE,
  UTF_8_ENCODING,
  MEDIA_TYPE_X_WWW_FORM_URLENCODED,
  MEDIA_TYPE_JSON,
  MEDIA_TYPE_TEXT_PLAIN,
  SERVER_API_URL,
  SESSION_ID,
  ROUTER_LOGIN,
  ROUTER_MAIN,
  HEAD_TOKEN,
};
