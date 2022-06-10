package com.example.typingindicator.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.models.TextMessage
import com.example.typingindicator.R

class ChatAdapter(
    private val context: Context,
    private val messages: List<TextMessage>,
) : RecyclerView.Adapter<ChatAdapter.DataAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataAdapterViewHolder {
        val layout = when (viewType) {
            IS_SENDER -> R.layout.sender_message_item
            IS_RECEIVER -> R.layout.receiver_message_item
            else -> throw IllegalArgumentException("Invalid view type")
        }

        val view = LayoutInflater
            .from(context)
            .inflate(layout, parent, false)

        return DataAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataAdapterViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        val cometChatUser = CometChat.getLoggedInUser()
        return if (cometChatUser.uid.equals(message.sender.uid)) IS_SENDER else IS_RECEIVER
    }

    companion object {
        private const val IS_SENDER = 0
        private const val IS_RECEIVER = 1
    }

    class DataAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private fun bindSenderMessage(message: TextMessage) {
            val senderMessageTxt: TextView = itemView.findViewById(R.id.senderMessageTxt)
            senderMessageTxt.text = message.text
        }

        private fun bindReceiverMessage(message: TextMessage) {
            val receiverMessageTxt: TextView = itemView.findViewById(R.id.receiverMessageTxt)
            receiverMessageTxt.text = message.text
        }

        fun bind(message: TextMessage) {
            val cometChatUser = CometChat.getLoggedInUser()
            if (cometChatUser.uid.equals(message.sender.uid)) {
                bindSenderMessage(message)
            } else {
                bindReceiverMessage(message)
            }
        }
    }
}