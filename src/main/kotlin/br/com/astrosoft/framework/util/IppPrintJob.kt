package br.com.astrosoft.framework.util

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets

class IppPrintJob {
  @Throws(IOException::class)
  fun printDocument(
    uri: URI, documentInputStream: InputStream
  ): Short {
    val httpURLConnection = uri.toURL().openConnection() as HttpURLConnection
    httpURLConnection.doOutput = true
    httpURLConnection.setRequestProperty("Content-Type", "application/ipp")
    val outputStream = httpURLConnection.outputStream
    val dataOutputStream = DataOutputStream(httpURLConnection.outputStream)
    dataOutputStream.writeShort(0x0101) // ipp version
    dataOutputStream.writeShort(0x0002) // print job operation
    dataOutputStream.writeInt(0x002A) // request id
    dataOutputStream.writeByte(0x01) // operation group tag
    writeAttribute(dataOutputStream, 0x47, "attributes-charset", "utf-8")
    writeAttribute(dataOutputStream, 0x48, "attributes-natural-language", "en")
    writeAttribute(dataOutputStream, 0x45, "printer-uri", uri.toString())
    dataOutputStream.writeByte(0x03) // end tag
    documentInputStream.transferTo(outputStream)
    dataOutputStream.close()
    outputStream.close()
    return if (httpURLConnection.responseCode == 200) {
      val dataInputStream = DataInputStream(httpURLConnection.inputStream)
      println(
        String.format(
          "ipp version %d.%s",
          dataInputStream.readByte(), dataInputStream.readByte()
        )
      )
      dataInputStream.readShort()
    } else {
      throw IOException(
        String.format(
          "post to %s failed with http status %d",
          uri, httpURLConnection.responseCode
        )
      )
    }
  }

  @Throws(IOException::class)
  fun writeAttribute(
    dataOutputStream: DataOutputStream, tag: Int, name: String, value: String
  ) {
    val charset = StandardCharsets.UTF_8
    dataOutputStream.writeByte(tag)
    dataOutputStream.writeShort(name.length)
    dataOutputStream.write(name.toByteArray(charset))
    dataOutputStream.writeShort(value.length)
    dataOutputStream.write(value.toByteArray(charset))
  }
}