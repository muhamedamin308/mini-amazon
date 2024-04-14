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
import com.example.miniamazon.databinding.ItemBillingLayoutBinding
import com.example.miniamazon.util.gone

class CartBillingAdapter : RecyclerView.Adapter<CartBillingAdapter.CartBillingViewHolder>() {
    inner class CartBillingViewHolder(
        private val binding: ItemBillingLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(cart: Cart) {
            binding.apply {
                Glide.with(itemView)
                    .load(cart.products.images[0])
                    .into(imageProductCart)
                cartProductNameTv.text = cart.products.name
                quantityTv.text = cart.quantity.toString()
                val priceAfterOffer =
                    cart.products.offerPercentage.getProductPrice(cart.products.price)
                        ?: cart.products.price
                cartProductPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                productImageColor.setImageDrawable(
                    ColorDrawable(
                        cart.selectedColor ?: Color.TRANSPARENT
                    )
                )
                productSizeTv.text =
                    cart.selectedSize ?: "".also { cardView.gone() }
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartBillingViewHolder =
        CartBillingViewHolder(
            ItemBillingLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: CartBillingViewHolder, position: Int) {
        val cart = differ.currentList[position]
        holder.bind(cart)
    }

}