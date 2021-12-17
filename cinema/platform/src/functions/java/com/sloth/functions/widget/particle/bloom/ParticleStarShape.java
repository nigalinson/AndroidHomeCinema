package com.sloth.functions.widget.particle.bloom;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2020/8/27
 * Description:粒子爆炸星形shape
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/8/27      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public class ParticleStarShape extends ParticleShape{
    /**
     * Construct the shape of particle.
     *
     * @param centerX The center x coordinate of the particle.
     * @param centerY The center y coordinate of the particle.
     * @param radius  The radius of the particle.
     */
    public ParticleStarShape(float centerX, float centerY, float radius) {
        super(centerX, centerY, radius);
    }

    @Override
    public void generateShape(Path path) {
        float R = getRadius() * 2;
        float r = getRadius();

        List<PointF> starPoints = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            double outerDoc = (18 + 72 * i) / 180d * Math.PI;
            PointF pointF1 = new PointF((float) (Math.cos(outerDoc) * R) ,
                    - (float) (Math.sin(outerDoc) * R));

            double innerDoc = (54 + 72 * i) / 180d * Math.PI;
            PointF pointF2 = new PointF((float)(Math.cos(innerDoc) * r) ,
                    -(float) (Math.sin(innerDoc) * r));

            starPoints.add(pointF1);
            starPoints.add(pointF2);
        }

        path.moveTo(starPoints.get(0).x, starPoints.get(0).y);
        for (int i = 1; i < starPoints.size(); i++){
            path.lineTo(starPoints.get(i).x, starPoints.get(i).y);
        }
        path.lineTo(starPoints.get(0).x, starPoints.get(0).y);

        Matrix matrix = new Matrix();
        matrix.postTranslate(getCenterX(), getCenterY());
        path.transform(matrix);
    }
}
