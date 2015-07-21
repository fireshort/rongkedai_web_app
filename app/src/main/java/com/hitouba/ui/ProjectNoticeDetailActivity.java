package com.hitouba.ui;

import android.app.Activity;

public class ProjectNoticeDetailActivity extends Activity
{
    /*

    private AsyncTaskEx<Void, Void, String> mContactUsTask;

    private TextView mContentTV;

    private ImageView mContactUsIv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notice_detail);

        mContentTV=(TextView)findViewById(R.id.contentTV);
        mContactUsIv=(ImageView)findViewById(R.id.contactUsIv);

        mContactUsTask=new ContactUsTask().execute();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Utils.tryCancelTask(mContactUsTask);
    }

    class ContactUsTask extends AsyncTaskEx<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... params)
        {
            ContactUsDao dao=new ContactUsDao();
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
        protected void onPostExecute(String content)
        {
            super.onPostExecute(content);
            //mContentTV.setText(Html.fromHtml(content));

            URLImageParser p=new URLImageParser(mContactUsIv);
            Spanned htmlSpan=Html.fromHtml(content,p,null);
            mContentTV.setText(htmlSpan);

            //mLoadingLayout.setLoadingShown(false);
        }
    }*/

}