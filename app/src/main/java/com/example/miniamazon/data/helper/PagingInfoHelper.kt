package com.example.miniamazon.data.helper

import com.example.miniamazon.data.classes.Product

internal data class PagingInfoHelper(
    var viewPosition: Long = 1,
    var oldProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)