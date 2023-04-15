package com.example.trello.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.models.Card
import com.example.trello.models.User
import de.hdodenhof.circleimageview.CircleImageView

class LabelColorItemAdapter (
    private val context: Context,
    private val list: ArrayList<String>,
    private val mSelectedColor: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_label_color,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        if(holder is ViewHolder){
            holder.viewMain.setBackgroundColor(Color.parseColor(item))
            if (item == mSelectedColor){
                holder.ivSelectedColor.visibility = View.VISIBLE
            }else{
                holder.ivSelectedColor.visibility = View.GONE
            }

            holder.itemView.setOnClickListener{
                if(onItemClickListener != null){
                    onItemClickListener!!.onClick(position, item)
                }
            }

        }
    }

    interface OnItemClickListener{
        fun onClick(position: Int, color: String)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val viewMain: View = view.findViewById(R.id.view_main)
        val ivSelectedColor: ImageView = view.findViewById(R.id.iv_selected_color)

    }
}
