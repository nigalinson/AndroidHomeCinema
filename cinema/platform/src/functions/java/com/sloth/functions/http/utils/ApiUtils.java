package com.sloth.functions.http.utils;

import com.google.protobuf.ByteString;
import com.sloth.functions.http.options.APIInfos;
import com.sloth.tools.util.LogUtils;
import java.io.File;
import java.util.Random;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/29 17:21
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/29         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ApiUtils {
    private static final String TAG = ApiUtils.class.getSimpleName();

    public static String genRandomPwd() {
        final int maxNum = 36;
        int i;
        int count = 0;
        int pwdLength = 8;
        char[] str = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        StringBuilder pwd = new StringBuilder();
        Random r = new Random();
        while (count < pwdLength) {
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }
        return pwd.toString();
    }

    public static MultipartBody.Part createFilePart(String path) {
        LogUtils.d(TAG, path);
        File file = new File(path);
        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse(APIInfos.HTTP_FILE_TYPE), file);
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("myfiles", file.getName(), requestFile);
    }

    /**
     * 创建文本类型的 RequestBody
     *
     * @param json 文本数据
     */
    public static RequestBody createRequestBody(String json) {
        LogUtils.d(TAG, json);
        return RequestBody.create(MediaType.parse(APIInfos.HTTP_INPUT_TYPE), json);
    }

    /**
     * 创建文本流类型的 RequestBody
     *
     * @param json 文本数据
     */
    public static RequestBody createRequestBody(byte[] json) {
        return RequestBody.create(MediaType.parse(APIInfos.HTTP_INPUT_TYPE), json);
    }

    /**
     * 创建文本流类型的 RequestBody
     *
     * @param json 文本数据
     */
    public static RequestBody createRequestBodyStream(byte[] json) {
        return RequestBody.create(MediaType.parse(APIInfos.HTTP_INPUT_TYPE_STREAM), json);
    }

    /**
     * 创建指定类型的body
     * @param json
     * @param formatType
     * @return
     */
    public static RequestBody createRequestBody(String json, int formatType) {
        if (formatType == 0) {
            return createRequestBody(json);
        } else {
            return createRequestBody(ByteString.copyFromUtf8(json).toByteArray());
        }
    }

}
