package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class NotaVenda(
  var loja: Int?,
  var pdv: Int?,
  var transacao: Int?,
  var pedido: Int?,
  var data: LocalDate?,
  var nota: String?,
  var tipoNf: String?,
  var hora: LocalTime?,
  var tipoPgto: String?,
  var valor: Double?,
  var cliente: Int?,
  var uf: String?,
  var nomeCliente: String?,
  var vendedor: String?,
  var valorTipo: Double?,
  var obs: String?,
  var autoriza: String?,
  var solicitacaoTroca: String?,
  var produtoTroca: String?,
  var userTroca: Int?,
  var loginTroca: String?,
  var userSolicitacao: Int?,
  var loginSolicitacao: String?,
) {
  var solicitacaoTrocaEnnum: ESolicitacaoTroca?
    get() = ESolicitacaoTroca.entries.firstOrNull { it.codigo == solicitacaoTroca }
    set(value) {
      solicitacaoTroca = value?.codigo
    }

  var produtoTrocaEnnum: EProdutoTroca?
    get() = EProdutoTroca.entries.firstOrNull { it.codigo == produtoTroca }
    set(value) {
      produtoTroca = value?.codigo
    }

  val solicitacaoTrocaDescricao: String
    get() = solicitacaoTrocaEnnum?.descricao ?: ""
  val produtoTrocaDescricao: String
    get() = produtoTrocaEnnum?.descricao ?: ""

  fun update() {
    saci.updateNotaVenda(this)
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
    fun findAll(filtro: FiltroNotaVenda): List<NotaVenda> {
      return saci.findNotaVenda(filtro)
    }
  }
}

data class FiltroNotaVenda(
  val loja: Int,
  val pesquisa: String,
  val autoriza: String = "T",
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)

enum class ESolicitacaoTroca(val codigo: String, val descricao: String) {
  Troca("T", "Troca"),
  Estorno("E", "Estorno"),
  Reembolso("R", "Reembolso"),
  MudaCliente("M", "Muda Cliente"),
}

enum class EProdutoTroca(val codigo: String, val descricao: String) {
  Com("C", "Com Produto"),
  Sem("S", "Sem Produto"),
  Misto("M", "Misto"),
}
