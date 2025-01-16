package br.com.astrosoft.framework.model.printText

object PrinterToHtml {
  private val FONT_SIZE = 3
  fun toHtml(text: String): String {
    val html =
      text


    val htmlFormat = html.lines().joinToString(separator = "<br>") { linha ->
      "<code>$linha</code>"
    }

    return htmlFormat
  }


}