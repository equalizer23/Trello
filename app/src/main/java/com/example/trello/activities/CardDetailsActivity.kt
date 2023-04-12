package com.example.trello.activities

import android.app.Activity
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.trello.R
import com.example.trello.constants.Constants
import com.example.trello.databinding.ActivityCardDetailsBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.Board
import com.example.trello.models.Card
import com.example.trello.models.Task

class CardDetailsActivity : BaseActivity() {
    private var binding: ActivityCardDetailsBinding? = null
    private lateinit var mBoardDetails: Board
    private var mTaskListPosition: Int = -1
    private var mCardPosition: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getIntentData()

        setupActionBar()

        binding?.etNameCardDetails
            ?.setText(mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition].title)

        binding?.etNameCardDetails?.setSelection(binding?.etNameCardDetails?.text.toString().length)

        binding?.btnUpdateCardDetails?.setOnClickListener {
            if(binding?.etNameCardDetails?.text.toString().isNotEmpty()){
                updateCardDetails()
            }
            else{
                showErrorSnackBar("PLease enter the name of the card")
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarCardDetailsActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title =
                mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition].title
        }

        binding?.toolbarCardDetailsActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun deleteCard(){
        var cardList : ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cardsList
        cardList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskListPosition].cardsList = cardList

        showProgressDialog()
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    private fun alertDialogForDeleteList(title: String){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title")
        builder.setIcon(R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes"){ dialogInterface, which ->
            dialogInterface.dismiss()

            deleteCard()

        }

        builder.setNegativeButton("No"){ dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun addTaskListSuccess() {
        hideProgressBar()

        setResult(Activity.RESULT_OK)
    }

    private fun updateCardDetails(){
        val card = Card(
            binding?.etNameCardDetails?.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition].assignedTo
        )

        mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition] = card

        showProgressDialog()
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card ->{
                alertDialogForDeleteList(
                    mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition].title)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}