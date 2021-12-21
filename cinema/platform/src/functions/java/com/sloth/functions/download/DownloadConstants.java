package com.sloth.functions.download;

import android.os.Environment;

import com.sloth.tools.util.Utils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/20 15:14
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/20         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class DownloadConstants {

    public static String getDownloadMovieFolder(){
        return Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath();
    }

    public static String downloadMovieFilePath(long id){
        return getDownloadMovieFolder() + "/" + id + ".mp4";
    }

}
