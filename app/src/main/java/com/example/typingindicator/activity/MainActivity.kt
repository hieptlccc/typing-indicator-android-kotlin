package com.example.typingindicator.activity

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.example.typingindicator.R
import com.example.typingindicator.adapter.UsersAdapter
import com.example.typingindicator.constants.Constants
import com.cometchat.pro.core.UsersRequest
import com.cometchat.pro.core.UsersRequest.UsersRequestBuilder


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var logoutIv: ImageView? = null
    private var usersRv: RecyclerView? = null

    private var pDialog: ProgressDialog? = null

    private var adapter: UsersAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initEvents()
        initCometChat()
        if (CometChat.getLoggedInUser() != null) {
            loadUsers()
        }
    }

    private fun initViews() {
        logoutIv = findViewById(R.id.logoutIv)
        usersRv = findViewById(R.id.usersRv)

        pDialog = ProgressDialog(this)
        pDialog!!.setMessage("Loading")
        pDialog!!.setCanceledOnTouchOutside(false)
    }

    private fun initEvents() {
        logoutIv!!.setOnClickListener(this)
    }

    private fun initCometChat() {
        val appSettings = AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(
            Constants.COMETCHAT_REGION).build()

        CometChat.init(this, Constants.COMETCHAT_APP_ID, appSettings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(successMessage: String) {
            }

            override fun onError(e: CometChatException) {
            }
        })
    }

    private fun loadUsers() {
        var usersRequest:UsersRequest?=null
        val limit:Int=30

        usersRequest=UsersRequest.UsersRequestBuilder().setLimit(limit).build()

        usersRequest?.fetchNext(object :CometChat.CallbackListener<List<User>>(){
            override fun onSuccess(users: List<User>?) {
                initRecyclerView(users as ArrayList<User>?);
            }

            override fun onError(p0: CometChatException?) {
                Toast.makeText(this@MainActivity, "Cannot load list of users", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initRecyclerView(users: ArrayList<User>?) {
        if (users == null || users.size == 0) {
            return;
        }
        usersRv!!.layoutManager = LinearLayoutManager(this)
        adapter = this.let { UsersAdapter(this, users) }
        usersRv!!.adapter = adapter
        pDialog!!.dismiss()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (CometChat.getLoggedInUser() == null) {
            goToLoginActivity();
        }
    }

    override fun onClick(view: View?) {
        when(view!!.id) {
            R.id.logoutIv -> logout()
        }
    }

    private fun logout() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Do you want to logout ?")
            .setCancelable(false)
            .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                    dialog, id -> handleLogout()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Logout")
        alert.show()
    }

    private fun handleLogout() {
        pDialog!!.show()
        CometChat.logout(object : CometChat.CallbackListener<String>() {
            override fun onSuccess(p0: String?) {
                goToLoginActivity()
                pDialog!!.dismiss()
            }
            override fun onError(p0: CometChatException?) {
                pDialog!!.dismiss()
                Toast.makeText(this@MainActivity, "Cannot logout, please try again", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun goToLoginActivity() {
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}