package com.ids.librascan.controller.Activities

import Base.ActivityCompactBase
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ids.librascan.R
import com.ids.librascan.apis.RetrofitClient
import com.ids.librascan.apis.RetrofitInterface
import com.ids.librascan.controller.MyApplication
import com.ids.librascan.databinding.ActivityLoginBinding
import com.ids.librascan.model.ResponseLogin
import com.ids.librascan.utils.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.*

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

    @SuppressLint("ResourceAsColor")
    private fun init() {
        AppHelper.setAllTexts(activityLoginBinding.rootLogin, this)
        configureGoogleSignIn()

        activityLoginBinding.btLogin.setOnClickListener {
            if (MyApplication.clientKey == "") {
                startActivity(Intent(this, QrCodeActivity::class.java))
            } else {
                mGoogleSignInClient.signOut()
                signInGoogle()
            }
        }
        activityLoginBinding.btScan.setOnClickListener {
                if (MyApplication.clientKey != "") {
                    startActivity(Intent(this, QrCodeActivity::class.java))
                } else {
                    signInGoogle()
                }
            }
        activityLoginBinding.btEn.setOnClickListener {
                AppHelper.changeLanguage(this, "en")
                startActivity(Intent(this@ActivityLogin, ActivityLogin::class.java))
                finish()

        }
        activityLoginBinding.btAr.setOnClickListener {
                AppHelper.changeLanguage(this, "ar")
                startActivity(Intent(this@ActivityLogin, ActivityLogin::class.java))
                finish()
        }

        if (MyApplication.languageCode ==AppConstants.LANG_ENGLISH){
            activityLoginBinding.btEn.background = ContextCompat.getDrawable(this@ActivityLogin, R.drawable.rounded)
        }
        else{
            activityLoginBinding.btAr.background = ContextCompat.getDrawable(this@ActivityLogin, R.drawable.rounded)
        }
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id_auth))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        getContent.launch(signInIntent)
    }

    private fun setUpContent() {
        getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val intent: Intent = it.data!!
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(intent)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    wtf(e.toString())
                    MyApplication.isLogin = false
                    wtf("exception: " + e.message)
                    wtf("exception code: " + e.statusCode)
                }

            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        wtf("id token: " + acct.idToken)
        wtf("acct id : " + acct.id)
        wtf("displayName: " + acct.displayName)
        wtf("email: " + acct.email)
        wtf("photoUrl: " + acct.photoUrl)
        wtf("serverAuthCode: " + acct.serverAuthCode)
        wtf("isExpired: " + acct.isExpired)
        wtf("account : " + acct)
        wtf("getId : " + acct.id)
        wtf("getIdToken : " + acct.idToken)
        wtf("getEmail : " + acct.email)
        wtf("getDisplayName : " + acct.displayName)
        wtf("getPhotoUrl : " + acct.photoUrl)
        wtf("getServerAuthCode : " + acct.serverAuthCode)
        wtf("isExpired : " + acct.isExpired)
        wtf("getObfuscatedIdentifier : " + "")
        wtf("getGivenName : " + acct.givenName)
        wtf("getFamilyName : " + acct.familyName)
        performGoogleLogin(acct.idToken!!, acct.displayName!!)
    }

    private fun performGoogleLogin(idToken: String, displayName: String) {
        activityLoginBinding.progressBar.show()
        RetrofitClient.client?.create(RetrofitInterface::class.java)
            ?.getLogin(idToken)?.enqueue(object :
                Callback<ResponseLogin> {
                override fun onResponse(
                    call: Call<ResponseLogin>,
                    response: Response<ResponseLogin>
                ) {
                    if (response.isSuccessful) {
                        MyApplication.isLogin = true
                        wtf("after data set ")
                        try {
                            this@ActivityLogin.runOnUiThread {
                                activityLoginBinding.progressBar.hide()
                            }
                            activityLoginBinding.progressBar.hide()
                            wtf("after progressBar ")
                           // toast(response.body()!!.message!!)
                            startActivity(Intent(this@ActivityLogin, ActivitySessions::class.java))
                            wtf("after startActivity ")
                            finish()
                            wtf("after finish ")
                        } catch (e: Exception) {
                            wtf("ex:: " + e.message)
                           // errorDialog(this@ActivityLogin,e.toString())
                        }

                    } else {
                        this@ActivityLogin.runOnUiThread {
                          activityLoginBinding.progressBar.hide()
                            toast(AppHelper.getRemoteString("login_failed",this@ActivityLogin))
                            try {
                               // errorDialog(this@ActivityLogin, response.errorBody()!!.toString())
                            } catch (e: Exception) {
                            }

                        }
                    }
                }
                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    AppHelper.createDialogError(this@ActivityLogin,t.toString(),"performGoogleLogin")
                    toast(AppHelper.getRemoteString("login_failed",this@ActivityLogin))
                }
            })
    }



}
