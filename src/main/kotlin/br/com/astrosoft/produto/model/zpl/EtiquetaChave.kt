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
  private fun print(impressora: String, dados: List<DadosEtiquetaNota>, copias: Int) {
    val zpl = dados.joinToString("\n") {
      template(it)
    }
    val zplNvezes = zpl.repeat(copias)
    CupsUtils.printCups(impressora, zplNvezes)
  }

  private fun print(impressora: String, dados: List<DadosEtiquetaPedido>, copias: Int) {
    val template = dados.joinToString("\n") { dado -> template(dado) }
    val zplNvezes = template.repeat(copias)
    CupsUtils.printCups(impressora, zplNvezes)
  }

  @JvmName("printRessuprimento")
  private fun print(impressora: String, dados: List<DadosEtiquetaRessuprimento>, copias: Int) {
    val zpl = dados.joinToString("\n") {
      template(it)
    }
    val zplNvezes = zpl.repeat(copias)
    CupsUtils.printCups(impressora, zplNvezes)
  }

  @JvmName("printPreviewNota")
  private fun printPreview(impressoras: Set<String>, dados: List<DadosEtiquetaNota>, copias: Int) {
    val zpl = dados.joinToString("\n") {
      template(it)
    }
    val zplNvezes = zpl.repeat(copias)
    ZPLPreview.showZPLPreview(impressoras, zplNvezes) {
      it.distinct().forEach { impressora ->
        print(impressora, dados, copias)
      }
    }
  }

  private fun printPreview(impressoras: Set<String>, dados: List<DadosEtiquetaPedido>, copias: Int) {
    val zpl = dados.joinToString("\n") { dado -> template(dado) }
    ZPLPreview.showZPLPreview(impressoras, zpl) {
      it.distinct().forEach { impressora ->
        print(impressora, dados, copias)
      }
    }
  }

  @JvmName("printPreviewRessuprimento")
  private fun printPreview(impressoras: Set<String>, dados: List<DadosEtiquetaRessuprimento>, copias: Int) {
    val zpl = dados.joinToString("\n") {
      template(it)
    }
    ZPLPreview.showZPLPreview(impressoras, zpl) {
      it.distinct().forEach { impressora ->
        print(impressora, dados, copias)
      }
    }
  }

  fun printPreviewExp(impressoras: Set<String>, produtos: List<ProdutoNFS>, copia: Int) {
    val dadosEdtiquetas = produtos.map { produto ->
      DadosEtiquetaNota(
        titulo = "Exp",
        usuario = produto.usuarioExp ?: "",
        loja = produto.loja,
        nota = produto.nota ?: "",
        data = produto.dataExp,
        hora = produto.horaExp,
        local = produto.local ?: ""
      )
    }.distinct()
    printPreview(impressoras, dadosEdtiquetas, copia)
  }

  @JvmName("printPreviewEntNota")
  fun printPreviewEnt(impressoras: Set<String>, produtos: List<ProdutoNFS>, copias: Int) {
    val dadosEtiquetas = produtos.map { produto ->
      DadosEtiquetaNota(
        titulo = "Entregue",
        usuario = produto.usuarioCD ?: "",
        loja = produto.loja,
        nota = produto.nota ?: "",
        data = produto.dataCD,
        hora = produto.horaCD,
        local = produto.local ?: ""
      )
    }.distinct()
    printPreview(impressoras, dadosEtiquetas, copias)
  }

  @JvmName("printPreviewEntVenda")
  fun printPreviewEnt(impressoras: Set<String>, produtos: List<ProdutoPedidoVenda>, copias: Int) {
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
    printPreview(impressoras, dadosEtiquetas, copias)
  }

  @JvmName("printPreviewEntTransf")
  fun printPreviewEnt(impressoras: Set<String>, produtos: List<ProdutoPedidoTransf>, copias: Int) {
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
    printPreview(impressoras, dadosEtiquetas, copias)
  }

  @JvmName("printPreviewEntRessuprimento")
  fun printPreviewEnt(impressoras: Set<String>, produtos: List<ProdutoRessuprimento>, copias: Int) {
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
    printPreview(impressoras, dadosEtiquetas, copias)
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
  val numero: Int,
  val data: String,
  val hora: String,
  val local: String,
)