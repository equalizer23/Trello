package com.example.trello.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.trello.activities.*
import com.example.trello.constants.Constants
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

    fun signInUser(activity: Activity) {
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

                }
            }.addOnFailureListener { e ->
                hideProgressBar()
                Log.e(activity.javaClass.simpleName, "Error", e)
            }
    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}