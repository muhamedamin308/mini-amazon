package com.example.miniamazon.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.miniamazon.R
import com.example.miniamazon.data.classes.order.Order
import com.example.miniamazon.data.classes.order.OrderController
import com.example.miniamazon.data.classes.order.OrderStatus
import com.example.miniamazon.databinding.OrderItemViewBinding

@Suppress("DEPRECATION")
class OrdersHistoryAdapter : RecyclerView.Adapter<OrdersHistoryAdapter.OrdersViewHolder>() {
    inner class OrdersViewHolder(
        val binding: OrderItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                binding.orderId.text = order.orderId.toString()
                binding.orderDate.text = order.orderDate
                val resources = itemView.resources
                val status = OrderController.getOrderStatus(order.orderStatus)
                val colorDrawable = when (status) {
                    OrderStatus.Canceled -> ColorDrawable(resources.getColor(R.color.canceled))
                    OrderStatus.Confirmed -> ColorDrawable(resources.getColor(R.color.confirmed))
                    OrderStatus.Delivered -> ColorDrawable(resources.getColor(R.color.delivered))
                    OrderStatus.Returned -> ColorDrawable(resources.getColor(R.color.returned))
                    OrderStatus.Shipped -> ColorDrawable(resources.getColor(R.color.shipped))
                    OrderStatus.Ordered -> ColorDrawable(resources.getColor(R.color.ordered))
                }
                orderStatusColor.setImageDrawable(colorDrawable)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean =
            oldItem.cartProducts == newItem.cartProducts

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrdersHistoryAdapter.OrdersViewHolder =
        OrdersViewHolder(
            OrderItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: OrdersHistoryAdapter.OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)
        holder.itemView.setOnClickListener {
            onClick?.invoke(order)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    var onClick: ((Order) -> Unit)? = null
}