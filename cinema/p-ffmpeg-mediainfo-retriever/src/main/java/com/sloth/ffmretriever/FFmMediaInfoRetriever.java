package com.sloth.ffmretriever;

import android.graphics.Bitmap;
import android.util.Pair;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.mediainfo.MediaInfo;
import com.sloth.pinsplatform.mediainfo.MediaInfoRetriever;

import java.util.HashMap;

import wseemann.media.FFmpegMediaMetadataRetriever;

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
@RouterService(interfaces = MediaInfoRetriever.class, key = Strategies.MediaInfoRetrieverEngine.FFMPEG)
public class FFmMediaInfoRetriever implements MediaInfoRetriever {

    @Override
    public MediaInfo mediaInfo(String path) {
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        try{
            retriever.setDataSource(path);
            String width = retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String rotation = retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            long duration = Long.parseLong(retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
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
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        try{
            retriever.setDataSource(path);
            int width = Integer.parseInt(retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int height = Integer.parseInt(retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
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
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        try{
            retriever.setDataSource(path);
            String rotation = retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
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
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        try{
            retriever.setDataSource(path);
            return Long.parseLong(retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            retriever.release();
        }
        return 0;
    }

    @Override
    public Bitmap cover(String path) {
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        Bitmap b = null;
        try{
            retriever.setDataSource(path, new HashMap<String, String>());
            b = retriever.getFrameAtTime(0L, FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC); // get frame at one second
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            retriever.release();
        }
        return b;
    }

    @Override
    public Bitmap cover(String path, long position) {
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        Bitmap b = null;
        try{
            retriever.setDataSource(path, new HashMap<String, String>());
            b = retriever.getFrameAtTime(position, FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC); // get frame at one second
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            retriever.release();
        }
        return b;
    }

}
