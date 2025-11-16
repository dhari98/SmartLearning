@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal() // ✅ هذا يجب أن يكون أولاً لتحميل ksp plugin

        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LearnEnglish"
include(":app")
 