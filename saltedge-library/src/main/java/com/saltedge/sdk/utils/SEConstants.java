/*
Copyright © 2015 Salt Edge. https://saltedge.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.saltedge.sdk.utils;

public class SEConstants {

    //WebView statuses
    public static final String KEY_STAGE = "stage";
    public static final String STATUS_FETCHING = "fetching";
    public static final String STATUS_SYNC = "sync";
    public static final String STATUS_ERROR = "error";
    public static final String STATUS_SUCCESS = "success";

    //JSON keys
    public static final String KEY_DATA = "data";
    public static final String KEY_META = "meta";
    public static final String KEY_NEXT_ID = "next_id";
    public static final String KEY_ERROR_MESSAGE = "error_message";
    public static final String KEY_IDENTIFIER = "identifier";
    public static final String KEY_OAUTH = "oauth";
    public static final String KEY_CONNECT_URL = "connect_url";

    //String resources
    public static final String REQUEST_ERROR = "Request error";
    public static final String PARSE_ERROR = "Parse error";
    public static final String LOADING = "Loading";
    public static final String WARNING = "Warning";
    public static final String CANNOT_BE_NULL = "cannot be null";
    public static final String COULD_NOT_CONNECT_ACCOUNT = "Could not connect account";
    public static final String ERROR_CLIENT_ID_IS_NULL = "Client id cannot be null or empty";
    public static final String ERROR_APP_SECRET_IS_NULL = "App secret cannot be null or empty";

    //Providers keys
    public static final String KEY_NAME = "name";
    public static final String KEY_MODE = "mode";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CODE = "code";
    public static final String KEY_AUTOMATIC_FETCH = "automatic_fetch";
    public static final String KEY_INTERACTIVE = "interactive";
    public static final String KEY_INSTRUCTION = "instruction";
    public static final String KEY_HOME_URL = "home_url";
    public static final String KEY_LOGIN_URL = "login_url";
    public static final String KEY_FORUM_URL = "forum_url";
    public static final String KEY_COUNTRY_CODE = "country_code";
    public static final String KEY_ALLOWED_COUNTRIES = "allowed_countries";
    public static final String KEY_REFRESH_TIMEOUT = "refresh_timeout";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_UPDATED_AT = "updated_at";

    //Login keys
    public static final String KEY_ID = "id";
    public static final String KEY_SECRET = "secret";
    public static final String KEY_FINISHED = "finished";
    public static final String KEY_FINISHED_RECENT = "finished_recent";
    public static final String KEY_PARTIAL = "partial";
    public static final String KEY_CUSTOMER_EMAIL = "customer_email";
    public static final String KEY_PROVIDER_CODE = "provider_code";
    public static final String KEY_PROVIDER_NAME = "provider_name";
    public static final String KEY_LAST_FAIL_AT = "last_fail_at";
    public static final String KEY_LAST_FAIL_MESSAGE = "last_fail_message";
    public static final String KEY_LAST_REQUEST_AT = "last_request_at";
    public static final String KEY_LAST_SUCCESS_AT = "last_success_at";
    public static final String KEY_INTERACTIVE_HTML = "interactive_html";
    public static final String KEY_INTERACTIVE_FIELDS_NAMES = "interactive_fields_names";

    //Account keys
    public static final String KEY_NATURE = "nature";
    public static final String KEY_BALANCE = "balance";
    public static final String KEY_CURRENCY_CODE = "currency_code";
    public static final String KEY_EXTRA = "extra";
    public static final String KEY_LOGIN_ID = "login_id";

    //Transaction keys
    public static final String KEY_DUPLICATED = "duplicated";
    public static final String KEY_MADE_ON = "made_on";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_ACCOUNT_ID = "account_id";

    //Other keys
    public static final String KEY_RETURN_TO = "return_to";
    public static final String KEY_LOCALE = "locale";
    public static final String KEY_RETURN_LOGIN_ID = "return_login_id";
    public static final String KEY_PROVIDER_MODES = "provider_modes";
    public static final String KEY_FROM_ID = "from_id";
    public static final String KEY_FROM_DATE = "from_date";
    public static final String KEY_TO_DATE = "to_date";
    public static final String URL_EMPTY = "URL is empty";
    public static final String JAVASCRIPT_CALLBACK = "javascript_callback_type";
    public static final String IFRAME = "iframe";
    public static final int FILECHOOSER_RESULT_CODE = 666;
}
