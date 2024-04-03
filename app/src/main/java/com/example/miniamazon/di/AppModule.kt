package com.example.miniamazon.di

import android.app.Application
import android.content.SharedPreferences
import com.example.miniamazon.data.firebase.FirebaseDataLayer
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.miniamazon.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    @Provides
    @Singleton
    fun provideFirebaseFireStoreDatabase() = Firebase.firestore

    @Provides
    fun provideIntroductionSharedPreferences(
        application: Application
    ): SharedPreferences = application.getSharedPreferences(
        Constants.Introduction.INTRODUCTION_SHARED_PREFERENCES,
        Application.MODE_PRIVATE
    )

    @Provides
    @Singleton
    fun provideFirebaseData(
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore
    ) = FirebaseDataLayer(fireStore, firebaseAuth)

    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference
}