package com.sloth.functions.dynamicbean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/9/23 15:25
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/9/23         Carl            1.0                    1.0
 * Why & What is modified:
 */
public final class DynamicBean {
    private final Object _obj;

    private JSONObject _tmpJsonObj;
    private JSONArray _tmpJsonArr;

    public DynamicBean(Object obj) {
        this._obj = obj;
    }

    public Integer toInt(){
        return toInt(null);
    }

    public Integer toInt(Integer def){
        if(null == _obj){ return def; }

        if(_obj instanceof Integer){
            return (Integer) _obj;
        }

        if(_obj instanceof Long){
            return (int)((Long)_obj).longValue();
        }

        if(_obj instanceof String){
            try{
                return Integer.parseInt((String)_obj);
            }catch (Exception e){
                return def;
            }
        }
        return def;
    }

    public Long toLong(){
        return toLong(null);
    }

    public Long toLong(Long def){
        if(null == _obj){ return def; }

        if(_obj instanceof Long){
            return (Long)_obj;
        }

        if(_obj instanceof Integer){
            return ((Integer)_obj).longValue();
        }

        if(_obj instanceof String){
            try{
                return Long.parseLong((String)_obj);
            }catch (Exception e){
                return def;
            }
        }
        return def;
    }

    public String toStr(){
        return toStr(null);
    }

    public String toStr(String def){
        if(null == _obj){ return def; }

        if(_obj instanceof JSONObject){
            return ((JSONObject)_obj).toString();
        }

        if(_obj instanceof JSONArray){
            return ((JSONArray)_obj).toString();
        }

        if(_tmpJsonObj != null){
            return _tmpJsonObj.toString();
        }

        if(_tmpJsonArr != null){
            return _tmpJsonArr.toString();
        }

        return _obj.toString();
    }

    public Boolean toBool(){
        return toBool(null);
    }

    public Boolean toBool(Boolean def){
        if(null == _obj){ return def; }

        if(_obj instanceof Boolean){
            return (Boolean) _obj;
        }

        if(_obj instanceof String){
            return ((String)_obj).equalsIgnoreCase("true")
                    || ((String)_obj).equalsIgnoreCase("1");
        }

        if(_obj instanceof Integer || _obj instanceof Long || _obj instanceof Double || _obj instanceof Float){
            return _obj.equals(1);
        }

        return def;
    }

    public Float toFloat(){
        return toFloat(null);
    }

    public Float toFloat(Float def){
        if(null == _obj){ return def; }

        if(_obj instanceof Float){
            return (Float) _obj;
        }

        if(_obj instanceof Double){
            return ((Double) _obj).floatValue();
        }


        if(_obj instanceof Integer){
            return (float) (Integer) _obj;
        }

        if(_obj instanceof Long){
            return (float) (Long) _obj;
        }

        if(_obj instanceof String){
            try{
                return Float.parseFloat((String)_obj);
            }catch (Exception e){
                return def;
            }
        }
        return def;
    }

    public Double toDouble(){
        return toDouble(null);
    }

    public Double toDouble(Double def){
        if(null == _obj){ return def; }

        if(_obj instanceof Double){
            return (Double) _obj;
        }

        if(_obj instanceof Float){
            return (double) (Float) _obj;
        }

        if(_obj instanceof Integer){
            return (double) (Integer) _obj;
        }

        if(_obj instanceof Long){
            return (double) (Long) _obj;
        }

        if(_obj instanceof String){
            try{
                return Double.parseDouble((String)_obj);
            }catch (Exception e){
                return def;
            }
        }
        return def;
    }

    public DynamicBean get(String key){
        if(_obj != null && _obj instanceof JSONObject){
            return new DynamicBean(((JSONObject)_obj).opt(key));
        }

        if(_obj != null && _obj instanceof String){
            if(_tmpJsonObj == null){
                try {
                    _tmpJsonObj = new JSONObject((String)_obj);
                } catch (JSONException e) { }
            }

            if(_tmpJsonObj != null){
                return new DynamicBean(_tmpJsonObj.opt(key));
            }
        }

        return new DynamicBean(null);
    }

