package com.bd.deliverytiger.app.ui.chat

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.ActivityChatBinding
import com.bd.deliverytiger.app.ui.chat.history.ChatHistoryFragment

class ChatActivity: AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbarTitle.text = "Chat"
        binding.backBtn.setOnClickListener {
            super.onBackPressed()
        }


        val fragment = ChatHistoryFragment.newInstance()
        val tag = ChatHistoryFragment.tag
        supportFragmentManager.beginTransaction().apply {
            add(R.id.containerChat, fragment, tag)
            commit()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}