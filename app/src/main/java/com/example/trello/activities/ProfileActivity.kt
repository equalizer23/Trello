package com.example.trello.activities

import android.app.Activity
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
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.activities.BaseActivity
import com.example.trello.constants.Constants
import com.example.trello.databinding.ActivityProfileBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
import kotlin.collections.HashMap

class ProfileActivity : BaseActivity() {
    private var binding: ActivityProfileBinding? = null
    private var mSelectedImageUri: Uri? = null
    private var mUserDetails: User? = null
    private  var mProfileImageURL: String = ""
    private var anyChangesMade = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
        onClickAddImage()
        FireStoreClass().loadUserData(this)
        buttonUpdate()
    }

    private fun updateUserProfileData(){
        var userHashMap = HashMap<String, Any>()
        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails?.image){
            userHashMap[Constants.IMAGE] = mProfileImageURL
            anyChangesMade = true

        }

        if(binding?.etName?.text.toString() != mUserDetails?.name){
            userHashMap[Constants.NAME] = binding?.etName?.text.toString()
            anyChangesMade = true
        }

        if(binding?.etMobile?.text.toString() != mUserDetails?.mobile.toString()){
            userHashMap[Constants.MOBILE] = binding?.etMobile?.text.toString().toLong()
            anyChangesMade = true
        }

        if(anyChangesMade){
            FireStoreClass().updateUserProfileData(this, userHashMap)
        }
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

    private fun getFileExtension(uri: Uri?) : String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun profileUpdateSuccess(){
        hideProgressBar()
        Toast.makeText(
            this,
            "Profile Updated Successfully",
            Toast.LENGTH_LONG).show()

        setResult(Activity.RESULT_OK)
        finish()
    }

    fun profileUpdatedFailure(){
        hideProgressBar()
        Toast.makeText(
            this,
            "Error while updating a profile",
            Toast.LENGTH_LONG).show()
        finish()

    }

    private fun uploadUserImage(){
        showProgressDialog()

        if(mSelectedImageUri != null){
            val sRef: StorageReference =
                FirebaseStorage.getInstance()
                    .reference.child("USER_IMAGE" + System.currentTimeMillis()
                            + "." + getFileExtension(mSelectedImageUri))

            sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
                Log.i(
                    "Firebase Image URl",
                    it.metadata!!.reference!!.downloadUrl.toString()
                )
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.e("Download Image Uri", uri.toString())
                    mProfileImageURL = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener{
                exception ->
                Toast.makeText(
                    this,
                    "${exception.message}",
                    Toast.LENGTH_LONG).show()
                hideProgressBar()
            }
        }
    }

    private fun buttonUpdate(){
        binding?.btnUpdate?.setOnClickListener {
            if(mSelectedImageUri != null){
                uploadUserImage()
            }else{
                showProgressDialog()

                updateUserProfileData()
            }
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
                mSelectedImageUri = data.data
                try {
                    Glide
                        .with(this)
                        .load(Uri.parse(mSelectedImageUri.toString()))
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