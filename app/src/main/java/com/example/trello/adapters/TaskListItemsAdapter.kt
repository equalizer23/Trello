package com.example.trello.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.R
import com.example.trello.activities.TaskListActivity
import com.example.trello.databinding.TaskItemBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.Task
import com.google.api.Distribution.BucketOptions.Linear

open class TaskListItemsAdapter(
    private val context: Context, private var list: ArrayList<Task>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder{
        val view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(
            (15.toDp().toPx()), 0, (40.toDp().toPx()), 0)

        view.layoutParams = layoutParams
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

            holder.tvTaskListTitle.text = model.title
            holder.tvAddTaskList.setOnClickListener {
                holder.tvAddTaskList.visibility = View.GONE
                holder.cvAddTaskListName.visibility = View.VISIBLE
            }

            holder.ibCloseListName.setOnClickListener {
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.cvAddTaskListName.visibility = View.GONE
            }

            holder.ibDoneListName.setOnClickListener {
                val listName = holder.etTaskListName.text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }
                else{
                    Toast.makeText(
                        context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }

            holder.ibEditListName.setOnClickListener{
                holder.etEditTaskListName.setText(model.title)
                holder.llTitleView.visibility = View.GONE
                holder.cvEditTaskListName.visibility = View.VISIBLE
            }

            holder.ibCloseEditableView.setOnClickListener {
                holder.llTitleView.visibility = View.VISIBLE
                holder.cvEditTaskListName.visibility = View.GONE
            }

            holder.ibEditDoneListName.setOnClickListener {
                val listName: String = holder.etEditTaskListName.text.toString()

                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position, listName, model)
                    }
                }
                else{
                    Toast.makeText(
                        context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            holder.ibDeleteListName.setOnClickListener {
                alertDialogForDeleteList(position, model.title)
            }

            holder.tvAddCard.setOnClickListener {
                holder.cvAddCard.visibility = View.VISIBLE
                holder.tvAddCard.visibility = View.GONE
            }

            holder.ibCloseCardName.setOnClickListener {
                holder.tvAddCard.visibility = View.VISIBLE
                holder.cvAddCard.visibility = View.GONE
            }

            holder.ibDoneCardName.setOnClickListener {
                val cardName = holder.etCardName.text.toString()
                if(cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.addCardToTaskList(position, cardName)
                    }
                }
                else{
                    Toast.makeText(
                        context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }

            holder.rvCardList.layoutManager = LinearLayoutManager(
                context
            )
            holder.rvCardList.setHasFixedSize(true)
            val adapter = CardsListAdapter(context, model.cardsList)
            holder.rvCardList.adapter = adapter

        }
    }

    private fun Int.toDp(): Int =
        (this/ Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this* Resources.getSystem().displayMetrics.density).toInt()

    private fun alertDialogForDeleteList(position: Int, title: String){
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title")
        builder.setIcon(R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes"){ dialogInterface, which ->
            dialogInterface.dismiss()

            if (context is TaskListActivity){
                context.deleteTaskList(position)
            }

        }

        builder.setNegativeButton("No"){ dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvAddTaskList: TextView = view.findViewById(R.id.tv_add_task_list)
        val llTaskItem: LinearLayout = view.findViewById(R.id.ll_task_item)
        val tvTaskListTitle: TextView = view.findViewById(R.id.tv_task_list_title)
        val cvAddTaskListName: CardView = view.findViewById(R.id.cv_add_task_list_name)
        val ibCloseListName: ImageButton = view.findViewById(R.id.ib_close_list_name)
        val ibDoneListName: ImageButton = view.findViewById(R.id.ib_done_list_name)
        val etTaskListName: TextView = view.findViewById(R.id.et_task_list_name)
        val ibEditListName: ImageButton = view.findViewById(R.id.ib_edit_list_name)
        val etEditTaskListName: TextView = view.findViewById(R.id.et_edit_task_list_name)
        val llTitleView: LinearLayout = view.findViewById(R.id.ll_title_view)
        val cvEditTaskListName: CardView = view.findViewById(R.id.cv_edit_task_list_name)
        val ibCloseEditableView: ImageButton = view.findViewById(R.id.ib_edit_close_list_name)
        val ibEditDoneListName: ImageButton = view.findViewById(R.id.ib_edit_done_list_name)
        val ibDeleteListName: ImageButton = view.findViewById(R.id.ib_delete_list_name)
        val tvAddCard: TextView = view.findViewById(R.id.tv_add_card)
        val cvAddCard: CardView = view.findViewById(R.id.cv_add_card)
        val ibCloseCardName: ImageButton = view.findViewById(R.id.ib_close_card_name)
        val ibDoneCardName: ImageButton = view.findViewById(R.id.ib_done_card_name)
        val etCardName: EditText = view.findViewById(R.id.et_card_name)
        val rvCardList: RecyclerView = view.findViewById(R.id.rv_card_list)
    }
}