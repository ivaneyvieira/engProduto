package br.com.astrosoft.framework.util

import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.net.URI

object CupsUtils {
  private const  val HOST = "172.20.47.2"
  fun printCups(impressora: String, text: String) {
    val printerURI = URI.create("http://$HOST:631/ipp/$impressora")
    val status = IppPrintJob()
      .printDocument(printerURI, ByteArrayInputStream(text.toByteArray()))
    println(String.format("ipp status: %04X", status))
  }

  private val etiqueta = """
    |^XA
    |^FT20,070^A0N,70,50^FH^FDNF ENTRADA:1212^FS
    |^FT600,070^A0N,70,50^FH^FD30/06/18^FS
    |^FT20,140^A0N,70,50^FH^FDPRODUTO:000019^FS
    |^FT400,140^A0N,70,50^FH^FD - ^FS
    |^FT20,210^A0N,70,50^FH^FDTGR  SD ADA CT  20X012^FS
    |^FT20,280^A0N,70,50^FH^FDPALLET COM: 5CXS^FS
    |^FT20,350^A0N,70,50^FH^FDENTRADA: 1/5 PALLET^FS
    |^FT20,420^A0N,70,50^FH^FDESTOQUE: 1/5PALLET^FS
    |^FT220,650^A0N,250,300^FH^FD1^FS
    |^FO220,700^BY1^BCN,50,Y,N,N^FD000019  5 1/5^FS
    |^XZ""".trimMargin()
}


