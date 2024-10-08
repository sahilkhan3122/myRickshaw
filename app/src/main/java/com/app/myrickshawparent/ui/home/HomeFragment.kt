package com.app.myrickshawparent.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseFragment
import com.app.myrickshawparent.databinding.HomeFragmentBinding
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.NotifyMe
import com.app.myrickshawparent.network.utility.NotifyType
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.home.adapter.HomeItemAdapter
import com.app.myrickshawparent.ui.home.item.HomeResponse
import com.app.myrickshawparent.ui.track.TrackActivity
import com.app.myrickshawparent.ui.track.response.PopupResponse
import com.app.myrickshawparent.util.Constants
import com.app.myrickshawparent.util.Constants.homeResponseData
import com.app.myrickshawparent.util.Constants.homeResponseFullData
import com.app.myrickshawparent.util.afterTextChangedWithMinLength
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.gone
import com.app.myrickshawparent.util.hideKeyboard
import com.app.myrickshawparent.util.printLog
import com.app.myrickshawparent.util.showToastMessage
import com.app.myrickshawparent.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFragmentBinding>() {

    private lateinit var homeViewModel: HomeViewModel
    private var homeList: ArrayList<HomeResponse.ChildrensItem> = ArrayList()

    private var homeAdapter: HomeItemAdapter? = null

    override val layoutId: Int
        get() = R.layout.home_fragment

    private var resultLauncherTracking =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                homeViewModel.homeApi("")
            }
        }

    override fun setupObservable() {

        CoroutineScope(Dispatchers.IO).launch {
            Constants.notifyMutableStateFlow.emit(NotifyMe.Empty(NotifyType.EMPTY))
        }

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.viewModel = homeViewModel

        notifyDataObservable()

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvHome.layoutManager = layoutManager

        binding.searchBar.edtSearch.afterTextChangedWithMinLength(3) { search ->
            binding.searchBar.edtSearch.hideKeyboard()
            homeList.clear()
            homeViewModel.homeApi(search)
        }

        lifecycleScope.launch {
            homeViewModel.homeStateFlow.collect {
                when (it) {
                    is ResponseData.Empty -> {
                        loadingDialog.dismiss()
                    }

                    is ResponseData.Error -> {
                        loadingDialog.dismiss()
                        printLog("data_information", it.error)
                        requireActivity().showToastMessage(it.error)
                    }

                    is ResponseData.InternetConnection -> {
                        loadingDialog.dismiss()
                    }

                    is ResponseData.Loading -> {
                        loadingDialog.show()
                    }

                    is ResponseData.Success -> {
                        loadingDialog.dismiss()
                        if (it.data is HomeResponse) {
                            if (it.data.status) {
                                binding.tvNoDataFound.gone()
                                homeList.clear()
                                homeList.addAll(it.data.data.childrens)
                                if (homeAdapter != null) {
                                    homeAdapter!!.notifyDataSetChanged()
                                } else {
                                    homeAdapter =
                                        HomeItemAdapter(requireContext(), homeList) { position ->
                                            homeResponseData = homeList[position].route
                                            homeResponseFullData = homeList[position]
                                            resultLauncherTracking.launch(
                                                Intent(
                                                    requireContext(),
                                                    TrackActivity::class.java
                                                )
                                            )
                                        }
                                    binding.rvHome.adapter = homeAdapter
                                }
                            } else {
                                binding.tvNoDataFound.visible()
                            }
                        }
                    }

                    is ResponseData.Exception -> {
                        loadingDialog.dismiss()
                        requireActivity().exceptionHandle(
                            ExceptionData(code = it.code),
                            dataStore = dataStore
                        )
                    }

                }
            }
        }
    }

    /**
     * This function get notify data and perform operation
     * @see notifyDataObservable
     */
    private fun notifyDataObservable() {

        lifecycleScope.launch {
            Constants.notifyStateFlow.collect { newData ->
                when (newData) {

                    is NotifyMe.Notify<*> -> {
                        printLog("data_information", "notifyData: ${newData.data}")
                        val response: String = newData.data.toString()
                        val popupResponse = gson.fromJson(response, PopupResponse::class.java)
                        if (popupResponse.firebaseResponse.id != "") {
                            for (i in 0 until homeList.size) {
                                if (homeList[i].id.toString() == popupResponse.firebaseResponse.id) {
                                    homeList[i].busStatus = 0
                                    if (homeAdapter != null) {
                                        homeAdapter!!.notifyDataSetChanged()
                                    }
                                    break
                                }
                            }
                        }
                    }

                }
            }
        }
    }


}

