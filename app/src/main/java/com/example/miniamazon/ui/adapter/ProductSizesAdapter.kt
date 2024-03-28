package com.example.miniamazon.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.miniamazon.R
import com.example.miniamazon.databinding.SizesItemViewBinding

class ProductSizesAdapter(
    private val context: Context
) : RecyclerView.Adapter<ProductSizesAdapter.SizesViewHolder>() {
    private var selectedSize = -1

    inner class SizesViewHolder(
        private val binding: SizesItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, productSize: String) {
            binding.productSizeTv.text = productSize
            if (position == selectedSize) {
                binding.apply {
                    imageProductSizes.visibility = View.VISIBLE
                    productSizeTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.main_background
                        )
                    )
                }
            } else {
                binding.apply {
                    imageProductSizes.visibility = View.INVISIBLE
                    productSizeTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.cancel
                        )
                    )
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder =
        SizesViewHolder(
            SizesItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    override fun getItemCount(): Int = differ.currentList.size
    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(position, size)
        holder.itemView.setOnClickListener {
            if (selectedSize >= 0)
                notifyItemChanged(selectedSize)
            selectedSize = holder.adapterPosition
            notifyItemChanged(selectedSize)
            onItemClicked?.invoke(size)
        }
    }
    var onItemClicked: ((String) -> Unit)? = null
}