package com.example.projectmanager.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanager.databinding.ItemTaskBinding
import com.example.projectmanager.models.Task

class TaskListItemsAdapter (private val context: Context, var taskList: ArrayList<Task>)
    : RecyclerView.Adapter<TaskListItemsAdapter.ViewHolder>()
{

    class ViewHolder(private val itemBinding: ItemTaskBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
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
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}