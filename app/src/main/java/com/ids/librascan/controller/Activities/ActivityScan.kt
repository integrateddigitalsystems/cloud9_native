package com.ids.librascan.controller.Activities

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.zxing.Result
import com.ids.librascan.R
import com.ids.librascan.model.Url
import com.ids.librascan.model.UrlDatabase
import kotlinx.android.synthetic.main.pop_dialog.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import utils.toast

class ActivityScan : AppCompatActivity(), ZXingScannerView.ResultHandler {
  //  private lateinit var urlDao: UrlDao
    private var scannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)

        setPermission()
        init()
    }


    fun init() {
       // val db = Room.databaseBuilder(this,UrlDatabase::class.java, "urlScan_table").build()
       // urlDao = db.urlDao()
    }


    override fun handleResult(title: Result?) {

      //  UrlDatabase(this).getUrlDao()
        showDialog(title.toString())
       // intent.putExtra("Data", p0.toString())
        toast(title.toString())

    }

    private fun showDialog(title: String?){
        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.pop_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle(getString(R.string.save_data))
        val mAlertDialog = mBuilder.show()
        mDialogView.tVTitle.text = title.toString()
        mDialogView.btSave.setOnClickListener {
            val url = Url(title.toString())
            saveUrl(url)
            startActivity(Intent(this, ActivityScan::class.java))
            mAlertDialog.dismiss()

        }
        mDialogView.btCancel.setOnClickListener {
            startActivity(Intent(this, ActivityScan::class.java))
            mAlertDialog.dismiss()

        }
    }

    private fun saveUrl(url: Url){
        class SaveUrl : AsyncTask<Void, Void, Void>() {
            @Deprecated("Deprecated in Java")
            override fun doInBackground(vararg params: Void?): Void? {
                UrlDatabase(this@ActivityScan).getUrlDao().addUrl(url)
                return null
            }

            @Deprecated("Deprecated in Java")
            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                toast("Url Saved")

            }

        }
        SaveUrl().execute()
    }
    override fun onResume() {
        super.onResume()

        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun onStop() {
        super.onStop()
        scannerView?.stopCamera()
        onBackPressed()
    }

    private fun setPermission() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    toast(getString(R.string.permission_message))
                }
            }
        }

    }

}

