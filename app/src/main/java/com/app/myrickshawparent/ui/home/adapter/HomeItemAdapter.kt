package com.app.myrickshawparent.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.myrickshawparent.R
import com.app.myrickshawparent.databinding.ItemListChildBinding
import com.app.myrickshawparent.ui.home.item.HomeResponse
import com.app.myrickshawparent.util.gone
import com.app.myrickshawparent.util.visible
import com.bumptech.glide.Glide

class HomeItemAdapter(
    var context: Context,
    var childData: ArrayList<HomeResponse.ChildrensItem>,
    var callback: (position: Int) -> Unit,
) :
    RecyclerView.Adapter<HomeItemAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var binding = ItemListChildBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.tvClass.gone()
        holder.binding.btnTrack.visible()
        holder.binding.btnInSchool.visible()

        holder.binding.btnInSchool.setText(childData[position].statusLabel)

        if (childData[position].busStatus == 0) {
            holder.binding.btnTrack.isEnabled = false
            holder.binding.btnTrack.setButtonBackground(context.getColor(R.color.grayDark60))
        }else{
            holder.binding.btnTrack.isEnabled = true
            holder.binding.btnTrack.setButtonBackground(context.getColor(R.color.black))
        }

        Glide.with(context).load(childData[position].profile)
            .into(holder.binding.icImage)
        holder.binding.tvUserName.text = childData[position].fullName
        holder.binding.tvSchoolName.text = childData[position].school.schoolName
        holder.binding.btnTrack.setOnClickListener {
            callback.invoke(position)
        }
        holder.binding.btnInSchool.setText(childData[position].statusLabel)

        if (childData.size - 1 == position) {
            holder.binding.viewItemListChild.visible()
        } else {
            holder.binding.viewItemListChild.gone()
        }

    }

    override fun getItemCount(): Int = childData.size

    inner class MyViewHolder(var binding: ItemListChildBinding) :
        RecyclerView.ViewHolder(binding.root)
}