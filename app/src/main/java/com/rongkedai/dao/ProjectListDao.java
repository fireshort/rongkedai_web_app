package com.rongkedai.dao;

import com.rongkedai.bean.ProjectBean;
import com.rongkedai.misc.Urls;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectListDao extends AbsDao<ArrayList<ProjectBean>>
{

//    startIndex	选填	起始记录数
//    itemsPerPage	选填	一页显示多少条

    private long startIndex;

    private int itemPerPage;

    @Override
    protected void onPreSetupSignature()
    {
        super.onPreSetupSignature();
        put("startIndex",startIndex);
        put("itemsPerPage",itemPerPage);
    }

    @Override
    protected ArrayList<ProjectBean> parse(JSONObject response) throws JSONException
    {
        JSONArray array=response.getJSONArray("items");
        ArrayList<ProjectBean> list=new ArrayList<ProjectBean>(array.length());
        for(int i=0; i<array.length(); i++)
        {
            ProjectBean item=new ProjectBean();
            JSONObject obj=array.getJSONObject(i);
            item.setId(obj.getLong("id"));
            item.setName(obj.getString("name"));
            item.setApr(obj.getString("apr"));
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

    public void setStartIndex(long startIndex)
    {
        this.startIndex=startIndex;
    }

}
