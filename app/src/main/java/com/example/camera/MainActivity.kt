package com.example.camera

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.camera.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var bitmapInfo: Bitmap? = null
    private var text: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener {
            //openCamera.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                it.resolveActivity(packageManager).also { component ->
                    createPhotoFile()
                    val photoUri: Uri = FileProvider.getUriForFile(this, "com.example.camera.fileprovider", file)
                    it.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                }
            }
            openCamera.launch(intent)
        }

        binding.btnToString.setOnClickListener {
            //text = convertBitmapToString(bitmapInfo)
            text = convertBitmapToString(getBitmap())
            Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
        }
    }

    private val openCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                //val data = result.data!!
                //val bitmap = data.extras!!.get("data") as Bitmap
                val bitmap = BitmapFactory.decodeFile(file.toString())

                binding.imgView.setImageBitmap(bitmap)
            }
        }

    private lateinit var file: File
    private fun createPhotoFile() {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        file = File.createTempFile("IMG_${System.currentTimeMillis()}_", ".jpg", dir)
    }

    private fun getBitmap(): Bitmap {
        return BitmapFactory.decodeFile(file.toString())
    }

    private fun convertBitmapToString(bitmap: Bitmap?): String? {
        val outputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /*private suspend fun convertBitmapToString(bitmap: Bitmap?): String? {
         return withContext(Dispatchers.Default) {
             val outputStream = ByteArrayOutputStream()
             bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
             val byteArray = outputStream.toByteArray()

             Base64.encodeToString(byteArray, Base64.DEFAULT)
         }
    }*/
}