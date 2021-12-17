package com.sloth.functions.http.options;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/29 17:45
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/29         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class APIInfos {

    public static final int STATUS_NET_SUCCESS = 0;
    public static final int STATUS_NET_USER_LOGOUT = 21;
    public static final int STATUS_NET_USER_INVALID = -99;

    public static final String HTTP_INPUT_TYPE = "application/json";
    public static final String HTTP_INPUT_TYPE_STREAM = "application/octet-stream";
    public static final String HTTP_FILE_TYPE = "multipart/form-data";

    public static final int DEFAULT_PAGE_SIZE = 20;//默认每页请求数据
    public static final int DEFAULT_CURRENT_PAGE = 1;//默认当前请求页

    public static final String DEFAULT_ERROR_MSG = "Whoops！网络不给力\n快找个信号满满的地方再刷新一下吧";
    public static final String NETWORK_ERROR_MSG = "没有网络连接,请打开你的网络连接";
    public static final String NETWORK_TIMEOUT_ERROR_MSG = "网络通信出现问题,请确认您的网络状况良好后重试";
    public static final String USER_LOGOUT_ERROR_MSG = "请重新登录";

}
