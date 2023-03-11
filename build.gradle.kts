@file:Suppress("SpellCheckingInspection", "HardCodedStringLiteral")

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
  // Java support
  id("java")
  // Kotlin support
  id("org.jetbrains.kotlin.jvm") version "1.8.10"
  // Gradle IntelliJ Plugin
  id("org.jetbrains.intellij") version "1.13.2"
  // Gradle Changelog Plugin
  id("org.jetbrains.changelog") version "2.0.0"
  // Gradle Qodana Plugin
  id("org.jetbrains.qodana") version "0.1.13"
  // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
  id("io.gitlab.arturbosch.detekt") version "1.22.0"
  // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
  id("org.jlleitschuh.gradle.ktlint") version "11.1.0"
  // Gradle Kover Plugin
  id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

val pluginsVersion: String = properties("pluginsVersion").get()
val sassVersion: String = properties("sassVersion").get()

// Configure project's dependencies
repositories {
  mavenCentral()
  maven(url = "https://maven-central.storage-download.googleapis.com/repos/central/data/")
  maven(url = "https://repo.eclipse.org/content/groups/releases/")
  maven(url = "https://www.jetbrains.com/intellij-repository/releases")
  maven(url = "https://www.jetbrains.com/intellij-repository/snapshots")
}


dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
}

// Configure Gradle IntelliJ Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  pluginName.set(properties("pluginName"))
  version.set(properties("platformVersion"))
  type.set(properties("platformType"))
  downloadSources.set(true)
  instrumentCode.set(true)
  updateSinceUntilBuild.set(true)
  // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
  plugins.set(listOf(
    "com.intellij.settingsSync",
    "com.intellij.java",
    "training",
    "org.jetbrains.kotlin",
    // javascript
    "JavaScript",
    "Pythonid:$pluginsVersion",
    "org.jetbrains.plugins.ruby:$pluginsVersion",
    "org.jetbrains.plugins.go:$pluginsVersion",
    "com.jetbrains.php:$pluginsVersion",
    "org.jetbrains.plugins.sass:$sassVersion",
//      "com.mallowigi:41.1.0",
//      "com.intellij.ja:221.179",
    "XPathView",
  ))
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
  groups.empty()
  repositoryUrl.set(properties("pluginRepositoryUrl"))
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
  config = files("./detekt-config.yml")
  buildUponDefaultConfig = true
  autoCorrect = true
}

// Configure Gradle Qodana Plugin - read more: https://github.com/JetBrains/gradle-qodana-plugin
qodana {
  cachePath.set(provider { file(".qodana").canonicalPath })
  reportPath.set(provider { file("build/reports/inspections").canonicalPath })
  saveReport.set(true)
  showReport.set(environment("QODANA_SHOW_REPORT").map { it.toBoolean() }.getOrElse(false))
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover.xmlReport {
  onCheck.set(true)
}

tasks {
  properties("javaVersion").get().let {
    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
      sourceCompatibility = it
      targetCompatibility = it
    }
    withType<KotlinCompile> {
      kotlinOptions.jvmTarget = it
      kotlinOptions.freeCompilerArgs += listOf("-Xskip-prerelease-check", "-Xjvm-default=all")
    }
  }

  wrapper {
    gradleVersion = properties("gradleVersion").get()
  }


  withType<Detekt> {
    jvmTarget = properties("javaVersion").get()
    reports.xml.required.set(true)
  }

  withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
  }


  sourceSets {
    main {
      java.srcDirs("src/main/kotlin")
      resources.srcDirs("src/main/resources")
    }
  }



  patchPluginXml {
    version.set(properties("pluginVersion"))
    sinceBuild.set(properties("pluginSinceBuild"))
    untilBuild.set(properties("pluginUntilBuild"))

    // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
    pluginDescription.set(providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
      val start = "<!-- Plugin description -->"
      val end = "<!-- Plugin description end -->"

      with(it.lines()) {
        if (!containsAll(listOf(start, end))) {
          throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
        }
        subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
      }
    })

    val changelog = project.changelog // local variable for configuration cache compatibility
    // Get the latest available change notes from the changelog file
    changeNotes.set(properties("pluginVersion").map { pluginVersion ->
      with(changelog) {
        renderItem(
          (getOrNull(pluginVersion) ?: getUnreleased())
            .withHeader(false)
            .withEmptySections(false),
          Changelog.OutputType.HTML,
        )
      }
    })
  }

  // Configure UI tests plugin
  // Read more: https://github.com/JetBrains/intellij-ui-test-robot
  runIdeForUiTests {
    systemProperty("robot-server.port", "8082")
    systemProperty("ide.mac.message.dialogs.as.sheets", "false")
    systemProperty("jb.privacy.policy.text", "<!--999.999-->")
    systemProperty("jb.consents.confirmation.enabled", "false")
  }

  signPlugin {
    certificateChain.set(environment("CERTIFICATE_CHAIN"))
    privateKey.set(environment("PRIVATE_KEY"))
    password.set(environment("PRIVATE_KEY_PASSWORD"))
  }

  buildSearchableOptions {
    enabled = false
  }

  publishPlugin {
    dependsOn("patchChangelog")
    token.set(environment("PUBLISH_TOKEN"))
    // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
    // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
    // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
    channels.set(properties("pluginVersion")
      .map { listOf(it.split('-').getOrElse(1) { "default" }.split('.').first()) })
  }
}
