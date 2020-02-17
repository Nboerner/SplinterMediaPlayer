package com.example.mtmoon


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_good.*

/**
 * A simple [Fragment] subclass.
 */
class GoodFragment : Fragment() {


    private var shockTracker: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_good, container, false)
    }

    fun shockButtonClick(v: View) {
        //// shock here
        //while...
        if (shockTracker == 0) {
            shockButton.setBackgroundResource(R.drawable.shock)
            shockTracker = 1
        } else {
            shockButton.setBackgroundResource(R.drawable.noshock)
            shockTracker = 0
        }
    }
}
