package br.com.astrosoft.produto.model.zpl

import br.com.astrosoft.framework.util.CupsUtils
import br.com.astrosoft.framework.util.SystemUtils

object EtiquetaChave {
  private fun template(chave: String): String {
    val template = "/templatePrint/etiquetaChave.zpl"
    val zpl = SystemUtils.readFile(template)
    return zpl.replace("[chave]", chave)
  }

  fun print(impresora: String, chave: String) {
    CupsUtils.printCups(impresora, template(chave)) {
      println(it)
    }
  }
}