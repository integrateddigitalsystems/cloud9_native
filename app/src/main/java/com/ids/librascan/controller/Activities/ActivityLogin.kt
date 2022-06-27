package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ids.librascan.R
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivityLoginBinding
import com.ids.librascan.utils.AppHelper
import utils.AppConstants
import utils.LocaleUtils
import utils.toast
import utils.wtf
import java.util.*

class ActivityLogin : ActivityCompactBase() {
    lateinit var activityLoginBinding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var getContent: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        setUpContent()
        super.onCreate(savedInstanceState)
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding.root)
        init()


    }

    private fun init() {
        AppHelper.setAllTexts(activityLoginBinding.rootLogin, this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id_auth))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        activityLoginBinding.btLogin.setOnClickListener {
            signInGoogle()
        }

       activityLoginBinding.btChangeLanguage.setOnClickListener {
            if (MyApplication.languageCode.equals(AppConstants.LANG_ENGLISH)) {
                AppHelper.changeLanguage(this,"ar")
                startActivity(Intent(this@ActivityLogin, ActivityLogin::class.java))
                finish()
            } else {
                AppHelper.changeLanguage(this,"en")
                startActivity(Intent(this@ActivityLogin, ActivityLogin::class.java))
                finish()
            }
        }
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        getContent.launch(signInIntent)
    }

    private fun setUpContent() {
        getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                if (it.resultCode ==Activity.RESULT_OK){
                    val intent: Intent = it.data!!
                    val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
                    try {
                        val account = task.getResult(ApiException::class.java)!!
                        firebaseAuthWithGoogle(account.idToken!!)
                    } catch (e: ApiException) {
                        wtf(e.toString())
                    }

                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    MyApplication.isLogin = true
                    val user = firebaseAuth.currentUser
                    startActivity(Intent(this, ActivityMain::class.java))
                    finish()
                    toast(getString(R.string.success_login))
                    wtf("firebaseAuthWithGoogle: ${user?.displayName}")

                } else {
                    wtf(task.exception.toString())
                   toast(getString(R.string.login_faild))
                }
            }
    }


}
