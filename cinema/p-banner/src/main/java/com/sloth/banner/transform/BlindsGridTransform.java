package com.sloth.banner.transform;

import android.content.Context;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sloth.banner.transform.agent.BaseAgentTransform;
import com.sloth.functions.widget.blinds.DrawBlindsView;

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
 * todo 第二张预览图支持
 */
public class BlindsGridTransform extends BaseAgentTransform<DrawBlindsView> implements Orientable {

    private static final String TAG = BlindsGridTransform.class.getSimpleName();

    private int rowCount = 10, columnCount = 5, itemSpace = 15;

    private long duration = 1000;

    public BlindsGridTransform() { }

    public BlindsGridTransform(int rowCount, int columnCount, int itemSpace) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.itemSpace = itemSpace;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public int getItemSpace() {
        return itemSpace;
    }

    public void setItemSpace(int itemSpace) {
        this.itemSpace = itemSpace;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private int orientation = RecyclerView.HORIZONTAL;

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    @Override
    public int orientation() {
        return orientation;
    }

    private boolean isHorizontal(){
        return orientation == RecyclerView.HORIZONTAL;
    }

    /**
     * args
     * 0-vertical
     * 1-itemSpace
     * 2-rowCount
     * 3-columnCount
     * 4-duration
     *
     * @param view
     * @param position
     */
    @Override
    protected void transforming(AgentDecor<DrawBlindsView> view, float position) {
        //offset keep x or y still
        if(isHorizontal()){
            view.setTranslationX(-view.getWidth() * position);
        }else{
            view.setTranslationY(-view.getHeight() * position);
        }

        if(position > 1 || position <= -1){
            ViewCompat.setTranslationZ(view, -1f);
            view.clear();
            view.getChildAt(0).setAlpha(1);
        }else if(position <= 0){
            ViewCompat.setTranslationZ(view, 1f);

            if(position >= -0.6f){
                view.getChildAt(0).setAlpha(1);
            }else{
                if(!view.isPlaying()){
                    view.play(
                            snapExit(view),
                            snapEnter(cacheBackView),
                            !isHorizontal() ,
                            itemSpace, rowCount, columnCount, duration);
                }
                view.getChildAt(0).setAlpha(0);
            }
        }else if(position <= 1){
            ViewCompat.setTranslationZ(view, 0f);
            view.clear();
            view.getChildAt(0).setAlpha(1);
            cacheBackView = view;
        }
    }

    private AgentDecor cacheBackView;

    @Override
    protected DrawBlindsView createAgentView(Context context) {
        return new DrawBlindsView(context);
    }

}
