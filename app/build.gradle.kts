import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.omouravictor.br_investidor"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.omouravictor.br_investidor"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiPropertiesFile = rootProject.file("api.properties")
        val apiProperties = Properties().apply { load(apiPropertiesFile.inputStream()) }

        buildConfigField(
            "String",
            "STOCKS_API_BASE_URL",
            apiProperties.getProperty("STOCKS_API_BASE_URL")
        )
        buildConfigField(
            "String",
            "STOCKS_API_KEY",
            apiProperties.getProperty("STOCKS_API_KEY")
        )
        buildConfigField(
            "String",
            "CURRENCY_EXCHANGE_RATES_API_BASE_URL",
            apiProperties.getProperty("CURRENCY_EXCHANGE_RATES_API_BASE_URL")
        )
        buildConfigField(
            "String",
            "CURRENCY_EXCHANGE_RATES_API_KEY",
            apiProperties.getProperty("CURRENCY_EXCHANGE_RATES_API_KEY")
        )
        buildConfigField(
            "String",
            "NEWS_API_BASE_URL",
            apiProperties.getProperty("NEWS_API_BASE_URL")
        )
        buildConfigField(
            "String",
            "NEWS_API_KEY",
            apiProperties.getProperty("NEWS_API_KEY")
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    val androidxCoreVersion = "1.12.0"
    implementation("androidx.core:core-ktx:$androidxCoreVersion")

    val androidxAppCompatVersion = "1.6.1"
    implementation("androidx.appcompat:appcompat:$androidxAppCompatVersion")

    val materialVersion = "1.11.0"
    implementation("com.google.android.material:material:$materialVersion")

    val androidxConstraintLayoutVersion = "2.1.4"
    implementation("androidx.constraintlayout:constraintlayout:$androidxConstraintLayoutVersion")

    val androidxLifecycleVersion = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$androidxLifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$androidxLifecycleVersion")

    val androidxNavigationVersion = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$androidxNavigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$androidxNavigationVersion")

    val androidxLegacyVersion = "1.0.0"
    implementation("androidx.legacy:legacy-support-v4:$androidxLegacyVersion")

    val junitVersion = "4.13.2"
    testImplementation("junit:junit:$junitVersion")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.0")

    val hiltVersion = "2.48"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")

    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    val okHttpVersion = "4.9.3"
    implementation(platform("com.squareup.okhttp3:okhttp-bom:$okHttpVersion"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    val facebookShimmerVersion = "0.5.0"
    implementation("com.facebook.shimmer:shimmer:$facebookShimmerVersion")

    val mpAndroidChartVersion = "3.1.0"
    implementation("com.github.PhilJay:MPAndroidChart:$mpAndroidChartVersion")

    val firebaseBomVersion = "33.0.0"
    implementation(platform("com.google.firebase:firebase-bom:$firebaseBomVersion"))

    val firebaseAuthVersion = "23.0.0"
    implementation("com.google.firebase:firebase-auth:$firebaseAuthVersion")

    val firebaseFirestoreVersion = "25.0.0"
    implementation("com.google.firebase:firebase-firestore:$firebaseFirestoreVersion")

    val glideVersion = "4.16.0"
    implementation("com.github.bumptech.glide:glide:$glideVersion")

    val splashScreenVersion = "1.0.1"
    implementation("androidx.core:core-splashscreen:$splashScreenVersion")

    val guavaVersion = "33.4.0-android"
    implementation("com.google.guava:guava:$guavaVersion")
}

kapt {
    correctErrorTypes = true
}