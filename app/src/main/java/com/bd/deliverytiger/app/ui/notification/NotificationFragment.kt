package com.bd.deliverytiger.app.ui.notification


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R

class NotificationFragment : Fragment() {

    private lateinit var notificationRV: RecyclerView

    companion object{
        fun newInstance(): NotificationFragment = NotificationFragment().apply {  }
        val tag = NotificationFragment::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationRV = view.findViewById(R.id.notification_rv)

        val list: MutableList<String> = mutableListOf()
        for (i in 0..10){
            list.add("Notification title will be here $i")
        }

        val notificationAdapter = NotificationAdapter(requireContext(), list)
        with(notificationRV){
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = notificationAdapter
        }
    }

}
