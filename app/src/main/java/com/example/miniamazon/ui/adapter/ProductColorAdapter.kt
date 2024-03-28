package com.example.miniamazon.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.miniamazon.databinding.ColorItemViewBinding

class ProductColorAdapter : RecyclerView.Adapter<ProductColorAdapter.ColorsViewHolder>() {
    private var selectedColor = -1
    inner class ColorsViewHolder(
        private val binding: ColorItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(productColor: Int, position: Int) {
            val imageDrawable = ColorDrawable(productColor)
            binding.color.setImageDrawable(imageDrawable)
            if (position == selectedColor) {
                binding.apply {
                    selectedImage.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    selectedImage.visibility = View.INVISIBLE
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean =
            oldItem == newItem
        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean =
            oldItem == newItem
    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder =
        ColorsViewHolder(
            ColorItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    override fun getItemCount(): Int = differ.currentList.size
    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color, position)

        holder.itemView.setOnClickListener {
            if (selectedColor >= 0)
                notifyItemChanged(selectedColor)
            selectedColor = holder.adapterPosition
            notifyItemChanged(selectedColor)
            onItemClicked?.invoke(color)
        }
    }
    var onItemClicked: ((Int) -> Unit)? = null
}