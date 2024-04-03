package com.example.miniamazon.ui.adapter

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
import com.example.miniamazon.databinding.RecommendedItemsBinding
import com.example.miniamazon.util.gone

class RecommendedProductsAdapter :
    RecyclerView.Adapter<RecommendedProductsAdapter.RecommendedProductsViewHolder>() {

    inner class RecommendedProductsViewHolder(private val binding: RecommendedItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            binding.apply {
                tvName.text = product.name
                Glide.with(itemView)
                    .load(product.images[0])
                    .into(imageProduct)
                product.offerPercentage?.let {
                    var newPrice = 1f - it
                    newPrice *= product.price
                    tvNewPrice.text = "$ ${String.format("%.2f", newPrice)}"
                    tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                if (product.offerPercentage == null) {
                    tvNewPrice.gone()
                    tvPrice.alpha = 1f
                    tvPrice.textSize = 15f
                }
                tvPrice.text = "$ ${product.price}"
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
    ): RecommendedProductsViewHolder =
        RecommendedProductsViewHolder(
            RecommendedItemsBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )


    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: RecommendedProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick: ((Product) -> Unit)? = null

}