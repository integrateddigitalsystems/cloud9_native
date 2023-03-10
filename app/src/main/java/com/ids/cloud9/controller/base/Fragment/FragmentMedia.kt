package com.ids.cloud9.controller.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
    var binding: com.ids.cloud9.databinding.LayoutMediaBinding? = null
    lateinit var mPermissionResult: ActivityResultLauncher<Intent>
    var arrayMedia: ArrayList<ItemSpinner> = arrayListOf()
    var adapterMedia: AdapterMedia? = null
    var ctProd = 0
    var sigList: ArrayList<SignatureListItem> = arrayListOf()
    var CODE_CAMERA = 1
    var CODE_VIDEO = 2
    var CODE_GALLERY = 3
    var code: Int? = -1
    val GRANTED = 0
    val DENIED = 1
    val BLOCKED = -1
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
        }
        binding!!.llVideo.setOnClickListener {
            code = CODE_VIDEO
            val cameraIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            mPermissionResult.launch(cameraIntent)
        }

        binding!!.llGallery.setOnClickListener {
            code = CODE_GALLERY
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/* video/*"
            // resultcode = CODE_IMAGE
            mPermissionResult.launch(intent)
        }
        if (MyApplication.selectedVisit!!.reasonId == AppConstants.PENDING_REASON_ID || MyApplication.selectedVisit!!.reasonId == AppConstants.COMPLETED_REASON_ID || MyApplication.selectedVisit!!.reasonId == AppConstants.ON_THE_WAY_REASON_ID || MyApplication.selectedVisit!!.reasonId == AppConstants.SCHEDULED_REASON_ID) {
            binding!!.llMediaButtons.hide()
        }
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
        if (requireActivity().getContentResolver() != null) {
            val cursor: Cursor =
                requireActivity().getContentResolver().query(uri!!, null, null, null, null)!!
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

            var file: File? = null
            try {
                if (code == CODE_CAMERA) {
                    try {
                        var file: File? = null
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                            val path: Bitmap = it.data!!.extras!!.get("data") as Bitmap
                            val ur = getImageUri(requireActivity(), path)
                            val paths = getRealPathFromURI(ur)
                            file = File(paths)
                        } else {
                            file = File(it.data!!.data!!.path!!)
                        }
                        var urlImage = file.toString()
                        saveMedia(file)
                        arrayMedia.add(ItemSpinner(0, urlImage, false, false, true, 1))
                        adapterMedia!!.notifyDataSetChanged()
                    } catch (e: Exception) {
                    }
                } else if (code == CODE_GALLERY) {
                    file = getFile(requireActivity(), it.data!!.data!!)
                    var type = -1
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
                    adapterMedia!!.notifyDataSetChanged()
                } else if (code == CODE_VIDEO) {
                    try {
                        var videoUri = it.data!!.data
                        var urlImage = getRealPathFromURI(videoUri)
                        var file = File(urlImage)
                        if (file.exists()) {
                            saveMedia(file)
                        }
                        arrayMedia.add(ItemSpinner(0, urlImage, false, false, true, 0))
                        adapterMedia!!.notifyDataSetChanged()
                    } catch (e: Exception) {
                    }
                }
            } catch (ex: Exception) {
            }
        }
    }
    private fun getBitmapFromView(view: View): Bitmap {
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
    }
    fun saveMedia(file: File) {
        binding!!.llLoading.show()
        var base64 = AppHelper.convertImageFileToBase64(file)
        var sigReq = SignatureRequest(
            MyApplication.selectedVisit!!.id!!,
            file.name,
            "data:video/mp4;base64," + base64,
            0,
            false,
            MyApplication.userItem!!.applicationUserId!!,
            null,
            null
        )
        var arr: ArrayList<SignatureRequest> = arrayListOf()
        arr.add(sigReq)
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java)
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
            adapterMedia!!.notifyDataSetChanged()
            binding!!.llLoading.hide()
        } else {
            binding!!.rvMedia.hide()
            binding!!.tvNoMedia.show()
            binding!!.llLoading.hide()
        }
    }
    fun getSignatures() {
        binding!!.llLoading.show()
        RetrofitClientAuth.client!!.create(RetrofitInterface::class.java).getSignatures(
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
        RetrofitClientAuth.client!!.create(
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
                            adapterMedia!!.notifyDataSetChanged()
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
            if (MyApplication.selectedVisit!!.reasonId != AppConstants.PENDING_REASON_ID && MyApplication.selectedVisit!!.reasonId != AppConstants.COMPLETED_REASON_ID && MyApplication.selectedVisit!!.reasonId != AppConstants.ON_THE_WAY_REASON_ID) {
                requireContext().createActionDialog(
                    getString(R.string.sure_delete_media),
                    0
                ){
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