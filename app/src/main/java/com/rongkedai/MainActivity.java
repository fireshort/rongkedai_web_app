package com.rongkedai;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.rongkedai.misc.Urls;
import com.rongkedai.ui.ProjectListActivity;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.borrow_tv,R.id.project_notice_tv,R.id.website_notice_view,R.id.signin_tv,R.id.about_tv,R.id.discussion_tv,R.id.account_tv})
    public void launchActivity(TextView tv)
    {
        Intent intent;
        switch(tv.getId())
        {
        case R.id.borrow_tv:
            intent=new Intent(this,ProjectListActivity.class);
            //intent.putExtra("url",Urls.PROJECT_LIST_WEB);
            startActivity(intent);
            break;
        case R.id.website_notice_view:
            intent=new Intent(this,WebViewActivity.class);
            intent.putExtra("url",Urls.WEBSITE_NOTICE_WEB);
            startActivity(intent);
            break;
        case R.id.about_tv:
            intent=new Intent(this,WebViewActivity.class);
            intent.putExtra("url",Urls.ABOUT_US_WEB);
            startActivity(intent);
            break;
        case R.id.project_notice_tv:
            intent=new Intent(this,WebViewActivity.class);
            intent.putExtra("url",Urls.PROJECT_NOTICE_WEB);
            startActivity(intent);
            break;
        case R.id.signin_tv:
            intent=new Intent(this,WebViewActivity.class);
            intent.putExtra("url",Urls.SIGNIN_WEB);
            startActivity(intent);
            break;
        case R.id.discussion_tv:
            intent=new Intent(this,WebViewActivity.class);
            intent.putExtra("url",Urls.DISCUSSION_WEB);
            startActivity(intent);
            break;
        case R.id.account_tv:
            intent=new Intent(this,WebViewActivity.class);
            intent.putExtra("url",Urls.ACCOUNT_WEB);
            startActivity(intent);
            break;

        }

    }
}
