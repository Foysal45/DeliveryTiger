package com.bd.deliverytiger.app.ui.chat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.chat.ChatUserData
import com.bd.deliverytiger.app.api.model.chat.FirebaseCredential
import com.bd.deliverytiger.app.databinding.ActivityChatBinding
import com.bd.deliverytiger.app.fcm.FCMData
import com.bd.deliverytiger.app.ui.chat.compose.ChatComposeFragment
import com.bd.deliverytiger.app.ui.chat.history.ChatHistoryFragment
import com.bd.deliverytiger.app.ui.login.LoginActivity
import com.bd.deliverytiger.app.utils.SessionManager
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import timber.log.Timber

class ChatActivity: AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private var credential: FirebaseCredential? = null
    private var documentName: String? = null
    private var sender: ChatUserData? = null
    private var receiver: ChatUserData? = null
    private lateinit var firebaseApp: FirebaseApp


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
        if (!SessionManager.isLogin) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        sender = ChatUserData(
            SessionManager.courierUserId.toString(), SessionManager.userName, SessionManager.mobile,
            imageUrl = "https://static.ajkerdeal.com/delivery_tiger/profile/${SessionManager.courierUserId}.jpg",
            role = "dt",
            fcmToken = SessionManager.firebaseToken
        )
        credential = FirebaseCredential(firebaseWebApiKey = BuildConfig.FirebaseWebApiKey)

        //val firebaseApp = initFirebaseDatabase(credential)
        firebaseApp = Firebase.app
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent ?: return
        val bundle = intent.extras
        if (bundle != null) {
            val model: FCMData? = bundle.getParcelable("data")
            val notificationType = bundle.getString("notificationType") ?: ""
            if (notificationType.isNotEmpty()) {
                if (model != null) {
                    // From foreground
                    documentName = bundle.getString("notificationType")
                    receiver = ChatUserData(
                        model.senderId ?: "",
                        model.senderName ?: "", "", "", "",
                        model.senderRole ?: ""
                    )
                } else {
                    // From background
                    documentName = bundle.getString("notificationType")
                    receiver = ChatUserData(
                        bundle.getString("senderId") ?: "",
                        bundle.getString("senderName") ?: "", "", "", "",
                        bundle.getString("senderRole") ?: ""
                    )
                }
                //credential = FirebaseCredential(firebaseWebApiKey = BuildConfig.FirebaseWebApiKey)
            } else {
                // From home
                bundle.getBundle("chatConfig")?.run {
                    documentName = getString("documentName")
                    receiver = getParcelable("receiver")
                    //credential = getParcelable("credential")
                    //sender = getParcelable("sender")
                }
            }
            navigation()
        }
        intent.removeExtra("notificationType")
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun navigation() {
        if (receiver?.id.isNullOrEmpty()) {
            val bundleChatHistory = bundleOf(
                "documentName" to documentName,
                "firebaseStorageUrl" to credential?.storageUrl,
                "firebaseWebApiKey" to credential?.firebaseWebApiKey,
                "sender" to sender
            )
            goToChatHistory(firebaseApp, bundleChatHistory)
        } else {
            val bundleChatCompose = bundleOf(
                "documentName" to documentName,
                "firebaseStorageUrl" to credential?.storageUrl,
                "firebaseWebApiKey" to credential?.firebaseWebApiKey,
                "sender" to sender,
                "receiver" to receiver
            )
            //goToChatCompose(firebaseApp, bundleChatCompose)
            goToChatComposeWithHistory(firebaseApp, bundleChatCompose)
        }


    }

    override fun onStart() {
        super.onStart()
        supportFragmentManager.addOnBackStackChangedListener(onBackStackChangeLister)
    }

    override fun onStop() {
        super.onStop()
        supportFragmentManager.removeOnBackStackChangedListener(onBackStackChangeLister)
    }

    private val onBackStackChangeLister = FragmentManager.OnBackStackChangedListener {
        when (supportFragmentManager.findFragmentById(R.id.container)) {
            is ChatHistoryFragment -> {
                setToolbar("Chat History")
            }
            /*is ChatComposeFragment -> {
                setToolbar(receiver?.name ?: "Chat")
            }*/
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

    private fun goToChatComposeWithHistory(firebaseApp: FirebaseApp, bundle: Bundle) {
        val fragment1 = ChatHistoryFragment.newInstance(firebaseApp, bundle)
        val tag1 = ChatHistoryFragment.tag
        val fragment2 = ChatComposeFragment.newInstance(firebaseApp, bundle)
        val tag2 = ChatComposeFragment.tag
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment1, tag1)
            commit()
        }
        supportFragmentManager.beginTransaction().apply {
            add(R.id.container, fragment2, tag2)
            addToBackStack(tag2)
            commit()
        }
    }

    fun setToolbar(title: String) {
        supportActionBar?.title = title
    }


}