    public void set(String key, DynamicBean bean){
        if(_obj != null && _obj instanceof JSONObject){
            try {
                ((JSONObject) _obj).putOpt(key, dynamicBeanToRealValue(bean));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(_obj != null && _obj instanceof String){
            if(_tmpJsonObj == null){
                try {
                    _tmpJsonObj = new JSONObject((String)_obj);
                } catch (JSONException e) { }
            }

            if(_tmpJsonObj != null){
                try {
                    _tmpJsonObj.putOpt(key, dynamicBeanToRealValue(bean));
                } catch (JSONException e) { }
            }
        }
    }

    public void set(String key, Integer v){
        set(key, new DynamicBean(v));
    }

    public void set(String key, Long v){
        set(key, new DynamicBean(v));
    }

    public void set(String key, String v){
        set(key, new DynamicBean(v));
    }

    public void set(String key, Boolean v){
        set(key, new DynamicBean(v));
    }

    public void set(String key, Float v){
        set(key, new DynamicBean(v));
    }

    public void set(String key, Double v){
        set(key, new DynamicBean(v));
    }

    public DynamicBean get(int index){
        if(_obj != null && _obj instanceof JSONArray){
            return new DynamicBean(((JSONArray)_obj).opt(index));
        }

        if(_obj != null && _obj instanceof String){
            if(_tmpJsonArr == null){
                try {
                    _tmpJsonArr = new JSONArray((String)_obj);
                } catch (JSONException e) { }
            }

            if(_tmpJsonArr != null){
                return new DynamicBean(_tmpJsonArr.opt(index));
            }
        }

        return new DynamicBean(null);
    }

    public void set(int index, DynamicBean bean){
        if( _obj != null && _obj instanceof JSONArray){
            try {
                ((JSONArray) _obj).put(index, dynamicBeanToRealValue(bean));
            } catch (JSONException e) { }
        }

        if(_obj != null && _obj instanceof String){
            if(_tmpJsonArr == null){
                try {
                    _tmpJsonArr = new JSONArray((String)_obj);
                } catch (JSONException e) { }
            }

            if(_tmpJsonArr != null){
                try {
                    _tmpJsonArr.put(index, dynamicBeanToRealValue(bean));
                } catch (JSONException e) { }
            }
        }
    }

    public void set(int index, Integer v){
        set(index, new DynamicBean(v));
    }

    public void set(int index, Long v){
        set(index, new DynamicBean(v));
    }

    public void set(int index, String v){
        set(index, new DynamicBean(v));
    }

    public void set(int index, Boolean v){
        set(index, new DynamicBean(v));
    }

    public void set(int index, Float v){
        set(index, new DynamicBean(v));
    }

    public void set(int index, Double v){
        set(index, new DynamicBean(v));
    }

    private static Object dynamicBeanToRealValue(DynamicBean bean) {
        if(bean._obj instanceof String){
            if(bean._tmpJsonObj == null){
                try {
                    bean._tmpJsonObj = new JSONObject((String)bean._obj);
                } catch (JSONException e) { }
            }

            if(bean._tmpJsonObj != null){
                return bean._tmpJsonObj;
            }

            if(bean._tmpJsonArr == null){
                try {
                    bean._tmpJsonArr = new JSONArray((String)bean._obj);
                } catch (JSONException e) { }
            }

            if(bean._tmpJsonArr != null){
                return bean._tmpJsonArr;
            }
        }

        return bean._obj;
    }

    public Iterator<String> keys(){
        if(_obj != null && _obj instanceof JSONObject){
            return ((JSONObject)_obj).keys();
        }

        if(_obj != null && _obj instanceof String){
            if(_tmpJsonObj == null){
                try {
                    _tmpJsonObj = new JSONObject((String)_obj);
                } catch (JSONException e) { }
            }

            if(_tmpJsonObj != null){
                return _tmpJsonObj.keys();
            }
        }

        List<String> empty = Collections.emptyList();
        return empty.iterator();
    }

    public int length(){
        if(_obj != null && _obj instanceof JSONObject){
            return ((JSONObject)_obj).length();
        }

        if(_obj != null && _obj instanceof JSONArray){
            return ((JSONArray)_obj).length();
        }

        if(_obj != null && _obj instanceof String){
            if(_tmpJsonObj == null){
                try {
                    _tmpJsonObj = new JSONObject((String)_obj);
                } catch (JSONException e) { }
            }

            if(_tmpJsonObj != null){
                return _tmpJsonObj.length();
            }

            if(_tmpJsonArr == null){
                try {
                    _tmpJsonArr = new JSONArray((String)_obj);
                } catch (JSONException e) { }
            }

            if(_tmpJsonArr != null){
                return _tmpJsonArr.length();
            }
        }

        return 0;
    }

    public boolean isNull(){
        return (_obj == null);
    }

    public boolean notNull(){
        return (_obj != null);
    }

    public DynamicBean opt(String key, Opt<?> opt){
        if(opt instanceof OptInt){
            Integer res =  get(key).toInt();
            if(res != null){
                ((OptInt)opt).execute(res);
            }
        }else if(opt instanceof OptLong){
            Long res = get(key).toLong();
            if(res != null){
                ((OptLong)opt).execute(res);
            }
        }else if(opt instanceof OptStr){
            String res = get(key).toStr();
            if(res != null){
                ((OptStr)opt).execute(res);
            }
        }else if(opt instanceof OptStr2){
            String res = get(key).toStr();
            if(res != null && !"".equals(res)){
                ((OptStr2)opt).execute(res);
            }
        }else if(opt instanceof OptFloat){
            Float res = get(key).toFloat();
            if(res != null){
                ((OptFloat)opt).execute(res);
            }
        }else if(opt instanceof OptDouble){
            Double res = get(key).toDouble();
            if(res != null){
                ((OptDouble)opt).execute(res);
            }
        }else{
            Object o = get(key)._obj;
            if(o != null){
                ((Opt<Object>)opt).execute(o);
            }
        }
        return this;
    }

    public static DynamicBean parse(String txt){
        if("{}".equalsIgnoreCase(txt)){
            return new DynamicBean(new JSONObject());
        }else if("[]".equalsIgnoreCase(txt)){
            return new DynamicBean(new JSONArray());
        }else{
            return new DynamicBean(txt);
        }
    }

}
