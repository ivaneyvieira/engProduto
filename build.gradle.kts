import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  // fix for https://github.com/mvysny/vaadin-boot-example-gradle/issues/3
  dependencies {
    classpath("com.vaadin:vaadin-prod-bundle:${project.properties["vaadinVersion"]}")
  }
}

plugins {
  kotlin("jvm") version "1.9.20"
  id("application")
  id("com.vaadin")
}

defaultTasks("clean", "build")

repositories {
  mavenLocal()
  mavenCentral()
  jcenter()
  maven {
    url = uri("https://maven.vaadin.com/vaadin-addons")
  }
}


tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "17"
}

dependencies {
  // Vaadin
  implementation("com.github.mvysny.karibudsl:karibu-dsl-v23:2.1.0")
  implementation("com.vaadin:vaadin-core:${properties["vaadinVersion"]}") {
    afterEvaluate {
      if (vaadin.productionMode) {
        exclude(module = "vaadin-dev")
      }
    }
  }
  implementation("com.github.mvysny.vaadin-boot:vaadin-boot:12.1")
  implementation("com.github.mvysny.vaadin-simple-security:vaadin-simple-security:0.2")

  // logging
  // currently we are logging through the SLF4J API to slf4j-simple. See src/main/resources/simplelogger.properties file for the logger configuration
  implementation("org.slf4j:slf4j-simple:2.0.7")

  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("reflect"))

  // db
  implementation("com.zaxxer:HikariCP:3.4.1")
  implementation("org.sql2o:sql2o:1.6.0")
  implementation("org.simpleflatmapper:sfm-sql2o:8.2.3")
  implementation("mysql:mysql-connector-java:5.1.48")
  implementation("net.sourceforge.jtds:jtds:1.3.1")

  //libs
  implementation("org.cups4j:cups4j:0.7.9")
  implementation("com.jcraft:jsch:0.1.55")
  implementation("org.imgscalr:imgscalr-lib:4.2")

  //vaadin
  implementation("pl.unforgiven:superfields:0.18.1")
  implementation("org.vaadin.crudui:crudui:7.1.0")
  implementation("org.vaadin.stefan:lazy-download-button:1.0.0")
  implementation("com.flowingcode.addons:font-awesome-iron-iconset:5.2.2")
  implementation("org.vaadin.stefan:lazy-download-button:1.0.0")

  //Report
  implementation("net.sourceforge.dynamicreports:dynamicreports-core:6.12.1") {
    exclude(group = "com.lowagie", module = "itext")
  } // https://mvnrepository.com/artifact/net.sf.jasperreports/jasperreports-fonts
  implementation("net.sf.jasperreports:jasperreports:6.20.0")
  implementation("net.sf.jasperreports:jasperreports-fonts:6.20.0")
  implementation("com.lowagie:itext:2.1.7")

  //Planilhas
  implementation("com.github.nwillc:poink:0.4.6")
  implementation("io.github.rushuat:ocell:0.1.7")

  //Legado
  implementation("com.github.wmixvideo:nfe:3.0.67")
  implementation("com.squareup.okhttp3:okhttp:4.9.3")
  implementation("org.claspina:confirm-dialog:2.0.0")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

application {
  mainClass.set("br.com.astrosoft.framework.view.layout.MainKt")
}
