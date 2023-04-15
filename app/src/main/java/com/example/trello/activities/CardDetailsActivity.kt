package com.example.trello.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.LinearGradient
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trello.R
import com.example.trello.adapters.CardMemberListItemsAdapter
import com.example.trello.constants.Constants
import com.example.trello.databinding.ActivityCardDetailsBinding
import com.example.trello.dialogs.AssignedToCardDialog
import com.example.trello.dialogs.LabelColorListDialog
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {
    private var binding: ActivityCardDetailsBinding? = null
    private lateinit var mBoardDetails: Board
    private var mTaskListPosition: Int = -1
    private var mCardPosition: Int = -1
    private var mSelectedColor = ""
    private lateinit var mMembersDetailList: ArrayList<String>
    private var mSelectedDueDateMilliSeconds: Long = 0
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

        binding?.tvSelectLabelColor?.setOnClickListener {
            labelColorsListDialog()
        }

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition].color
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }

        binding?.tvSelectDueDate?.setOnClickListener {
            showDatePicker()
        }

        mSelectedDueDateMilliSeconds = mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition].dueDate

        if(mSelectedDueDateMilliSeconds < 0){
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = sdf.format(Date(mSelectedDueDateMilliSeconds))
            binding?.tvSelectDueDate?.text = selectedDate
        }

        FireStoreClass().getAssignedMembersListDetails(this, mMembersDetailList)
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
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailList = intent.getStringArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
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
        finish()
    }

    private fun updateCardDetails(){
        val card = Card(
            binding?.etNameCardDetails?.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliSeconds
        )

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)


        mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition] = card
       // mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)


        showProgressDialog()
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    private fun colorsList() : ArrayList<String>{
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    private fun setColor(){
        binding?.tvSelectLabelColor?.text = ""
        binding?.tvSelectLabelColor?.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorsListDialog(){
        val colorsList: ArrayList<String> = colorsList()
        val listDialog = object: LabelColorListDialog(
            this,
            mSelectedColor,
            colorsList,
            "Select Label Color"
        ){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }

        listDialog.show()
    }

    fun membersAssignedToCardDialog(usersList: ArrayList<User>){

        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition].assignedTo

        if(cardAssignedMembersList.size > 0){
            for(i in usersList.indices){
                for(j in cardAssignedMembersList){
                    if(usersList[i].id == j){
                        usersList[i].selected = true
                    }
                }
            }
        }
        else{
            for (i in usersList.indices){
                usersList[i].selected = false
            }
        }

        val listDialog = object: AssignedToCardDialog(
            this,
            usersList
        ){
            override fun onUserSelected(user: User, action: String) {
                if(action == Constants.SELECT){
                    if(!mBoardDetails.taskList[mTaskListPosition]
                            .cardsList[mCardPosition].assignedTo.contains(user.id)){

                        mBoardDetails.taskList[mTaskListPosition]
                            .cardsList[mCardPosition].assignedTo.add(user.id)
                    }
                }
                else{
                    mBoardDetails.taskList[mTaskListPosition]
                        .cardsList[mCardPosition].assignedTo.remove(user.id)

                    for (i in usersList.indices){
                        if(usersList[i].id == user.id){
                            usersList[i].selected = false
                        }
                    }
                }

                setupSelectedMembersList(usersList)
            }

        }

        listDialog.show()
    }

    fun setupSelectedMembersList(usersList: ArrayList<User>){
        val cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition].cardsList[mCardPosition].assignedTo

        val selectedMembersList : ArrayList<SelectedMembers> = ArrayList()

            for(i in usersList.indices){
                for(j in cardAssignedMembersList){
                    if(usersList[i].id == j){
                        val selectedMember = SelectedMembers(
                            usersList[i].id,
                            usersList[i].image
                        )
                        selectedMembersList.add(selectedMember)
                    }
                }
            }

        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("", ""))
            binding?.tvSelectMembers?.visibility = View.GONE
            binding?.rvSelectedMembersList?.visibility = View.VISIBLE

            binding?.rvSelectedMembersList?.layoutManager = GridLayoutManager(this, 6)

            val adapter = CardMemberListItemsAdapter(this, selectedMembersList, true)
            binding?.rvSelectedMembersList?.adapter = adapter
            adapter.setOnClickListener(
                object: CardMemberListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        membersAssignedToCardDialog(usersList)
                    }

                }
            )
        }
        else{
            binding?.tvSelectMembers?.visibility = View.VISIBLE
            binding?.rvSelectedMembersList?.visibility = View.GONE
            binding?.tvSelectMembers?.setOnClickListener{
                membersAssignedToCardDialog(usersList)
            }
        }

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

    private fun showDatePicker(){
        val c  = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)

        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                val sDayOfMonth = if(dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear =
                    if((month + 1) < 10) "0$month" else "$month"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                binding?.tvSelectDueDate?.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val theDate = sdf.parse(selectedDate)

                mSelectedDueDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )

        dpd.show()
    }
}