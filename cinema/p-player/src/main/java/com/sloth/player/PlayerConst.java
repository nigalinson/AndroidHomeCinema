package com.sloth.player;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/7/15 18:01
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/7/15         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class PlayerConst {

    public enum PlayerType {

        /**
         * google官方Exo
         */
        EXO(1),
        /**
         * 原生VideoView
         */
        NATIVE(2)
        ;

        PlayerType(int type) {
            this.type = type;
        }

        public int type;

        public int getType() {
            return type;
        }
    }

    public enum SurfaceType{
        /**
         * 性能较好但不支持层级的surface
         */
        Surface(1),
        /**
         * 内存消耗较大，但对动画和层级支持比较完善
         */
        Texture(2)
        ;

        SurfaceType(int code) {
            this.code = code;
        }

        public int code;
    }

    public enum ScaleType{
        FIT_XY(1),
        FIT_CENTER(2);

        ScaleType(int code) {
            this.code = code;
        }

        public int code;
    }

}
