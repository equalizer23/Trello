package com.example.trello.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.constants.Constants
import com.example.trello.databinding.ActivityCreateBordBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.Board
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException

class CreateBordActivity : BaseActivity(){
    private var binding: ActivityCreateBordBinding? = null
    private var mSelectedImageUri: Uri? = null
    private lateinit var mUserName: String
    private var mBoardImageURL: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBordBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
        onClickAddImage()

        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        buttonCreateBoard()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarCreateBordActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Create Board"
        }

        binding?.toolbarCreateBordActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun buttonCreateBoard(){
        binding?.btnCreateBoard?.setOnClickListener {
            if(mSelectedImageUri != null){
                uploadUserImage()
            }
            else{
                showProgressDialog()
                createBoard()
            }
        }
    }

    private fun createBoard(){
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserId())

        val board = Board(
            binding?.etName?.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        FireStoreClass().createBoard(this, board)
    }

    private fun uploadUserImage(){
        showProgressDialog()

        if(mSelectedImageUri != null){
            val sRef: StorageReference =
                FirebaseStorage.getInstance()
                    .reference.child("BOARD_IMAGE" + System.currentTimeMillis()
                            + "." + getFileExtension(mSelectedImageUri))

            sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
                Log.i(
                    "Firebase Image URl",
                    it.metadata!!.reference!!.downloadUrl.toString()
                )
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.e("Download Image Uri", uri.toString())
                    mBoardImageURL = uri.toString()
                    createBoard()
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

    private fun getFileExtension(uri: Uri?) : String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun boardCreatedSuccessfully(){
        hideProgressBar()
        Toast.makeText(
            this,
            "Board Created Successfully",
            Toast.LENGTH_LONG).show()

        setResult(Activity.RESULT_OK)
        finish()
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

                    startActivityForResult(galleryIntent, Constants.GALLERY)
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
        if (requestCode == Constants.GALLERY) {
            if (data != null) {
                mSelectedImageUri = data.data
                try {
                    Glide
                        .with(this)
                        .load(Uri.parse(mSelectedImageUri.toString()))
                        .centerCrop()
                        .placeholder(R.drawable.ic_board_place_holder)
                        .into(binding?.civBoardImage!!)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onClickAddImage() {
        binding?.civBoardImage?.setOnClickListener {
            choosePhotoFromGallery()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}