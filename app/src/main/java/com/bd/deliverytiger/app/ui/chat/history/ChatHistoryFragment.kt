package com.bd.deliverytiger.app.ui.chat.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.databinding.FragmentChatHistoryBinding
import com.bd.deliverytiger.app.ui.chat.model.ChatHistoryData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class ChatHistoryFragment(): Fragment() {

    private var binding: FragmentChatHistoryBinding? = null
    private lateinit var historyReference: DatabaseReference
    private lateinit var dataAdapter: ChatHistoryAdapter

    private lateinit var valueEventListener: ValueEventListener

    private var isLoading = false
    private val visibleThreshold = 6
    private var totalCount = 0
    private val queryLimit = 15

    companion object {
        fun newInstance(): ChatHistoryFragment = ChatHistoryFragment().apply {  }
        val tag: String = ChatHistoryFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentChatHistoryBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataAdapter = ChatHistoryAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        binding?.progressBar?.visibility = View.VISIBLE
        historyReference = Firebase.database.getReference("DeliveryTiger").child("chat").child("agentHistory").child("1")
        historyReference.orderByKey().limitToLast(queryLimit).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding?.progressBar?.visibility = View.GONE
                if (snapshot.exists()) {
                    Timber.tag("chatDebug").d(snapshot.toString())
                    val historyList: MutableList<ChatHistoryData> = mutableListOf()
                    snapshot.children.forEach { dataSnapshot ->
                        val model = dataSnapshot.getValue(ChatHistoryData::class.java)
                        model?.let {
                            historyList.add(it)
                        }
                    }
                    historyList.reverse()
                    dataAdapter.initLoad(historyList)

                } else {
                    binding?.emptyView?.visibility = View.VISIBLE
                    Timber.tag("chatDebug").d("agentHistory $snapshot")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                binding?.progressBar?.visibility = View.GONE
                binding?.emptyView?.visibility = View.VISIBLE
                Timber.tag("chatDebug").d("agentHistory ${error.message}")
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
        historyReference.orderByKey().endAt(endKey).limitToLast(queryLimit).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    isLoading = false
                    Timber.tag("chatDebug").d(snapshot.toString())
                    val historyList: MutableList<ChatHistoryData> = mutableListOf()
                    snapshot.children.forEach { dataSnapshot ->
                        val model = dataSnapshot.getValue(ChatHistoryData::class.java)
                        model?.let {
                            historyList.add(it)
                        }
                    }
                    historyList.reverse()
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

        valueEventListener = historyReference.orderByKey().limitToLast(1).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.tag("chatDebug").d(snapshot.toString())
                val historyList: MutableList<ChatHistoryData> = mutableListOf()
                snapshot.children.forEach { dataSnapshot ->
                    val model = dataSnapshot.getValue(ChatHistoryData::class.java)
                    model?.let {
                        historyList.add(it)
                    }
                }
                historyList.reverse()
                dataAdapter.addNewData(historyList)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag("chatDebug").d("agentHistory valueEventListener ${error.message}")
            }
        })

    }

    override fun onDestroyView() {
        binding = null
        historyReference.removeEventListener(valueEventListener)
        super.onDestroyView()
    }

}