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

  private fun print(impressora: String, dados: DadosEtiquetaNota) {
    CupsUtils.printCups(impressora, template(dados)) {
      println(it)
    }
  }

  private fun printPreview(impressora: String, dados: DadosEtiquetaNota) {
    val zpl = template(dados)
    ZPLPreview.showZPLPreview(impressora, zpl) {
      print(impressora, dados)
    }
  }

  private fun print(impressora: String, dados: DadosEtiquetaPedido) {
    CupsUtils.printCups(impressora, template(dados)) {
      println(it)
    }
  }

  private fun printPreview(impressora: String, dados: DadosEtiquetaPedido) {
    val zpl = template(dados)
    ZPLPreview.showZPLPreview(impressora, zpl) {
      print(impressora, dados)
    }
  }

  fun printPreviewEnt(impressora: String, produto: ProdutoNF) {
    printPreview(impressora,
                 DadosEtiquetaNota(titulo = "Entregue",
                                   usuario = produto.usuarioNameCD,
                                   loja = produto.loja,
                                   nota = produto.nota,
                                   data = produto.dataCD,
                                   hora = produto.horaCD,
                                   local = produto.localizacao ?: ""))
  }

  fun printPreviewEnt(impressora: String, nota: NotaSaida) {
    printPreview(impressora,
                 DadosEtiquetaNota(titulo = "Entregue",
                                   usuario = nota.usuarioNameCD,
                                   loja = nota.loja,
                                   nota = nota.nota,
                                   data = nota.dataCD,
                                   hora = nota.horaCD,
                                   local = nota.localizacao ?: ""))
  }

  fun printPreviewEnt(impressora: String, produto: ProdutoPedidoVenda) {
    printPreview(impressora,
                 DadosEtiquetaPedido(titulo = "Entregue",
                                     usuario = produto.usuarioNameCD,
                                     loja = produto.loja,
                                     pedido = produto.ordno.toString(),
                                     data = produto.dataCD,
                                     hora = produto.horaCD,
                                     local = produto.localizacao ?: ""))
  }

  fun printPreviewEnt(impressora: String, pedido: PedidoVenda) {
    printPreview(impressora,
      DadosEtiquetaPedido(titulo = "Entregue",
        usuario = pedido.usuarioNameCD,
        loja = pedido.loja,
        pedido = pedido.ordno.toString(),
        data = pedido.dataCD,
        hora = pedido.horaCD,
        local = pedido.localizacao ?: ""))
  }

  fun printPreviewEnt(impressora: String, ressuprimento: Ressuprimento) {
    printPreview(impressora,
      DadosEtiquetaPedido(titulo = "Entregue",
        usuario = pedido.usuarioNameCD,
        loja = pedido.loja,
        pedido = pedido.ordno.toString(),
        data = pedido.dataCD,
        hora = pedido.horaCD,
        local = pedido.localizacao ?: ""))
  }

  fun printPreviewEnt(impressora: String, produto: ProdutoRessuprimento) {
    printPreview(impressora,
      DadosEtiquetaPedido(titulo = "Entregue",
        usuario = pedido.usuarioNameCD,
        loja = pedido.loja,
        pedido = pedido.ordno.toString(),
        data = pedido.dataCD,
        hora = pedido.horaCD,
        local = pedido.localizacao ?: ""))
  }

  fun printPreviewExp(impressora: String, nota: NotaSaida) {
    printPreview(impressora,
                 DadosEtiquetaNota(titulo = "Exp",
                                   usuario = nota.usuarioNameExp,
                                   loja = nota.loja,
                                   nota = nota.nota,
                                   data = nota.dataExp,
                                   hora = nota.horaExp,
                                   local = nota.localizacao ?: ""))
  }

  fun printExp(impressora: String, produto: ProdutoNF) {
    print(impressora,
          DadosEtiquetaNota(titulo = "Exp",
                            usuario = produto.usuarioNameExp,
                            loja = produto.loja,
                            nota = produto.nota,
                            data = produto.dataExp,
                            hora = produto.horaExp,
                            local = produto.localizacao ?: ""))
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