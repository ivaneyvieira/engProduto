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
  var dataEmissao: LocalDate? = null
  var fornecedorNota: Int? = null
  var fornecedorCad: String? = null
  var cnpjEmitente: String = ""
  var nomeFornecedor: String = ""
  var valorTotalProdutos: Double = 0.00
  var valorTotal: Double = 0.00
  var chave: String = ""
  var xmlNfe: String? = null
  var preEntrada: String? = null


  val pedido: Int
    get(){
      val regXPed = "<xPed>([^<]*)</xPed>".toRegex()
      val pedidoX = regXPed.find(xmlNfe ?: "")?.groups?.get(1)?.value
      val regPed = "${sigla}[^0-9]{0,4}[0-9]{4,15}+".toRegex(RegexOption.IGNORE_CASE)
      val pedido = regPed.find(xmlNfe ?: "")?.value
      val pedidoStr = pedidoX ?: pedido ?: return 0
      val regNumero = "[0-9]+".toRegex()

      return regNumero.find(pedidoStr)?.value?.toIntOrNull() ?: 0
    }
  val dataPedido: LocalDate?
    get() = null

  val notaFiscal
    get() = "$numero/$serie"

  fun itensNotaReport(): List<ItensNotaReport> {
    val nota = NotaEntradaFileXML.find(chave) ?: return emptyList()
    return nota.itensNotaReport()
  }

  fun produtosNdd(): List<ProdutoNotaEntradaNdd> {
    return xmlFile().produtosNotaEntradaNDD()
  }

  fun xmlFile(): ProdutoNotaEntradaVO {
    return ProdutoNotaEntradaVO(
      id = id,
      xmlNfe = xmlNfe ?: "",
      numeroProtocolo = "",
      dataHoraRecebimento = "",
    )
  }

  companion object {
    fun findAll(filter: FiltroNotaEntradaXML) = saci.listNFEntrada(filter).toList()
  }
}

data class FiltroNotaEntradaXML(
  val loja: Loja?,
  val dataInicial: LocalDate,
  val dataFinal: LocalDate,
  val numero: Int,
  val cnpj: String,
  val fornecedor: String,
  val preEntrada: EPREEntradaXML,
  val query: String,
)

enum class EPREEntradaXML(val codigo: String, val descricao: String) {
  TODOS("T", "Todos"),
  SIM("S", "Sim"),
  NAO("N", "Não")
}