package br.com.astrosoft.framework.util

import java.io.ByteArrayInputStream
import java.net.URI

object PrintJava {
  private const  val HOST = "172.20.47.1"
  private fun printCups(impressora: String, text: String) {
    val printerURI = URI.create("http://$HOST:631/ipp/$impressora")
    val status = IppPrintJob()
      .printDocument(printerURI, ByteArrayInputStream(text.toByteArray()))
    println(String.format("ipp status: %04X", status))
  }
}


