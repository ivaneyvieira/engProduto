package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class NotaVendaDet(
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
  var hora: LocalTime?,
  var valor: Double?,
  var cliente: Int?,
  var uf: String?,
  var nomeCliente: String?,
  var vendedor: String?,
  var obs: String?,
) {
  fun parcelas(): List<ParcelasVenda> {
    return saci.findParcelasVenda(
      loja = loja ?: return emptyList(),
      pdv = pdv ?: return emptyList(),
      transacao = transacao ?: return emptyList()
    )
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
    fun findAll(filtro: FiltroNotaVendaDet): List<NotaVendaDet> {
      return saci.findNotaVendaDet(filtro)
    }
  }
}

data class FiltroNotaVendaDet(
  val loja: Int,
  val pesquisa: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)