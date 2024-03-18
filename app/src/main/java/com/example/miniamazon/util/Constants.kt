package com.example.miniamazon.util

import com.example.miniamazon.R

object Constants {
    const val TAG = "TAGAmazon"
    const val USER_COLLECTION = "user"
    const val PRODUCTS_COLLECTION = "Products"
    const val CATEGORY_COLLECTION = "category"
    const val IS_OFFER_EXIST = "offerPercentage"

    object Introduction {
        const val INTRODUCTION_SHARED_PREFERENCES = "IntroductionSP"
        const val INTRODUCTION_SHARED_KEY = "IntroKey"
        val ACCOUNT_OPTIONS = R.id.action_introductionFragment2_to_accountsFragment2
        const val SHOPPING_ACTIVITY = 111
    }

    data object Categories {
        const val APPLIANCES = "Appliances"
        const val FASHION = "Fashion"
        const val ELECTRONICS = "Electronics"
        const val GROCERY = "Grocery"
        const val VIDEO_GAME = "Video Games"
        const val PERFUMES = "Perfumes"
        const val NEW_DEALS = "New Deals"
        const val RECOMMENDED_FOR_YOU = "Recommended For You"
        const val SPECIAL_PRODUCTS = "Special Products"
    }
}