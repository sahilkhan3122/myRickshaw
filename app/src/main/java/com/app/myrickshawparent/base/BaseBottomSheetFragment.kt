package com.app.myrickshawparent.base

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment<T : ViewBinding>(val isShowBluer: Boolean = true) :
    BottomSheetDialogFragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = inflateBinding(inflater, container)
        setupUI()
        return binding.root
    }

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): T

    override fun onStart() {
        super.onStart()
        setupOnStart()
        if (isShowBluer) {
            blurBackground()
        }
    }

    protected abstract fun setupUI()
    protected abstract fun setupOnStart()

    private fun blurBackground() {
        val rootView = requireActivity().window.decorView.rootView
        val bitmap = getScreenShot(rootView)
        val blurredBitmap = blurBitmap(bitmap)
        dialog?.window?.setBackgroundDrawable(BitmapDrawable(resources, blurredBitmap))
    }

    private fun getScreenShot(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }

    private fun blurBitmap(bitmap: Bitmap): Bitmap {
        val outputBitmap = Bitmap.createBitmap(bitmap)
        val renderScript = RenderScript.create(requireContext())
        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createFromBitmap(renderScript, outputBitmap)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.setRadius(25f)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(outputBitmap)
        return outputBitmap
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}
