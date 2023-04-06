package com.example.trello.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.R
import com.example.trello.databinding.TaskItemBinding
import com.example.trello.models.Task
import com.google.api.Distribution.BucketOptions.Linear

open class TaskListItemsAdapter(
    private val context: Context, private var list: ArrayList<Task>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder{
        val view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )

        view.layoutParams = layoutParams

        layoutParams.setMargins(
            (15.toDp().toPx()), 0, (40.toDp().toPx()), 0)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size - 1){
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.llTaskItem.visibility = View.GONE

            }
            else{
                holder.tvAddTaskList.visibility = View.GONE
                holder.llTaskItem.visibility = View.VISIBLE
            }
        }
    }

    private fun Int.toDp(): Int =
        (this/ Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this* Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvAddTaskList: TextView = view.findViewById(R.id.tv_add_task_list)
        val llTaskItem: LinearLayout = view.findViewById(R.id.ll_task_item)
    }
}