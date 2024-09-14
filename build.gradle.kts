@file:Suppress("SpellCheckingInspection", "HardCodedStringLiteral")

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = providers.gradleProperty(key).get()

fun fileProperties(key: String) = project.findProperty(key).toString().let { if (it.isNotEmpty()) file(it) else null }

fun environment(key: String) = providers.environmentVariable(key)

plugins {
  // Java support
  id("java")
  alias(libs.plugins.kotlin)
  alias(libs.plugins.gradleIntelliJPlugin)
  alias(libs.plugins.changelog)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktlint)
}

// Import variables from gradle.properties file
val pluginGroup: String by project
val pluginName: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project
val pluginVerifierIdeVersions: String by project

val platformType: String by project
val platformVersion: String by project
val platformPlugins: String by project
val platformDownloadSources: String by project

val javaVersion: String by project


val pluginsVersion: String = properties("pluginsVersion")
val sassVersion: String = properties("sassVersion")
val angularVersion: String = properties("angularVersion")


group = pluginGroup
version = pluginVersion


// Configure project's dependencies
repositories {
  mavenCentral()
  mavenLocal()
  gradlePluginPortal()

  intellijPlatform {
    defaultRepositories()
    jetbrainsRuntime()
  }
}


dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
  implementation("commons-io:commons-io:2.11.0")
  implementation("com.thoughtworks.xstream:xstream:1.4.20")
  compileOnly(fileTree("libs"))

  intellijPlatform {
    intellijIdeaUltimate(platformVersion, useInstaller = false)
    instrumentationTools()
    pluginVerifier()
    zipSigner()

    bundledPlugins(
      "com.intellij.java",
      "org.jetbrains.kotlin",
      "com.intellij.settingsSync",
      "JavaScript",
      "training",
      "XPathView"
    )

    plugins(
      "PythonCore:$pluginsVersion",
      "AngularJS:$angularVersion",
      "org.jetbrains.plugins.go:$pluginsVersion",
      "org.jetbrains.plugins.ruby:$pluginsVersion",
      "com.jetbrains.php:$pluginsVersion",
      "org.jetbrains.plugins.sass:$sassVersion",
    )
  }
}

kotlin {
  jvmToolchain(17)
}


intellijPlatform {
  pluginConfiguration {
    id = pluginGroup
    name = pluginName
    version = pluginVersion

    ideaVersion {
      sinceBuild = pluginSinceBuild
      untilBuild = pluginUntilBuild
    }

    changeNotes = provider {
      with(changelog) {
        renderItem(
          (getOrNull(pluginVersion) ?: getUnreleased())
            .withHeader(false)
            .withEmptySections(false),
          Changelog.OutputType.HTML,
        )
      }
    }
  }

  publishing {
    token = environment("PUBLISH_TOKEN")
    channels = listOf(pluginVersion.split('-').getOrElse(1) { "default" }.split('.').first())
  }

  signing {
    certificateChain = environment("CERTIFICATE_CHAIN")
    privateKey = environment("PRIVATE_KEY")
    password = environment("PRIVATE_KEY_PASSWORD")
  }

}


changelog {
  groups.empty()
  repositoryUrl.set(properties("pluginRepositoryUrl"))
}

detekt {
  config.setFrom("./detekt-config.yml")
  buildUponDefaultConfig = true
  autoCorrect = true
}


tasks {
  javaVersion.let {
    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
      sourceCompatibility = it
      targetCompatibility = it
    }

    withType<Detekt> {
      jvmTarget = it
      reports.xml.required.set(true)
    }
  }

  wrapper {
    gradleVersion = properties("gradleVersion")
  }

  buildSearchableOptions {
    enabled = false
  }

  register("markdownToHtml") {
    val input = File("./docs/CHANGELOG.md")
    File("./docs/CHANGELOG.html").run {
      writeText(markdownToHTML(input.readText()))
    }
  }
}
