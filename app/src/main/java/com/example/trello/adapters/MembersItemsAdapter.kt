package com.example.trello.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.constants.Constants
import com.example.trello.models.Card
import com.example.trello.models.User
import de.hdodenhof.circleimageview.CircleImageView

class MembersItemsAdapter (private val context: Context, private val list: ArrayList<User>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private lateinit var onClickListener: OnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_member,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is ViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.userProfileImage)

            holder.tvMemberName.text = model.name
            holder.tvMembersEmail.text = model.email

            if(model.selected){
                holder.ivSelectedMember.visibility = View.VISIBLE
            }
            else{
                holder.ivSelectedMember.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    if(model.selected){
                        onClickListener!!.onClick(position, model, Constants.UN_SELECT)
                    }
                    else{
                        onClickListener!!.onClick(position, model, Constants.SELECT)
                    }

                }

            }

        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, user: User, action: String)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val userProfileImage: CircleImageView = view.findViewById(R.id.iv_member_image)
        val tvMemberName: TextView = view.findViewById(R.id.tv_member_name)
        val tvMembersEmail: TextView = view.findViewById(R.id.tv_member_email)
        val ivSelectedMember: ImageView = view.findViewById(R.id.iv_selected_member)
    }
}
