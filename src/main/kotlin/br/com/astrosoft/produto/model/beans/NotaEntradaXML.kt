package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import br.com.astrosoft.produto.nfeXml.ItensNotaReport
import br.com.astrosoft.produto.nfeXml.ProdutoNotaEntradaVO
import java.time.LocalDate

class NotaEntradaXML {
  var id: Int = 0
  var loja: Int = 0
  var sigla: String = ""
  var numero: Int = 0
  var serie: Int = 0
  var natureza: String? = ""
  var dataEmissao: LocalDate? = null
  var fornecedorNota: Int? = null
  var fornecedorCad: String? = null
  var cnpjEmitente: String = ""
  var nomeFornecedor: String = ""
  var valorTotalProdutos: Double = 0.00
  var valorTotal: Double = 0.00
  var chave: String = ""
  var xmlNfe: String? = null
  var entrada: String? = null
  var preEntrada: String? = null
  var ordno: Int? = null
  var pedidoEdit: Int? = null

  var pedido: Int
    get() {
      return if (pedidoEdit == null) {
        val regXPed = "<xPed>([^<]*)</xPed>".toRegex()
        val pedidoX = regXPed.find(xmlNfe ?: "")?.groups?.get(1)?.value
        val regPed = "${sigla}[^0-9]{0,4}[0-9]{4,15}+".toRegex(RegexOption.IGNORE_CASE)
        val pedido = regPed.find(xmlNfe ?: "")?.value
        val pedidoStr = pedidoX ?: pedido ?: return 0
        val regNumero = "[0-9]+".toRegex()

        regNumero.find(pedidoStr)?.value?.toIntOrNull() ?: ordno ?: 0
      } else {
        pedidoEdit ?: 0
      }
    }
    set(value) {
      pedidoEdit = value
    }

  val notaFiscal
    get() = "$numero/$serie"

  fun itensNotaReport(): List<ItensNotaReport> {
    val nota = NotaEntradaFileXML.find(chave) ?: return emptyList()
    return nota.itensNotaReport()
  }

  fun produtosNdd(): List<ProdutoNotaEntradaNdd> {
    return xmlFile().produtosNotaEntradaNDD()
  }

  fun produtosPedido(): List<PedidoXML> {
    return saci.listPedidoXml(
      loja = loja,
      pedido = pedido,
    )
  }

  fun xmlFile(): ProdutoNotaEntradaVO {
    return ProdutoNotaEntradaVO(
      id = id,
      xmlNfe = xmlNfe ?: "",
      numeroProtocolo = "",
      dataHoraRecebimento = "",
    )
  }

  fun save() {
    saci.saveNFEntrada(this)
  }

  fun consultaNfeFile(): ConsultaNfeFile? {
    return ConsultaNfeFile(this)
  }

  fun processaEntrada() {
    val consulta = ConsultaNfeFile(this)
    var parameters = consulta.inv2Parameters
    saci.processaEntrada(parameters)
    val itensParam = consulta.iprd2Parameters(parameters)
    itensParam.forEach { param ->
      saci.processaItensEntrada(param)
    }
  }

  companion object {
    fun findAll(filter: FiltroNotaEntradaXML) = saci.listNFEntrada(filter)
  }
}

data class FiltroNotaEntradaXML(
  val loja: Int,
  val dataInicial: LocalDate,
  val dataFinal: LocalDate,
  val numero: Int,
  val cnpj: String,
  val fornecedor: String,
  val preEntrada: EEntradaXML,
  val entrada: EEntradaXML,
  val query: String,
  val pedido: Int,
)

enum class EEntradaXML(val codigo: String, val descricao: String) {
  TODOS("T", "Todos"),
  SIM("S", "Sim"),
  NAO("N", "Não")
}