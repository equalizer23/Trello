package com.example.trello.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.databinding.ItemBoardBinding
import com.example.trello.models.Board
import de.hdodenhof.circleimageview.CircleImageView

class BoardItemsAdapter(
    private val context: Context,
    private val list: ArrayList<Board>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemBoardBinding.inflate(LayoutInflater.from(context), parent, false)
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.ivBoardImage)

            holder.boardName.text = model.name
            holder.createdBy.text = "Created by ${model.createdBy}"

            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

     
    interface OnClickListener{
        fun onClick(position: Int, model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    private class MyViewHolder(binding: ItemBoardBinding): RecyclerView.ViewHolder(binding.root){
        val ivBoardImage = binding.ivBoardImage
        val boardName = binding.tvName
        val createdBy = binding.tvCreatedBy
    }

}