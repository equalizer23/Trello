package com.example.trello.activities

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.activities.BaseActivity
import com.example.trello.databinding.ActivityProfileBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.User
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class ProfileActivity : BaseActivity() {
    private var binding: ActivityProfileBinding? = null
    private var saveImageToInternalStorage: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
        onClickAddImage()
        FireStoreClass().loadUserData(this)
    }

    fun setUserDataInUI(user: User){
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding?.userImage!!)

        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if (user.mobile != 0L){
            binding?.etMobile?.setText(user.mobile.toString())
        }

    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )

                    startActivityForResult(galleryIntent, GALLERY)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationaleDialogForPermissions()
            }
        }).onSameThread().check()
    }


    private fun showRationaleDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("It look like you have turned off permissions, " +
                "required for this feature").setPositiveButton("GO TO SETTINGS"){ _, _ ->
            try{
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            catch(e: ActivityNotFoundException){
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel"){ dialog, which ->
            dialog.dismiss()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            if (data != null) {
                saveImageToInternalStorage = data.data
                try {
                    Glide
                        .with(this)
                        .load(Uri.parse(saveImageToInternalStorage.toString()))
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(binding?.userImage!!)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun onClickAddImage() {
        binding?.userImage?.setOnClickListener {
            choosePhotoFromGallery()
        }
    }


    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarProfileActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "My Profile"
        }

        binding?.toolbarProfileActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    companion object{
        private const val GALLERY = 1
        private const val CAMERA = 2
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}