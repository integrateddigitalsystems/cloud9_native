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


class ActivtyVisitDetails : AppCompactBase(), RVOnItemClickListener {

    var binding: ActivityVisitDetailsBinding? = null
    var simp: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    var simpTime: SimpleDateFormat = SimpleDateFormat("HH:mm")
    var arrayProd: ArrayList<ProductsItem> = arrayListOf()
    var arrayReccomend: ArrayList<ActivitiesListItem> = arrayListOf()
    lateinit var mPermissionResult: ActivityResultLauncher<Intent>
    var arrayMedia: ArrayList<ItemSpinner> = arrayListOf()
    var adapterMedia: AdapterMedia? = null
    var ctProd = 0
    var CODE_CAMERA = 1
    var CODE_VIDEO = 2
    var CODE_GALLERY = 3
    var code: Int? = -1
    val GRANTED = 0
    val DENIED = 1
    val BLOCKED = -1
    var simpTimeAA: SimpleDateFormat = SimpleDateFormat("hh:mm aa")
    var simpOrg: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    var currLayout: LinearLayout? = null
    var alertDialog: androidx.appcompat.app.AlertDialog? = null
    var currLayPost = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
        setUp()
        setUpContent()
        listeners()

    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        if (getContentResolver() != null) {
            val cursor: Cursor = getContentResolver().query(uri!!, null, null, null, null)!!
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }


    @Throws(IOException::class)
    fun getFile(context: Activity, uri: Uri): File {
        try {
            val destinationFilename =
                File(context.filesDir.path + File.separatorChar + queryName(context, uri))
            try {
                context.contentResolver.openInputStream(uri).use { ins ->
                    createFileFromStream(
                        ins!!,
                        destinationFilename
                    )
                }
            } catch (ex: java.lang.Exception) {
                Log.e("Save File", ex.message!!)
                ex.printStackTrace()
            }
            return destinationFilename
        } catch (ex: java.lang.Exception) {
            Log.wtf("createFile", "error")
            return File("")
        }
    }

    fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: java.lang.Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    fun setUpContent() {


        mPermissionResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {

            Toast.makeText(this@ActivtyVisitDetails, "TESTWORK", Toast.LENGTH_SHORT).show()
            var file: File? = null
            when (code) {

                CODE_CAMERA -> {
                    try {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                            var path: Bitmap = it.data!!.extras!!.get("data") as Bitmap
                            var ur = getImageUri(this, path)
                            var paths = getRealPathFromURI(ur)
                            file = File(paths)
                        } else {
                            file = File(it.data!!.data!!.path)
                        }


                        arrayMedia.add(ItemSpinner(0, file.absolutePath, false, false, true, 1))
                        adapterMedia!!.notifyDataSetChanged()

                    } catch (e: Exception) {
                        Toast.makeText(this@ActivtyVisitDetails, e.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                CODE_GALLERY -> {
                    file = getFile(this, it.data!!.data!!)

                    var type = -1

                    if (AppHelper.isImageFile(file.path))
                        type = 1
                    else
                        type = 0

                    arrayMedia.add(ItemSpinner(0, file.absolutePath, false, false, true, type))
                    adapterMedia!!.notifyDataSetChanged()
                }

                CODE_VIDEO -> {
                    try {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                            var path: Bitmap = it.data!!.extras!!.get("data") as Bitmap
                            var ur = getImageUri(this, path)
                            var paths = getRealPathFromURI(ur)
                            file = File(paths)
                        } else {
                            file = File(it.data!!.data!!.path)
                        }


                        arrayMedia.add(ItemSpinner(0, file.absolutePath, false, false, true, 1))
                        adapterMedia!!.notifyDataSetChanged()

                    } catch (e: Exception) {
                    }
                }

                else -> {

                }
            }

        }
    }

    fun init() {

        AppHelper.setTextColor(this, binding!!.tvVisit, R.color.medium_blue)
        binding!!.llBorderVisit.hide()
        binding!!.llSelectedVisitBorder.show()
        currLayout = binding!!.visitLayout
        setUpVisitDetails()
        binding!!.tvVisitTitle.text = MyApplication.selectedVisit!!.title


        binding!!.layoutMedia.rvMedia.layoutManager =
            GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false)
        adapterMedia = AdapterMedia(arrayMedia, this, this)
        binding!!.layoutMedia.rvMedia.adapter = adapterMedia
    }

    fun setDataReccomend() {
        binding!!.layoutReccomend.rvReccomendations.layoutManager = LinearLayoutManager(this)
        binding!!.layoutReccomend.rvReccomendations.adapter =
            AdapterReccomendations(arrayReccomend, this, this)
        binding!!.llLoading.hide()
    }

