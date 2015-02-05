package com.rongkedai.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import butterknife.InjectView;
import com.rongkedai.R;
import com.rongkedai.bean.ProjectBean;
import com.rongkedai.dao.ProjectListDao;
import com.rongkedai.misc.Setting;
import com.yuexiaohome.framework.exception.AppException;
import com.yuexiaohome.framework.lib.AsyncTaskEx;
import com.yuexiaohome.framework.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ProjectListActivity extends Activity
        implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener
{
    @InjectView(R.id.project_list_lv)
    ListView mGridView;

    @InjectView(R.id.project_load_more_pb)
    View mLoadingFooter;

    private BaseAdapter mAdapter;

    private LayoutInflater mInflater;

    private AsyncTaskEx<Void, Void, ArrayList<ProjectBean>> mListTask;

    private AsyncTaskEx<Void, Void, ArrayList<ProjectBean>> mLoadMoreTask;

    private List<ProjectBean> mList=new ArrayList<ProjectBean>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_list);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        mInflater=getLayoutInflater();
        mAdapter=new ListAdapter();

        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(this);

        mListTask=new RefreshTask().execute();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mGridView=null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent,View view,int position,long id)
    {
//        Intent intent=new Intent(this,TerminalDetailActivity.class);
//        intent.putExtra("id",id);
//        startActivity(intent);
    }

    private boolean mAllLoaded=false;

    private boolean mLastItemVisible=false;

    @Override
    public void onScrollStateChanged(AbsListView view,int scrollState)
    {
        if(scrollState==SCROLL_STATE_IDLE
                &&!mAllLoaded
                &&mLastItemVisible
                &&Utils.isTaskStopped(mLoadMoreTask))
        {
            mLoadMoreTask=new LoadMoreTask().execute();
        }
    }

    @Override
    public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,
            int totalItemCount)
    {
//        L.v(String.format("%d %d %d", firstVisibleItem, visibleItemCount, totalItemCount));
        mLastItemVisible=(firstVisibleItem+visibleItemCount)==totalItemCount;
    }

    class RefreshTask extends AsyncTaskEx<Void, Void, ArrayList<ProjectBean>>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            ProjectListActivity.this.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected ArrayList<ProjectBean> doInBackground(Void... params)
        {
            ProjectListDao dao=new ProjectListDao();
            dao.setStartIndex(0);
            dao.setItemPerPage(Setting.ITEMS_PER_PAGE);
            try
            {
                return dao.doAction();
            }catch(AppException e)
            {
                setFailure(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<ProjectBean> ProjectBeans)
        {
            mList.clear();
            mList.addAll(ProjectBeans);
            mAdapter.notifyDataSetChanged();
            ProjectListActivity.this.setProgressBarIndeterminateVisibility(false);
            mAllLoaded=ProjectBeans.size()<Setting.ITEMS_PER_PAGE;
            mGridView.smoothScrollToPosition(0);
        }
    }

    class LoadMoreTask extends AsyncTaskEx<Void, Void, ArrayList<ProjectBean>>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mLoadingFooter.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<ProjectBean> doInBackground(Void... params)
        {
            ProjectListDao dao=new ProjectListDao();
            dao.setStartIndex(mList.size());
            dao.setItemPerPage(Setting.ITEMS_PER_PAGE);
            try
            {
                return dao.doAction();
            }catch(AppException e)
            {
                setFailure(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<ProjectBean> ProjectBeans)
        {
            mLoadingFooter.setVisibility(View.GONE);
            mList.addAll(ProjectBeans);
            mAdapter.notifyDataSetChanged();
            mAllLoaded=ProjectBeans.size()<Setting.ITEMS_PER_PAGE;
        }
    }

    class ListAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return mList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return mList.get(position).getId();
        }

        @Override
        public View getView(int position,View convertView,ViewGroup parent)
        {
//            if(convertView==null)
//                convertView=mInflater.inflate(R.layout.terminal_list_item,parent,false);
//            TextView titleTV=(TextView)convertView.findViewById(R.id.term_item_title_tv);
//            TextView brandTV=(TextView)convertView.findViewById(R.id.term_item_brand_tv);
//            ImageView imgIV=(ImageView)convertView.findViewById(R.id.term_item_img_iv);
//            ProjectBean item=mList.get(position);
//            titleTV.setText(item.getTitle());
//            brandTV.setText(item.getBrand());
            return convertView;
        }
    }

}