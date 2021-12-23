package com.sloth.icrawler;

import java.util.Map;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/23 16:52
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/23         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface InfoFinder {

    void find(Long id, String name, Listener listener);

    interface Listener{
        void findInfo(Long id, Map<String, String> infoMap);
    }

}
