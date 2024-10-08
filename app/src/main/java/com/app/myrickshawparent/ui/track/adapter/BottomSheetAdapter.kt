package com.app.myrickshawparent.ui.track.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.myrickshawparent.databinding.ItemTrackRouteBinding
import com.app.myrickshawparent.ui.home.item.HomeResponse
import com.app.myrickshawparent.util.gone
import com.app.myrickshawparent.util.visible

class BottomSheetAdapter(
    var context: Context,
    private var bottomSheetList: MutableList<HomeResponse.RouteStopsItem>,
    var callback: (position: Int) -> Unit = {},
) :
    RecyclerView.Adapter<BottomSheetAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemTrackRouteBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.tvPickDescription.setLineSpacing(10f, 1.2f)
        holder.binding.tvPickDescription.text = bottomSheetList[position].address
        holder.binding.tvTime.text = bottomSheetList[position].status

        if (bottomSheetList.size - 1 == position) {
            holder.binding.viewItemListChild.visible()
            holder.binding.icEllipse.visible()
        } else {
            holder.binding.viewItemListChild.gone()
            holder.binding.icEllipse.gone()
        }
        if (position == 0) {
            holder.binding.lineTop.gone()
        } else {
            holder.binding.lineTop.visible()
        }
    }

    override fun getItemCount(): Int = bottomSheetList.size

    inner class MyViewHolder(var binding: ItemTrackRouteBinding) :
        RecyclerView.ViewHolder(binding.root)
}