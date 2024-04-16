package com.example.miniamazon.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.miniamazon.R
import com.example.miniamazon.data.classes.Address
import com.example.miniamazon.databinding.ItemAddressLayoutBinding
import com.example.miniamazon.util.hide
import com.example.miniamazon.util.show

class AddressAdapter(
    private val context: Context
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    private var selectedAddress = -1

    inner class AddressViewHolder(
        val binding: ItemAddressLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address, isSelected: Boolean) {
            binding.apply {
                addressTv.text = address.homeTitle
                if (isSelected) {
                    imageAddressBg.show()
                    addressTv.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.main_background
                        )
                    )
                } else {
                    imageAddressBg.hide()
                    addressTv.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.cancel
                        )
                    )
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean =
            oldItem.homeTitle == newItem.homeTitle && oldItem.fullName == newItem.fullName

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean =
            oldItem == newItem
    }
    val differ = AsyncListDiffer(this, diffCallback)

    init {
        differ.addListListener { _, _ ->
            notifyItemChanged(selectedAddress)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder =
        AddressViewHolder(
            ItemAddressLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position)
        holder.binding.addressTv.setOnClickListener {
            if (selectedAddress >= 0) notifyItemChanged(selectedAddress)
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }
    }

    var onClick: ((Address) -> Unit)? = null
}
