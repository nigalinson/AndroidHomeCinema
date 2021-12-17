package com.sloth.push;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/17 18:31
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/17         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class PushData implements Parcelable {

    protected PushData(Parcel in) {
        type = in.readInt();
        json = in.readString();
    }

    public static final Creator<PushData> CREATOR = new Creator<PushData>() {
        @Override
        public PushData createFromParcel(Parcel in) {
            return new PushData(in);
        }

        @Override
        public PushData[] newArray(int size) {
            return new PushData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(json);
    }

    @IntDef({Type.ERR, Type.ADD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type{
        int ERR = 0;
        int ADD = 1;
    }

    private int type;

    private String json;

    public PushData(@Type int type) {
        this.type = type;
    }

    public PushData(@Type int type, String json) {
        this.type = type;
        this.json = json;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
