package com.hitouba.dao;

import com.hitouba.bean.ProjectBean;
import com.hitouba.misc.Urls;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectListDao extends AbsDao<ArrayList<ProjectBean>>
{

//    pageNumber	页码
//    itemsPerPage	选填	一页显示多少条

    private long pageNumber;

    private int itemPerPage;

    @Override
    protected void onPreSetupSignature()
    {
        super.onPreSetupSignature();
        put("pageNumber",pageNumber);
        put("itemsPerPage",itemPerPage);
    }

    @Override
    protected ArrayList<ProjectBean> parse(JSONObject response) throws JSONException
    {
        JSONArray array=response.getJSONArray("result");
        ArrayList<ProjectBean> list=new ArrayList<ProjectBean>(array.length());
        for(int i=0; i<array.length(); i++)
        {
            ProjectBean item=new ProjectBean();
            JSONObject obj=array.getJSONObject(i);
            item.setId(obj.getLong("id"));
            item.setName(obj.getString("name"));
            item.setApr(obj.getString("apr"));
            item.setTime_limit(obj.getString("time_limit"));
            item.setAccount(obj.getLong("account"));
            item.setAccount_no(obj.getLong("account_no"));
            item.setAccount_yes(obj.getDouble("account_yes"));
            item.setStyle(obj.getInt("style"));
            item.setStatus(obj.getInt("status"));
            item.setFunds(obj.getString("funds"));
            item.setUse(obj.getString("use"));
            item.setIs_vouch(obj.getInt("is_vouch"));
            list.add(item);
        }
        return list;
    }

    @Override
    protected String getUrl()
    {
        return Urls.PROJECT_LIST;
    }

    public void setItemPerPage(int itemPerPage)
    {
        this.itemPerPage=itemPerPage;
    }

    public void setPageNumber(long pageNumber)
    {
        this.pageNumber=pageNumber;
    }

}
