package com.hitouba.dao;

import com.hitouba.bean.NoticeBean;
import com.hitouba.misc.Urls;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectNoticeListDao extends AbsDao<ArrayList<NoticeBean>>
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
    protected ArrayList<NoticeBean> parse(JSONObject response) throws JSONException
    {
        JSONArray array=response.getJSONArray("result");
        ArrayList<NoticeBean> list=new ArrayList<NoticeBean>(array.length());
        for(int i=0; i<array.length(); i++)
        {
            NoticeBean item=new NoticeBean();
            JSONObject obj=array.getJSONObject(i);
            item.setId(obj.getLong("id"));
            item.setTitle(obj.getString("title"));
            list.add(item);
        }
        return list;
    }

    @Override
    protected String getUrl()
    {
        return Urls.PROJECT_NOTICE_LIST;
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
