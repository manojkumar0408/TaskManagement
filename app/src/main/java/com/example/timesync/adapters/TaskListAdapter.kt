package com.example.timesync.adapters

import android.annotation.SuppressLint
import android.util.Log
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
import java.util.Calendar

class TaskListAdapter(private val onDeleteClickListener: (Task) -> Unit) :
    ListAdapter<Task, TaskListAdapter.ViewHolder>(TaskDiffCallback()) {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)
        val dueDateTextView: TextView = itemView.findViewById(R.id.textDueDate)
        val deleteImageView: ImageView = itemView.findViewById(R.id.imageDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_list_item, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTask = getItem(position)
        holder.titleTextView.text = currentTask.title
        holder.descriptionTextView.text = currentTask.description
        if (currentTask.dueDate != null) {
            val dateTime: Array<String> = getDate(currentTask.dueDate, "MM/dd/yy HH:mm")
            holder.dueDateTextView.text = "Due Date: ${dateTime[0]} ${dateTime[1]}"
        }
        holder.deleteImageView.setOnClickListener {
            Log.i("clicked", "adapter")
            onDeleteClickListener.invoke(currentTask)
        }
    }

    private class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

    fun getDate(
        milliSeconds: Long,
        dateFormat: String?
    ): Array<String> {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds)
        val date: String = formatter.format(calendar.getTime())
        return date.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
    }
}