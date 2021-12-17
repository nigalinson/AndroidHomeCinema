package com.sloth.functions.widget.particle.bloom;

import android.graphics.Path;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2020/8/27
 * Description shape基类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/8/27      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public abstract class ParticleShape {
    private final float mRadius;
    private final float mCenterX;
    private final float mCenterY;
    private final Path mPath;

    /**
     * Construct the shape of particle.
     *
     * @param centerX The center x coordinate of the particle.
     * @param centerY The center y coordinate of the particle.
     * @param radius The radius of the particle.
     * */
    public ParticleShape(float centerX, float centerY, float radius){
        mCenterX = centerX;
        mCenterY = centerY;
        mRadius  = radius;
        mPath = new Path();
    }

    /**
     * Return the radius of the particle.
     * */
    public float getRadius() {
        return mRadius;
    }

    /**
     * Return the center x coordinate of the particle.
     * */
    public float getCenterX() {
        return mCenterX;
    }

    /**
     * Return the center y coordinate of the particle.
     * */
    public float getCenterY() {
        return mCenterY;
    }

    /**
     * Need to implement this method to generate shape.
     * You only need to implement this method, you don't have to deal with the call timing.
     *
     * 只需实现该接口即可，无需在意它何时被调用
     *
     * @param path The path you need to handle.
     * */
    public abstract void generateShape(Path path);

    /**
     * Get the path shape for particle.
     * */
    public Path getShapePath(){
        return mPath;
    }
}
