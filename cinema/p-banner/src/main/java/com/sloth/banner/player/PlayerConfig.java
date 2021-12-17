package com.sloth.banner.player;

import com.sloth.player.PlayerConst;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/7/15 14:17
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/7/15         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class PlayerConfig {

    private int playerType;
    private boolean loop;
    private int scaleType;
    private int surfaceType;
    private boolean autoScaleViewPort;

    //是否显示预览图
    private boolean snapshot;
    //封面截图综合质量 0.0 ~1.0
    private float snapshotMixedQuality;

    public PlayerConfig() {
        playerType = PlayerConst.PlayerType.EXO.type;
        surfaceType = PlayerConst.SurfaceType.Surface.code;
        scaleType = PlayerConst.ScaleType.FIT_XY.code;
        autoScaleViewPort = false;
        snapshot = true;
        loop = false;
        snapshotMixedQuality = 0.3f;
    }

    public int getPlayerType() {
        return playerType;
    }

    public void setPlayerType(int playerType) {
        this.playerType = playerType;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public int getScaleType() {
        return scaleType;
    }

    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
    }

    public int getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(int surfaceType) {
        this.surfaceType = surfaceType;
    }

    public boolean isAutoScaleViewPort() {
        return autoScaleViewPort;
    }

    public void setAutoScaleViewPort(boolean autoScaleViewPort) {
        this.autoScaleViewPort = autoScaleViewPort;
    }

    public boolean isSnapshot() {
        return snapshot;
    }

    public void setSnapshot(boolean snapshot) {
        this.snapshot = snapshot;
    }

    public float getSnapshotMixedQuality() {
        return snapshotMixedQuality;
    }

    public void setSnapshotMixedQuality(float snapshotMixedQuality) {
        this.snapshotMixedQuality = snapshotMixedQuality;
    }
}
