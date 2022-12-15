package com.android.woopons.dashboard.ui.termsconditions

import android.graphics.Bitmap
import android.net.http.SslError
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.android.woopons.databinding.ActivityTermsAndConditionsBinding
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.Constants
import com.kaopiz.kprogresshud.KProgressHUD

class TermsAndConditionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsAndConditionsBinding
    var kProgressHUD: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsAndConditionsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        kProgressHUD = AppUtils.getKProgressHUD(this@TermsAndConditionsActivity)
        kProgressHUD?.show()
        binding.rlBack.setOnClickListener {
            finish()
        }
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.webChromeClient = WebChromeClient()

        binding.webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                kProgressHUD?.dismiss()
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                super.onReceivedSslError(view, handler, error)
                kProgressHUD?.dismiss()
            }
        }
        binding.webView.loadUrl(Constants.IMAGE_BASE_URL + Constants.TERMS)
    }
}