package com.rongkedai.dao;

import com.rongkedai.bean.AccountBean;
import com.rongkedai.misc.Urls;
import org.json.JSONException;
import org.json.JSONObject;

public class MyAccountDao extends AbsDao<AccountBean>
{
    @Override
    protected void onPreSetupSignature()
    {
        super.onPreSetupSignature();
    }

    @Override
    protected AccountBean parse(JSONObject response) throws JSONException
    {
        AccountBean accountBean=new AccountBean();
        accountBean.setUsername(response.getString("user"));
        accountBean.setUser_id(response.getJSONObject("account").getLong("user_id"));
        return accountBean;
    }

    @Override
    protected String getUrl()
    {
        return Urls.MY_ACCOUNT;
    }


}
