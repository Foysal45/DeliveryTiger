package com.bd.deliverytiger.app.ui.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A simple [Fragment] subclass.
 */
class AnnouncementBottomSheet : BottomSheetDialogFragment() {

    private lateinit var titleTV: TextView
    private lateinit var messageTV: TextView

    companion object{
        fun newInstance(): AnnouncementBottomSheet = AnnouncementBottomSheet().apply {  }
        val tag = AnnouncementBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_announcement_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTV = view.findViewById(R.id.announcement_title_tv)
        messageTV = view.findViewById(R.id.announcement_msg_tv)

        messageTV.text = "ডেলিভারী করুন নিশ্চিন্তে, নির্ভয়ে! Tazul ভাইয়ের মত আপনিও ব্যাবহার করুন Delivery Tiger সার্ভিস।\n" +
                "\n" +
                "বাংলাদেশের প্রথম লজিস্টিক মার্কেটপ্লেস Delivery Tiger নিয়ে এলো অস্থির এক অফার! Delivery Tiger কুরিয়ারের মাধ্যমে ঢাকা থেকে দেশের ৬৪ জেলা শহর ও ৪৯২ উপজেলায় আপনার পণ্য ডেলিভারী দিতে পারবেন মাত্র ২৫ টাকায়! সাথে ৭ কার্যদিবসের মধ্যে পেমেন্ট গ্রহনের সুবিধা!\n" +
                "\n" +
                "আপনাদের সন্তুষ্টি, আমাদের অর্জন......"
    }

}
