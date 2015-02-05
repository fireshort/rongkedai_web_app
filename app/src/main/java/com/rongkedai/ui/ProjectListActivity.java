package com.rongkedai.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.rongkedai.R;
import com.rongkedai.WebViewActivity;
import com.rongkedai.bean.ProjectBean;
import com.rongkedai.dao.ProjectListDao;
import com.rongkedai.misc.Setting;
import com.rongkedai.misc.Urls;
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

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.project_list);
        ButterKnife.inject(this);

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
            dao.setPageNumber(1);
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
            dao.setPageNumber(mList.size()/Setting.ITEMS_PER_PAGE+1);
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
            ViewHolder holder;
            if(convertView!=null)
            {
                holder=(ViewHolder)convertView.getTag();
            }else
            {
                convertView=mInflater.inflate(R.layout.project_list_item,parent,false);
                holder=new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            final ProjectBean item=mList.get(position);
            holder.name.setText(item.getName());
            String apr="年利率："+item.getApr()+"%";
            if(!item.getFunds().equals("0"))
                apr+=" 奖励："+item.getFunds()+"%";
            holder.apr.setText(apr);
            String limit="天";
            if(item.getStyle()==3)
                limit="个月";
            holder.time_limit.setText("期限："+item.getTime_limit()+limit);
            holder.account.setText("借款金额："+item.getAccount()+"元");
            holder.acount_no.setText("还需金额："+item.getAccount_no()+"元");
            holder.progress.setText("进度："+(int)(item.getAccount_yes()/item.getAccount()*100)+"%");

            String buttonTxt="";
            String buttonBg="#0e99da";
            holder.borrowBtn.setEnabled(false);
            switch(item.getStatus())
            {
            case 3:
                buttonTxt="我要投资";
                buttonBg="#ff0000";
                holder.borrowBtn.setEnabled(true);
                holder.borrowBtn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent=new Intent(ProjectListActivity.this,WebViewActivity.class);
                        intent.putExtra("url",Urls.INTO_BORROW_DETAIL_WEB+item.getId());
                        startActivity(intent);

                    }
                });
                break;
            case 4:
                buttonTxt="已满标";
                buttonBg="#0e99da";
                break;
            case 5:
                buttonTxt="还款中";
                buttonBg="#0e99da";
                break;
            case 6:
                buttonTxt="已还完";
                buttonBg="#CCCCCC";
                break;
            default:
                buttonTxt="预审中";
                buttonBg="#CCCCCC";
                break;
            }
            holder.borrowBtn.setText(buttonTxt);
            //holder.borrowBtn.setBackgroundColor(buttonBg);
            holder.borrowBtn.getBackground().setColorFilter(Color.parseColor(buttonBg), PorterDuff.Mode.DARKEN);


            // etc...

            return convertView;
//            if(convertView==null)
//                convertView=mInflater.inflate(R.layout.terminal_list_item,parent,false);
//            TextView titleTV=(TextView)convertView.findViewById(R.id.term_item_title_tv);
//            TextView brandTV=(TextView)convertView.findViewById(R.id.term_item_brand_tv);
//            ImageView imgIV=(ImageView)convertView.findViewById(R.id.term_item_img_iv);
//            ProjectBean item=mList.get(position);
//            titleTV.setText(item.getTitle());
//            brandTV.setText(item.getBrand());
        }
    }

    static class ViewHolder
    {
        @InjectView(R.id.name_tv)
        TextView name;

        @InjectView(R.id.apr_tv)
        TextView apr;

        @InjectView(R.id.time_limit_tv)
        TextView time_limit;

        @InjectView(R.id.account_tv)
        TextView account;

        @InjectView(R.id.account_no_tv)
        TextView acount_no;

        @InjectView(R.id.borrow_btn)
        Button borrowBtn;

        @InjectView(R.id.progress_tv)
        TextView progress;

        public ViewHolder(View view)
        {
            ButterKnife.inject(this,view);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //add top-left icon click event deal
        switch(item.getItemId()){
        case android.R.id.home:
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}