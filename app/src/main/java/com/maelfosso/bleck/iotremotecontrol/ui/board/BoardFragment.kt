package com.maelfosso.bleck.iotremotecontrol.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.maelfosso.bleck.iotremotecontrol.R

class BoardFragment : Fragment() {

    private lateinit var boardViewModel: BoardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        boardViewModel =
            ViewModelProviders.of(this).get(BoardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_board, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }
}