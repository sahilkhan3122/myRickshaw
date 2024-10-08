package com.app.myrickshawparent.ui.mychild

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.myrickshawparent.R
import com.app.myrickshawparent.base.BaseFragment
import com.app.myrickshawparent.databinding.MyChildFragmentBinding
import com.app.myrickshawparent.network.utility.ExceptionData
import com.app.myrickshawparent.network.utility.ResponseData
import com.app.myrickshawparent.ui.mychild.adapter.ChildAdapter
import com.app.myrickshawparent.ui.mychild.response.ChildrenResponse
import com.app.myrickshawparent.util.afterTextChangedWithMinLength
import com.app.myrickshawparent.util.exceptionHandle
import com.app.myrickshawparent.util.gone
import com.app.myrickshawparent.util.hideKeyboard
import com.app.myrickshawparent.util.showToastMessage
import com.app.myrickshawparent.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyChildFragment : BaseFragment<MyChildFragmentBinding>() {

    override val layoutId: Int
        get() = R.layout.my_child_fragment

    private lateinit var childViewModel: ChildViewModel
    private var childList: ArrayList<ChildrenResponse.DataItem> = ArrayList()

    override fun setupObservable() {

        childViewModel = ViewModelProvider(this)[ChildViewModel::class.java]
        binding.viewModel = childViewModel

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val childAdapter = ChildAdapter(requireContext(), childList) {}
        binding.rvChild.layoutManager = layoutManager
        binding.rvChild.adapter = childAdapter

        binding.searchBar.edtSearch.afterTextChangedWithMinLength(3) { search ->
            binding.searchBar.edtSearch.hideKeyboard()
            childList.clear()
            childViewModel.childApi(search)
        }

        lifecycleScope.launch {
            childViewModel.childStateFlow.collect {
                when (it) {
                    is ResponseData.Empty -> {
                        loadingDialog.dismiss()
                    }

                    is ResponseData.Error -> {
                        loadingDialog.dismiss()
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
                        if (it.data is ChildrenResponse) {
                            if (it.data.status) {
                                binding.tvNoDataFound.gone()
                                childList.clear()
                                childList.addAll(it.data.data)
                                childAdapter.notifyDataSetChanged()
                            } else {
                              binding.tvNoDataFound.visible()
                               // requireActivity().showToastMessage(it.data.message)
                            }
                        }
                    }

                    is ResponseData.Exception -> {
                        loadingDialog.dismiss()
                        requireActivity().exceptionHandle(ExceptionData(code = it.code), dataStore = dataStore)
                    }

                }
            }
        }
    }
}
