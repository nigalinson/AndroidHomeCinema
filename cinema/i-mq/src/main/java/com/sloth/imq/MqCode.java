package com.sloth.imq;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/17 14:15
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/17         Carl            1.0                    1.0
 * Why & What is modified:
 */

@Retention(RetentionPolicy.SOURCE)
public @interface MqCode {

    int ERR = 0;

    int ADD = 1;

}
