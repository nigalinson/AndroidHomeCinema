package com.sloth.functions.widget.particle.bloom;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2020/8/27
 * Description:粒子爆炸
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/8/27      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public class CircleShapeDistributor extends ParticleShapeDistributor {
    @Override
    public ParticleShape getShape(BloomParticle particle) {
        return new ParticleCircleShape(particle.getInitialX(), particle.getInitialY(), particle.getRadius());
    }
}
