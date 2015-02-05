package com.rongkedai.dao;

import com.yuexiaohome.framework.exception.AppException;
import com.rongkedai.misc.Setting;
import com.yuexiaohome.framework.http.HttpException;
import com.yuexiaohome.framework.http.HttpMethod;
import com.yuexiaohome.framework.http.HttpUtility;
import com.yuexiaohome.framework.util.Digest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbsDao<T>
{

    private Map<String, String> mParams=new HashMap<String, String>();

    public T doAction() throws AppException
    {
        //onPreSetupSignature();
        //setupSignature();
        //onPostSetupSignature();
        try
        {
            String response=HttpUtility.executeNormalTask(HttpMethod.Post,getUrl(),mParams);
            JSONObject object=new JSONObject(response);
            return parse(object);
            //checkResultCode(object);
            //return parse(object.getJSONObject("data"));
        }catch(HttpException e)
        {
            throw new AppException("Network error",e);
        }catch(JSONException e)
        {
            throw new AppException("Malformed json",e);
        }
    }

    private void checkResultCode(JSONObject object) throws JSONException, AppException
    {
        JSONObject status=object.getJSONObject("status");
        int code=status.getInt("code");
        if(code!=1)
        {
            String msg=status.optString("message");
            throw new AppException(code,msg);
        }
    }

    protected abstract T parse(JSONObject response) throws JSONException;

    protected abstract String getUrl();

    protected void onPreSetupSignature()
    {
        put("apiVersion","1.0");
        put("lang",Setting.getLang());
    }

    protected void onPostSetupSignature()
    {

    }

    protected void put(String key,String value)
    {
        mParams.put(key,value);
    }

    protected void put(String key,int value)
    {
        mParams.put(key,String.valueOf(value));
    }

    protected void put(String key,long value)
    {
        mParams.put(key,String.valueOf(value));
    }

    protected void put(String key,boolean value)
    {
        mParams.put(key,String.valueOf(value));
    }

    protected void setupSignature()
    {
        String sign=getSignature(mParams);
        mParams.put("signature",sign);
    }

    private String getSignature(Map<String, String> params)
    {
        Set<String> keys=params.keySet();
        String[] paramsKey=keys.toArray(new String[keys.size()]);
        Arrays.sort(paramsKey);
        StringBuilder builder=new StringBuilder();
        for(String key : paramsKey)
        {
            builder.append(key).append("=").append(params.get(key));
        }
        String source=builder.append("hua-wei-tdd-app").toString();
        return Digest.getMD5(source);
    }

}
