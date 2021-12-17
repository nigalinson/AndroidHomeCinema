package com.sloth.functions.widget.particle.bloom;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2020/8/27
 * Description:粒子爆炸矩形shape
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/8/27      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public class ParticleRectShape extends ParticleShape {
    private float mXRadius = 10;
    private float mYRadius = 10;

    /**
     * Construct the shape of particle.
     *
     * @param centerX The center x coordinate of the particle.
     * @param centerY The center y coordinate of the particle.
     * @param radius The radius of the particle.
     * */
    public ParticleRectShape(float centerX, float centerY, float radius) {
        super(centerX, centerY, radius);
    }

    /**
     * Construct the shape of particle.
     *
     * @param rx The x-radius of the oval used to round the corners
     * @param ry The y-radius of the oval used to round the corners
     * @param centerX The center x coordinate of the particle.
     * @param centerY The center y coordinate of the particle.
     * @param radius The radius of the particle.
     * */
    public ParticleRectShape(float rx, float ry, float centerX, float centerY, float radius) {
        super(centerX, centerY, radius);
        mXRadius = rx;
        mYRadius = ry;
    }

    @Override
    public void generateShape(Path path) {
        path.addRoundRect(new RectF(getCenterX() - getRadius(), getCenterY() - getRadius(), getCenterX() + getRadius(),
                getCenterY() + getRadius()), mXRadius, mYRadius, Path.Direction.CW);
    }
}
