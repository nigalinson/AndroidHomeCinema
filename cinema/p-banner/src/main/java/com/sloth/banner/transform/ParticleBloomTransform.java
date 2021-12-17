package com.sloth.banner.transform;

import android.content.Context;

import androidx.core.view.ViewCompat;

import com.sloth.banner.transform.agent.BaseAgentTransform;
import com.sloth.functions.widget.particle.bloom.BloomSceneMaker;
import com.sloth.functions.widget.particle.bloom.BloomView;

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
 * todo 目前实现比较卡顿，后期尝试优化流畅度
 */
public class ParticleBloomTransform extends BaseAgentTransform<BloomView> {

    private final BloomSceneMaker bloomSceneMaker;

    public ParticleBloomTransform(BloomSceneMaker bloomSceneMaker) {
        this.bloomSceneMaker = bloomSceneMaker;
    }

    @Override
    protected void transforming(AgentDecor<BloomView> view, float position) {
        //still offset
        view.setTranslationX(-view.getWidth() * position);

        if(position > 1 || position <= -1){
            view.setTranslationX(0);
            view.clear();
            view.getChildAt(0).setAlpha(1);
            ViewCompat.setTranslationZ(view, -1f);
        }else if(position <= 0){
            if(position >= -0.6f){
                view.getChildAt(0).setAlpha(1);
            }else{
                view.play(snapExit(view), bloomSceneMaker);
                view.getChildAt(0).setAlpha(0);
            }
            ViewCompat.setTranslationZ(view, 1f);
        }else if(position <= 1){
            view.clear();
            view.getChildAt(0).setAlpha(1);
            ViewCompat.setTranslationZ(view, 0f);
        }
    }

    @Override
    protected BloomView createAgentView(Context context) {
        return new BloomView(context);
    }
}
