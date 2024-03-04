plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.miniamazon"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.miniamazon"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    dataBinding { enable = true }
    viewBinding { enable = true }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")
    //loading button
    implementation("com.github.leandroborgesferreira:loading-button-android:2.3.0")
    //Glide
    implementation("com.github.bumptech.glide:glide:4.13.0")
    //circular image
    implementation("de.hdodenhof:circleimageview:3.1.0")
    //viewpager2 indicatior
    implementation("io.github.vejei.viewpagerindicator:viewpagerindicator:1.0.0-alpha.1")
    //stepView
    implementation("com.shuhart.stepview:stepview:1.5.1")
    //Android Ktx
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.2")
    //Dagger hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")
    //Firebase
    implementation("com.google.firebase:firebase-auth:21.0.6")
}

kapt {
    correctErrorTypes = true
}