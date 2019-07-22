buildscript {

    repositories {
        jcenter()
        google()
    }
    dependencies {
        classpath(Depends.BuildPlugins.androidPlugin)
        classpath(Depends.BuildPlugins.kotlinPlugin)
    }
}

allprojects {
    repositories {
        jcenter()
        google()
    }
}
