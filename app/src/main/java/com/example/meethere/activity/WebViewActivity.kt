package com.example.meethere.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.meethere.R
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {
    private var browser: WebView? = null

    internal inner class MyJavaScriptInterface {
        @JavascriptInterface
        fun processDATA(data: String?) {
            val extra = Bundle()
            val intent = Intent()
            extra.putString("data", data)
            intent.putExtras(extra)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        browser = findViewById<View>(R.id.webView) as WebView
        browser!!.settings.javaScriptEnabled = true
        browser!!.addJavascriptInterface(MyJavaScriptInterface(), "Android")
        browser!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                browser!!.loadUrl("javascript:sample2_execDaumPostcode();")
            }
        }
        browser!!.loadUrl("http://13.124.215.113/daum_address.html")

        btnCancel.setOnClickListener() {
            finish()
        }
    }
}