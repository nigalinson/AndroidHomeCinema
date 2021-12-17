package com.sloth.functions.widget.particle.bloom;

import android.animation.TimeInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/18 18:50
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/18         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class BloomSceneMaker {
    //effects
    public static final int makeIndexDuration = 0;
    public static final int makeIndexInterpolator = 1;
    public static final int makeIndexAnchor = 2;
    public static final int makeIndexSpeedRange = 3;
    public static final int makeIndexScaleRange = 4;
    public static final int makeIndexSkewRange = 5;
    public static final int makeIndexRotationSpeedRange = 6;
    public static final int makeIndexAccelerationRange = 7;
    public static final int makeIndexFadeOutStart = 8;
    public static final int makeIndexFadeOutInterpolator = 9;

    //independent settings
    public static final int makeIndexShape = 10;
    public static final int makeIndexRadius = 11;
    public static final int makeIndexRowAndColumn = 12;


    private final Object[] effects = new Object[20];

    /**
     * Sets the length of the bloom effect animation in milliseconds.
     *
     * @param duration The length of the animation, in milliseconds. This value cannot be negative.
     * */
    public BloomSceneMaker setDuration(long duration){
        effects[makeIndexDuration] = duration;
        return this;
    }

    /**
     * Set the interpolator of the bloom effect animation.
     * the default is {@link AccelerateDecelerateInterpolator}.
     *
     * @param interpolator The animation interpolator.
     * */
    public BloomSceneMaker setInterpolator(TimeInterpolator interpolator) {
        effects[makeIndexInterpolator] = interpolator;
        return this;
    }

    /**
     * Set anchor points for all the particles.
     *
     * @param anchorX The bloom anchor x coordinate.
     * @param anchorY The bloom anchor y coordinate.
     * */
    public BloomSceneMaker setAnchor(float anchorX, float anchorY){
        effects[makeIndexAnchor] = new float[]{anchorX, anchorY};
        return this;
    }

    /**
     * Set the speed range for the particles.
     *
     * @param minSpeed The minimum speed value, the default is 0.1.
     * @param maxSpeed The maximum speed value, the default is 0.5.
     * */
    public BloomSceneMaker setSpeedRange(float minSpeed, float maxSpeed){
        effects[makeIndexSpeedRange] = new float[]{minSpeed, maxSpeed};
        return this;
    }

    /**
     * Set the scale range for the particles.
     *
     * @param minScale The minimum scale value.
     * @param maxScale The maximum scale value.
     * */
    public BloomSceneMaker setScaleRange(float minScale, float maxScale){
        effects[makeIndexScaleRange] = new float[]{minScale, maxScale};
        return this;
    }

    /**
     * Set the skew range for the particles.
     *
     * @param minSkew The minimum speed value.
     * @param maxSkew The maximum speed value.
     * */
    public BloomSceneMaker setSkewRange(float minSkew, float maxSkew){
        effects[makeIndexSkewRange] = new float[]{minSkew, maxSkew};
        return this;
    }

    /**
     * Set the rotation speed range for the particles.
     *
     * @param minRotationSpeedRange The minimum speed value.
     * @param maxRotationSpeedRange The maximum speed value.
     * */
    public BloomSceneMaker setRotationSpeedRange(float minRotationSpeedRange, float maxRotationSpeedRange){
        effects[makeIndexRotationSpeedRange] = new float[]{minRotationSpeedRange, maxRotationSpeedRange};
        return this;
    }

    /**
     * Set particle acceleration, the acceleration is measured in pixels per square millisecond.
     * The angel controls the acceleration direction.
     *
     * Calculated as follows:
     *
     * float angelInRadsAcc = (float) (accelerationAngle*Math.PI / 180f)
     *
     * The final x axis acceleration:  accelerationX = (float) (value * Math.cos(angleInRadsAcc));
     * The final y axis acceleration:  accelerationY = (float) (value * Math.sin(angleInRadsAcc));
     *
     * @param acceleration The acceleration value.
     * @param accelerationAngle The acceleration angele [0-360].
     * */
    public BloomSceneMaker setAcceleration(float acceleration, int accelerationAngle) {
        return setAccelerationRange(acceleration, acceleration, accelerationAngle, accelerationAngle);
    }

    /**
     * This method takes random acceleration and acceleration angles from the range of acceleration and acceleration angles you set.
     *
     * Set particle acceleration, the acceleration is measured in pixels per square millisecond.
     * The angel controls the acceleration direction.
     *
     * Calculated as follows:
     *
     * float angelInRadsAcc = (float) (accelerationAngle*Math.PI / 180f)
     *
     * The final x axis acceleration:  accelerationX = (float) (value * Math.cos(angleInRadsAcc));
     * The final y axis acceleration:  accelerationY = (float) (value * Math.sin(angleInRadsAcc));
     *
     * @param minAcceleration The minimum acceleration.
     * @param maxAcceleration The maximum acceleration.
     * @param minAccelerationAngel The minimum acceleration angel[0-360].
     * @param maxAccelerationAngel The maximum acceleration angel[0-360].
     * */
    public BloomSceneMaker setAccelerationRange(float minAcceleration, float maxAcceleration, int minAccelerationAngel, int maxAccelerationAngel) {
        effects[makeIndexAccelerationRange] = new float[]{minAcceleration, maxAcceleration, minAccelerationAngel, maxAccelerationAngel};
        return this;
    }

    /**
     * Set the fade out effect for all particle.
     *
     * @param startTime Start time that relative of {@link #setDuration(long)}. For example, if you set the duration to 800,
     *                  then you can set the fadeout start time to 600, that is, start the fade out effect when the particle animation is executed to 600.
     * */
    public BloomSceneMaker setFadeOut(long startTime){
        return setFadeOut(startTime, new LinearInterpolator());
    }

    /**
     * Set the fade out effect for all particle.
     *
     * @param startTime Start time that relative of {@link #setDuration(long)}. For example, if you set the duration to 800,
     *                  then you can set the fadeout start time to 600, that is, start the fade out effect when the particle animation is executed to 600.
     * @param interpolator The interpolator of the fade out effect.
     * */
    public BloomSceneMaker setFadeOut(long startTime, TimeInterpolator interpolator){
        effects[makeIndexFadeOutStart] = startTime;
        effects[makeIndexFadeOutInterpolator] = interpolator;
        return this;
    }

    public BloomSceneMaker setShape(ParticleShapeDistributor particleShapeDistributor){
        effects[makeIndexShape] = particleShapeDistributor;
        return this;
    }

    public BloomSceneMaker setRadius(int rad){
        effects[makeIndexRadius] = rad;
        return this;
    }

    public BloomSceneMaker setRowAndColumn(int row, int column){
        effects[makeIndexRowAndColumn] = new int[]{row, column};
        return this;
    }

    public void apply(BloomView view){
        if(effects[makeIndexRadius] != null){
            view.setParticleRadius((Integer) effects[makeIndexRadius]);
        }

        if(effects[makeIndexShape] != null){
            view.setBloomShapeDistributor((ParticleShapeDistributor) effects[makeIndexShape]);
        }

        if(effects[makeIndexRowAndColumn] != null){
            int[] rowAndColumn = (int[]) effects[makeIndexRowAndColumn];
            view.setRow(rowAndColumn[0]);
            view.setColumn(rowAndColumn[1]);
        }
        BloomEffector.Builder builder = new BloomEffector.Builder();
        /**
         *  public static final int makeIndexDuration = 0;
         *     public static final int makeIndexInterpolator = 1;
         *     public static final int makeIndexAnchor = 2;
         *     public static final int makeIndexSpeedRange = 3;
         *     public static final int makeIndexScaleRange = 4;
         *     public static final int makeIndexSkewRange = 5;
         *     public static final int makeIndexRotationSpeedRange = 6;
         *     public static final int makeIndexAccelerationRange = 7;
         *     public static final int makeIndexFadeOutStart = 8;
         *     public static final int makeIndexFadeOutInterpolator = 9;
         */

        if(effects[makeIndexDuration] != null){
            builder.setDuration((Long) effects[makeIndexDuration]);
        }
        if(effects[makeIndexInterpolator] != null){
            builder.setInterpolator((TimeInterpolator) effects[makeIndexInterpolator]);
        }
        if(effects[makeIndexAnchor] != null){
            float[] anchor = (float[]) effects[makeIndexAnchor];
            builder.setAnchor(anchor[0], anchor[1]);
        }
        if(effects[makeIndexSpeedRange] != null){
            float[] speedRange = (float[]) effects[makeIndexSpeedRange];
            builder.setSpeedRange(speedRange[0], speedRange[1]);
        }
        if(effects[makeIndexScaleRange] != null){
            float[] scaleRange = (float[]) effects[makeIndexScaleRange];
            builder.setScaleRange(scaleRange[0], scaleRange[1]);
        }
        if(effects[makeIndexSkewRange] != null){
            float[] skewRange = (float[]) effects[makeIndexSkewRange];
            builder.setSkewRange(skewRange[0], skewRange[1]);
        }
        if(effects[makeIndexRotationSpeedRange] != null){
            float[] rotationSpeedRange = (float[]) effects[makeIndexRotationSpeedRange];
            builder.setRotationSpeedRange(rotationSpeedRange[0], rotationSpeedRange[1]);
        }
        if(effects[makeIndexAccelerationRange] != null){
            float[] accelerationRange = (float[]) effects[makeIndexAccelerationRange];
            builder.setAccelerationRange(accelerationRange[0], accelerationRange[1], (int)accelerationRange[2], (int)accelerationRange[3]);
        }
        if(effects[makeIndexFadeOutStart] != null){
            if(effects[makeIndexFadeOutInterpolator] != null){
                builder.setFadeOut((Long) effects[makeIndexFadeOutStart], (TimeInterpolator) effects[makeIndexFadeOutInterpolator]);
            }else{
                builder.setFadeOut((Long) effects[makeIndexFadeOutStart]);
            }
        }

        BloomEffector bloomEffector = builder.build();
        view.setEffector(bloomEffector);
    }

}
