plugins {
  kotlin("jvm") version "1.6.10"
  id("org.gretty") version "3.0.6"
  war
  id("com.vaadin") version "22.0.2"
  id("org.graalvm.buildtools.native") version "0.9.4"
}

val karibudslVersion = "1.1.1"
val vaadinVersion = "22.0.2"

defaultTasks("clean", "build")

repositories {
  mavenCentral()
  maven { setUrl("https://maven.vaadin.com/vaadin-addons") }
  maven { setUrl("https://maven.vaadin.com/vaadin-prereleases") }
  gradlePluginPortal()
}

gretty {
  contextPath = "/"
  servletContainer = "jetty9.4"
}

dependencies { // Karibu-DSL dependency
  implementation("com.github.mvysny.karibudsl:karibu-dsl:$karibudslVersion")
  implementation("com.github.mvysny.karibu-tools:karibu-tools:0.8")

  // Vaadin
  implementation("com.vaadin:vaadin-core:${vaadinVersion}")
  providedCompile("javax.servlet:javax.servlet-api:3.1.0")

  // logging
  // currently we are logging through the SLF4J API to SLF4J-Simple. See src/main/resources/simplelogger.properties file for the logger configuration
  implementation("org.slf4j:slf4j-simple:1.7.32")

  implementation(kotlin("stdlib-jdk8"))

  // logging
  implementation("ch.qos.logback:logback-classic:1.2.9")
  implementation("org.slf4j:slf4j-api:1.7.32")
  implementation("org.sql2o:sql2o:1.6.0")
  implementation("org.simpleflatmapper:sfm-sql2o:8.2.3")
  implementation("mysql:mysql-connector-java:5.1.48")
  implementation("net.sourceforge.jtds:jtds:1.3.1")
  implementation("org.imgscalr:imgscalr-lib:4.2")
  implementation("com.jcraft:jsch:0.1.55")
  implementation("org.cups4j:cups4j:0.7.8") // https://mvnrepository.com/artifact/org.jsoup/jsoup
  implementation("org.jsoup:jsoup:1.14.3")

  // logging
  implementation("org.vaadin.tatu:twincolselect:1.2.0")
  implementation("org.vaadin.gatanaso:multiselect-combo-box-flow:1.1.0")
  implementation("org.vaadin.tabs:paged-tabs:2.0.1")
  implementation("org.claspina:confirm-dialog:2.0.0")

  implementation("org.vaadin.crudui:crudui:5.0.0")
  implementation("org.vaadin.stefan:lazy-download-button:1.0.0") //implementation("com.github.nwillc:poink:0.4.6")
  implementation("com.flowingcode.addons:font-awesome-iron-iconset:2.1.2")
  implementation("org.vaadin.haijian:exporter:3.0.1")
  implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.2")
  implementation("com.beust:klaxon:5.5")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.0")
  implementation("com.github.wmixvideo:nfe:3.0.67")

  //implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")

  //implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")

  implementation("org.jetbrains.kotlin:kotlin-reflect") // https://mvnrepository.com/artifact/net.sourceforge.dynamicreports/dynamicreports-core
  implementation("net.sourceforge.dynamicreports:dynamicreports-core:6.12.1") {
    exclude(group = "com.lowagie", module = "itext")
  } // https://mvnrepository.com/artifact/net.sf.jasperreports/jasperreports-fonts
  implementation("net.sf.jasperreports:jasperreports:6.18.1")
  implementation("net.sf.jasperreports:jasperreports-fonts:6.18.1")
  implementation("de.f0rce.signaturepad:signature-widget:2.0.0")

  implementation("com.lowagie:itext:4.2.2")
  implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
  implementation("com.sun.mail:javax.mail:1.6.2")
  implementation("com.sun.mail:gimap:2.0.1") // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
  implementation("com.squareup.okhttp3:okhttp:4.9.3")
  implementation("org.vaadin.addons.componentfactory:vcf-pdf-viewer:2.0.0")
}

vaadin {
  pnpmEnable = false
  productionMode = true
}
