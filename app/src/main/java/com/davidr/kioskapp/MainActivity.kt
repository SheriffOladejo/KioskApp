package com.davidr.kioskapp

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.DataOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var clickCount = 0
    val REQUEST_ENABLE = 123
    private val SET_PASSWORD = 2

    private lateinit var url: String


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
                    hideNavigationBar()
                }
            }
        }
    }

    private fun initWebview() {
        val ll = findViewById<LinearLayout>(R.id.ll)
        ll.setOnClickListener{
            clickCount++

            if (clickCount == 4) {
                clickCount = 0
                stopLockTask()


            }
        }



        val webView = findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = WebViewClient()

        webView.webChromeClient = WebChromeClient()

        webView.loadUrl(url)

        handler.postDelayed(reloadRunnable, reloadIntervalMillis.toLong())

    }

    private lateinit var dpm: DevicePolicyManager

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val receivedIntent = intent
        url = receivedIntent.getStringExtra("url").toString()


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
            hideNavigationBar()
        }





    }

    private val handler = Handler(Looper.getMainLooper())
    private val reloadIntervalMillis = 10 * 60 * 1000 // 5 minutes in milliseconds


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