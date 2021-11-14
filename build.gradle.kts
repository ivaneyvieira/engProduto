import Build_gradle.Defs.kotlin_version
import Build_gradle.Defs.vaadin10_version
import Build_gradle.Defs.vaadinonkotlin_version
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object Defs {
  const val vaadinonkotlin_version = "1.1.1"
  const val vaadin10_version = "14.7.4"
  const val kotlin_version = "1.5.31"
  const val vaadin_plugin = "0.14.6.0"
}

plugins {
  kotlin("jvm") version "1.5.31"
  id("org.gretty") version "3.0.6"
  war
  id("com.vaadin") version "0.14.7.3"
  id("com.google.cloud.tools.jib") version "3.0.0"
}

defaultTasks("clean", "build")

repositories {
  mavenCentral()
  maven { setUrl("https://maven.vaadin.com/vaadin-addons") }
}

gretty {
  contextPath = "/"
  servletContainer = "jetty9.4"
}

val staging: Configuration by configurations.creating

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

group = "produto"
version = "1.0"

java.sourceCompatibility = JavaVersion.VERSION_1_8

dependencies {
  // Karibu-DSL dependency
  implementation("com.github.mvysny.karibudsl:karibu-dsl:$vaadinonkotlin_version")
  implementation("com.github.mvysny.karibu-tools:karibu-tools:0.7")

  // Vaadin 14
  implementation("com.vaadin:vaadin-core:${vaadin10_version}") {
    // Webjars are only needed when running in Vaadin 13 compatibility mode
    listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
           "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
           "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
      .forEach { exclude(group = it) }
  }
  providedCompile("javax.servlet:javax.servlet-api:3.1.0")

  // logging
  implementation("ch.qos.logback:logback-classic:1.2.3")
  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("org.sql2o:sql2o:1.6.0")
  implementation("org.simpleflatmapper:sfm-sql2o:8.2.3")
  implementation("mysql:mysql-connector-java:5.1.48")
  implementation("net.sourceforge.jtds:jtds:1.3.1")
  implementation("org.imgscalr:imgscalr-lib:4.2")
  implementation("com.jcraft:jsch:0.1.55")
  implementation("org.cups4j:cups4j:0.7.8")
  // https://mvnrepository.com/artifact/org.jsoup/jsoup
  implementation("org.jsoup:jsoup:1.13.1")
  
  // logging
  implementation("org.vaadin.tatu:twincolselect:1.2.0")
  implementation("org.vaadin.gatanaso:multiselect-combo-box-flow:1.1.0")
  implementation("org.vaadin.tabs:paged-tabs:2.0.1")
  implementation("org.claspina:confirm-dialog:2.0.0")

  implementation("org.vaadin.crudui:crudui:4.1.0")
  implementation("org.vaadin.stefan:lazy-download-button:1.0.0")
  //implementation("com.github.nwillc:poink:0.4.6")
  implementation("com.flowingcode.addons:font-awesome-iron-iconset:2.1.2")
  implementation("org.vaadin.haijian:exporter:3.0.1")
  implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.2")
  implementation("com.beust:klaxon:5.5")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.12.3")
  implementation("com.github.wmixvideo:nfe:3.0.58")

  //implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")

  implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
  
  implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
  // https://mvnrepository.com/artifact/net.sourceforge.dynamicreports/dynamicreports-core
  implementation("net.sourceforge.dynamicreports:dynamicreports-core:6.12.1") {
    exclude(group = "com.lowagie", module = "itext")
  }
  // https://mvnrepository.com/artifact/net.sf.jasperreports/jasperreports-fonts
  implementation("net.sf.jasperreports:jasperreports:6.17.0")
  implementation("net.sf.jasperreports:jasperreports-fonts:6.17.0")
  implementation("de.f0rce.signaturepad:signature-widget:2.0.0")
  
  implementation("com.lowagie:itext:2.1.7")
  implementation("javax.xml.bind:jaxb-api:2.3.1")
  implementation("com.sun.mail:javax.mail:1.6.2")
  implementation("com.sun.mail:gimap:1.6.2")
}

vaadin {
  pnpmEnable = false
  productionMode = false
}

jib {
  from {
    image = "jetty:9.4.40-jre11"
  }
  container {
    appRoot = "/var/lib/jetty/webapps/ROOT"
    user = "root" // otherwise we'll get https://github.com/appropriate/docker-jetty/issues/80
  }
}