/*
 * Copyright (C) 2018 Jian Yang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sloth.functions.widget.particle.fall;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Random;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2020/9/23
 * Description:粒子Model
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/23      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public class ParticleModel {
    // 默认小球宽高
    public static final int PART_WH = 30;
    // 随机数，随机出位置和大小
    static Random random = new Random();
    //center x of circle
    float cx;
    //center y of circle
    float cy;
    // 半径
    float radius;
    // 颜色
    int color;
    // 透明度
    float alpha;
    // 整体边界
    Rect mBound;
    //点信息是否还有效（超出视界、alpha为0等） - 无效的不需要绘制
    private boolean valid = true;

    public ParticleModel(int color, int radius, int virtualItemWidth, int virtualItemHeight, Rect bound, Point point) {
        int row = point.y; //行是高
        int column = point.x; //列是宽

        this.mBound = bound;
        this.color = color;
        this.radius = radius;
        this.alpha = Math.abs(random.nextInt(10)) / 10f;
        //随机失效一些点位
        valid = Math.abs(random.nextInt(10)) < 8;
        this.cx = bound.left + virtualItemWidth * column;
        this.cy = bound.top + virtualItemHeight * row;
    }

    // 每一步动画都得重新计算出自己的状态值
    public void advance(float factor) {
        cx = cx + factor * random.nextInt(mBound.width()) * (random.nextFloat() - 0.5f);
        cy = cy + factor * random.nextInt(mBound.height() / 2);

        radius = radius - factor * random.nextInt(2);

        alpha = (1f - factor) * (1 + random.nextFloat());

        if(cx >= mBound.right || cx < mBound.left || cy >= mBound.bottom ||cy < mBound.top || radius <= 3 || alpha <= 0.3f){
            valid = false;
        }
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getCx() {
        return cx;
    }

    public void setCx(float cx) {
        this.cx = cx;
    }

    public float getCy() {
        return cy;
    }

    public void setCy(float cy) {
        this.cy = cy;
    }

    public boolean isValid() {
        return valid;
    }
}
