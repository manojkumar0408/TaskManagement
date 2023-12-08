package com.example.timesync.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.timesync.R
import com.example.timesync.db.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskListAdapter(
    private val onDeleteClickListener: (Task) -> Unit,
    private val onEditClickListener: (Task) -> Unit,
    private val onItemClickListener: (Task) -> Unit
) : ListAdapter<Task, TaskListAdapter.ViewHolder>(TaskDiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)
        val dueDateTextView: TextView = itemView.findViewById(R.id.textDueDate)
        val deleteImageView: ImageView = itemView.findViewById(R.id.imageDelete)
        val editImageView: ImageView = itemView.findViewById(R.id.imageEdit) // Added ImageView for edit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_list_item, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTask = getItem(position)
        holder.titleTextView.text = currentTask.title
        holder.descriptionTextView.text = currentTask.description
        currentTask.dueDate?.let {
            val dateTime: Array<String> = getDate(it, "MM/dd/yy HH:mm")
            holder.dueDateTextView.text = "Due Date: ${dateTime[0]} ${dateTime[1]}"
        }

        // Set the delete click listener
        holder.deleteImageView.setOnClickListener {
            onDeleteClickListener.invoke(currentTask)
        }

        // Set the edit click listener
        holder.editImageView.setOnClickListener {
            onEditClickListener.invoke(currentTask)
        }
        holder.itemView.setOnClickListener {
            onItemClickListener.invoke(currentTask)
        }

    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem

        }
    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): Array<String> {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        val date: String = formatter.format(calendar.time)
        return date.split(" ").toTypedArray()
    }
}
