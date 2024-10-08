package com.app.myrickshawparent.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.app.myrickshawparent.ui.loader.LoadingDialog
import com.app.myrickshawparent.prefrence.MyDataStore
import com.google.gson.Gson

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    abstract val layoutId: Int
    protected lateinit var binding: T

    lateinit var loadingDialog: LoadingDialog

    lateinit var gson: Gson

    lateinit var dataStore: MyDataStore

    abstract fun setupObservable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        loadingDialog = LoadingDialog(requireContext())
        dataStore = MyDataStore(requireContext())
        setupObservable()
        gson = Gson()
        return binding.root
    }
}