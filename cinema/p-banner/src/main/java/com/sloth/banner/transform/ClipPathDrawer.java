package com.sloth.banner.transform;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import com.sloth.banner.transform.drawer.Drawer;
import java.util.Random;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/1/26 16:02
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/1/26         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ClipPathDrawer implements Drawer {

    private static final String TAG = ClipPathDrawer.class.getSimpleName();

    public enum Mode{
        /**
         * 常规切割
         */
        SIMPLE_RIGHT_2_LEFT(100),
        SIMPLE_LEFT_2_RIGHT(101),
        SIMPLE_TOP_2_BOTTOM(102),
        SIMPLE_BOTTOM_2_TOP(103),
        SIMPLE_LEFT_TOP_2_RIGHT_BOTTOM(104),
        SIMPLE_RIGHT_TOP_2_LEFT_BOTTOM(105),
        SIMPLE_LEFT_BOTTOM_2_RIGHT_TOP(106),
        SIMPLE_RIGHT_BOTTOM_2_LEFT_TOP(107),

        /**
         * 百叶窗效果
         */
        MULTI_CLIP_ROW(110),
        MULTI_CLIP_COLUMN(111),

        /**
         * 斜边切割（默认从右往左，斜率1）
         */
        HYPOTENUSE_LEFT_TOP(200),
        HYPOTENUSE_LEFT_BOTTOM(201),
        HYPOTENUSE_RIGHT_TOP(202),
        HYPOTENUSE_RIGHT_BOTTOM(203),

        /**
         * 窗体（对称开闭）
         */
        WINDOW_OPEN_VERTICAL(300),
        WINDOW_OPEN_HORIZONTAL(301),
        WINDOW_CLOSE_VERTICAL(302),
        WINDOW_CLOSE_HORIZONTAL(303),

        /**
         * 圆形显隐
         */
        CIRCLE_CLIP_REVEAL(400),
        CIRCLE_CLIP_HIDE(401),

        /**
         * 斜边锯齿（默认从右往左，斜率1）
         */
        SAWTOOTH_LEFT_TOP(500),
        SAWTOOTH_LEFT_BOTTOM(501),
        SAWTOOTH_RIGHT_TOP(502),
        SAWTOOTH_RIGHT_BOTTOM(503),
        ;

        Mode(int code) {
            this.code = code;
        }

        public int code;
    }

    private int mode;
    private int width, height;

    /**
     * 斜边斜率y/x，默认 1
     */
    private final int slope = 1;
    /**
     * 圆形显隐远点位置
     */
    private int centerX = -1, centerY = -1;

    /**
     * 横竖最少锯齿数量
     */
    private final int sawToothCounts = 15;
    /**
     * 奇偶数计算标志 - 无意义
     */
    private boolean flagSawToothOdd = false;

    /**
     * 百叶窗效果条纹数
     */
    private final int multiClipLines = 6;

    public ClipPathDrawer(int mode, long duration) {
        this.mode = mode;
    }

    public ClipPathDrawer(long duration, int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ClipPathDrawer(int mode, long duration, int width, int height) {
        this.mode = mode;
        this.width = width;
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setCenter(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void randomMode(Mode... modes) {
        Mode[] pool;
        if(modes != null && modes.length > 0) {
            pool = modes;
        }else{
            pool = Mode.values();
        }
        for(int i = 0; i < pool.length; i++){
            if(this.mode == pool[i].code){
                if(i == pool.length - 1){
                    mode = pool[0].code;
                }else{
                    mode = pool[i + 1].code;
                }
                return;
            }

        }
        this.mode = pool[0].code;
    }

    @Override
    public void advance(Canvas canvas, float fraction) {
        Path path = new Path();

        //简单切割
        if(mode == Mode.SIMPLE_RIGHT_2_LEFT.code){
            path.addRect(new RectF(0, 0, width - width * fraction, height), Path.Direction.CW);
        }else if(mode == Mode.SIMPLE_LEFT_2_RIGHT.code){
            path.addRect(new RectF(fraction * width, 0, width, height), Path.Direction.CW);
        }else if(mode == Mode.SIMPLE_TOP_2_BOTTOM.code){
            path.addRect(new RectF(0, height * fraction, width, height), Path.Direction.CW);
        }else if(mode == Mode.SIMPLE_BOTTOM_2_TOP.code){
            path.addRect(new RectF(0, 0, width, height - height * fraction), Path.Direction.CW);
        }else if(mode == Mode.SIMPLE_LEFT_TOP_2_RIGHT_BOTTOM.code){
            path.addRect(new RectF(width * fraction, height * fraction, width, height), Path.Direction.CW);
        }else if(mode == Mode.SIMPLE_RIGHT_TOP_2_LEFT_BOTTOM.code){
            path.addRect(new RectF(0, height * fraction, width - width * fraction, height), Path.Direction.CW);
        }else if(mode == Mode.SIMPLE_LEFT_BOTTOM_2_RIGHT_TOP.code){
            path.addRect(new RectF(width * fraction, 0, width, height - height * fraction), Path.Direction.CW);
        }else if(mode == Mode.SIMPLE_RIGHT_BOTTOM_2_LEFT_TOP.code){
            path.addRect(new RectF(0, 0, width - width * fraction, height - height * fraction), Path.Direction.CW);
        }
        //多行、多列切割
        else if(mode == Mode.MULTI_CLIP_ROW.code){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int rowSize = height / multiClipLines;
                for(int i = 0; i < multiClipLines; i++){

                    Path itemPath = new Path();
                    itemPath.addRect(new RectF(0, rowSize * i + (rowSize* fraction), width, rowSize * i + rowSize), Path.Direction.CW);
                    path.op(itemPath, Path.Op.UNION);
                }
            }else{
                //4.4以下不支持path op操作
            }
        }else if(mode == Mode.MULTI_CLIP_COLUMN.code){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int columnSize = width / multiClipLines;
                for(int i = 0; i < multiClipLines; i++){
                    Path itemPath = new Path();
                    itemPath.addRect(new RectF(columnSize * i, 0, columnSize * i + (columnSize * (1 - fraction)), height), Path.Direction.CW);
                    path.op(itemPath, Path.Op.UNION);
                }
            }else{
                //4.4以下不支持path op操作
            }
        }
        //斜边
        else if(mode == Mode.HYPOTENUSE_LEFT_TOP.code){
            double boundX = width * 2 * fraction - width;
            double boundY = height * 2 * fraction - height;

            path.moveTo(width,height);
            path.lineTo(width, (float) boundY);
            path.lineTo((float) boundX, height);
            path.close();
        }else if(mode == Mode.HYPOTENUSE_RIGHT_TOP.code){
            double boundX = width * 2 * (1.0- fraction);
            double boundY = height * 2 * fraction - height;

            path.moveTo(0,height);
            path.lineTo(0, (float) boundY);
            path.lineTo((float) boundX, height);
            path.close();
        }else if(mode == Mode.HYPOTENUSE_LEFT_BOTTOM.code){
            double boundX = width * 2 * fraction - width;
            double boundY = height * 2 * (1.0 - fraction);

            path.moveTo(width, 0);
            path.lineTo(width, (float) boundY);
            path.lineTo((float) boundX, 0);
            path.close();
        }else if(mode == Mode.HYPOTENUSE_RIGHT_BOTTOM.code){
            double boundX = width * 2 * (1.0 - fraction);
            double boundY = height * 2 * (1.0 - fraction);

            path.moveTo(0,0);
            path.lineTo(0, (float) boundY);
            path.lineTo((float) boundX, 0);
            path.close();
        }
        //window
        else if(mode == Mode.WINDOW_OPEN_VERTICAL.code){
            int half = height / 2;
            path.addRect(new RectF(0, 0, width, half * (1.0f - fraction)), Path.Direction.CW);
            path.addRect(new RectF(0, half + (half * fraction), width, height), Path.Direction.CW);
        }else if(mode == Mode.WINDOW_OPEN_HORIZONTAL.code){
            int half = width / 2;
            path.addRect(new RectF(0, 0, half * (1.0f - fraction), height), Path.Direction.CW);
            path.addRect(new RectF(half + (half * fraction), 0, width, height), Path.Direction.CW);
        }else if(mode == Mode.WINDOW_CLOSE_VERTICAL.code){
            int half = height / 2;
            path.addRect(new RectF(0, half * fraction, width, half + half * (1.0f - fraction)), Path.Direction.CW);
        }else if(mode == Mode.WINDOW_CLOSE_HORIZONTAL.code){
            int half = width / 2;
            path.addRect(new RectF(half * fraction, 0, half + half * (1.0f - fraction), height), Path.Direction.CW);
        }
        //圆形显隐
        else if(mode == Mode.CIRCLE_CLIP_REVEAL.code){
            assignCenter(fraction);

            RectF cet = new RectF(centerX - tmpRadius, centerY - tmpRadius,
                    centerX + tmpRadius, centerY + tmpRadius);

            Path halfArc = new Path();

            halfArc.addArc(cet, 0,180);

            float r = tmpRadius * (1.0f - fraction);

            cet.inset(r, r);
            halfArc.arcTo(cet, 180, -180);
            halfArc.close();

            Path another = new Path(halfArc);

            Matrix matrix = new Matrix();
            matrix.postRotate(180, centerX, centerY);
            another.transform(matrix);

            path.addPath(halfArc);
            path.addPath(another);

        }else if(mode == Mode.CIRCLE_CLIP_HIDE.code){
            assignCenter(fraction);
            float r = tmpRadius * (1.0f - fraction);
            path.addCircle(centerX, centerY, r, Path.Direction.CW);
        }
        //斜边锯齿（矩形拼接产生锯齿视觉）
        else if(mode == Mode.SAWTOOTH_LEFT_TOP.code){
            double boundX = width * 2 * fraction - width;
            double boundY = height * 2 * fraction - height;

            path.moveTo(width, (float) boundY);
            path.lineTo(width,height);
            path.lineTo((float) boundX, height);

            //锯齿
            int partWidth = width / sawToothCounts;
            int partHeight = height / sawToothCounts;
            float anchorX = (float) boundX;
            float anchorY = height;

            if(fraction == 0.0f){
                //起始状态不用计算锯齿
            }else{
                if(flagSawToothOdd){
                    anchorY += (float)partHeight / 2;
                }
                flagSawToothOdd = !flagSawToothOdd;
                while(anchorX < width){
                    path.lineTo(anchorX = anchorX + partWidth, anchorY);
                    path.lineTo(anchorX, anchorY = anchorY - partHeight);
                }
            }

            path.close();
        }else if(mode == Mode.SAWTOOTH_RIGHT_TOP.code){
            double boundX = width * 2 * (1.0- fraction);
            double boundY = height * 2 * fraction - height;

            path.moveTo(0, (float) boundY);
            path.lineTo(0,height);
            path.lineTo((float) boundX, height);

            //锯齿
            int partWidth = width / sawToothCounts;
            int partHeight = height / sawToothCounts;
            float anchorX = (float) boundX;
            float anchorY = height;
            if(fraction == 0.0f){
                //起始状态不用计算锯齿
            }else{
                if(flagSawToothOdd){
                    anchorY += (float)partHeight / 2;
                }
                flagSawToothOdd = !flagSawToothOdd;
                while(anchorX > 0){
                    path.lineTo(anchorX = anchorX - partWidth, anchorY);
                    path.lineTo(anchorX, anchorY = anchorY - partHeight);
                }
            }
            path.close();
        }else if(mode == Mode.SAWTOOTH_LEFT_BOTTOM.code){
            double boundX = width * 2 * fraction - width;
            double boundY = height * 2 * (1.0 - fraction);

            path.moveTo(width, (float) boundY);
            path.lineTo(width, 0);
            path.lineTo((float) boundX, 0);

            //锯齿
            int partWidth = width / sawToothCounts;
            int partHeight = height / sawToothCounts;
            float anchorX = (float) boundX;
            float anchorY = 0;

            if(fraction == 0.0f){
                //起始状态不用计算锯齿
            }else{
                if(flagSawToothOdd){
                    anchorY += (float)partHeight / 2;
                }
                flagSawToothOdd = !flagSawToothOdd;
                while(anchorX < width){
                    path.lineTo(anchorX = anchorX + partWidth, anchorY);
                    path.lineTo(anchorX, anchorY = anchorY + partHeight);
                }
            }
            path.close();
        }else if(mode == Mode.SAWTOOTH_RIGHT_BOTTOM.code){
            double boundX = width * 2 * (1.0 - fraction);
            double boundY = height * 2 * (1.0 - fraction);

            path.moveTo(0, (float) boundY);
            path.lineTo(0,0);
            path.lineTo((float) boundX, 0);

            //锯齿
            int partWidth = width / sawToothCounts;
            int partHeight = height / sawToothCounts;
            float anchorX = (float) boundX;
            float anchorY = 0;

            if(fraction == 0.0f){
                //起始状态不用计算锯齿
            }else{
                if(flagSawToothOdd){
                    anchorY += (float)partHeight / 2;
                }
                flagSawToothOdd = !flagSawToothOdd;
                while(anchorX > 0){
                    path.lineTo(anchorX = anchorX - partWidth, anchorY);
                    path.lineTo(anchorX, anchorY = anchorY + partHeight);
                }
            }

            path.close();
        }
        //未知
        else{}

        if(path != null){
            canvas.clipPath(path);
        }
    }

    private float tmpRadius = 0;

    private void assignCenter(float fraction) {
        if(centerX != -1 && centerY != -1){
            return;
        }

        Random rd = new Random(System.currentTimeMillis());
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        centerX = Math.abs(rd.nextInt(halfWidth)) + (halfWidth / 2);
        centerY = Math.abs(rd.nextInt(halfHeight)) + (halfHeight / 2);

        int halfX = width / 2;
        int halfY = height / 2;
        if(centerX <= halfX && centerY <= halfY){
            tmpRadius = (float) Math.sqrt(Math.pow(centerX - width, 2) + Math.pow(centerY - height, 2));
        }else if(centerX <= halfX && centerY > halfY){
            tmpRadius = (float) Math.sqrt(Math.pow(centerX - width, 2) + Math.pow(centerY, 2));
        }else if(centerX > halfX && centerY <= halfY){
            tmpRadius = (float) Math.sqrt(Math.pow(centerX, 2) + Math.pow(centerY - height, 2));
        }else{
            tmpRadius = (float) Math.sqrt(Math.pow(centerX, 2) + Math.pow(centerY, 2));
        }
    }

}
