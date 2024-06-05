import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "2.0.0-RC3"
  id("io.github.goooler.shadow") version "8.1.8"
  java
  `java-library`
}

repositories {
  mavenCentral()
  mavenLocal()
  maven("https://repo.runderscore.com/releases")
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
  maven("https://oss.sonatype.org/content/groups/public/")
  maven("https://jitpack.io")
  maven("https://repo.codemc.io/repository/maven-snapshots/")
  maven("https://repo.jeff-media.com/public/")
  maven("https://repo.aikar.co/content/groups/aikar/")
  maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
  maven("https://repo.codemc.org/repository/maven-public")
  maven("https://repo.papermc.io/repository/maven-public/")
  maven("https://repo.mrivanplays.com/repository/other-developers/")
  maven("https://repo.mrivanplays.com/repository/maven-all/")
}

dependencies {
  // Spigot
  compileOnly("org.spigotmc:spigot-api:1.20.6-R0.1-SNAPSHOT")

  // Utilities
  compileOnly("org.apache.commons:commons-compress:1.26.2")
  compileOnly("commons-io:commons-io:2.16.1")
  compileOnly("org.apache.commons:commons-collections4:4.5.0-M1")
  implementation("com.google.code.gson:gson:2.11.0")
  implementation("org.reflections:reflections:0.10.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")

  // Own libraries
  implementation("com.roughlyunderscore:ULib:1.0") // It's provided by UEAPI but IntelliJ refuses to download the sources/javadocs for it if I don't specify it here
  implementation("com.roughlyunderscore:delayed-armor-equip-event:1.0.2-delayed")
  implementation("com.roughlyunderscore:UnderscoreEnchantsAPI:2.2.0")

  // Databases
  compileOnly("com.mysql:mysql-connector-j:8.4.0")
  compileOnly("com.zaxxer:HikariCP:5.1.0")
  compileOnly("org.mongodb:mongodb-driver-sync:5.1.0")

  // Economy
  compileOnly("me.lokka30:treasury-api:2.0.1")
  compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

  // Networking
  implementation("com.squareup.retrofit2:retrofit:2.11.0")
  implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")

  // Math
  compileOnly("org.mariuszgromada.math:MathParser.org-mXparser:6.0.0")

  // Miscellaneous Spigot-related libraries
  implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
  compileOnly("me.clip:placeholderapi:2.11.6")
  compileOnly("com.github.cryptomorin:XSeries:11.0.0")
  implementation("dev.triumphteam:triumph-gui:3.1.10")
  implementation("com.jeff_media:SpigotUpdateChecker:3.0.3")
  implementation("org.bstats:bstats-bukkit:3.0.2")
}

group = "com.roughlyunderscore"
version = "2.2"
description = "UnderscoreEnchants"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks {
  build {
    dependsOn(shadowJar)
  }
}

tasks.jar {
  enabled = false
}

tasks.withType<KotlinCompile> {
  compilerOptions {
    freeCompilerArgs.addAll(listOf(
      "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
      "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi"
    ))
  }
}

tasks {
  named<ShadowJar>("shadowJar") {
    relocate("com.jeff_media.updatechecker", "com.roughlyunderscore.libs.updater")
    relocate("co.aikar.commands", "com.roughlyunderscore.libs.acf.commands")
    relocate("co.aikar.locales", "com.roughlyunderscore.libs.acf.locales")
    relocate("com.jeff_media.armorequipevent", "com.roughlyunderscore.libs.armorevent")
    relocate("dev.triumphteam.gui", "com.roughlyunderscore.libs.triumphgui")
    relocate("org.bstats", "com.roughlyunderscore.libs.bstats")
    relocate("com.google.gson", "com.roughlyunderscore.libs.gson")
    relocate("com.roughlyunderscore.ulib", "com.roughlyunderscore.libs.ulib")
  }
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
  options.encoding = "UTF-8"
}
