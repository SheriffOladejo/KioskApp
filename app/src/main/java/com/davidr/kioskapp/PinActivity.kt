package com.davidr.kioskapp

import android.Manifest
import android.Manifest.permission.MANAGE_DEVICE_POLICY_LOCK_TASK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ozcanalasalvar.otp_view.view.OtpView

class PinActivity : AppCompatActivity() {

    private lateinit var url: String
    private lateinit var url_field: EditText

    private fun checkPermission() {
        // Check if the permission is not granted

        if (ContextCompat.checkSelfPermission(this,
                MANAGE_DEVICE_POLICY_LOCK_TASK
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(MANAGE_DEVICE_POLICY_LOCK_TASK), 123)
        } else {
            Toast.makeText(
                applicationContext,
                "already granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    applicationContext,
                    "permission granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        checkPermission()

        url_field = findViewById(R.id.url)

        val optView = findViewById<OtpView>(R.id.otpView)
        optView.apply {
            setActiveColor(getColor(R.color.white))
            setPassiveColor(getColor(R.color.purple_200))
            setDigits(4)
            setAutoFocusEnabled(false)
            setErrorEnabled(false)
            setTextColor(getColor(R.color.purple_200))
            setTextSize(22)
            setTextChangeListener(object : OtpView.ChangeListener {
                override fun onTextChange(value: String, completed: Boolean) {
                    if (completed && value.equals("4321")) {
                        val activityIntent = Intent(context, MainActivity::class.java)

                        url = url_field.text.toString()
                        activityIntent.putExtra("url", url)
                        context.startActivity(activityIntent)

                        finish()
                    }
                    else if (completed && !value.equals("4321")) {
                        Toast.makeText(context, "Incorrect pin", Toast.LENGTH_LONG).show();
                    }
                }
            })
        }

    }
}