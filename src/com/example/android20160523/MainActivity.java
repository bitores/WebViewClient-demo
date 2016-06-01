package com.example.android20160523;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button sBtn = null;
	private EditText sText = null;
	private WebView sClient = null;
	private ProgressBar sPb = null;
	private String sTil ="";
	// webview实现file文件上传
	private Context context = null;
//	private String mCameraFilePath = null;  
	
	public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;  
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("onCreate", "i");
		
		context = getApplicationContext();
		
		sBtn = (Button)findViewById(R.id.myButton);
		sText = (EditText)findViewById(R.id.myEdit);
		sPb = (ProgressBar)findViewById(R.id.progressBar1);
		sPb.setMax(100);
		
		sClient = (WebView)findViewById(R.id.webView1);
		sClient.getSettings().setJavaScriptEnabled(true);
		sClient.getSettings().setAllowFileAccess(true);
		sClient.getSettings().setAllowContentAccess(true);
		/*
		 * 在WebView的设计中，不是什么任务都由WebView类完成的，辅助的类完全其它辅助性的工作，WebViewy主要负责解析、渲染。
		 * WebViewClient就是辅助WebView处理各种通知、请求事件的，具体来说包括：
		 * onLoadResource、onPageStart、onPageFinish、onReceiveError、onReceivedHttpAuthRequest；
		 * WebChromeClient是辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
		 * onCloseWindow(关 闭WebView)、onCreateWindow()、onJsAlert (WebView上alert是弹不出来东西的，需要定制你的WebChromeClient处理弹出)、
		 * onJsPrompt、 onJsConfirm、onProgressChanged、onReceivedIcon、onReceivedTitle；
		 * 
		 * */
		
		sClient.setWebViewClient(new WebViewClient(){
	       @Override
	       public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	   if (url.startsWith("tel:")){ 
	                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
	                startActivity(intent);
	                return true;
                } 
		        view.loadUrl(url);
		        return true;
	        }
	
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				sPb.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}
		});
		
		sClient.setWebChromeClient(new WebChromeClient(){
			// js中alert
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				// TODO Auto-generated method stub
				return super.onJsAlert(view, url, message, result);
			}
			// loadurl loading
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				sPb.setProgress(newProgress);  
		        if(newProgress==100){  
		        	sPb.setVisibility(View.GONE);  
		        }  
				super.onProgressChanged(view, newProgress);
			}
			
			//扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
            }

            // For Android > 5.0
            public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
                openFileChooserImplForAndroid5(uploadMsg);
                return true;
            }
		});
		
		// 提供对外接口
		sClient.addJavascriptInterface(new BrowserInterface(this), "BrowserInterface");
		sClient.loadUrl("file:///android_asset/index.html");
		
		sBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = sText.getText().toString();
				Log.i("onclick", str);
//				if("vedio"==str){
					sClient.loadUrl("http://player.youku.com/embed/XNTM5MTUwNDA0");
//				} else if("file"==str){
//					sClient.loadUrl("file:///android_asset/index.html");
//				} else {
//					sClient.loadUrl("http://"+str);
//				}
				
			}
			
		});
	}
	
	private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (sClient.canGoBack() && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //获取历史列表
            WebBackForwardList mWebBackForwardList = sClient
                    .copyBackForwardList();
            //判断当前历史列表是否最顶端,其实canGoBack已经判断过
            if (mWebBackForwardList.getCurrentIndex() > 0) {
            	sClient.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null: intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5){
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null: intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
            if (!Thread.currentThread().isInterrupted()){
                switch (msg.what) {
                    case 0:
                        sPb.setVisibility(View.VISIBLE);// 显示进度对话框
                        break;
                    case 1:
                    	sPb.setVisibility(View.GONE);// 隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。
                        break;
                }
            }

            super.handleMessage(msg);
        }
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
