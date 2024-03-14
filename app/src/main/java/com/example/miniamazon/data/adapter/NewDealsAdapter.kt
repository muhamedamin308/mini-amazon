package com.example.miniamazon.data.adapter

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.miniamazon.data.classes.Product
import com.example.miniamazon.databinding.NewDealsItemsBinding

class NewDealsAdapter :
    RecyclerView.Adapter<NewDealsAdapter.NewDealsViewHolder>() {

    inner class NewDealsViewHolder(private val binding: NewDealsItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView)
                    .load(product.images[0])
                    .into(imageNewDeals)
                tvDealProductName.text = product.name
                product.offerPercentage?.let {
                    var remaining = 1f - it
                    remaining *= product.price
                    tvNewPrice.text = "$ ${String.format("%.2f", remaining)}"
                    tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                if (product.offerPercentage == null) {
                    tvNewPrice.visibility = View.GONE
                    tvOldPrice.alpha = 1f
                    tvOldPrice.textSize = 15f
                }
                tvOldPrice.text = "$ ${product.price}"
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewDealsViewHolder =
        NewDealsViewHolder(
            NewDealsItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: NewDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

}