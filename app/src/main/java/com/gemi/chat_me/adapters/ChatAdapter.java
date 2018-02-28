package com.gemi.chat_me.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gemi.chat_me.Application.BaseApp;
import com.gemi.chat_me.Models.Messages;
import com.gemi.chat_me.R;

import java.util.List;

/**
 * Created by macbookpro on 2/7/18.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    List<Messages> messagesList;

    public ChatAdapter(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ChatViewHolder holder, int position) {
        Messages messages = messagesList.get(position);
        if (messages.getSenderID().equals(BaseApp.auth.getCurrentUser().getUid())) {
            holder.sender.setText(messages.getMessage());
            holder.sender.setVisibility(View.VISIBLE);
            holder.receiver.setVisibility(View.GONE);
        } else if (messages.getReceiverID().equals(BaseApp.auth.getCurrentUser().getUid())) {
            holder.receiver.setText(messages.getMessage());
            holder.sender.setVisibility(View.GONE);
            holder.receiver.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView sender, receiver;

        public ChatViewHolder(View itemView) {
            super(itemView);
            sender = (TextView) itemView.findViewById(R.id.sender);
            receiver = (TextView) itemView.findViewById(R.id.receiver);
        }
    }
}