    fun getReccomendations() {
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getReccomendations(
            MyApplication.userItem!!.applicationUserId!!.toInt()
        ).enqueue(object : Callback<ActivitiesList> {
            override fun onResponse(
                call: Call<ActivitiesList>,
                response: Response<ActivitiesList>
            ) {
                arrayReccomend.clear()
                arrayReccomend.addAll(response.body()!!
                    .filter {
                        it.entity.equals(MyApplication.selectedVisit!!.title)
                    })
                setDataReccomend()

            }

            override fun onFailure(call: Call<ActivitiesList>, t: Throwable) {
                binding!!.llLoading.hide()
            }

        })
    }

    fun setUpProductsPage() {
        binding!!.layoutProdcution.rvProducts.layoutManager = LinearLayoutManager(this)
        binding!!.layoutProdcution.rvProducts.adapter = AdapterProducts(arrayProd, this, this)
        binding!!.llLoading.hide()
    }

    fun getReports(position: Int) {

        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getReports(
            arrayProd.get(position).product.categoryId
        )?.enqueue(object : Callback<ArrayList<Report>> {
            override fun onResponse(
                call: Call<ArrayList<Report>>,
                response: Response<ArrayList<Report>>
            ) {
                if (response.body()!!.size > 0) {
                    arrayProd.get(position).reports.clear()
                    arrayProd.get(position).reports.addAll(response.body()!!)

                }

                ctProd++
                if (ctProd == arrayProd.size) {
                    setUpProductsPage()
                }

            }

            override fun onFailure(call: Call<ArrayList<Report>>, throwable: Throwable) {

                binding!!.llLoading.hide()
            }
        })

    }

    fun setUpProducts() {
        if (arrayProd.size > 0) {
            for (i in arrayProd.indices) {
                arrayProd.get(i).reports = arrayListOf()
                getReports(i)
            }
        } else {
            binding!!.llLoading.hide()
        }
    }

    fun getProducts() {
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getProducts(
            MyApplication.selectedVisit!!.id!!
        )?.enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                arrayProd.clear()
                arrayProd.addAll(response.body()!!)
                setUpProducts()


            }

