package ro.upt.sma.heart

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings.Secure
import android.support.wearable.activity.WearableActivity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_heart_rate.*
import ro.upt.sma.heart.model.HeartMeasurement
import ro.upt.sma.heart.presenters.monitor.HeartMonitorPresenter
import ro.upt.sma.heart.presenters.monitor.HeartMonitorView
import ro.upt.upt.sma.heart.injection.Injection

class HeartMonitorActivity : WearableActivity() , HeartMonitorView {

    private lateinit var presenter: HeartMonitorPresenter

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate)

        checkPermission()

        val userId = Secure.getString(contentResolver, Secure.ANDROID_ID).substring(0, 4)
        tv_heart_code_value.text = userId

        this.presenter = Injection.provideHeartMonitorPresenter(this, userId)
    }

    override fun onResume() {
        super.onResume()

        presenter.bind(this)
    }

    override fun onPause() {
        super.onPause()

        presenter.unbind()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission.BODY_SENSORS), 112)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 112) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_LONG)
                        .show()
            }
        }
    }

    override fun showLastMeasurement(heartMeasurement: HeartMeasurement) {
        //TODO("Set heart textview value")

        tv_heart_value.text = heartMeasurement.value.toString()
    }

}
