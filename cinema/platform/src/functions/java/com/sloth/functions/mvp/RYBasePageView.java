package com.sloth.functions.mvp;

import java.util.List;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/30 15:29
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/30         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface RYBasePageView<D> extends RYBaseView {

    void refreshSuccess(List<D> t);

    void loadMoreSuccess(List<D> t);

    void refreshFailed(String msg);

    void loadModeFailed(String msg);

}
