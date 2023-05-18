package com.example.projectmanager.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanager.Constants
import com.example.projectmanager.R
import com.example.projectmanager.adapters.TaskListItemsAdapter
import com.example.projectmanager.databinding.ActivityTaskListBinding
import com.example.projectmanager.firebase.FirestoreClass
import com.example.projectmanager.models.Board
import com.example.projectmanager.models.Task

class TaskListActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var boardDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, boardDocumentId)
    }

    fun boardDetails(board: Board) {
        hideProgressDialog()
        setupActionBar(board.name)

        val addTaskList = Task(getString(R.string.add_list))
        board.taskList.add(addTaskList)
        binding.rvTaskList.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this, board.taskList)
        binding.rvTaskList.adapter = adapter
    }

    private fun setupActionBar(title: String) {
        setSupportActionBar(binding.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = title
        }
        binding.toolbarTaskListActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

}