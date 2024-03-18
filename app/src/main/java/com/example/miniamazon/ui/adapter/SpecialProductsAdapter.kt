package com.example.miniamazon.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.miniamazon.data.classes.Product
import com.example.miniamazon.databinding.SpecialRvItemBinding

class SpecialProductsAdapter :
    RecyclerView.Adapter<SpecialProductsAdapter.SpecialProductViewHolder>() {
    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    inner class SpecialProductViewHolder(private val binding: SpecialRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView)
                    .load(product.images[0])
                    .into(imageSpecialProduct)
                tvSpecialProductName.text = product.name
                tvSpecialProductPrice.text = "$ ${product.price}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductViewHolder =
        SpecialProductViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: SpecialProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }
}