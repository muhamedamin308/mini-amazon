package com.example.miniamazon.util

import com.example.miniamazon.R

object Constants {
    const val IS_OFFER_EXIST = "offerPercentage"
    const val PROFILE_STORAGE_PATH = "profileImages"

    data object Collections {
        const val USER_COLLECTION = "user"
        const val CART_COLLECTION = "cart"
        const val ORDER_COLLECTION = "order"
        const val ADDRESS_COLLECTION = "address"
        const val PRODUCTS_COLLECTION = "Products"
        const val CATEGORY_COLLECTION = "category"
    }

    data object Introduction {
        const val INTRODUCTION_SHARED_PREFERENCES = "IntroductionSP"
        const val INTRODUCTION_SHARED_KEY = "IntroKey"
        val ACCOUNT_OPTIONS = R.id.action_introductionFragment2_to_accountsFragment2
        const val SHOPPING_ACTIVITY = 111
    }

    data object Categories {
        const val NEW_DEALS = "New Deals"
        const val SPECIAL_PRODUCTS = "Special Products"
    }

    data object Taps {
        const val HOME = "Home"
        const val PERFUMES = "Perfumes"
        const val APPLIANCES = "Appliances"
        const val ELECTRONICS = "Electronics"
        const val GROCERY = "Grocery"
        const val FASHION = "Fashion"
        const val VIDEO_GAMES = "Video Games"
    }
}