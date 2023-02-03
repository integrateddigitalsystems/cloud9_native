package com.ids.cloud9.controller.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.exoplayer2.Player
import com.ids.cloud9.controller.adapters.AdapterMedia
import com.ids.cloud9.controller.adapters.RVOnItemClickListener.RVOnItemClickListener
import com.ids.cloud9.databinding.LayoutMediaBinding

import com.ids.cloud9.model.ItemSpinner
import com.ids.cloud9.utils.AppHelper
import java.io.*
import java.util.ArrayList

class FragmentMedia : Fragment() , RVOnItemClickListener , Player.Listener{

    var binding : com.ids.cloud9.databinding.LayoutMediaBinding?=null
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

    fun init(){
     //   arrayMedia.add(ItemSpinner(0,"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",true , true , true , 0 ))
        binding!!.rvMedia.layoutManager =
            GridLayoutManager(requireContext(), 1, LinearLayoutManager.HORIZONTAL, false)
        adapterMedia = AdapterMedia(arrayMedia,requireActivity(),this )
        binding!!.rvMedia.adapter = adapterMedia
    }


    fun listeners(){
        binding!!.llCamera.setOnClickListener {
            code = CODE_CAMERA
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .cameraOnly()
                    .maxResultSize(1080, 1080)
                    .createIntent {
                        mPermissionResult.launch(it)
                    }
            }else{
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
            val cursor: Cursor = requireActivity().getContentResolver().query(uri!!, null, null, null, null)!!
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

            Toast.makeText(requireActivity(), "TESTWORK", Toast.LENGTH_SHORT).show()
            var file: File? = null
           // Log.wtf("DATA_TAG",it.data!!.extras)
            when (code) {



                CODE_CAMERA -> {
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

                        arrayMedia.add(ItemSpinner(0,urlImage, false, false, true, 1))
                        adapterMedia!!.notifyDataSetChanged()

                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                CODE_GALLERY -> {
                    file = getFile(requireActivity(), it.data!!.data!!)

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
                            val path: Bitmap = it.data!!.extras!!.get("data") as Bitmap
                            val ur = getImageUri(requireActivity(), path)
                            val paths = getRealPathFromURI(ur)
                            file = File(paths)
                        } else {
                            file = File(it.data!!.data!!.path!!)
                        }

                        var urlImage = file.toString()

                        arrayMedia.add(ItemSpinner(0, urlImage, false, false, true, 0))
                        adapterMedia!!.notifyDataSetChanged()

                    } catch (e: Exception) {
                    }
                }

                else -> {

                }
            }

        }
    }

    override fun onItemClicked(view: View, position: Int) {

    }
}