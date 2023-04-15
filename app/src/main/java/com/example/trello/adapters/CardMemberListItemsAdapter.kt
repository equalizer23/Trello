package com.example.trello.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.models.SelectedMembers
import de.hdodenhof.circleimageview.CircleImageView

open class CardMemberListItemsAdapter(
    private val context: Context,
    private val list: ArrayList<SelectedMembers>,
    private val assignMembers: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return MembersViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_card_selected, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MembersViewHolder){
            if(position == list.size - 1 && assignMembers){
                holder.ivAddMember.visibility = View.VISIBLE
                holder.ivSelectedMember.visibility = View.GONE
            }
            else{
                holder.ivAddMember.visibility = View.GONE
                holder.ivSelectedMember.visibility = View.VISIBLE

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.ivSelectedMember)


            }

            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick()
                }
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick()
    }

    class MembersViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val ivAddMember: CircleImageView = view.findViewById(R.id.iv_add_member)
        val ivSelectedMember: CircleImageView = view.findViewById(R.id.iv_selected_member_image)

    }


}