package com.sloth.functions.mediainfo;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Pair;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.mediainfo.MediaInfo;
import com.sloth.pinsplatform.mediainfo.MediaInfoRetriever;
import java.util.HashMap;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/19 16:09
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = MediaInfoRetriever.class, key = Strategies.MediaInfoRetrieverEngine._DEFAULT, defaultImpl = true)
public class DefaultMediaInfoRetriever implements MediaInfoRetriever {

    @Override
    public MediaInfo mediaInfo(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try{
            retriever.setDataSource(path);
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            return new MediaInfo(width, height, rotation, duration);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            retriever.release();
        }
        return null;
    }

    @Override
    public Pair<Integer, Integer> videoSize(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try{
            retriever.setDataSource(path);
            int width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            return new Pair<>(width, height);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            retriever.release();
        }
        return null;
    }

    @Override
    public int rotation(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try{
            retriever.setDataSource(path);
            String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            return Integer.parseInt(rotation);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            retriever.release();
        }
        return 0;
    }

    @Override
    public long duration(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try{
            retriever.setDataSource(path);
            return Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            retriever.release();
        }
        return 0;
    }

    @Override
    public Bitmap cover(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap b = null;
        try{
            retriever.setDataSource(path, new HashMap<String, String>());
            b = retriever.getFrameAtTime(0L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC); // get frame at one second
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            retriever.release();
        }
        return b;
    }

    @Override
    public Bitmap cover(String path, long position) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap b = null;
        try{
            retriever.setDataSource(path, new HashMap<String, String>());
            b = retriever.getFrameAtTime(position, MediaMetadataRetriever.OPTION_CLOSEST_SYNC); // get frame at one second
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            retriever.release();
        }
        return b;
    }

}
