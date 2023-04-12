package com.example.trello.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.provider.MediaStore.Audio.Genres.Members
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trello.R
import com.example.trello.adapters.MembersItemsAdapter
import com.example.trello.constants.Constants
import com.example.trello.databinding.ActivityMembersBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.Board
import com.example.trello.models.User

class MembersActivity : BaseActivity() {
    private var binding: ActivityMembersBinding? = null
    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChanges: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()
        showProgressDialog()
        FireStoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)

    }

    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id)
        FireStoreClass().assignMemberToBoard(this, mBoardDetails, user)
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMembersActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Members"
        }

        binding?.toolbarMembersActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    fun setupMembersList(list:ArrayList<User>){
        hideProgressBar()

        mAssignedMembersList = list
        binding?.rvMembersList?.layoutManager = LinearLayoutManager(this)
        binding?.rvMembersList?.setHasFixedSize(true)

        val adapter = MembersItemsAdapter(this, list)

        binding?.rvMembersList?.adapter = adapter

    }

    override fun onBackPressed() {
        if(anyChanges){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member ->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.show()
        val tvAdd: TextView = dialog.findViewById(R.id.tv_add)
        val tvCancel: TextView = dialog.findViewById(R.id.tv_cancel)
        val email: AppCompatEditText = dialog.findViewById(R.id.et_email_search_member)

        tvAdd.setOnClickListener {
            if(email.text.toString().isNotEmpty()){
                dialog.dismiss()
                showProgressDialog()
                FireStoreClass().getMemberDetails(this, email.text.toString())
            }
            else{
                dialog.dismiss()
                showErrorSnackBar("Please enter an email")
            }
        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }



    }

    fun memberAssignAccess(user: User){
        hideProgressBar()
        anyChanges = true
        mAssignedMembersList.add(user)
        setupMembersList(mAssignedMembersList)
    }



    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}