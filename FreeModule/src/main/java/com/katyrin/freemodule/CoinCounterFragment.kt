package com.katyrin.freemodule

import android.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.katyrin.freemodule.databinding.FragmentCoinCounterBinding
import com.katyrin.freemodule.utils.toast

class CoinCounterFragment : Fragment() {

    private lateinit var viewModel: CoinCounterViewModel
    private var binding: FragmentCoinCounterBinding? = null
    private var count = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCoinCounterBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel = ViewModelProvider(this).get(CoinCounterViewModel::class.java)
        binding?.apply {
            countTextView.text = count.toString()
            payButton.setOnClickListener {
                toast("pay button click")
                countTextView.text = (++count).toString()
            }
            adsButton.setOnClickListener {
                toast("ads button click")
                countTextView.text = (--count).toString()
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): Fragment = CoinCounterFragment()
    }
}