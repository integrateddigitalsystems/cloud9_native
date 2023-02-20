package com.ids.cloud9.controller.activities

import android.Manifest
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ids.cloud9.controller.adapters.AdapterDialog
import com.ids.cloud9.controller.adapters.AdapterMedia
import com.ids.cloud9.controller.adapters.AdapterProducts
import com.ids.cloud9.controller.adapters.AdapterReccomendations
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.custom.AppCompactBase
import com.ids.cloud9.databinding.ActivityVisitDetailsBinding
import com.ids.cloud9.databinding.ReasonDialogBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class ActivtyVisitDetails :AppCompactBase(), RVOnItemClickListener {

    var binding: ActivityVisitDetailsBinding? = null
    lateinit var mPermissionResult: ActivityResultLauncher<Intent>
    var CODE_CAMERA = 1
    var CODE_VIDEO = 2
    var CODE_GALLERY = 3
    var code: Int? = -1
    val BLOCKED = -1
    var currLayout: LinearLayout? = null
    var currLayPost = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
        setUp()
        listeners()
    }
    fun init() {
        binding!!.llTool.ivDrawer.hide()
        binding!!.llTool.layoutFragment.show()
        AppHelper.setTextColor(this, binding!!.tvVisit, R.color.medium_blue)
        binding!!.llBorderVisit.hide()
        binding!!.llSelectedVisitBorder.show()
        binding!!.llTool.tvTitleTool.text = MyApplication.selectedVisit!!.title
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CODE_CAMERA -> {
                var permissioned = false
                for (item in grantResults) {
                    permissioned = item == PackageManager.PERMISSION_GRANTED
                }
                if (permissioned) {
                    MyApplication.permission = 0
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        if (MyApplication.permission!! >= 2) {
                            for (item in permissions) {
                                var x = checkSelfPermission(item)
                                if (checkSelfPermission(item) == BLOCKED) {
                                    mPermissionResult.launch(
                                        Intent(android.provider.Settings.ACTION_SETTINGS)
                                    )
                                    toast(
                                        AppHelper.getRemoteString(
                                            "grant_settings_permission",
                                            this
                                        )
                                    )
                                    break
                                }
                            }
                        } else {
                            MyApplication.permission = MyApplication.permission!! + 1
                        }
                    }
                }
            }

        }
    }
    fun setUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), CODE_GALLERY
            )
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), CODE_GALLERY
            )
        }
    }
    fun restartLayouts() {
        AppHelper.setTextColor(this, binding!!.tvCompany, R.color.text_gray)
        AppHelper.setTextColor(this, binding!!.tvVisit, R.color.text_gray)
        AppHelper.setTextColor(this, binding!!.tvMedia, R.color.text_gray)
        AppHelper.setTextColor(this, binding!!.tvReccom, R.color.text_gray)
        AppHelper.setTextColor(this, binding!!.tvProducts, R.color.text_gray)
        AppHelper.setTextColor(this, binding!!.tvSignature, R.color.text_gray)
        binding!!.llBorderCompany.show()
        binding!!.llSelectedCompanyBorder.hide()
        binding!!.llBorderVisit.show()
        binding!!.llSelectedVisitBorder.hide()
        binding!!.llMediaBorder.show()
        binding!!.llSelectedMediaBorder.hide()
        binding!!.llBorderRecc.show()
        binding!!.llSelectedReccBorder.hide()
        binding!!.llProductsBorder.show()
        binding!!.llSelectedProductsBorder.hide()
        binding!!.llBorderSignature.show()
        binding!!.llSelectedSignatureBorder.hide()
    }
    fun listeners() {
        binding!!.llCompany.setOnClickListener {
            if (currLayPost != 1) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvCompany, R.color.medium_blue)
                binding!!.llSelectedCompanyBorder.show()
                Navigation.findNavController(binding!!.fragmentContainerView)
                    .navigate(R.id.fragmentCompany)
                val navBuilder = NavOptions.Builder()
                if (currLayPost > 1) {
                    navBuilder.setEnterAnim(R.anim.right_in).setExitAnim(R.anim.right_out)
                        .setPopEnterAnim(R.anim.right_in).setPopExitAnim(R.anim.right_out)
                } else {
                    navBuilder.setEnterAnim(R.anim.left_in).setExitAnim(R.anim.left_out)
                        .setPopEnterAnim(R.anim.left_in).setPopExitAnim(R.anim.left_out)
                }
                val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
                navController.navigate(R.id.fragmentCompany, null, navBuilder.build())
            }
            currLayPost = 1
        }
        binding!!.llVisit.setOnClickListener {
            if (currLayPost != 0) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvVisit, R.color.medium_blue)
                binding!!.llBorderVisit.hide()
                binding!!.llSelectedVisitBorder.show()
                Navigation.findNavController(binding!!.fragmentContainerView)
                    .navigate(R.id.fragmentVisitDetails)
                val navBuilder = NavOptions.Builder()
                navBuilder.setEnterAnim(R.anim.right_in).setExitAnim(R.anim.right_out)
                    .setPopEnterAnim(R.anim.right_in).setPopExitAnim(R.anim.right_out)
                val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
                navController.navigate(R.id.fragmentVisitDetails, null, navBuilder.build())
                currLayPost = 0
            }
        }
        binding!!.llTool.ivCalendar.setOnClickListener {
            finishAffinity()
            startActivity(
                Intent(
                    this,
                    ActivityMain::class.java
                )
            )
        }
        binding!!.llMedia.setOnClickListener {
            if (currLayPost != 5) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvMedia, R.color.medium_blue)
                binding!!.llMediaBorder.hide()
                binding!!.llSelectedMediaBorder.show()
                Navigation.findNavController(binding!!.fragmentContainerView)
                    .navigate(R.id.fragmentMedia)
                val navBuilder = NavOptions.Builder()
                if (currLayPost > 5) {
                    navBuilder.setEnterAnim(R.anim.right_in).setExitAnim(R.anim.right_out)
                        .setPopEnterAnim(R.anim.right_in).setPopExitAnim(R.anim.right_out)
                } else {
                    navBuilder.setEnterAnim(R.anim.left_in).setExitAnim(R.anim.left_out)
                        .setPopEnterAnim(R.anim.left_in).setPopExitAnim(R.anim.left_out)
                }
                val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
                navController.navigate(R.id.fragmentMedia, null, navBuilder.build())
                currLayPost = 5
            }
        }
        binding!!.llProducts.setOnClickListener {
            if (currLayPost != 2) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvProducts, R.color.medium_blue)
                binding!!.llProductsBorder.hide()
                binding!!.llSelectedProductsBorder.show()
                Navigation.findNavController(binding!!.fragmentContainerView)
                    .navigate(R.id.fragmentProducts)
                val navBuilder = NavOptions.Builder()
                if (currLayPost > 2) {
                    navBuilder.setEnterAnim(R.anim.right_in).setExitAnim(R.anim.right_out)
                        .setPopEnterAnim(R.anim.right_in).setPopExitAnim(R.anim.right_out)
                } else {
                    navBuilder.setEnterAnim(R.anim.left_in).setExitAnim(R.anim.left_out)
                        .setPopEnterAnim(R.anim.left_in).setPopExitAnim(R.anim.left_out)
                }
                val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
                navController.navigate(R.id.fragmentProducts, null, navBuilder.build())
                currLayPost = 2
            }
        }
        binding!!.llRecommendations.setOnClickListener {
            if (currLayPost != 3) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvReccom, R.color.medium_blue)
                binding!!.llBorderRecc.hide()
                binding!!.llSelectedReccBorder.show()
                Navigation.findNavController(binding!!.fragmentContainerView)
                    .navigate(R.id.fragmentReccomendations)
                val navBuilder = NavOptions.Builder()
                if (currLayPost > 3) {
                    navBuilder.setEnterAnim(R.anim.right_in).setExitAnim(R.anim.right_out)
                        .setPopEnterAnim(R.anim.right_in).setPopExitAnim(R.anim.right_out)
                } else {
                    navBuilder.setEnterAnim(R.anim.left_in).setExitAnim(R.anim.left_out)
                        .setPopEnterAnim(R.anim.left_in).setPopExitAnim(R.anim.left_out)
                }
                val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
                navController.navigate(R.id.fragmentReccomendations, null, navBuilder.build())
                currLayPost = 3
            }
        }
        binding!!.llSignature.setOnClickListener {
            if (currLayPost != 4) {
                restartLayouts()
                AppHelper.setTextColor(this, binding!!.tvSignature, R.color.medium_blue)
                binding!!.llBorderSignature.hide()
                binding!!.llSelectedSignatureBorder.show()
                Navigation.findNavController(binding!!.fragmentContainerView)
                    .navigate(R.id.fragmentProducts)
                val navBuilder = NavOptions.Builder()
                if (currLayPost < 4) {
                    navBuilder.setEnterAnim(R.anim.left_in).setExitAnim(R.anim.left_out)
                        .setPopEnterAnim(R.anim.left_in).setPopExitAnim(R.anim.left_out)
                } else {
                    navBuilder.setEnterAnim(R.anim.right_in).setExitAnim(R.anim.right_out)
                        .setPopEnterAnim(R.anim.right_in).setPopExitAnim(R.anim.right_out)
                }
                val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
                navController.navigate(R.id.fragmentSignature, null, navBuilder.build())
                currLayPost = 4
            }
        }
        binding!!.llTool.btBack.setOnClickListener {
            finish()
        }
    }
    override fun onItemClicked(view: View, position: Int) {
    }
}