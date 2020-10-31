package com.bd.deliverytiger.app.ui.chat.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.databinding.FragmentChatBinding
import com.bd.deliverytiger.app.ui.chat.model.ChatData
import com.bd.deliverytiger.app.utils.SessionManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class ChatFragment(): Fragment() {

    private var binding: FragmentChatBinding? = null
    private lateinit var chatRoomReference: DatabaseReference
    private lateinit var dataAdapter: ChatAdapter

    private lateinit var valueEventListener: ValueEventListener

    private var isLoading = false
    private val visibleThreshold = 6
    private var totalCount = 0
    private val queryLimit = 15

    private var bundle: Bundle? = null
    private var agentId = 0

    companion object {
        fun newInstance(bundle: Bundle): ChatFragment = ChatFragment().apply {
            this.bundle = bundle
        }
        val tag: String = ChatFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentChatBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        agentId = bundle?.getInt("agentId", 0) ?: 0

        dataAdapter = ChatAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
        }

        binding?.progressBar?.visibility = View.VISIBLE
        val userId = SessionManager.courierUserId
        val agentId = 328702
        chatRoomReference = Firebase.database.getReference("DeliveryTiger")
            .child("chat")
            .child("merchantMsg")
            .child(userId.toString())
            .child(userId.toString()+agentId)
        Timber.tag("chatDebug").d("Reference $chatRoomReference")
        chatRoomReference.orderByKey().limitToLast(queryLimit).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding?.progressBar?.visibility = View.GONE
                if (snapshot.exists()) {
                    Timber.tag("chatDebug").d(snapshot.toString())
                    val historyList: MutableList<ChatData> = mutableListOf()
                    snapshot.children.forEach { dataSnapshot ->
                        val model = dataSnapshot.getValue(ChatData::class.java)
                        model?.let {
                            historyList.add(it)
                        }
                    }
                    //historyList.reverse()
                    dataAdapter.initLoad(historyList)

                } else {
                    binding?.emptyView?.visibility = View.VISIBLE
                    Timber.tag("chatDebug").d("chatRoomReference $snapshot")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                binding?.progressBar?.visibility = View.GONE
                binding?.emptyView?.visibility = View.VISIBLE
                Timber.tag("chatDebug").d("chatRoomReference ${error.message}")
            }
        })

        valueEventLister()

        binding?.recyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val currentItemCount = recyclerView.layoutManager?.itemCount ?: 0
                    val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    Timber.d("onScrolled ${!isLoading} $currentItemCount <= ${lastVisibleItem + visibleThreshold}")
                    if (!isLoading && currentItemCount <= lastVisibleItem + visibleThreshold /*&& currentItemCount < totalCount*/) {
                        isLoading = true
                        val endKey = dataAdapter.lastItem().key
                        fetchMoreHistory(endKey)
                        Timber.d("onScrolled fetchMoreHistory $endKey")
                    }
                }
            }
        })

    }

    private fun fetchMoreHistory(endKey: String?) {
        binding?.progressBar?.visibility = View.VISIBLE
        chatRoomReference.orderByKey().endAt(endKey).limitToLast(queryLimit).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    isLoading = false
                    Timber.tag("chatDebug").d(snapshot.toString())
                    val historyList: MutableList<ChatData> = mutableListOf()
                    snapshot.children.forEach { dataSnapshot ->
                        val model = dataSnapshot.getValue(ChatData::class.java)
                        model?.let {
                            historyList.add(it)
                        }
                    }
                    //historyList.reverse()
                    dataAdapter.pagingLoad(historyList)
                    if (historyList.size < queryLimit - 1) {
                        isLoading = true
                    }

                } else {
                    isLoading = true
                }
                binding?.progressBar?.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding?.progressBar?.visibility = View.GONE
                Timber.tag("chatDebug").d("agentHistory fetchMoreHistory ${error.message}")
            }

        })
    }

    private fun valueEventLister() {

        valueEventListener = chatRoomReference.orderByKey().limitToLast(1).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.tag("chatDebug").d(snapshot.toString())
                val historyList: MutableList<ChatData> = mutableListOf()
                snapshot.children.forEach { dataSnapshot ->
                    val model = dataSnapshot.getValue(ChatData::class.java)
                    model?.let {
                        historyList.add(it)
                    }
                }
                //historyList.reverse()
                dataAdapter.addNewData(historyList)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag("chatDebug").d("agentHistory valueEventListener ${error.message}")
            }
        })

    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}