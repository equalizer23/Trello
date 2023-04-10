package com.example.trello.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.models.Card
import com.example.trello.models.User
import de.hdodenhof.circleimageview.CircleImageView

class MembersItemsAdapter (private val context: Context, private val list: ArrayList<User>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

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


        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, card: Card)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val userProfileImage: CircleImageView = view.findViewById(R.id.iv_member_image)
        val tvMemberName: TextView = view.findViewById(R.id.tv_member_name)
        val tvMembersEmail: TextView = view.findViewById(R.id.tv_member_email)
    }
}
