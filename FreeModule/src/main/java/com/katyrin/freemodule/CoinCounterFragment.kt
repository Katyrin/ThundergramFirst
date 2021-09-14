package com.katyrin.freemodule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.katyrin.freemodule.data.AppState
import com.katyrin.freemodule.data.Event
import com.katyrin.freemodule.databinding.FragmentCoinCounterBinding
import com.katyrin.freemodule.storage.StorageImpl
import com.katyrin.freemodule.utils.cellNumber
import com.katyrin.freemodule.utils.launchWhenStarted
import com.katyrin.freemodule.utils.toast
import kotlinx.coroutines.flow.onEach

class CoinCounterFragment : Fragment() {

    private var mRewardedAd: RewardedAd? = null
    private var binding: FragmentCoinCounterBinding? = null
    private val viewModel: CoinCounterViewModel by viewModels {
        ViewModelFactory(StorageImpl(requireContext()))
    }

    private val rewardedAdLoadCallback = object : RewardedAdLoadCallback() {
        override fun onAdFailedToLoad(adError: LoadAdError) {
            toast(adError.message)
            mRewardedAd = null
        }

        override fun onAdLoaded(rewardedAd: RewardedAd) {
            toast("Ad was loaded.")
            mRewardedAd = rewardedAd
        }
    }

    private val fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdShowedFullScreenContent() {
            toast("Ad was shown.")
            loadRewardedAd()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
            toast("Ad failed to show.")
            loadRewardedAd()
        }

        override fun onAdDismissedFullScreenContent() {
            toast("Ad was dismissed.")
            mRewardedAd = null
            loadRewardedAd()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCoinCounterBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState ?: viewModel.getCount()
        initAds()
        initViews()
        getCoroutinesResult()
    }

    private fun initAds() {
        MobileAds.initialize(requireContext()) {}
        RequestConfiguration.Builder().setTestDeviceIds(listOf("AB3034792ED476712252E3AE416A3296"))
        loadRewardedAd()
    }

    private fun initViews() {
        binding?.apply {
            payButton.setOnClickListener { viewModel.setCount(10) }
            adsButton.setOnClickListener { showRewardedAd() }
        }
    }

    private fun getCoroutinesResult() {
        viewModel.sharedFlow
            .onEach(::renderData)
            .launchWhenStarted(lifecycleScope)
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.ShowCount -> binding?.countTextView?.text = appState.count
            is AppState.CallNumber -> callNumberState(appState.event)
            is AppState.Error -> toast(appState.message)
            is AppState.ShowLackCount -> toast(R.string.lack_count_message)
        }
    }

    private fun callNumberState(event: Event) {
        requireActivity().cellNumber(event.uri)
        viewModel.setCount(event.count)
    }

    private fun loadRewardedAd() {
        RewardedAd.load(
            requireContext(),
            "ca-app-pub-3940256099942544/5224354917",
            AdRequest.Builder().build(),
            rewardedAdLoadCallback
        )
        mRewardedAd?.fullScreenContentCallback = fullScreenContentCallback
    }

    private fun showRewardedAd() {
        if (mRewardedAd != null) mRewardedAd?.show(requireActivity()) { onUserEarnedReward() }
        else toast("The rewarded ad wasn't ready yet.")
    }

    private fun onUserEarnedReward() {
        viewModel.setCount(1)
        loadRewardedAd()
    }

    override fun onDestroyView() {
        binding = null
        mRewardedAd = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(): Fragment = CoinCounterFragment()
    }
}