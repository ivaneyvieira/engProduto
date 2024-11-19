package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format

data class ProdutoNotaEntradaNdd(
  val id: Int,
  val numeroProtocolo: String,
  val codigo: String,
  val codBarra: String,
  val descricao: String,
  val ncm: String,
  val cst: String,
  val cfop: String,
  val un: String,
  val quantidade: Double,
  val valorUnitario: Double,
  val valorTotal: Double,
  val baseICMS: Double,
  val valorIPI: Double,
  val aliqICMS: Double,
  val aliqIPI: Double,
  val valorOutros: Double?,
  val valorFrete: Double?,
) {
  val valorICMS
    get() = baseICMS * aliqICMS / 100

  var pedidoXML: PedidoXML? = null

  val codigoPedido: Int?
    get() {
      return pedidoXML?.codigo
    }

  val refForPedido: String?
    get() {
      return pedidoXML?.refFor
    }

  val barcodePedido: String?
    get() {
      return pedidoXML?.barcode
    }

  val descricaoPedido: String?
    get() {
      return pedidoXML?.descricao
    }

  val gradePedido: String?
    get() {
      return pedidoXML?.grade
    }

  val quantPedido: Int?
    get() {
      return pedidoXML?.quant
    }

  var quantFatPedido: Int?
    get() {
      return pedidoXML?.quantFat
    }
    set(value) {
      pedidoXML?.quantFat = value
    }

  val valorUnitPedido: Double?
    get() {
      return pedidoXML?.valorUnit
    }

  val fatorPedido: Double?
    get() {
      return pedidoXML?.fator
    }

  val quantConvPedido: Double?
    get() {
      val embalagem = fatorPedido ?: return null
      val quant = quantidade
      return (quant * embalagem)
    }

  val valorConvPedido: Double?
    get() {
      val embalagem = fatorPedido ?: return null
      val valorUnit = valorUnitario
      return (valorUnit / embalagem)
    }

  val difRefPedido: Boolean
    get() {
      return this.codigo != this.refForPedido
    }

  val difBarPedido: Boolean
    get() {
      return this.barcodePedido != this.codBarra
    }

  val difQtdPedido: Boolean
    get() {
      return this.quantConvPedido?.toInt() != this.quantFatPedido
    }

  val difValPedido: Boolean
    get() {
      return this.valorConvPedido?.format("0.0000") != this.valorUnitPedido?.format("0.0000")
    }

  val embalagemFatorPedido: Double?
    get() = pedidoXML?.embalagemFator

  val unidadePedido: String?
    get() = pedidoXML?.unidade
}