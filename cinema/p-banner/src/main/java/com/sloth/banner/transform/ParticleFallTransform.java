package com.sloth.banner.transform;

import android.content.Context;

import androidx.core.view.ViewCompat;

import com.sloth.banner.transform.agent.BaseAgentTransform;
import com.sloth.functions.widget.particle.fall.ParticleFallView;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/14 11:26
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/14         Carl            1.0                    1.0
 * Why & What is modified:
 * todo 卡顿优化
 */
public class ParticleFallTransform extends BaseAgentTransform<ParticleFallView> {

    @Override
    protected void transforming(AgentDecor<ParticleFallView> view, float position) {
        if(position > 1 || position <= -1){
            view.setAlpha(1);
            view.setTranslationX(0);
            view.clear();
            view.getChildAt(0).setAlpha(1);
            ViewCompat.setTranslationZ(view, -1f);
        }else if(position <= 0){
            view.setTranslationX(-view.getWidth() * position);

            if(position >= -0.6f){
                view.getChildAt(0).setAlpha(1);
            }else{
                view.play(snapExit(view), 20 , 20, 30, 2000L);
                view.getChildAt(0).setAlpha(0);
            }
            ViewCompat.setTranslationZ(view, 1f);
        }else if(position <= 1){
            view.setAlpha(1);
            view.setTranslationX(-view.getWidth() * position);
            view.clear();
            view.getChildAt(0).setAlpha(1);
            ViewCompat.setTranslationZ(view, 0f);
        }
    }

    @Override
    protected ParticleFallView createAgentView(Context context) {
        return new ParticleFallView(context);
    }
}
