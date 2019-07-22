plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    google()

}
buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.jmailen.gradle:kotlinter-gradle:1.26.0")
    }
}

