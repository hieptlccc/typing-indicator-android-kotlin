package com.example.typingindicator.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.MessagesRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.*
import com.example.typingindicator.R
import com.example.typingindicator.adapter.ChatAdapter

class ChatActivity : AppCompatActivity(), View.OnClickListener {
    private var chatRv: RecyclerView? = null
    private var chatEdt: EditText? = null
    private var sendBtn: Button? = null
    private var typingTxt: TextView? = null

    private var pDialog: ProgressDialog? = null

    private var adapter: ChatAdapter? = null

    private var textMessages: ArrayList<TextMessage> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initViews()
        initEvents()
        loadMessages()
    }

    override fun onResume() {
        super.onResume()
        listenRealtimeMessages()
    }

    override fun onPause() {
        super.onPause()
        CometChat.removeMessageListener(intent?.getStringExtra("uid").toString());
    }

    private fun initViews() {
        chatRv = findViewById(R.id.chatRv)
        chatEdt = findViewById(R.id.chatEdt)
        sendBtn = findViewById(R.id.sendBtn)
        typingTxt = findViewById(R.id.typingTxt)

        typingTxt!!.visibility = View.GONE

        pDialog = ProgressDialog(this)
        pDialog!!.setMessage("Loading")
        pDialog!!.setCanceledOnTouchOutside(false)
    }

    private fun initEvents() {
        sendBtn!!.setOnClickListener(this)
        chatEdt!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val receiverID = intent?.getStringExtra("uid").toString()
                if (s.toString().equals("")) {
                    val typingIndicator = TypingIndicator(receiverID, CometChatConstants.RECEIVER_TYPE_USER)
                    CometChat.endTyping(typingIndicator)
                } else {
                    val typingIndicator = TypingIndicator(receiverID, CometChatConstants.RECEIVER_TYPE_USER)
                    CometChat.startTyping(typingIndicator)
                }
            }
        })
    }

    private fun loadMessages() {
        pDialog!!.show()
        var messagesRequest: MessagesRequest? = null
        val limit:Int=30
        val UID:String = intent?.getStringExtra("uid").toString();

        messagesRequest=MessagesRequest.MessagesRequestBuilder()
            .setLimit(limit)
            .setUID(UID)
            .build()

        messagesRequest?.fetchPrevious(object:CometChat.CallbackListener<List<BaseMessage>>(){
            override fun onSuccess(p0: List<BaseMessage>?) {
                if (!p0.isNullOrEmpty()) {
                    for (baseMessage in p0) {
                        if (baseMessage is TextMessage) {
                            textMessages.add(baseMessage)
                        }
                    }
                    initRecyclerView(textMessages)
                }
                pDialog!!.dismiss()
            }
            override fun onError(p0: CometChatException?) {
                Toast.makeText(this@ChatActivity, "Cannot load the list of messages. Please try again", Toast.LENGTH_LONG).show()
                pDialog!!.dismiss()
            }
        })
    }

    private fun initRecyclerView(textMessages: ArrayList<TextMessage>) {
        if (textMessages.size == 0) {
            return;
        }
        chatRv!!.layoutManager = LinearLayoutManager(this)
        adapter = this.let { ChatAdapter(this, textMessages) }
        chatRv!!.adapter = adapter
        pDialog!!.dismiss()
    }

    private fun listenRealtimeMessages() {
        val listenerID:String = intent?.getStringExtra("uid").toString();

        CometChat.addMessageListener(listenerID,object :CometChat.MessageListener(){

            override fun onTextMessageReceived(message: TextMessage) {
                textMessages.add(message)
                adapter?.notifyDataSetChanged()
            }

            override fun onTypingStarted(typingIndicator: TypingIndicator?) {
                typingTxt!!.visibility = View.VISIBLE
                typingTxt!!.text = typingIndicator?.sender?.name + " is typing...";
            }

            override fun onTypingEnded(typingIndicator: TypingIndicator?) {
                typingTxt!!.visibility = View.GONE
                typingTxt!!.text = "";
            }

        })
    }

    override fun onClick(view: View?) {
        when(view!!.id) {
            R.id.sendBtn -> sendMessage()
        }
    }

    private fun sendMessage() {
        val messageText:String = chatEdt!!.text.toString()
        if (messageText == "") {
            Toast.makeText(this@ChatActivity, "Please input your text message", Toast.LENGTH_LONG).show()
        }
        val receiverID:String = intent?.getStringExtra("uid").toString()
        val receiverType:String = CometChatConstants.RECEIVER_TYPE_USER
        val textMessage = TextMessage(receiverID, messageText,receiverType)
        CometChat.sendMessage(textMessage, object : CometChat.CallbackListener<TextMessage>() {
            override fun onSuccess(p0: TextMessage) {
                chatEdt!!.setText("")
                textMessages.add(p0)
                adapter?.notifyDataSetChanged()
                val typingIndicator = TypingIndicator(receiverID, CometChatConstants.RECEIVER_TYPE_USER)
                CometChat.endTyping(typingIndicator)
            }

            override fun onError(p0: CometChatException?) {
                chatEdt!!.setText("")
                Toast.makeText(this@ChatActivity, "Cannot send your message. Please try again", Toast.LENGTH_LONG).show()
                val typingIndicator = TypingIndicator(receiverID, CometChatConstants.RECEIVER_TYPE_USER)
                CometChat.endTyping(typingIndicator)
            }
        })
    }
}