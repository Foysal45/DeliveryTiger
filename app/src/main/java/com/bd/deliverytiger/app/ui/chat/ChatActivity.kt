package com.bd.deliverytiger.app.ui.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.chat.ChatUserData
import com.bd.deliverytiger.app.api.model.chat.FirebaseCredential
import com.bd.deliverytiger.app.databinding.ActivityChatBinding
import com.bd.deliverytiger.app.ui.chat.compose.ChatComposeFragment
import com.bd.deliverytiger.app.ui.chat.history.ChatHistoryFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

class ChatActivity: AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val bundle = intent?.getBundleExtra("chatConfig")
        val credential = bundle?.getParcelable<FirebaseCredential>("credential") ?: throw Exception("Invalid firebase credential")
        val documentName: String? = bundle.getString("documentName")
        val sender: ChatUserData? = bundle.getParcelable("sender")
        val receiver: ChatUserData? = bundle.getParcelable("receiver")

        //val firebaseApp = initFirebaseDatabase(credential)
        val firebaseApp = Firebase.app

        if (receiver?.id.isNullOrEmpty()) {

            val bundleChatHistory = bundleOf(
                "documentName" to documentName,
                "firebaseStorageUrl" to credential.storageUrl,
                "firebaseWebApiKey" to credential.firebaseWebApiKey,
                "sender" to sender
            )
            goToChatHistory(firebaseApp, bundleChatHistory)
        } else {
            val bundleChatCompose = bundleOf(
                "documentName" to documentName,
                "firebaseStorageUrl" to credential.storageUrl,
                "firebaseWebApiKey" to credential.firebaseWebApiKey,
                "sender" to sender,
                "receiver" to receiver
            )
            goToChatCompose(firebaseApp, bundleChatCompose)
        }
    }

    private fun initFirebaseDatabase(credential: FirebaseCredential): FirebaseApp {
        return try {
            val options = FirebaseOptions.Builder()
                .setProjectId(credential.projectId)
                .setApplicationId(credential.applicationId)
                .setApiKey(credential.apikey)
                .setDatabaseUrl(credential.databaseUrl)
                .build()

            FirebaseApp.initializeApp(this, options, "Ajkerdeal Seller")
            FirebaseApp.getInstance("Ajkerdeal Seller")
        } catch (e: Exception) {
            FirebaseApp.getInstance("Ajkerdeal Seller")
        }
    }

    private fun goToChatCompose(firebaseApp: FirebaseApp, bundle: Bundle) {
        val fragment = ChatComposeFragment.newInstance(firebaseApp, bundle)
        val tag = ChatComposeFragment.tag
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment, tag)
            commit()
        }
    }

    private fun goToChatHistory(firebaseApp: FirebaseApp, bundle: Bundle) {
        val fragment = ChatHistoryFragment.newInstance(firebaseApp, bundle)
        val tag = ChatHistoryFragment.tag
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment, tag)
            commit()
        }
    }

    fun setToolbar(title: String) {
        supportActionBar?.title = title
    }


}