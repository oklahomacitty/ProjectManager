package com.example.projectmanager.firebase

import android.app.Activity
import android.util.Log
import com.example.projectmanager.Constants
import com.example.projectmanager.activity.MainActivity
import com.example.projectmanager.activity.SignInActivity
import com.example.projectmanager.activity.SignUpActivity
import com.example.projectmanager.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                Log.e(activity.javaClass.simpleName, "Error writing document")
            }
    }

    fun signInUser(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document ->
                val loggedUser = document.toObject(User::class.java)

                when(activity) {
                    is SignInActivity -> {
                        loggedUser?.let { activity.signInSuccess(loggedUser) }
                    }
                    is MainActivity -> {
                        loggedUser?.let { activity.updateNavigationUserDetails(loggedUser) }
                    }
                }
            }.addOnFailureListener {
                when(activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error writing document")
            }
    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""

    }
}