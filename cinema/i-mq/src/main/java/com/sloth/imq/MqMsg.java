package com.sloth.imq;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/17 14:18
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/17         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class MqMsg {

    @MqCode
    private int mqCode;

    //json
    private String json;

    public MqMsg(@MqCode int mqCode) {
        this.mqCode = mqCode;
    }

    public MqMsg(@MqCode int mqCode, String json) {
        this.mqCode = mqCode;
        this.json = json;
    }

    public int getMqCode() {
        return mqCode;
    }

    public void setMqCode(int mqCode) {
        this.mqCode = mqCode;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
