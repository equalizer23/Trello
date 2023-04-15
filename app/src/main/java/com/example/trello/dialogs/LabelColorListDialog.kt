package com.example.trello.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.trello.R
import com.example.trello.adapters.LabelColorItemAdapter

abstract  class LabelColorListDialog(
    context: Context,
    private val mSelectedColor: String = "",
    private val list: ArrayList<String>,
    private var title: String  = ""
) : Dialog(context) {

    val adapter: LabelColorItemAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_label_color, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view: View){
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val rvList: RecyclerView = view.findViewById(R.id.rvList)
        tvTitle.text = title
        rvList.layoutManager = LinearLayoutManager(context)
        val adapter = LabelColorItemAdapter(context, list, mSelectedColor)
        rvList.adapter = adapter

        adapter.onItemClickListener = object: LabelColorItemAdapter.OnItemClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }

        }

    }

    protected abstract fun onItemSelected(color: String)

}