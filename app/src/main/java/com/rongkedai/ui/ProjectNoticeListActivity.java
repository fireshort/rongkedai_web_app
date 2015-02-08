package com.rongkedai.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.rongkedai.R;
import com.rongkedai.WebViewActivity;
import com.rongkedai.bean.NoticeBean;
import com.rongkedai.dao.ProjectNoticeListDao;
import com.rongkedai.misc.Setting;
import com.rongkedai.misc.Urls;
import com.yuexiaohome.framework.exception.AppException;
import com.yuexiaohome.framework.lib.AsyncTaskEx;
import com.yuexiaohome.framework.util.L;
import com.yuexiaohome.framework.util.Utils;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import java.util.ArrayList;
import java.util.List;

public class ProjectNoticeListActivity extends ActionBarActivity
        implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    @InjectView(R.id.project_list_lv)
    ListView listView;

    @InjectView(R.id.project_load_more_pb)
    View mLoadingFooter;

    @InjectView(R.id.ptr_frame)
    PtrClassicFrameLayout ptrFrame;

    private BaseAdapter mAdapter;

    private LayoutInflater mInflater;

    private AsyncTaskEx<Void, Void, ArrayList<NoticeBean>> mListTask;

    private AsyncTaskEx<Void, Void, ArrayList<NoticeBean>> mLoadMoreTask;


    private List<NoticeBean> mList = new ArrayList<NoticeBean>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.project_list);
        ButterKnife.inject(this);

        mInflater = getLayoutInflater();
        mAdapter = new ListAdapter();

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);

        //mListTask=new RefreshTask().execute();
        mListTask = new RefreshTask();

        ptrFrame.setLastUpdateTimeRelateObject(this);
        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                new RefreshTask().execute();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                View child1 = listView.getChildAt(0);
                ViewGroup.LayoutParams glp = child1.getLayoutParams();
                int top = child1.getTop();
                if(glp instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)glp;
                    return top == mlp.topMargin + listView.getPaddingTop();
                } else {
                    return top == listView.getPaddingTop();
                }
               //return PtrDefaultHandler.checkContentCanBePulledDown(frame,content,header);
            }
        });
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrame.autoRefresh();
            }
        }, 100);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listView = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent=new Intent(this,TerminalDetailActivity.class);
//        intent.putExtra("id",id);
//        startActivity(intent);
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", Urls.PROJECT_NOTICE_DETAIL_WEB + id);
        intent.putExtra("useWideViewPort", false);
        startActivity(intent);
    }

    private boolean mAllLoaded = false;

    private boolean mLastItemVisible = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE
                && !mAllLoaded
                && mLastItemVisible
                && Utils.isTaskStopped(mLoadMoreTask)) {
            mLoadMoreTask = new LoadMoreTask().execute();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
//        L.v(String.format("%d %d %d", firstVisibleItem, visibleItemCount, totalItemCount));
        mLastItemVisible = (firstVisibleItem + visibleItemCount) == totalItemCount;
    }

    class RefreshTask extends AsyncTaskEx<Void, Void, ArrayList<NoticeBean>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProjectNoticeListActivity.this.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected ArrayList<NoticeBean> doInBackground(Void... params) {
            ProjectNoticeListDao dao = new ProjectNoticeListDao();
            dao.setPageNumber(1);
            dao.setItemPerPage(Setting.ITEMS_PER_PAGE);
            try {
                return dao.doAction();
            } catch (AppException e) {
                setFailure(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<NoticeBean> NoticeBeans) {
            mList.clear();
            mList.addAll(NoticeBeans);
            mAdapter.notifyDataSetChanged();
            ProjectNoticeListActivity.this.setProgressBarIndeterminateVisibility(false);
            mAllLoaded = NoticeBeans.size() < Setting.ITEMS_PER_PAGE;
            listView.smoothScrollToPosition(0);

            ptrFrame.refreshComplete();
            new LoadMoreTask().execute();
        }
    }

    class LoadMoreTask extends AsyncTaskEx<Void, Void, ArrayList<NoticeBean>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingFooter.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<NoticeBean> doInBackground(Void... params) {
            ProjectNoticeListDao dao = new ProjectNoticeListDao();
            dao.setPageNumber(mList.size() / Setting.ITEMS_PER_PAGE + 1);
            dao.setItemPerPage(Setting.ITEMS_PER_PAGE);
            try {
                return dao.doAction();
            } catch (AppException e) {
                setFailure(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<NoticeBean> NoticeBeans) {
            mLoadingFooter.setVisibility(View.GONE);
            mList.addAll(NoticeBeans);
            mAdapter.notifyDataSetChanged();
            mAllLoaded = NoticeBeans.size() < Setting.ITEMS_PER_PAGE;
        }
    }

    class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return mList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = mInflater.inflate(R.layout.project_notice_list_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            final NoticeBean item = mList.get(position);
            holder.title.setText(item.getTitle());

            return convertView;
        }
    }

    static class ViewHolder {
        @InjectView(R.id.title_tv)
        TextView title;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //add top-left icon click event deal
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_refresh:
                new RefreshTask().execute();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}