package br.com.astrosoft.produto.model.zpl

import br.com.astrosoft.framework.util.CupsUtils
import br.com.astrosoft.framework.util.SystemUtils
import br.com.astrosoft.produto.model.beans.ProdutoNFS
import br.com.astrosoft.produto.model.beans.ProdutoPedidoTransf
import br.com.astrosoft.produto.model.beans.ProdutoPedidoVenda
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento

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
    val zpl = dados.joinToString("\n") {
      template(it)
    }
    CupsUtils.printCups(impressora, zpl)
  }

  private fun print(impressora: String, dados: List<DadosEtiquetaPedido>) {
    val template = dados.joinToString("\n") { dado -> template(dado) }
    CupsUtils.printCups(impressora, template)
  }

  @JvmName("printRessuprimento")
  private fun print(impressora: String, dados: List<DadosEtiquetaRessuprimento>) {
    val zpl = dados.joinToString("\n") {
      template(it)
    }
    CupsUtils.printCups(impressora, zpl)
  }

  @JvmName("printPreviewNota")
  private fun printPreview(impressoras: Set<String>, dados: List<DadosEtiquetaNota>) {
    val zpl = dados.joinToString("\n") {
      template(it)
    }
    ZPLPreview.showZPLPreview(impressoras, zpl) {
      impressoras.forEach { impressora ->
        print(impressora, dados)
      }
    }
  }

  private fun printPreview(impressoras: Set<String>, dados: List<DadosEtiquetaPedido>) {
    val zpl = dados.joinToString("\n") { dado -> template(dado) }
    ZPLPreview.showZPLPreview(impressoras, zpl) {
      impressoras.forEach { impressora ->
        print(impressora, dados)
      }
    }
  }

  @JvmName("printPreviewRessuprimento")
  private fun printPreview(impressoras: Set<String>, dados: List<DadosEtiquetaRessuprimento>) {
    val zpl = dados.joinToString("\n") {
      template(it)
    }
    ZPLPreview.showZPLPreview(impressoras, zpl) {
      impressoras.forEach { impressora ->
        print(impressora, dados)
      }
    }
  }

  fun printPreviewExp(impressoras: Set<String>, produtos: List<ProdutoNFS>) {
    val dadosEdtiquetas = produtos.map { produto ->
      DadosEtiquetaNota(
        titulo = "Exp",
        usuario = produto.usuarioNameExp,
        loja = produto.loja,
        nota = produto.nota,
        data = produto.dataExp,
        hora = produto.horaExp,
        local = produto.local
      )
    }.distinct()
    printPreview(impressoras, dadosEdtiquetas)
  }

  @JvmName("printPreviewEntNota")
  fun printPreviewEnt(impressoras: Set<String>, produtos: List<ProdutoNFS>) {
    val dadosEtiquetas = produtos.map { produto ->
      DadosEtiquetaNota(
        titulo = "Entregue",
        usuario = produto.usuarioNameCD,
        loja = produto.loja,
        nota = produto.nota,
        data = produto.dataCD,
        hora = produto.horaCD,
        local = produto.local
      )
    }.distinct()
    printPreview(impressoras, dadosEtiquetas)
  }

  @JvmName("printPreviewEntVenda")
  fun printPreviewEnt(impressoras: Set<String>, produtos: List<ProdutoPedidoVenda>) {
    val dadosEtiquetas = produtos.map { produto ->
      DadosEtiquetaPedido(
        titulo = "Entregue",
        usuario = produto.usuarioNameCD,
        loja = produto.loja,
        pedido = produto.ordno.toString(),
        data = produto.dataCD,
        hora = produto.horaCD,
        local = produto.localizacao ?: ""
      )
    }.distinct()
    printPreview(impressoras, dadosEtiquetas)
  }

  @JvmName("printPreviewEntTransf")
  fun printPreviewEnt(impressoras: Set<String>, produtos: List<ProdutoPedidoTransf>) {
    val dadosEtiquetas = produtos.map { produto ->
      DadosEtiquetaPedido(
        titulo = "Entregue",
        usuario = produto.usuarioNameCD,
        loja = produto.loja,
        pedido = produto.ordno.toString(),
        data = produto.dataCD,
        hora = produto.horaCD,
        local = produto.localizacao ?: ""
      )
    }.distinct()
    printPreview(impressoras, dadosEtiquetas)
  }

  @JvmName("printPreviewEntRessuprimento")
  fun printPreviewEnt(impressoras: Set<String>, produtos: List<ProdutoRessuprimento>) {
    val dadosEtiquetas = produtos.map { produto ->
      DadosEtiquetaRessuprimento(
        titulo = "Entregue",
        usuario = "",
        numero = produto.ordno ?: 0,
        data = "",
        hora = "",
        local = produto.localizacao ?: ""
      )
    }.distinct()
    printPreview(impressoras, dadosEtiquetas)
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