package com.example.projectmanager.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanager.activity.TaskListActivity
import com.example.projectmanager.databinding.ItemTaskBinding
import com.example.projectmanager.models.Board
import com.example.projectmanager.models.Task

class TaskListItemsAdapter (private val context: Context, var taskList: ArrayList<Task>)
    : RecyclerView.Adapter<TaskListItemsAdapter.ViewHolder>()
{

    class ViewHolder(private val itemBinding: ItemTaskBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(context: Context, position: Int, task: Task) {
            itemBinding.tvTaskListTitle.text = task.title
            itemBinding.tvAddTaskList.setOnClickListener {
                itemBinding.tvAddTaskList.visibility = View.GONE
                itemBinding.cvAddTaskListName.visibility = View.VISIBLE
            }
            itemBinding.ibCloseListName.setOnClickListener {
                itemBinding.tvAddTaskList.visibility = View.VISIBLE
                itemBinding.cvAddTaskListName.visibility = View.GONE
            }
            itemBinding.ibDoneListName.setOnClickListener {
                val listName = itemBinding.etTaskListName.text.toString()
                if (listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.createTaskList(listName)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }
            itemBinding.ibEditListName.setOnClickListener {
                itemBinding.etEditTaskListName.setText(task.title)
                itemBinding.llTitleView.visibility = View.GONE
                itemBinding.cvEditTaskListName.visibility = View.VISIBLE
            }
            itemBinding.ibCloseEditableView.setOnClickListener {
                itemBinding.llTitleView.visibility = View.VISIBLE
                itemBinding.cvEditTaskListName.visibility = View.GONE
            }
            itemBinding.ibDoneEditListName.setOnClickListener {
                val listName = itemBinding.etEditTaskListName.text.toString()
                if (listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.updateTaskList(position, listName, task)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }
            itemBinding.ibDeleteList.setOnClickListener {
                alertDialogForDeleteList(context, position, task.title)
            }
        }

        private fun alertDialogForDeleteList(context: Context, position: Int, title: String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Alert")
            builder.setMessage("Are you sure you want to delete $title?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            //performing positive action
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                dialogInterface.dismiss()
                if (context is TaskListActivity) {
                    context.deleteTaskList(position)
                }
            }
            //performing negative action
            builder.setNegativeButton("No") { dialogInterface, which ->
                dialogInterface.dismiss()
            }
            //create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        fun setListInVisible() {
            itemBinding.tvAddTaskList.visibility = View.VISIBLE
            itemBinding.llTaskItem.visibility = View.GONE
        }
        fun setListVisible() {
            itemBinding.tvAddTaskList.visibility = View.GONE
            itemBinding.llTaskItem.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemTaskBinding.inflate(LayoutInflater.from(parent.context))
        val layoutParams = LinearLayout.LayoutParams((parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDp().toPx()), 0, (40.toDp()).toPx(), 0)
        view.root.layoutParams = layoutParams
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]
        if (position == taskList.size - 1) {
            holder.setListInVisible()
        } else {
            holder.setListVisible()
        }
        holder.bind(context, position, task)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}