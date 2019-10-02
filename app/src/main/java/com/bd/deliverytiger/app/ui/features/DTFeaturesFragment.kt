package com.bd.deliverytiger.app.ui.features


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.home.HomeActivity

/**
 * A simple [Fragment] subclass.
 */
class DTFeaturesFragment : Fragment() {

    companion object{
        fun newInstance(): DTFeaturesFragment {
            val fragment = DTFeaturesFragment()
            return fragment
        }
        val tag = DTFeaturesFragment::class.java.name
    }

    private lateinit var rvFeathers: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dtfeatures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        rvFeathers = view.findViewById(R.id.rvFeathers)

        val fAdapter = FeaturesListAdapter(context!!,resources.getStringArray(R.array.features_list))

        rvFeathers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("DT ফিচারস")
    }

}
