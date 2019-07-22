object Depends {
    val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}"

    object BuildPlugins {
        const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
        const val androidPlugin = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:${Versions.AndroidX.appCompact}"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintLayoutVersion}"
        const val recyclerview = "androidx.recyclerview:recyclerview:${Versions.AndroidX.recyclerView}"
        const val materialDesign = "com.google.android.material:material:1.0.0-rc01"
    }

    object Lint {
        const val kotlin = "org.jmailen.gradle:kotlinter-gradle:${Versions.kotlinter}"
    }

    object Rx {
        const val Java = "io.reactivex.rxjava2:rxjava:${Versions.Rx.java}"
        const val Android = "io.reactivex.rxjava2:rxandroid:${Versions.Rx.android}"
    }


    object Coroutines {
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    }

    object Koin {
        const val android = "org.koin:koin-androidx-scope:${Versions.koinVersion}"
        const val viewModel = "org.koin:koin-androidx-viewmodel:${Versions.koinVersion}"
        const val Test = "org.koin:koin-test:${Versions.koinVersion}"
        const val ext = "org.koin:koin-androidx-ext:${Versions.koinVersion}"
    }

    object Testing {
        const val junit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlinVersion}"
        const val arch = "androidx.arch.core:core-testing:2.0.1"
        const val mockitoForKotlin =
            "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.Testing.mockitoForKotlinVersion}"
    }

}