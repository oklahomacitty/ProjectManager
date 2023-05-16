package com.example.projectmanager.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectmanager.R
import com.example.projectmanager.databinding.ActivityProfileBinding
import com.example.projectmanager.firebase.FirestoreClass
import com.example.projectmanager.model.User
import java.io.IOException

class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var mSelectedImageFileUri: Uri? = null
    private val TAG = ProfileActivity.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        FirestoreClass().loadUserData(this@ProfileActivity)

        binding.ivProfileUserImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                requestPermission()
            }
        }
    }

    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            try {
                Log.d(TAG, "requestPermission: try")
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
                Log.i(TAG, "Uri: $uri")
            }
            catch (e: Exception){
                Log.e(TAG, "requestPermission: ", e)
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        }
        else{
            //Android is below 11(R)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    private val storageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        Log.d(TAG, "storageActivityResultLauncher: ")
        //here we will handle the result of our intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            if (Environment.isExternalStorageManager()){
                //Manage External Storage Permission is granted
                Log.d(TAG, "storageActivityResultLauncher: Manage External Storage Permission is granted")
                showImageChooser()
            }
            else{
                //Manage External Storage Permission is denied....
                Log.d(TAG, "storageActivityResultLauncher: Manage External Storage Permission is denied....")
                showToast("Manage External Storage Permission is denied....")
            }
        }
        else{
            //Android is below 11(R)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null) {
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this@ProfileActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivProfileUserImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            }
        } else {
            showToast("Oops, you just denied the permission for storage")
        }
    }

    private fun showImageChooser() {
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMyProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = getString(R.string.my_profile_title)
        }
        binding.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    fun setUserDataInUI(user: User) {
        Glide
            .with(this@ProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivProfileUserImage)

        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if (user.mobile != 0L) {
            binding.etMobile.setText(user.mobile.toString())
        }
    }

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }
}