/*
Copyright © 2019 Salt Edge. https://saltedge.com

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

    //Base model keys
    public static final String KEY_ID = "id";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_UPDATED_AT = "updated_at";

    //WebView/Saltbridge keys
    public static final String KEY_STAGE = "stage";
    public static final String KEY_STAGES = "stages";
    public static final String STATUS_FETCHING = "fetching";
    public static final String STATUS_ERROR = "error";
    public static final String STATUS_SUCCESS = "success";
    public static final String KEY_API_STAGE = "api_stage";
    public static final String KEY_CUSTOM_FIELDS = "custom_fields";

    //JSON keys
    public static final String KEY_DATA = "data";
    public static final String KEY_META = "meta";
    public static final String KEY_NEXT_ID = "next_id";
    public static final String KEY_NEXT_PAGE = "next_page";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_ERROR = "error";
    public static final String KEY_CLASS = "class";
    public static final String KEY_DOCUMENTATION_URL = "documentation_url";
    public static final String KEY_IDENTIFIER = "identifier";
    public static final String KEY_CONNECT_URL = "connect_url";
    public static final String KEY_REDIRECT_URL = "redirect_url";
    public static final String KEY_EXPIRES_AT = "expires_at";
    public static final String KEY_REVOKED_BY = "revoked_by";
    public static final String KEY_REVOKED_AT = "revoked_at";
    public static final String KEY_REMOVED = "removed";
    public static final String KEY_COLLECTED_BY = "collected_by";
    public static final String KEY_FETCH_SCOPES = "fetch_scopes";
    public static final String KEY_SCOPES = "scopes";

    //String resources
    public static final String LOADING = "Loading";
    public static final String WARNING = "Warning";

    //Providers keys
    public static final String KEY_NAME = "name";
    public static final String KEY_MODE = "mode";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CODE = "code";
    public static final String KEY_CUSTOMER_NOTIFIED_ON_SIGN_IN = "customer_notified_on_sign_in";
    public static final String KEY_AUTOMATIC_FETCH = "automatic_fetch";
    public static final String KEY_INTERACTIVE = "interactive";
    public static final String KEY_IDENTIFICATION_MODE = "identification_mode";
    public static final String KEY_INSTRUCTION = "instruction";
    public static final String KEY_HOME_URL = "home_url";
    public static final String KEY_LOGIN_URL = "login_url";
    public static final String KEY_LOGO_URL = "logo_url";
    public static final String KEY_ALLOWED_COUNTRIES = "allowed_countries";
    public static final String KEY_REFRESH_TIMEOUT = "refresh_timeout";
    public static final String KEY_HOLDER_INFO = "holder_info";
    public static final String KEY_MAX_CONSENT_DAYS = "max_consent_days";
    public static final String KEY_INCLUDE_FAKE_PROVIDERS = "include_fake_providers";

    //Connections keys
    public static final String KEY_SECRET = "secret";
    public static final String KEY_DAILY_REFRESH = "daily_refresh";
    public static final String KEY_FINISHED = "finished";
    public static final String KEY_FINISHED_RECENT = "finished_recent";
    public static final String KEY_PARTIAL = "partial";
    public static final String KEY_CUSTOMER_EMAIL = "customer_email";
    public static final String KEY_PROVIDER_ID = "provider_id";
    public static final String KEY_PROVIDER_CODE = "provider_code";
    public static final String KEY_PROVIDER_NAME = "provider_name";
    public static final String KEY_LAST_FAIL_AT = "last_fail_at";
    public static final String KEY_FAIL = "fail";
    public static final String KEY_FAIL_AT = "fail_at";
    public static final String KEY_LAST_FAIL_MESSAGE = "last_fail_message";
    public static final String KEY_FAIL_MESSAGE = "fail_message";
    public static final String KEY_FAIL_ERROR_CLASS = "fail_error_class";
    public static final String KEY_LAST_REQUEST_AT = "last_request_at";
    public static final String KEY_LAST_SUCCESS_AT = "last_success_at";
    public static final String KEY_SUCCESS_AT = "success_at";
    public static final String KEY_NEXT_REFRESH_POSSIBLE_AT = "next_refresh_possible_at";
    public static final String KEY_STORE_CREDENTIALS = "store_credentials";
    public static final String KEY_SHOW_CONSENT_CONFIRMATION = "show_consent_confirmation";
    public static final String KEY_CONSENT_TYPES = "consent_types";
    public static final String KEY_CONSENT_PERIOD_DAYS = "consent_period_days";
    public static final String KEY_CONSENT_GIVEN_AT = "consent_given_at";
    public static final String KEY_CONSENT_EXPIRES_AT = "consent_expires_at";
    public static final String KEY_LAST_ATTEMPT = "last_attempt";
    public static final String KEY_LAST_STAGE = "last_stage";
    public static final String KEY_CONSENT = "consent";
    public static final String KEY_ATTEMPT = "attempt";

    //Account keys
    public static final String KEY_NATURE = "nature";
    public static final String KEY_BALANCE = "balance";
    public static final String KEY_CURRENCY_CODE = "currency_code";
    public static final String KEY_EXTRA = "extra";

    //Transaction keys
    public static final String KEY_DUPLICATED = "duplicated";
    public static final String KEY_UNDUPLICATED = "unduplicated";
    public static final String KEY_MADE_ON = "made_on";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_ACCOUNT_ID = "account_id";

    //Other keys
    public static final String KEY_CUSTOMER_ID = "customer_id";
    public static final String KEY_CONNECTION_ID = "connection_id";
    public static final String KEY_API_MODE = "api_mode";
    public static final String KEY_API_VERSION = "api_version";
    public static final String KEY_CATEGORIZATION = "categorization";
    public static final String KEY_DEVICE_TYPE = "device_type";
    public static final String KEY_REMOTE_IP = "remote_ip";
    public static final String KEY_EXCLUDE_ACCOUNTS = "exclude_accounts";
    public static final String KEY_IDENTIFY_MERCHANT = "identify_merchant";
    public static final String KEY_USER_PRESENT = "user_present";
    public static final String KEY_INTERACTIVE_HTML = "interactive_html";
    public static final String KEY_INTERACTIVE_FIELDS_NAMES = "interactive_fields_names";
    public static final String KEY_INTERACTIVE_FIELDS_OPTIONS = "interactive_fields_options";
    public static final String KEY_NAMES = "names";
    public static final String KEY_EMAILS = "emails";
    public static final String KEY_PHONE_NUMBERS = "phone_numbers";
    public static final String KEY_ADDRESSES = "addresses";
    public static final String KEY_CITY = "city";
    public static final String KEY_STATE = "state";
    public static final String KEY_STREET = "street";
    public static final String KEY_COUNTRY_CODE = "country_code";
    public static final String KEY_POST_CODE = "post_code";
    public static final String KEY_ENGLISH_NAME = "english_name";
    public static final String KEY_LOCALIZED_NAME = "localized_name";
    public static final String KEY_OPTION_VALUE = "option_value";
    public static final String KEY_SELECTED = "selected";
    public static final String KEY_CREDENTIALS = "credentials";
    public static final String KEY_OVERRIDE_CREDENTIALS_STRATEGY = "override_credentials_strategy";
    public static final String KEY_RETURN_TO = "return_to";
    public static final String KEY_LOCALE = "locale";
    public static final String KEY_PROVIDER_MODES = "provider_modes";
    public static final String KEY_FROM_ID = "from_id";
    public static final String KEY_FROM_DATE = "from_date";
    public static final String KEY_TO_DATE = "to_date";
    public static final String KEY_PERIOD_DAYS = "period_days";
    public static final String KEY_CONSENT_ID = "consent_id";
    public static final String KEY_DATE = "date";

    public static final String KEY_JAVASCRIPT_CALLBACK_TYPE = "javascript_callback_type";
    public static final String IFRAME = "iframe";
    public static final String KEY_RATE = "rate";
    public static final String KEY_TRANSACTION_IDS = "transaction_ids";
    public static final String KEY_KEEP_DAYS = "keep_days";
    public static final String KEY_CLEANUP_STARTED = "cleanup_started";

    //ERRORS
    public static final String REQUEST_ERROR = "Request error";
    public static final String ERROR_CANNOT_BE_NULL = "cannot be null";
    public static final String ERROR_COULD_NOT_CONNECT_ACCOUNT = "Could not connect account";
    public static final String ERROR_CLIENT_APP_ID_IS_NULL = "Client App Id cannot be null or empty";
    public static final String ERROR_CLIENT_APP_SECRET_IS_NULL = "Client App Secret cannot be null or empty";
    public static final String ERROR_CUSTOMER_IDENTIFIER_IS_NULL = "Customer identifier cannot be null or empty";
    public static final String ERROR_INVALID_REFRESH_SECRETS = "Invalid refresh secrets";
    public static final String ERROR_URL_EMPTY = "URL is empty";

    //RESULT CODES
    public static final int FILECHOOSER_RESULT_CODE = 666;


}
