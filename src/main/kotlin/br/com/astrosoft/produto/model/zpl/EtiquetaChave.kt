package br.com.astrosoft.produto.model.zpl

import br.com.astrosoft.framework.util.CupsUtils
import br.com.astrosoft.framework.util.SystemUtils
import br.com.astrosoft.produto.model.beans.*

object EtiquetaChave {
  private fun template(dados: DadosEtiquetaNota): String {
    val template = "/templatePrint/etiquetaChaveNota.zpl"
    val zpl = SystemUtils.readFile(template)
    return zpl.replace("[titulo]", dados.titulo)
      .replace("[usuario]", dados.usuario)
      .replace("[loja]", dados.loja.toString())
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
      .replace("[loja]", dados.loja.toString())
      .replace("[pedido]", dados.pedido)
      .replace("[data]", dados.data)
      .replace("[hora]", dados.hora)
      .replace("[local]", dados.local)
  }

  private fun template(dados: DadosEtiquetaRessuprimento): String {
    val template = "/templatePrint/etiquetaChaveRessuprimento.zpl"
    val zpl = SystemUtils.readFile(template)
    return zpl.replace("[titulo]", dados.titulo)
      .replace("[usuario]", dados.usuario)
      .replace("[numero]", dados.numero.toString())
      .replace("[data]", dados.data)
      .replace("[hora]", dados.hora)
      .replace("[local]", dados.local)
  }

  @JvmName("printNota")
  private fun print(impressora: String, dados: List<DadosEtiquetaNota>) {
    val zpl = dados.joinToString("\n"){
      template(it)
    }
    CupsUtils.printCups(impressora, zpl) {
      println(it)
    }
  }

  @JvmName("printPreviewNota")
  private fun printPreview(impressora: String, dados: List<DadosEtiquetaNota>) {
    val zpl = dados.joinToString("\n"){
      template(it)
    }
    ZPLPreview.showZPLPreview(impressora, zpl) {
      print(impressora, dados)
    }
  }

  private fun print(impressora: String, dados: List<DadosEtiquetaPedido>) {
    val template = dados.joinToString("\n") { dado -> template(dado) }
    CupsUtils.printCups(impressora, template) {
      println(it)
    }
  }

  @JvmName("printRessuprimento")
  private fun print(impressora: String, dados: List<DadosEtiquetaRessuprimento>) {
    val zpl = dados.joinToString("\n") {
      template(it)
    }
    CupsUtils.printCups(impressora, zpl) {
      println(it)
    }
  }

  private fun printPreview(impressora: String, dados: List<DadosEtiquetaPedido>) {
    val zpl = dados.joinToString("\n") { dado -> template(dado) }
    ZPLPreview.showZPLPreview(impressora, zpl) {
      print(impressora, dados)
    }
  }

  @JvmName("printPreviewRessuprimento")
  private fun printPreview(impressora: String, dados: List<DadosEtiquetaRessuprimento>) {
    val zpl = dados.joinToString("\n") {
      template(it)
    }
    ZPLPreview.showZPLPreview(impressora, zpl) {
      print(impressora, dados)
    }
  }

  fun printPreviewExp(impressora: String, produtos: List<ProdutoNFS>) {
    val dadosEdtiquetas = produtos.map { produto ->
      DadosEtiquetaNota(titulo = "Exp",
                        usuario = produto.usuarioNameExp,
                        loja = produto.loja,
                        nota = produto.nota,
                        data = produto.dataExp,
                        hora = produto.horaExp,
                        local = produto.localizacao ?: "")
    }.distinct()
    printPreview(impressora, dadosEdtiquetas)
  }

  @JvmName("printPreviewEntNota")
  fun printPreviewEnt(impressora: String, produtos: List<ProdutoNFS>) {
    val dadosEtiquetas = produtos.map {produto ->
      DadosEtiquetaNota(titulo = "Entregue",
                        usuario = produto.usuarioNameCD,
                        loja = produto.loja,
                        nota = produto.nota,
                        data = produto.dataCD,
                        hora = produto.horaCD,
                        local = produto.localizacao ?: "")
    }.distinct()
    printPreview(impressora, dadosEtiquetas)
  }

  fun printPreviewEnt(impressora: String, produtos: List<ProdutoPedidoVenda>) {
    val dadosEtiquetas = produtos.map { produto ->
      DadosEtiquetaPedido(titulo = "Entregue",
                          usuario = produto.usuarioNameCD,
                          loja = produto.loja,
                          pedido = produto.ordno.toString(),
                          data = produto.dataCD,
                          hora = produto.horaCD,
                          local = produto.localizacao ?: "")
    }.distinct()
    printPreview(impressora, dadosEtiquetas)
  }

  @JvmName("printPreviewEntRessuprimento")
  fun printPreviewEnt(impressora: String, produtos: List<ProdutoRessuprimento>) {
    val dadosEtiquetas = produtos.map { produto ->
      DadosEtiquetaRessuprimento(titulo = "Entregue",
                                 usuario = produto.usuarioNameCD,
                                 numero = produto.ordno,
                                 data = produto.dataCD,
                                 hora = produto.horaCD,
                                 local = produto.localizacao ?: "")
    }.distinct()
    printPreview(impressora, dadosEtiquetas)
  }

}

private data class DadosEtiquetaNota(
  val titulo: String,
  val usuario: String,
  val loja: Int,
  val nota: String,
  val data: String,
  val hora: String,
  val local: String,
                                    )

private data class DadosEtiquetaPedido(
  val titulo: String,
  val usuario: String,
  val loja: Int,
  val pedido: String,
  val data: String,
  val hora: String,
  val local: String,
                                      )

private data class DadosEtiquetaRessuprimento(
  val titulo: String,
  val usuario: String,
  val numero: Long,
  val data: String,
  val hora: String,
  val local: String,
                                             )