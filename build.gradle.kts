import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val vaadinVersion: String by extra
val karibuDslVersion: String by extra
val vaadinBootVersion: String by extra
val vaadinSimpleSecurityVersion: String by extra
val slf4jVersion: String by extra

plugins {
  kotlin("jvm") version "2.2.0"
  application
  id("com.vaadin") version "24.6.2"
}

defaultTasks("clean", "build")

repositories {
  mavenLocal()
  mavenCentral()
  //jcenter()
  maven {
    url = uri("https://maven.vaadin.com/vaadin-addons")
  }
  maven {
    url = uri("https://jaspersoft.jfrog.io/jaspersoft/jaspersoft-repo")
  }
  maven { url = uri("https://repo.maven.apache.org/maven2") }
  maven { url = uri("https://repo1.maven.org/maven2") }
}

dependencies {
  // Karibu-DSL dependency
  implementation("com.github.mvysny.karibudsl:karibu-dsl-v23:$karibuDslVersion")

  // Vaadin
  implementation("com.vaadin:vaadin-core:$vaadinVersion") {
    afterEvaluate {
      if (vaadin.productionMode.get()) {
        exclude(module = "vaadin-dev")
      }
    }
  }
  implementation("com.github.mvysny.vaadin-boot:vaadin-boot:$vaadinBootVersion")
  implementation("com.github.mvysny.vaadin-simple-security:vaadin-simple-security:$vaadinSimpleSecurityVersion")

  // logging
  // currently we are logging through the SLF4J API to slf4j-simple. See src/main/resources/simplelogger.properties file for the logger configuration
  implementation("org.slf4j:slf4j-simple:$slf4jVersion")

//  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))

  // db
  implementation("com.zaxxer:HikariCP:3.4.1")
  implementation("org.sql2o:sql2o:1.6.0")
  implementation("org.simpleflatmapper:sfm-sql2o:8.2.3")
  implementation("mysql:mysql-connector-java:5.1.48")
  implementation("net.sourceforge.jtds:jtds:1.3.1")

  //libs
  implementation("org.cups4j:cups4j:0.7.8")
  implementation("com.jcraft:jsch:0.1.55")
  implementation("org.imgscalr:imgscalr-lib:4.2")
  implementation("com.github.wmixvideo:nfe:4.0.73")
  implementation("com.sun.mail:jakarta.mail:2.0.2")

  //vaadin
  implementation("pl.unforgiven:superfields:0.18.1")
  implementation("org.vaadin.crudui:crudui:7.1.0")
  implementation("org.vaadin.stefan:lazy-download-button:1.0.0")
  implementation("com.flowingcode.addons:font-awesome-iron-iconset:5.2.2")
  implementation("org.vaadin.addons.flowingcode:grid-helpers:1.3.0")
  implementation("org.parttio:hugerte-for-flow:1.0.10")

  //Report
  implementation("net.sourceforge.dynamicreports:dynamicreports-core:6.20.1") {
    exclude(group = "org.glassfish.jaxb", module = "jaxb-core")
  }
  implementation("net.sf.jasperreports:jasperreports:6.20.6")
  implementation("net.sf.jasperreports:jasperreports-fonts:6.20.6")
  // implementation("com.lowagie:itext:2.1.7")

  //Planilhas
  implementation("org.apache.poi:poi:5.2.5")
  implementation("org.apache.poi:poi-ooxml:5.2.5")

  //Legado
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation("org.claspina:confirm-dialog:2.0.0")
  //implementation("org.codehaus.woodstox:stax2-api:4.1")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

tasks.withType<KotlinCompile> {
  compilerOptions.jvmTarget = JvmTarget.JVM_17
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

application {
  //mainClass.set("br.com.astrosoft.produto.viewmodel.estoqueCD.ProcessamentoKardecKt")
  mainClass.set("br.com.astrosoft.framework.view.layout.MainKt")
}

tasks.wrapper {
  distributionType = Wrapper.DistributionType.ALL
}


