package com.example.trello.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.trello.activities.CreateBordActivity
import com.example.trello.activities.*
import com.example.trello.adapters.MembersItemsAdapter
import com.example.trello.constants.Constants
import com.example.trello.models.Board
import com.example.trello.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass : BaseActivity(){

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo)
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
                Log.e("Success", "Success")
            }.addOnFailureListener { e ->
                activity.hideProgressBar()
                Log.e(activity.javaClass.simpleName, "Error", e)
            }
    }

    fun createBoard(activity: CreateBordActivity, board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document().set(board, SetOptions.merge())
            .addOnSuccessListener {
                activity.boardCreatedSuccessfully()
                Log.e("Success", "Success")
            }.addOnFailureListener { e ->
                activity.hideProgressBar()
                Log.e(activity.javaClass.simpleName, "Error", e)
            }
    }

    fun loadUserData(activity: Activity, readBoardsList: Boolean = false) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is SIgnInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserData(loggedInUser, readBoardsList)
                    }
                    is ProfileActivity ->{
                        activity.setUserDataInUI(loggedInUser)
                    }

                }
            }.addOnFailureListener { e ->
                hideProgressBar()
                Log.e(activity.javaClass.simpleName, "Error", e)
            }
    }

    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                val boardsList: ArrayList<Board> = ArrayList()
                for (i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                     board.documentID = i.id
                    boardsList.add(board)
                }

                activity.populateBoardListToUI(boardsList)
            }
            .addOnFailureListener {
                hideProgressBar()
            }
    }

    fun getBoardDetails(activity: TaskListActivity, boardDocumentId: String){
        mFireStore.collection(Constants.BOARDS)
            .document(boardDocumentId)
            .get()
            .addOnSuccessListener {
                    document ->
                val board =  document.toObject(Board::class.java)!!
                board.documentID = document.id
                activity.boardDetails(board)
            }
            .addOnFailureListener {
                hideProgressBar()
            }
    }


    fun updateUserProfileData(activity: ProfileActivity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                activity.profileUpdateSuccess()

            }.addOnFailureListener {
                hideProgressBar()

                activity.profileUpdatedFailure()
            }
    }

    fun addUpdateTaskList(activity: Activity, board: Board){
        val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(taskListHashMap)
            .addOnSuccessListener {
                if(activity is TaskListActivity){
                    activity.addTaskListSuccess()
                }
                else if (activity is CardDetailsActivity){
                    activity.addTaskListSuccess()
                }
            }
            .addOnFailureListener {
                hideProgressBar()
            }

    }

    fun getAssignedMembersListDetails(activity: Activity, assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                document ->
                val usersList: ArrayList<User> = ArrayList()
                for (i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }
                if(activity is MembersActivity){
                    activity.setupMembersList(usersList)
                }
                else if(activity is TaskListActivity){
                    activity.boardMembersDetailsList(usersList)
                }
                else if(activity is CardDetailsActivity){
                    activity.setupSelectedMembersList(usersList)
                }
            }
            .addOnFailureListener {
                showErrorSnackBar("Something went wrong")
            }
    }


    fun getMemberDetails(activity: MembersActivity, email: String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener {
                document ->
                if(document.documents.size > 0){
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }
                else{
                    activity.hideProgressBar()
                    showErrorSnackBar("No such member found")
                }
            }
            .addOnFailureListener {
                showErrorSnackBar("Something went wrong")
            }
    }

    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User){
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignAccess(user)
            }
            .addOnFailureListener {
                activity.hideProgressBar()
                showErrorSnackBar("Something went wrong")
            }
    }


}