package com.bm.antiquesitems

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bm.antiquesitems.databinding.ActivityItemsDetailBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.jar.Manifest

class ItemsDetail : AppCompatActivity() {

    private lateinit var binding: ActivityItemsDetailBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedbitmap : Bitmap? = null
    private lateinit var database : SQLiteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemsDetailBinding.inflate(layoutInflater)
        val view = binding.root
       setContentView(view)

        database = this.openOrCreateDatabase("Artinfos", MODE_PRIVATE,null)

      registerLauncher()

        val intent = getIntent()
        val info = intent.getStringExtra("info")
        if (info.equals("new")){
                binding.Antiquename.setText("")
                binding.authorname.setText("")
                binding.yeartxt.setText("")
                binding.button.visibility = View.VISIBLE
                binding.imageView.setImageResource(R.drawable.selectimage)


        }else
        {
            binding.button.visibility = View.INVISIBLE
            val selectedId = intent.getIntExtra("id",1)

            val cursor = database.rawQuery("SELECT * FROM artinfostable WHERE id = ?", arrayOf(selectedId.toString()) )


            val antiquenameIx = cursor.getColumnIndex("name")
            val authornameIx = cursor.getColumnIndex("authorName")
            val yearIx = cursor.getColumnIndex("YEAR")
            val imageIx = cursor.getColumnIndex("image")
            val idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()){
                binding.Antiquename.setText(cursor.getString(antiquenameIx))
                binding.authorname.setText(cursor.getString(authornameIx))
                binding.yeartxt.setText(cursor.getString(yearIx))

                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageView.setImageBitmap(bitmap)

            }

            cursor.close()

        }

    }


    fun savebtnOnclickmethod(view: View){

        val antiquename = binding.Antiquename.text.toString()
        val authorname = binding.authorname.text.toString()
        val year = binding.yeartxt.text.toString()

        if (selectedbitmap != null ){
           val smallbitmap = zipbitmap(selectedbitmap!!,300)

            val outputstream = ByteArrayOutputStream()
            smallbitmap.compress(Bitmap.CompressFormat.PNG,50,outputstream)
            val byteArray = outputstream.toByteArray()

            try {


               // val mtdatabase = this.openOrCreateDatabase("Artinfos", MODE_PRIVATE,null)

                database.execSQL("CREATE TABLE IF NOT EXISTS artinfostable (id INTEGER PRIMARY KEY , name VARCHAR, authorName VARCHAR, YEAR INT, image BLOB)")

                val sqlString = "INSERT INTO artinfostable (name, authorName, year, image) VALUES (?,?,?,?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, antiquename)
                statement.bindString(2, authorname)
                statement.bindString(3,year)
                statement.bindBlob(4,byteArray)
                statement.execute()
            }catch (e: Exception){


            }

            val intent = Intent (this@ItemsDetail, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }


    }

    private fun zipbitmap (image : Bitmap, maxsize:Int):Bitmap{

        var width = image.width
        var height = image.height

        val bitmapradius : Double = width / height.toDouble()
        if (bitmapradius > 1){
          width = maxsize
            val scaleheight = width / bitmapradius
            height = scaleheight.toInt()

        }else
        {
           height = maxsize
            val scalewidth = height * bitmapradius
            width = scalewidth.toInt()


        }

        return  Bitmap.createScaledBitmap(image,width,height, true)

    }

    fun selectImageOnclickMethod (view: View){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {  }).show()
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
                else{
              permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        }
        else {
          val intenttogallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intenttogallery)

        }

    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                 if (result.resultCode == RESULT_OK){
                     val intentfromResult = result.data
                     if (intentfromResult != null){

                         val imageData = intentfromResult.data

                         if (imageData != null){
                             try {  if (Build.VERSION.SDK_INT >=28){

                                     val source = ImageDecoder.createSource(this@ItemsDetail.contentResolver, imageData)
                                     selectedbitmap = ImageDecoder.decodeBitmap(source)
                                        binding.imageView.setImageBitmap(selectedbitmap)
                                 }
                             else{
                                 selectedbitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageData)
                                 binding.imageView.setImageBitmap(selectedbitmap)
                             }


                             }
                             catch (e : Exception){
                                 e.printStackTrace()
                                 Toast.makeText(this@ItemsDetail,"Bitmap Error !!!",Toast.LENGTH_LONG).show()
                             }
                         }


                     }
                 }
            }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            success -> if (success){
            val intenttogallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
             activityResultLauncher.launch(intenttogallery)
            }
            else {
            Toast.makeText(this@ItemsDetail,"Permission Needed!",Toast.LENGTH_LONG).show()

        }
        }

    }

}