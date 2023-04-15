package com.example.trello.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.R
import com.example.trello.adapters.LabelColorItemAdapter
import com.example.trello.adapters.MembersItemsAdapter
import com.example.trello.models.User

abstract class AssignedToCardDialog(
    context: Context,
    private val list: ArrayList<User>) : Dialog(context) {

    val adapter: MembersItemsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_assigned_to_card, null)

        setContentView(view)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view: View) {
        val rvListMember: RecyclerView = view.findViewById(R.id.rv_list_members)

        if(list.size > 0){
            rvListMember.layoutManager = LinearLayoutManager(context)
            val adapter = MembersItemsAdapter(context, list)
            rvListMember.adapter = adapter

            adapter.setOnClickListener(object :
                MembersItemsAdapter.OnClickListener {
                override fun onClick(position: Int, user: User, action: String) {
                    dismiss()
                    onUserSelected(user, action)
                }
            })
        }

    }

    protected abstract fun onUserSelected(user: User, action: String)
}