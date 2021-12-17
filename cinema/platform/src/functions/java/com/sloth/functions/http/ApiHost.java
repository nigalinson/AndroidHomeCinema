package com.sloth.functions.http;

import com.sloth.functions.http.AbsDefaultApiModule;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/29 19:07
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/29         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ApiHost extends AbsDefaultApiModule {

    private final String host;

    public ApiHost(String host) {
        this.host = host;
    }

    @Override
    public String apiHost() {
        return host;
    }
}
