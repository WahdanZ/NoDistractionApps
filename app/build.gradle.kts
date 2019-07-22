plugins {
    id("com.android.application")
    id("org.jmailen.kotlinter") version "1.26.0"
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")

}

android {
    compileSdkVersion(Versions.Android.compileSdkVersion)
    defaultConfig {
        applicationId = Config.Application.applicationId
        minSdkVersion(Versions.Android.minSdkVersion)
        targetSdkVersion(Versions.Android.targetSdkVersion)
        versionCode = Config.Application.appVersionCode
        versionName = Config.Application.appVersionName
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug") {

        }
        getByName("release") {

            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Depends.kotlin_stdlib)
    implementation(Depends.AndroidX.appcompat)
    implementation(Depends.AndroidX.recyclerview)
    implementation(Depends.AndroidX.constraintLayout)
    implementation(Depends.AndroidX.materialDesign)
    implementation(Depends.Koin.android)
    implementation(Depends.Koin.viewModel)
    implementation(Depends.Koin.ext)
    implementation(Depends.Rx.Java)
    implementation(Depends.Rx.Android)
    testImplementation(Depends.Testing.mockitoForKotlin)
    testImplementation(Depends.Testing.junit)
    testImplementation(Depends.Testing.arch)
    implementation(Depends.Koin.Test)


}

repositories {
    mavenCentral()
    maven("http://repository.jetbrains.com/all")
}
