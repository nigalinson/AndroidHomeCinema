package com.xunlei.downloadlib;

import com.xunlei.downloadlib.parameter.ThunderUrlInfo;

public class XLLoader {

    public native int parserThunderUrl(String str, ThunderUrlInfo thunderUrlInfo);


    public XLLoader() {
        System.loadLibrary("xl_stat");
        System.loadLibrary("xluagc");
        System.loadLibrary("xl_thunder_sdk");
        System.loadLibrary("xl_thunder_iface");
    }

    public static final class Holder {
        public static XLLoader INSTANCE = new XLLoader();
    }
}