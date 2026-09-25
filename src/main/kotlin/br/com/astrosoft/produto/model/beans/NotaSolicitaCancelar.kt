package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class NotaSolicitaCancelar(
    var loja: Int?,
    var pdv: Int?,
    var transacao: Int?,
    var pedido: Int?,
    var numMetodo: Int?,
    var nomeMetodo: String?,
    var mult: Double?,
    var data: LocalDate?,
    var nota: String?,
    var tipoNf: String?,
    var tipoNotaSaida: String?,
    var retiraFutura: Boolean?,
    var serie: String?,
    var hora: LocalTime?,
    var tipoPgto: String?,
    var documento: String?,
    var quantParcelas: Int?,
    var mediaPrazo: Int?,
    var valor: Double?,
    var cliente: Int?,
    var uf: String?,
    var nomeCliente: String?,
    var vendedor: String?,
    var valorTipo: Double?,
    var obs: String?,
) {
  val documentoStr: String
    get() {
      val doc = documento ?: return ""
      val quant = if (quantParcelas == null) "" else " (${quantParcelas}x)"
      return "$doc $quant"
    }
  
  fun produtos(): List<ProdutoNFS> {
    return saci.findProdutoNF(this)
  }
  
  val numeroInterno: Int?
    get() {
      val regex = Regex("""NI[^0-9A-Z]*(\d+)""")
      val obsInput = obs?.uppercase() ?: return null
      val match = regex.find(obsInput) ?: return null
      val groups = match.groupValues
      return groups.getOrNull(1)?.toIntOrNull()
    }
  
  companion object {
    fun findAll(filtro: FiltroSolicitaCancelar): List<NotaSolicitaCancelar> {
      return saci.findNotaSolicitaCancelar(filtro)
    }
  }
}

data class FiltroSolicitaCancelar(
    val loja: Int,
    val pdv: Int,
    val pesquisa: String,
    val tipoNota: ETipoNotaFiscal,
)