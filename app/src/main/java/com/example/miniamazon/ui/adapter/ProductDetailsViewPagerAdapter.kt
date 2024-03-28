package com.example.miniamazon.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.miniamazon.databinding.ViewPigerImageItemViewBinding

class ProductDetailsViewPagerAdapter :
    RecyclerView.Adapter<ProductDetailsViewPagerAdapter.ImageViewPagerViewHolder>() {
    inner class ImageViewPagerViewHolder(
        private val binding: ViewPigerImageItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(productImage: String) {
            Glide.with(itemView.context)
                .load(productImage)
                .into(binding.viewPagerImageProduct)
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewPagerViewHolder =
        ImageViewPagerViewHolder(
            ViewPigerImageItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ImageViewPagerViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

}