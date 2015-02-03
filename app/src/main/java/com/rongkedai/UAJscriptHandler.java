package com.rongkedai;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class UAJscriptHandler
{
    private final String tag="UAJscriptHandler";

    private Context context=null;

    public UAJscriptHandler(Context context)
    {
        Log.i(tag,"script handler created");
        this.context=context;
    }

    public void Log(String s)
    {
        Log.i(tag,s);
    }

    public void Info(String s)
    {
        // new UIUtil(context).showLongToast(s);
    }

    // @JavaScriptInterface
    public void PlaceCall(String number)
    {
        Log.i(tag,"Placing a phone call to ["+number+"]");
        String url="tel:"+number;
        Intent callIntent=new Intent(Intent.ACTION_CALL,Uri.parse(url));
        context.startActivity(callIntent);
    }

    public void SetSearchTerm(String searchTerm)
    {
        // BMSApplication app=(BMSApplication)context.getApplicationContext();
        // app.setSearchTerm(searchTerm);
    }
}