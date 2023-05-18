package com.example.projectmanager.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.projectmanager.Constants
import com.example.projectmanager.R
import com.example.projectmanager.adapters.BoardItemsAdapter
import com.example.projectmanager.databinding.ActivityMainBinding
import com.example.projectmanager.databinding.NavHeaderMainBinding
import com.example.projectmanager.firebase.FirestoreClass
import com.example.projectmanager.models.Board
import com.example.projectmanager.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var binding: ActivityMainBinding? = null

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_BOARD_REQUEST_CODE = 12
    }

    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        binding?.navView?.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this, true)

        binding?.includedAppBarMain?.fabCreateBoard?.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }
    }

    fun populateBoardsListToUI(boardsList: ArrayList<Board>) {
        hideProgressDialog()
        val rvBoardList = binding?.includedAppBarMain?.includedContentMain?.rvBoardsList
        val tvNoBoards = binding?.includedAppBarMain?.includedContentMain?.tvNoBoardsAvailable

        if (boardsList.size > 0) {
            rvBoardList?.visibility = View.VISIBLE
            tvNoBoards?.visibility = View.GONE

            rvBoardList?.layoutManager = LinearLayoutManager(this)
            rvBoardList?.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this, boardsList)
            rvBoardList?.adapter = adapter

            adapter.setOnCLickListener(object: BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, board: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, board.documentId)
                    startActivity(intent)
                }
            })
        } else {
            rvBoardList?.visibility = View.GONE
            tvNoBoards?.visibility = View.VISIBLE
        }
    }

    private fun setupActionBar() {
        binding?.apply {
            setSupportActionBar(includedAppBarMain.toolbarMainActivity)
            includedAppBarMain.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
            includedAppBarMain.toolbarMainActivity.title = getString(R.string.app_name)
            includedAppBarMain.toolbarMainActivity.setNavigationOnClickListener {
                toggleDrawer()
            }
        }
    }

    private fun toggleDrawer() {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            binding?.drawerLayout!!.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
             doubleBackToExit()
        }
    }

    fun updateNavigationUserDetails (user: User, readBoardsList: Boolean) {
        val viewHeader = binding?.navView?.getHeaderView(0)
        val navHeaderBinding: NavHeaderMainBinding = NavHeaderMainBinding.bind(viewHeader!!)

        mUserName = user.name

        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navHeaderBinding.navUserImage)

        navHeaderBinding.tvUsername.text = user.name

        if (readBoardsList) {
            showProgressDialog(getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //TODO Change deprecation method
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FirestoreClass().loadUserData(this)
        } else if (resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE) {
            FirestoreClass().getBoardsList(this)
        } else {
            Log.e("onActivityResult() canceled", "Canceled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this, ProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}