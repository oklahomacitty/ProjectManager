package com.example.projectmanager.firebase

import android.app.Activity
import android.util.Log
import com.example.projectmanager.Constants
import com.example.projectmanager.activity.MainActivity
import com.example.projectmanager.activity.ProfileActivity
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
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                Log.e(activity.javaClass.simpleName, "Error writing document")
            }
    }

    fun updateUserProfileData(activity: ProfileActivity,
                              userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile Data updated successfully")
                activity.showToast("Profile updated successfully")
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while created a board.", e)
                activity.showToast("Error when updating profile")
            }
    }

    fun loadUserData(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document ->
                val loggedUser = document.toObject(User::class.java)!!

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedUser)
                    }
                    is ProfileActivity -> {
                        activity.setUserDataInUI(loggedUser)
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
                    is ProfileActivity -> {
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