package com.dingrui.posasapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.dingrui.posasapp.utils.SharedPreferencesUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private WebView mWeb;
    public static ValueCallback mFilePathCallback;
    private static final int REQUEST_CODE_PICK_PHOTO = 12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWeb();
    }
    private void initWeb(){
        mWeb = findViewById(R.id.webview);
        mWeb.getSettings().setJavaScriptEnabled(true);
        mWeb.getSettings().setUseWideViewPort(true);
//        mWeb.getSettings().setLoadWithOverviewMode(true);
        mWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWeb.getSettings().setLoadsImagesAutomatically(true);
        mWeb.getSettings().setAllowFileAccess(true);
        mWeb.getSettings().setAppCacheEnabled(true);
        mWeb.getSettings().setDatabaseEnabled(true);
        mWeb.getSettings().setBuiltInZoomControls(true); // 显示放大缩小
        mWeb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //设置 缓存模式
        mWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 开启 DOM storage API 功能
        mWeb.getSettings().setDomStorageEnabled(true);
        mWeb.getSettings().setDefaultTextEncodingName("utf-8");
        mWeb.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //网页标题
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                takeForPhoto();
                mFilePathCallback = filePathCallback;
                return true;
            }
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {// android 系统版本>4.1.1
                takeForPhoto();
                mFilePathCallback = uploadMsg;
            }
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {//android 系统版本<3.0
                takeForPhoto();
                mFilePathCallback = uploadMsg;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {//android 系统版本3.0+
                takeForPhoto();
                mFilePathCallback = uploadMsg;
            }
        });
        mWeb.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("openid")){
                    String id= url.substring(url.indexOf("openid=")+7,url.length());
                    SharedPreferencesUtil.getInstance(MainActivity.this).putString("id",id);
                }else {
                    String id= SharedPreferencesUtil.getInstance(MainActivity.this).getString("id");
                    if (!url.contains("newAppLogin/login")){
                        String myUrl = null;
                        if (!url.contains("?")){
                            myUrl = url+"?openid="+SharedPreferencesUtil.getInstance(MainActivity.this).getString("id");
                        }else {
                            myUrl = url+"&openid="+SharedPreferencesUtil.getInstance(MainActivity.this).getString("id");
                        }
                        view.loadUrl(myUrl);
                    }
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        if (SharedPreferencesUtil.getInstance(this).getString("id") == null || TextUtils.isEmpty(SharedPreferencesUtil.getInstance(this).getString("id"))){
            mWeb.loadUrl(Contance.BASEURL+Contance.LOGIN);
        }else {
            mWeb.loadUrl(Contance.BASEURL+Contance.MAIN);
        }

    }
    /**
     * 调用相册
     */
    private void takeForPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PICK_PHOTO:
                //相册
                takePhotoResult(resultCode, data);
                break;
        }
    }
    //相册
    private void takePhotoResult(int resultCode, Intent data) {
        if (mFilePathCallback != null){
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                Cursor cursor = getContentResolver().query(result,null,null,null,null);
                if (cursor != null && cursor.moveToFirst()){
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    Uri uri = Uri.fromFile(new File(path));
                    if (Build.VERSION.SDK_INT > 18) {
                        mFilePathCallback.onReceiveValue(new Uri[]{uri});
                    } else {
                        mFilePathCallback.onReceiveValue(uri);
                    }
                }
            }else {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }
    }
    @Override
    public void onBackPressed() {
        if(mWeb.canGoBack()){
            mWeb.goBack();
        }else {
            finish();//
        }
    }
    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWeb != null) {
            mWeb.destroy();
            mWeb = null;
        }
        super.onDestroy();
    }

}
