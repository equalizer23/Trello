package com.example.trello.firebase

import android.app.Activity
import android.util.Log
import com.example.trello.activities.CreateBordActivity
import com.example.trello.activities.*
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
                activity.boardDetails(document.toObject(Board::class.java)!!)
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

}