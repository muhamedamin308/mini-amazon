package com.example.miniamazon.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.miniamazon.data.classes.Cart
import com.example.miniamazon.data.helper.getProductPrice
import com.example.miniamazon.databinding.ItemCartLayoutBinding

class MyCartAdapter : RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {
    inner class MyCartViewHolder(
        val binding: ItemCartLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(cart: Cart) {
            binding.apply {
                Glide.with(itemView)
                    .load(cart.products.images[0])
                    .into(imageProductCart)
                cartProductNameTv.text = cart.products.name
                val priceAfterOffer =
                    cart.products.offerPercentage.getProductPrice(cart.products.price)
                cartProductPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                quantityTv.text = cart.quantity.toString()
                productImageColor.setImageDrawable(
                    ColorDrawable(
                        cart.selectedColor ?: Color.TRANSPARENT
                    )
                )
                productSizeTv.text =
                    cart.selectedSize ?: "".also { cardView.visibility = View.GONE }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean =
            oldItem.products.id == newItem.products.id

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder =
        MyCartViewHolder(
            ItemCartLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)
        holder.itemView.setOnClickListener {
            onProductClicked?.invoke(cartProduct)
        }
        holder.binding.plus.setOnClickListener {
            onPlusClicked?.invoke(cartProduct)
        }
        holder.binding.minus.setOnClickListener {
            onMinusClicked?.invoke(cartProduct)
        }
    }

    var onProductClicked: ((Cart) -> Unit)? = null
    var onMinusClicked: ((Cart) -> Unit)? = null
    var onPlusClicked: ((Cart) -> Unit)? = null
}