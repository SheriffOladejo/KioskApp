package com.davidr.kioskapp

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.DataOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {

    private var clickCount = 0
    private var clickCount2 = 0
    private var editVisible = false
    val REQUEST_ENABLE = 123

    private lateinit var url: String
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var redirecturl: String


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_ENABLE -> {
                    //dpm.setMaximumTimeToLock(componentName, 3000L)
                    //dpm.setMaximumFailedPasswordsForWipe(componentName, 5)
//                    dpm.setPasswordQuality(
//                        componentName,
//                        DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED
//                    )
                    //dpm.setCameraDisabled(componentName, false)
//                    val isSufficient: Boolean = dpm.isActivePasswordSufficient()
//                    if (!isSufficient) {
//                        val setPasswordIntent = Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD)
//                        startActivityForResult(setPasswordIntent, SET_PASSWORD)
//                        dpm.setPasswordExpirationTimeout(componentName, 10000L)
//                    }
                    startLockTask()
                    initWebview()
                    //hideNavigationBar()
                }
            }
        }
    }

    private var count = 0

    private fun initWebview() {
        val webView = findViewById<WebView>(R.id.webView)
        url = "https://api.resamania.com/oauth/login/enjoy?client_id=1_bz0m1m1b97e227p52vhkdbrms8uqyhuguqgtkbsfska8o2kor4&redirect_uri=https%3A%2F%2Fapp.resamania.com%2Fenjoy%2F-%2Fmanagement%2Fentries%2Fpublic&response_type=code"
        val ll = findViewById<LinearLayout>(R.id.ll)
        ll.setOnClickListener{
            clickCount++

            if (clickCount == 4) {
                clickCount = 0
                stopLockTask()
            }
        }

        val urlEdit = findViewById<EditText>(R.id.url)

        urlEdit.imeOptions = EditorInfo.IME_ACTION_SEND

        findViewById<EditText>(R.id.url).setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    url = urlEdit.text.toString()
                    webView.loadUrl(url)
                    true
                }
                else -> false
            }
        }

        urlEdit.visibility = View.GONE
        val ll2 = findViewById<LinearLayout>(R.id.ll2)
        ll2.setOnClickListener{
            clickCount2++

            if (clickCount2 == 4) {
                clickCount2 = 0
                editVisible = !editVisible
                if (editVisible) {
                    urlEdit.visibility = View.VISIBLE
                }
                else {
                    urlEdit.visibility = View.GONE
                }

            }
        }

        webView.webViewClient = WebViewClient()

        webView.webChromeClient = WebChromeClient()

        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url_: String) {
                urlEdit.setText(url_)
                if (url_.startsWith("https://api.resamania.com") && count == 0) {
                    val postData = "_username=" + URLEncoder.encode(username, "UTF-8")
                        .toString() + "&_password=" + URLEncoder.encode(password, "UTF-8")
                    webView.postUrl(url_, postData.toByteArray())
                    count++
                    Toast.makeText(this@MainActivity, "Username: ${username}, password: ${password}", Toast.LENGTH_LONG).show()
                }
                else if (url_.startsWith("https://app.resamania.com")) {
                    webView.loadUrl(redirecturl)
                    count = 0
                    Toast.makeText(this@MainActivity, "Username: ${username}, password: ${password}", Toast.LENGTH_LONG).show()
                }

                super.onPageFinished(view, url_)
            }

            override fun onLoadResource(view: WebView, url: String) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url_: String): Boolean {
                urlEdit.setText(url_)
                if (url_.startsWith("https://api.resamania.com") && count == 0) {
                    val postData = "_username=" + URLEncoder.encode(username, "UTF-8")
                        .toString() + "&_password=" + URLEncoder.encode(password, "UTF-8")
                    webView.postUrl(url_, postData.toByteArray())
                    count++
                    Toast.makeText(this@MainActivity, "Username: ${username}, password: ${password}", Toast.LENGTH_LONG).show()
                }
                else if (url_.startsWith("https://app.resamania.com")) {
                    webView.loadUrl(redirecturl)
                    count = 0
                    Toast.makeText(this@MainActivity, "Username: ${username}, password: ${password}", Toast.LENGTH_LONG).show()
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
        })

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        //CookieManager.getInstance().setCookie("https://api.resamania.com", "") // Set an empty cookie string
        CookieSyncManager.getInstance().sync()

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)

        webView.requestFocus();
        webView.getSettings().setLightTouchEnabled(true)
        webView.getSettings().setJavaScriptEnabled(true)
        webView.getSettings().setGeolocationEnabled(true)
        webView.setSoundEffectsEnabled(true)
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.getSettings().setUseWideViewPort(true)
        // enable Web Storage: localStorage, sessionStorage
        webView.getSettings().setDomStorageEnabled(true)

        val cookieValue = "65f16f84c0b923.98413149"
        val cookieDomain = "https://api.resamania.com"
        val cookiePath = "/"
        val cookie = "REMEMBERME=$cookieValue; domain=$cookieDomain; path=$cookiePath"

        // Set the cookie
        cookieManager.setCookie(cookieDomain, cookie)

        val cookies = "REMEMBERME=65f16f84c0b923.98413149"
        CookieManager.getInstance().setCookie(url, cookies)


        webView.loadUrl(url)

        handler.postDelayed(reloadRunnable, reloadIntervalMillis.toLong())

    }

    fun getCookieMap(siteName: String): Map<String,String> {

        val manager = CookieManager.getInstance()
        val map = mutableMapOf<String,String>()

        manager.getCookie(siteName)?.let {cookies ->
            val typedArray = cookies.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (element in typedArray) {
                val split = element.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                if(split.size >= 2) {
                    map[split[0]] = split[1]
                } else if(split.size == 1) {
                    map[split[0]] = ""
                }
            }
        }

        return map
    }

    private fun executeRequest(url: String): WebResourceResponse? {
        try {
            val connection: URLConnection = URL(url).openConnection()
            val cookie: String = connection.getHeaderField("Set-Cookie")
            if (cookie != null) {
                Log.d("Cookie", cookie)
            }
            return null
            //return new WebResourceResponse(connection.getContentType(), connection.getHeaderField("encoding"), connection.getInputStream());
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private lateinit var dpm: DevicePolicyManager

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val receivedIntent = intent
        redirecturl = receivedIntent.getStringExtra("url").toString()
        username = receivedIntent.getStringExtra("username").toString()
        password = receivedIntent.getStringExtra("password").toString()

        initWebview()


        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = ComponentName(this, AdminReceiver::class.java)

//        dpm.setLockTaskPackages(adminComponent, arrayOf(packageName))
//        dpm.setGlobalSetting(adminComponent, Settings.Global.AUTO_TIME, "0")

        if (!dpm.isAdminActive(adminComponent)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            intent.putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Need permission"
            )
            startActivityForResult(intent, REQUEST_ENABLE)
        }
        else {
            startLockTask()
            initWebview()
            //hideNavigationBar()
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val reloadIntervalMillis = 10 * 60 * 1000 // 10 minutes in milliseconds


    private val reloadRunnable = object : Runnable {
        override fun run() {
            val webView = findViewById<WebView>(R.id.webView)
            // Reload the original URL
            webView.loadUrl("https://api.resamania.com/oauth/login/enjoy?client_id=1_bz0m1m1b97e227p52vhkdbrms8uqyhuguqgtkbsfska8o2kor4&redirect_uri=https%3A%2F%2Fapp.resamania.com%2Fenjoy%2F-%2Fmanagement%2Fentries%2Fpublic&response_type=code")

            // Schedule the next reload
            handler.postDelayed(this, reloadIntervalMillis.toLong())
        }
    }

    private fun hideNavigationBar() {
        try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            os.writeBytes("pm disable com.android.systemui\n")
            os.flush()
            try {
                var process: Process? = null
                process = Runtime.getRuntime().exec("su")
                val osReboot = DataOutputStream(process.outputStream)
                osReboot.writeBytes("reboot\n")
                osReboot.flush()
                process.waitFor()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // ...

    override fun onDestroy() {
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacks(reloadRunnable)
        super.onDestroy()
    }
}