            override fun onFailure(call: Call<Products>, throwable: Throwable) {

                binding!!.llLoading.hide()
            }
        })
    }

    fun setUpVisitDetails() {
        var date = simpOrg.parse(MyApplication.selectedVisit!!.visitDate)
        binding!!.layoutVisit.tvVisitDate.text = simp.format(date)
        binding!!.layoutVisit.fromTime.text =
            simpTime.format(simpOrg.parse(MyApplication.selectedVisit!!.fromTime))
        binding!!.layoutVisit.tvToTime.text =
            simpTime.format(simpOrg.parse(MyApplication.selectedVisit!!.toTime))

        var millFrom =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(MyApplication.selectedVisit!!.fromTime).time
        var millTo =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss").parse(MyApplication.selectedVisit!!.toTime).time

        var diff = millTo - millFrom

        var mins = diff / 60000


        var hours = mins / 60
        var min = mins % 60
        binding!!.layoutVisit.tvDuration.text = if (hours > 0) {
            hours.toString() + " hrs " + (if (min > 0)
                min.toString() + " mins"
            else
                "")
        } else {
            if (min > 0)
                min.toString() + " mins"
            else
                ""
        }


        binding!!.layoutVisit.tvStatusReason.text =
            if (MyApplication.selectedVisit!!.reasonId == AppConstants.ON_THE_WAY_REASON_ID) {
                AppConstants.ON_THE_WAY_REASON
            } else if (MyApplication.selectedVisit!!.reasonId == AppConstants.ARRIVED_REASON_ID) {
                AppConstants.ARRIVED_REASON
            } else if (MyApplication.selectedVisit!!.reasonId == AppConstants.PENDING_REASON_ID) {
                AppConstants.PENDING_REASON
            } else if (MyApplication.selectedVisit!!.reasonId == AppConstants.COMPLETED_REASON_ID) {
                AppConstants.COMPLETED_REASON
            } else {
                AppConstants.SCHEDULED_REASON
            }

        binding!!.layoutVisit.tvCompany.text = MyApplication.selectedVisit!!.company!!.companyName
        if (!MyApplication.selectedVisit!!.actualArrivalTime.isNullOrEmpty())
            binding!!.layoutVisit.tvActualArrivalTime.text =
                simpTimeAA.format(simpOrg.parse(MyApplication.selectedVisit!!.actualArrivalTime!!))
        else
            binding!!.layoutVisit.tvActualArrivalTime.text = ""

        if (!MyApplication.selectedVisit!!.actualCompletedTime.isNullOrEmpty())
            binding!!.layoutVisit.tvActualCompletedTime.text =
                simpTimeAA.format(simpOrg.parse(MyApplication.selectedVisit!!.actualCompletedTime!!))
        else
            binding!!.layoutVisit.tvActualCompletedTime.text = ""

        if (MyApplication.selectedVisit!!.actualDuration != null) {
            var dur = MyApplication.selectedVisit!!.actualDuration!!.toLong()


            var mins = dur / 60000


            var hours = mins / 60
            var min = mins % 60
            binding!!.layoutVisit.tvActualDurtionTime.text = if (hours > 0) {
                hours.toString() + " hrs " + (if (min > 0)
                    min.toString() + " mins"
                else
                    "")
            } else {
                if (min > 0)
                    min.toString() + " mins"
                else
                    ""
            }


        } else
            binding!!.layoutVisit.tvActualDurtionTime.text = ""

        if (!MyApplication.selectedVisit!!.remark.isNullOrEmpty())
            binding!!.layoutVisit.etRemark.text =
                MyApplication.selectedVisit!!.remark!!.toEditable()
        else
            binding!!.layoutVisit.etRemark.text = "".toEditable()


    }


    fun setUpCompanyData() {

        binding!!.layoutCompany.tvCompanyName.setTextLang(
            MyApplication.selectedVisit!!.company!!.companyName,
            MyApplication.selectedVisit!!.company!!.companyNameAr
        )
        binding!!.layoutCompany.tvEmail.setCheckText(MyApplication.selectedVisit!!.company!!.email)
        binding!!.layoutCompany.tvWebite.setCheckText(MyApplication.selectedVisit!!.company!!.website)
        binding!!.layoutCompany.tvPhone.setCheckText(MyApplication.selectedVisit!!.company!!.phoneNumber)
        binding!!.layoutCompany.tvFax.setCheckText(MyApplication.selectedVisit!!.company!!.fax)
        binding!!.layoutCompany.tvContactName.setTextLang(
            MyApplication.selectedVisit!!.contact!!.firstName + " " + MyApplication.selectedVisit!!.contact!!.lastName,
            MyApplication.selectedVisit!!.contact!!.firstNameAr + " " + MyApplication.selectedVisit!!.contact!!.lastNameAr
        )
        binding!!.layoutCompany.tvContactNumber.setCheckText(MyApplication.selectedVisit!!.contact!!.personalPhoneNumber)
        if (MyApplication.selectedVisit!!.companyAddress != null)
            binding!!.layoutCompany.tvAddress.setCheckText(MyApplication.selectedVisit!!.companyAddress!!.address)
        else
            binding!!.layoutCompany.tvAddress.text = ""
    }

    fun setUpDataVisit() {

        var arrSpinner: ArrayList<ItemSpinner> = arrayListOf()

        arrSpinner.add(
            ItemSpinner(
                AppConstants.SCHEDULED_REASON_ID,
                AppConstants.SCHEDULED_REASON,
                false
            )
        )
        arrSpinner.add(
            ItemSpinner(
                AppConstants.COMPLETED_REASON_ID,
                AppConstants.COMPLETED_REASON,
                true
            )
        )
        arrSpinner.add(
            ItemSpinner(
                AppConstants.ARRIVED_REASON_ID,
                AppConstants.ARRIVED_REASON,
                false
            )
        )
        arrSpinner.add(
            ItemSpinner(
                AppConstants.PENDING_REASON_ID,
                AppConstants.PENDING_REASON,
                false
            )
        )
        arrSpinner.add(
            ItemSpinner(
                AppConstants.ON_THE_WAY_REASON_ID,
                AppConstants.ON_THE_WAY_REASON,
                false
            )
        )


        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        var popupItemMultipleBinding = ReasonDialogBinding.inflate(layoutInflater)

        popupItemMultipleBinding.rvReasonStatus.layoutManager = LinearLayoutManager(this)
        popupItemMultipleBinding.rvReasonStatus.adapter =
            AdapterDialog(arrSpinner, this, this, true)

        builder.setView(popupItemMultipleBinding.root)
        alertDialog = builder.create()
        alertDialog!!.setCanceledOnTouchOutside(false)
        safeCall {
            alertDialog!!.show()
            alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

    }

    fun setUpDataVisit(pos: Int) {

        var arrSpinner: ArrayList<ItemSpinner> = arrayListOf()

        for (item in arrayProd.get(pos).reports)
            arrSpinner.add(ItemSpinner(item.id, item.name, false, true))


        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        var popupItemMultipleBinding = ReasonDialogBinding.inflate(layoutInflater)

        popupItemMultipleBinding.rvReasonStatus.layoutManager = LinearLayoutManager(this)
        popupItemMultipleBinding.rvReasonStatus.adapter =
            AdapterDialog(arrSpinner, this, this, true)

        builder.setView(popupItemMultipleBinding.root)
        alertDialog = builder.create()
        alertDialog!!.setCanceledOnTouchOutside(false)
        safeCall {
            alertDialog!!.show()
            alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

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
                                    /*startActivityForResult(
                                      ,
                                        0
                                    );*/
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
        AppHelper.setTextColor(this, binding!!.tvCompany, R.color.gray_border_tab)
        AppHelper.setTextColor(this, binding!!.tvVisit, R.color.gray_border_tab)
        AppHelper.setTextColor(this, binding!!.tvMedia, R.color.gray_border_tab)
        AppHelper.setTextColor(this, binding!!.tvReccom, R.color.gray_border_tab)
        AppHelper.setTextColor(this, binding!!.tvProducts, R.color.gray_border_tab)
        AppHelper.setTextColor(this, binding!!.tvSignature, R.color.gray_border_tab)

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
                binding!!.llBorderVisit.hide()
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

                currLayout = binding!!.companyLayout
                currLayPost = 0
            }
        }

        binding!!.layoutCompany.llGetDirection.setOnClickListener {
            if (MyApplication.selectedVisit!!.companyAddress != null && !MyApplication.selectedVisit!!.companyAddress!!.lat.isNullOrEmpty() && !MyApplication.selectedVisit!!.companyAddress!!.long.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(
                    "geo:0,0?q=" + MyApplication.selectedVisit!!.companyAddress!!.lat + "," + MyApplication.selectedVisit!!.companyAddress!!.long + "(" + MyApplication.selectedVisit!!.companyAddress!!.name + ")"
                )
                startActivity(intent)
            }
        }

        binding!!.layoutReccomend.btAddReccomend.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ActivityAddReccomendations::class.java

                )
            )

            //  overridePendingTransition(R.anim.cycling ,R.anim.cycling)
        }

        binding!!.layoutProdcution.btAddProducts.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ActivityAddProduct::class.java

                )
            )
            //  overridePendingTransition(R.anim.cycling ,R.anim.cycling)
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

                currLayout = binding!!.companyLayout
                currLayPost = 5
            }
        }


        binding!!.layoutSignature.redoClient.setOnClickListener {
            binding!!.layoutSignature.drawClient.redo()
        }

        binding!!.layoutSignature.undoClient.setOnClickListener {
            binding!!.layoutSignature.drawClient.undo()
        }

        binding!!.layoutSignature.redoEmployee.setOnClickListener {
            binding!!.layoutSignature.drawEmployee.redo()
        }

        binding!!.layoutSignature.undoEmployee.setOnClickListener {
            binding!!.layoutSignature.drawEmployee.undo()
        }

        binding!!.layoutSignature.btClearClient.setOnClickListener {
            binding!!.layoutSignature.drawClient.clearCanvas()
        }

        binding!!.layoutSignature.btClearEmployee.setOnClickListener {
            binding!!.layoutSignature.drawEmployee.clearCanvas()
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

                currLayout = binding!!.companyLayout
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

                currLayout = binding!!.companyLayout
                currLayPost = 3
            }
        }
        binding!!.layoutMedia.llCamera.setOnClickListener {
            code = CODE_CAMERA
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            mPermissionResult.launch(cameraIntent)
        }
        binding!!.layoutMedia.llVideo.setOnClickListener {
            code = CODE_VIDEO
            val cameraIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            mPermissionResult.launch(cameraIntent)
        }

        binding!!.layoutMedia.llGallery.setOnClickListener {
            code = CODE_GALLERY
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/* video/*"
            // resultcode = CODE_IMAGE
            mPermissionResult.launch(intent)
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


                currLayout = binding!!.signLayout
                currLayPost = 4
            }
        }

        binding!!.btBack.setOnClickListener {
            finish()
        }

        binding!!.layoutVisit.spStatusReason.setOnClickListener {
            setUpDataVisit()
        }
    }

    override fun onItemClicked(view: View, position: Int) {
        if (view.id == R.id.llJobReport) {
            if (arrayProd.get(position).reports.size > 0)
                setUpDataVisit(position)
        }
    }
}