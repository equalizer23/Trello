package com.example.trello.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.R
import com.example.trello.activities.TaskListActivity
import com.example.trello.models.Card
import com.example.trello.models.SelectedMembers

class CardsListAdapter(private val context: Context, private val cardsList: ArrayList<Card>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return cardsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = cardsList[position]

        if(holder is ViewHolder){

            if(model.color.isNotEmpty()){
                holder.viewLabelColor.visibility = View.VISIBLE
                holder.viewLabelColor.setBackgroundColor(Color.parseColor(model.color))
            }
            else{
                holder.viewLabelColor.visibility = View.GONE
            }
            holder.tvCardName.text = model.title

            if((context as TaskListActivity).mAssignedMembersList.size > 0){
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

                for(i in context.mAssignedMembersList.indices){
                    for(j in model.assignedTo){
                        if(context.mAssignedMembersList[i].id == j){
                            val selectedMember = SelectedMembers(
                                context.mAssignedMembersList[i].id,
                                context.mAssignedMembersList[i].image
                            )

                            selectedMembersList.add(selectedMember)

                        }
                    }
                }

                if(selectedMembersList.size > 0){
                    if(selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy){
                        holder.rvCardSelectedMembers.visibility = View.GONE
                    }
                    else{
                        holder.rvCardSelectedMembers.visibility = View.VISIBLE

                        holder.rvCardSelectedMembers.layoutManager = GridLayoutManager(context, 4)

                        val adapter = CardMemberListItemsAdapter(context, selectedMembersList, false)

                        holder.rvCardSelectedMembers.adapter = adapter

                    }
                }
                else{
                    holder.rvCardSelectedMembers.visibility = View.GONE
                }
            }



            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvCardName: TextView = view.findViewById(R.id.tv_card_name)
        val viewLabelColor: View = view.findViewById(R.id.view_label_color)
        val rvCardSelectedMembers: RecyclerView = view.findViewById(R.id.rv_card_selected_members_list)
    }
}