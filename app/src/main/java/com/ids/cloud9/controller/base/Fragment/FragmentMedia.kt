package com.ids.cloud9.controller.Fragment

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.exoplayer2.Player
import com.ids.cloud9.R
import com.ids.cloud9.controller.MyApplication
import com.ids.cloud9.controller.activities.ActivityFullScreen
import com.ids.cloud9.controller.adapters.AdapterMedia
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.LayoutMediaBinding
import com.ids.cloud9.model.*
import com.ids.cloud9.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


class FragmentMedia : Fragment(), RVOnItemClickListener, Player.Listener {
    var binding: LayoutMediaBinding? = null
    lateinit var mPermissionResult: ActivityResultLauncher<Intent>
    var arrayMedia: ArrayList<ItemSpinner> = arrayListOf()
    var adapterMedia: AdapterMedia? = null
    val BLOCKED = -1
    var sigList: ArrayList<SignatureListItem> = arrayListOf()
    var resultLauncher: ActivityResultLauncher<Intent>? = null
    var mPermissionResult2 =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { result ->

            var permission = false
            for (item in result) {
                permission = item.value
            }
            if (permission) {
                when(code){
                    CODE_CAMERA -> {
                        binding!!.llCamera.callOnClick()
                    }

                    CODE_GALLERY -> {
                        binding!!.llGallery.callOnClick()
                    }

                    CODE_VIDEO -> {
                        binding!!.llVideo.callOnClick()
                    }
                }

                Log.e(ContentValues.TAG, "onActivityResult: PERMISSION GRANTED")
                MyApplication.permissionAllow11 = 0
            } else {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    if (MyApplication.permissionAllow11!! >= 2) {
                        for (item in result) {
                            if (ContextCompat.checkSelfPermission(requireContext(), item.key) == BLOCKED) {
                               /* mPermissionResult.launch(
                                    Intent(android.provider.Settings.ACTION_SETTINGS)
                                )*/



                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.grant_settings_permission),
                                    Toast.LENGTH_LONG
                                ).show()
                                break
                            }
                        }
                    } else {
                        MyApplication.permissionAllow11 = MyApplication.permissionAllow11!! + 1
                    }
                }
            }
        }
    var CODE_CAMERA = 1
    var CODE_VIDEO = 2
    var CODE_GALLERY = 3
    var code: Int? = -1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LayoutMediaBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpContent()
        listeners()
        init()
    }

    fun setUp(array : Array<String>) {
        mPermissionResult2!!.launch(
            arrayOf(
                Manifest.permission.CAMERA

            )
        )
    }

    fun init() {
        binding!!.rvMedia.layoutManager =
            GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false)
        adapterMedia = AdapterMedia(arrayMedia, requireActivity(), this)
        binding!!.rvMedia.adapter = adapterMedia
        getSignatures()
    }

    fun listeners() {
        binding!!.llCamera.setOnClickListener {
            code = CODE_CAMERA
            val arrayPermissions: Array<String>
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayPermissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }
            else{
                arrayPermissions =  arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
            if(AppHelper.hasPermission(requireContext(), arrayPermissions)) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    ImagePicker.with(this)
                        .crop()
                        .compress(1024)
                        .cameraOnly()
                        .maxResultSize(1080, 1080)
                        .createIntent {
                            mPermissionResult.launch(it)
                        }
                } else {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    mPermissionResult.launch(cameraIntent)
                }
            }else{
                setUp(arrayPermissions)
            }
        }
        binding!!.llVideo.setOnClickListener {
            code = CODE_VIDEO
            val arrayPermissions: Array<String>
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayPermissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            }
            else{
                arrayPermissions =  arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
            if(AppHelper.hasPermission(requireContext(), arrayPermissions)) {
                val cameraIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                mPermissionResult.launch(cameraIntent)
            }else{
                setUp(arrayPermissions)
            }
        }

        binding!!.llGallery.setOnClickListener {
            val arrayPermissions: Array<String>
            code = CODE_GALLERY
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayPermissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }
            else{
                arrayPermissions =  arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }

            if(AppHelper.hasPermission(requireContext(), arrayPermissions)) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/* video/*"
                // resultcode = CODE_IMAGE
                mPermissionResult.launch(intent)
            }else{
                setUp(arrayPermissions)
            }
        }
        if (MyApplication.selectedVisit!!.reasonId ==  AppHelper.getReasonID(AppConstants.PENDING_REASON) || MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_COMPLETED) || MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.ON_THE_WAY_REASON) || MyApplication.selectedVisit!!.reasonId == AppHelper.getReasonID(AppConstants.REASON_SCHEDULED)) {
            binding!!.llMediaButtons.hide()
        }
    }

    @Throws(IOException::class)
    fun saveBitmap(
        context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {

        val values = ContentValues().apply {
            put(MediaColumns.DISPLAY_NAME, displayName)
            put(MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            }
        }

        val resolver = context.contentResolver
        var uri: Uri? = null

        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Failed to create new MediaStore record.")

            resolver.openOutputStream(uri)?.use {
                if (!bitmap.compress(format, 95, it))
                    throw IOException("Failed to save bitmap.")
            } ?: throw IOException("Failed to open output stream.")

            return uri

        } catch (e: IOException) {

            uri?.let { orphanUri ->
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(orphanUri, null, null)
            }

            throw e
        }
    }

    /*fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        val path =
            MediaStore.MEDIA_CO
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }*/
    fun getRealPathFromURI(uri: Uri?): String {
        var path = ""
        if (requireActivity().getContentResolver() != null) {
            val cursor: Cursor =
                requireActivity().getContentResolver().query(uri!!, null, null, null, null)!!
            safeCall {
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
                wtf(ex.message!!)
                ex.printStackTrace()
            }
            return destinationFilename
        } catch (ex: java.lang.Exception) {
            wtf("error")
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
            wtf(ex.message!!)
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

            var file: File
            safeCall {
                if (code == CODE_CAMERA) {
                    safeCall {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                            val path: Bitmap =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    it.data!!.extras!!.getParcelable("data", Bitmap::class.java)!!
                                } else {
                                    it.data!!.extras!!.get("data") as Bitmap
                                }
                            val ur = saveBitmap(
                                requireActivity(),
                                path,
                                Bitmap.CompressFormat.JPEG,
                                "image/jpeg",
                                "title"
                            )
                            val paths = getRealPathFromURI(ur)
                            file = File(paths)
                        } else {
                            file = File(it.data!!.data!!.path!!)
                        }
                        val urlImage = file.toString()
                        saveMedia(file)
                        arrayMedia.add(ItemSpinner(0, urlImage, false, false, true, 1))
                        adapterMedia!!.notifyItemChanged(arrayMedia.size - 1)
                    }
                } else if (code == CODE_GALLERY) {
                    file = getFile(requireActivity(), it.data!!.data!!)
                    val type: Int
                    if (AppHelper.isImageFile(file.path))
                        type = 1
                    else
                        type = 0
                    saveMedia(file)
                    arrayMedia.add(
                        ItemSpinner(
                            0,
                            file.absolutePath,
                            false,
                            false,
                            true,
                            type
                        )
                    )
                    adapterMedia!!.notifyItemChanged(arrayMedia.size - 1)
                } else {
                    try {
                        val videoUri = it.data!!.data
                        val urlImage = getRealPathFromURI(videoUri)
                        file = File(urlImage)
                        if (file.exists()) {
                            saveMedia(file)
                        }
                        arrayMedia.add(ItemSpinner(0, urlImage, false, false, true, 0))
                        adapterMedia!!.notifyItemChanged(arrayMedia.size - 1)
                    }catch(ex:Exception){
                        wtf(ex.toString())
                    }
                }
            }
        }
    }

    /*private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }*/

    fun byteArrayFromFile(file: File):ByteArray{

        val b = ByteArray(file.length().toInt())
        try {
            val fileInputStream = FileInputStream(file)
            fileInputStream.read(b)
            return b
        } catch (e: FileNotFoundException) {
            println("File Not Found.")
            return ByteArray(0)
        } catch (e1: IOException) {
            println("Error Reading The File.")
            return ByteArray(0)
        }
    }
    fun saveMedia(file: File) {
        binding!!.llLoading.show()
        var videodata = Base64.encodeToString(byteArrayFromFile(file), Base64.DEFAULT);
        val sigReq = SignatureRequest(
            MyApplication.selectedVisit!!.id!!,
            file.name,
            "data:video/mp4;base64," + videodata,
            0,
            false,
            MyApplication.userItem!!.applicationUserId!!,
            null,
            null
        )
        val arr: ArrayList<SignatureRequest> = arrayListOf()
        arr.add(sigReq)
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java)
            .saveAttachment(arr)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    binding!!.llLoading.hide()
                    try {
                        requireContext().createRetryDialog(
                            response.body()!!.message!!
                        ) {
                            getSignatures()
                        }
                    } catch (ex: Exception) {
                        binding!!.llLoading.hide()
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    binding!!.llLoading.hide()
                }
            })
    }

    fun setUpMedia(array: ArrayList<SignatureListItem>) {
        if (array.size > 0) {
            binding!!.rvMedia.show()
            binding!!.tvNoMedia.hide()
            arrayMedia.clear()
            for (item in array)
                arrayMedia.add(
                    ItemSpinner(
                        item.id,
                        MyApplication.BASE_URL_IMAGE + item.directory,
                        false,
                        false,
                        false,
                        if (AppHelper.isImageFile(item.directory)) 1 else 0,
                        null
                    )
                )
            adapterMedia!!.notifyItemChanged(arrayMedia.size - 1)
            binding!!.llLoading.hide()
        } else {
            binding!!.rvMedia.hide()
            binding!!.tvNoMedia.show()
            binding!!.llLoading.hide()
        }
    }

    fun getSignatures() {
        binding!!.llLoading.show()
        RetrofitClientSpecificAuth.client!!.create(RetrofitInterface::class.java).getSignatures(
            AppConstants.ENTITY_TYPE_CODE_SIGNATURE,
            MyApplication.selectedVisit!!.id!!
        ).enqueue(object : Callback<SignatureList> {
            override fun onResponse(call: Call<SignatureList>, response: Response<SignatureList>) {
                sigList.clear()
                sigList.addAll(response.body()!!.filter {
                    !it.isSignature
                })
                setUpMedia(sigList)
            }

            override fun onFailure(call: Call<SignatureList>, t: Throwable) {
                binding!!.llLoading.hide()
            }

        })
    }

    fun deleteMedia(pos: Int) {
        binding!!.llLoading.show()
        RetrofitClientSpecificAuth.client!!.create(
            RetrofitInterface::class.java
        ).deleteMedia(arrayMedia.get(pos).id!!)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    try {
                        createDialog(
                            response.body()!!.message!!
                        )
                        if (response.body()!!.success.equals("true")) {
                            arrayMedia.removeAt(pos)
                            adapterMedia!!.notifyItemChanged(arrayMedia.size - 1)
                            getSignatures()
                        } else {
                            binding!!.llLoading.hide()
                        }
                    } catch (ex: Exception) {
                        binding!!.llLoading.hide()
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    binding!!.llLoading.hide()
                }
            })
    }

    override fun onItemClicked(view: View, position: Int) {
        if (view.id == R.id.btClose) {
            if (MyApplication.selectedVisit!!.reasonId !=  AppHelper.getReasonID(AppConstants.PENDING_REASON) && MyApplication.selectedVisit!!.reasonId != AppHelper.getReasonID(AppConstants.REASON_COMPLETED) && MyApplication.selectedVisit!!.reasonId != AppHelper.getReasonID(AppConstants.ON_THE_WAY_REASON)) {
                requireContext().createActionDialog(
                    getString(R.string.sure_delete_media),
                    0
                ) {
                    deleteMedia(position)
                }
            }
        } else {
            MyApplication.arrayVid.clear()
            MyApplication.arrayVid.addAll(arrayMedia)
            startActivity(
                Intent(
                    requireActivity(),
                    ActivityFullScreen::class.java
                ).putExtra(
                    "ID_MEDIA",
                    position
                )
            )
        }
    }
}