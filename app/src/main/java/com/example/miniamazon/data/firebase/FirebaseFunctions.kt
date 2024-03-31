package com.example.miniamazon.data.firebase

import com.example.miniamazon.data.classes.Cart
import com.example.miniamazon.util.Constants.CART_COLLECTION
import com.example.miniamazon.util.Constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseFunctions(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    private val cartCollection = fireStore
        .collection(USER_COLLECTION)
        .document(auth.uid!!)
        .collection(CART_COLLECTION)

    fun addProductToCart(
        cartProduct: Cart,
        onAction: (Cart?, Exception?) -> Unit
    ) {
        cartCollection
            .document()
            .set(cartProduct)
            .addOnSuccessListener {
                onAction(cartProduct, null)
            }.addOnFailureListener {
                onAction(null, it)
            }
    }

    fun increaseExcitingProductQuantity(
        documentPathID: String,
        onAction: (String?, Exception?) -> Unit
    ) {
        fireStore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentPathID)
            val doc = transition.get(documentRef)
            val cartObject = doc.toObject(Cart::class.java)
            cartObject?.let {
                val newQuantity = it.quantity + 1
                val increasedProduct = it.copy(quantity = newQuantity)
                transition.update(documentRef, "quantity", increasedProduct.quantity)
            }
        }.addOnSuccessListener {
            onAction(documentPathID, null)
        }.addOnFailureListener {
            onAction(null, it)
        }
    }
}