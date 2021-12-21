package com.sloth.functions.download;

import java.io.File;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/21 14:13
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/21         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class DownloadUtils {

    public static File findMovie(File subFile) {
        if(subFile.isFile()){
            if(subFile.getName().endsWith(".mp4")
                    || subFile.getName().endsWith(".mkv")
                    || subFile.getName().endsWith(".flv")
                    || subFile.getName().endsWith(".rmvb")
                    || subFile.getName().endsWith(".avi")
                    || subFile.getName().endsWith(".mov")){
                if(subFile.length() > 50 * 1024 * 1024){
                    //bigger than 50MB
                    return subFile;
                }
            }
            return null;
        }

        File[] children = subFile.listFiles();

        if(children == null || children.length == 0){
            return null;
        }

        for(File child: children){
            File res = findMovie(child);
            if(res != null){
                return res;
            }
        }
        return null;
    }
}
