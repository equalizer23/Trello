package com.example.trello.firebase

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.example.trello.CreateBordActivity
import com.example.trello.activities.*
import com.example.trello.constants.Constants
import com.example.trello.models.Board
import com.example.trello.models.User
import com.google.firebase.auth.FirebaseAuth
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

    fun loadUserData(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is SIgnInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserData(loggedInUser)
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