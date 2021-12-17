package com.sloth.functions.widget.particle.bloom;

import android.graphics.Path;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2020/8/27
 * Description:粒子shape基类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/8/27      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public class ParticleCircleShape extends ParticleShape {

    public ParticleCircleShape(float centerX, float centerY, float radius) {
        super(centerX, centerY, radius);
    }

    @Override
    public void generateShape(Path path) {
        path.addCircle(getCenterX(), getCenterY(), getRadius(), Path.Direction.CW);
    }
}
