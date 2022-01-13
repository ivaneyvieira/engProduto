package br.com.astrosoft.produto.model.zpl

import br.com.astrosoft.framework.util.CupsUtils
import br.com.astrosoft.framework.util.SystemUtils

object EtiquetaChave {
  private fun template(dados: DadosEtiqueta): String {
    val template = "/templatePrint/etiquetaChave.zpl"
    val zpl = SystemUtils.readFile(template)
    return zpl.replace("[titulo]", dados.titulo)
      .replace("[usuario]", dados.usuario)
      .replace("[nota]", dados.nota)
      .replace("[data]", dados.data)
      .replace("[hora]", dados.hora)
      .replace("[local]", dados.local)
  }

  fun print(impresora: String, dados: DadosEtiqueta) {
    CupsUtils.printCups(impresora, template(dados)) {
      println(it)
    }
  }

  fun printPreview(impresora: String, dados: DadosEtiqueta) {
    val zpl = template(dados)
    ZPLPreview.showZPLPreview(zpl){
      print(impresora, dados)
    }
  }
}

data class DadosEtiqueta(
  val titulo: String,
  val usuario: String,
  val nota: String,
  val data: String,
  val hora: String,
  val local: String,
                        )