package com.example.projectmanager.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectmanager.Constants
import com.example.projectmanager.R
import com.example.projectmanager.databinding.ActivityCreateBoardBinding
import com.example.projectmanager.firebase.FirestoreClass
import com.example.projectmanager.models.Board
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateBoardBinding

    private val TAG = CreateBoardActivity::class.java.simpleName
    private var mSelectedImageFileUri: Uri? = null

    private lateinit var mUserName: String
    private var mBoardImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        binding.ivBoardImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                requestPermission()
            }
        }

        binding.btnCreate.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadBoardImage()
            } else{
                showProgressDialog(getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    private fun createBoard() {
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserId())

        var board = Board(
            binding.etBoardName.text.toString(),
            mBoardImageUrl,
            mUserName,
            assignedUserArrayList
        )

        FirestoreClass().createBoard(this, board)
    }

    private fun uploadBoardImage() {
        showProgressDialog(getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE" + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(this, mSelectedImageFileUri)
            )
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                Log.i(
                    "Firebase Board Image URL",
                    taskSnapshot.metadata?.reference?.downloadUrl.toString()
                )
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mBoardImageUrl = uri.toString()
                    createBoard()
                }
            }.addOnFailureListener { exception ->
                showToast(exception.message.toString())
            }
        }
    }

    fun boardCreatedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = getString(R.string.create_board_title)
        }
        binding.toolbarCreateBoardActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() }
    }

    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            try {
                Log.d(TAG, "requestPermission: try")
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                mSelectedImageFileUri = Uri.fromParts("package", this.packageName, null)
                intent.data = mSelectedImageFileUri
                storageActivityResultLauncher.launch(intent)
                Log.i(TAG, "Uri: $mSelectedImageFileUri")
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
                Constants.READ_STORAGE_PERMISSION_CODE
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
                Constants.showImageChooser(this)
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
                Constants.READ_STORAGE_PERMISSION_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null) {
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this@CreateBoardActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivBoardImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}