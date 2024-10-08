package com.app.myrickshawparent.ui.mychild.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.myrickshawparent.databinding.ItemListChildBinding
import com.app.myrickshawparent.ui.mychild.response.ChildrenResponse
import com.app.myrickshawparent.util.gone
import com.app.myrickshawparent.util.visible
import com.bumptech.glide.Glide

class ChildAdapter(
    var context: Context,
    var childData: ArrayList<ChildrenResponse.DataItem>,
    var callback: (position: Int) -> Unit,
) :
    RecyclerView.Adapter<ChildAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var binding = ItemListChildBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(childData[position].profile)
            .into(holder.binding.icImage)
        holder.binding.tvUserName.text = childData[position].fullName
        holder.binding.tvSchoolName.text = childData[position].school.schoolName
        holder.binding.tvClass.text = childData[position].standard

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