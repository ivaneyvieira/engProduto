package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class DadosProdutosRessuprimento {
  var loja: Int? = null
  var lojaRessuprimento: Int? = null
  var pedido: Int? = null
  var data: LocalDate? = null
  var codFornecedor: Int? = null
  var totalPedido: Double? = null
  var observacao: String? = null
  var prdno: String? = null
  var codigo: Int? = null
  var descricao: String? = null
  var grade: String? = null
  var seqno: Int? = null
  var qttyVendaMes: Int? = null
  var qttyVendaMesAnt: Int? = null
  var estoque: Int? = null
  var qttyVendaMedia: Double? = null
  var qttySugerida: Int? = null
  var qttyPedida: Int? = null
  var estoqueLJ: Int? = null

  fun remove() {
    saci.removeDadosRessuprimento(this)
  }

  fun save() {
    saci.saveDadosRessuprimento(this)
  }

  companion object {
    fun find(filter: FiltroDadosProdutosRessuprimento): List<DadosProdutosRessuprimento> {
      return saci.findDadosRessuprimento(filter)
    }
  }
}

data class FiltroDadosProdutosRessuprimento(
  val loja: Int,
  val pesquisa: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)