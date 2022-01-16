package br.com.astrosoft.produto.model.zpl

import br.com.astrosoft.framework.util.CupsUtils
import br.com.astrosoft.framework.util.SystemUtils

object EtiquetaChave {
  private fun template(dados: DadosEtiquetaNota): String {
    val template = "/templatePrint/etiquetaChaveNota.zpl"
    val zpl = SystemUtils.readFile(template)
    return zpl.replace("[titulo]", dados.titulo)
      .replace("[usuario]", dados.usuario)
      .replace("[nota]", dados.nota)
      .replace("[data]", dados.data)
      .replace("[hora]", dados.hora)
      .replace("[local]", dados.local)
  }

  private fun template(dados: DadosEtiquetaPedido): String {
    val template = "/templatePrint/etiquetaChavePedido.zpl"
    val zpl = SystemUtils.readFile(template)
    return zpl.replace("[titulo]", dados.titulo)
      .replace("[usuario]", dados.usuario)
      .replace("[pedido]", dados.pedido)
      .replace("[data]", dados.data)
      .replace("[hora]", dados.hora)
      .replace("[local]", dados.local)
  }

  fun print(impresora: String, dados: DadosEtiquetaNota) {
    CupsUtils.printCups(impresora, template(dados)) {
      println(it)
    }
  }

  fun printPreview(impresora: String, dados: DadosEtiquetaNota) {
    val zpl = template(dados)
    ZPLPreview.showZPLPreview(impresora, zpl) {
      print(impresora, dados)
    }
  }

  fun print(impresora: String, dados: DadosEtiquetaPedido) {
    CupsUtils.printCups(impresora, template(dados)) {
      println(it)
    }
  }

  fun printPreview(impresora: String, dados: DadosEtiquetaPedido) {
    val zpl = template(dados)
    ZPLPreview.showZPLPreview(impresora, zpl) {
      print(impresora, dados)
    }
  }
}

data class DadosEtiquetaNota(
  val titulo: String,
  val usuario: String,
  val nota: String,
  val data: String,
  val hora: String,
  val local: String,
                        )


data class DadosEtiquetaPedido(
  val titulo: String,
  val usuario: String,
  val pedido: String,
  val data: String,
  val hora: String,
  val local: String,
                            )