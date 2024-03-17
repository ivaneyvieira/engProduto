package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class Reposicao(
  var loja: Int,
  var numero: Int,
  var data: LocalDate,
  var localizacao: String,
  var marca: Int,
  var observacao: String,
  var entregueNo: Int,
  var entregueNome: String,
  var entregueSNome: String,
  var recebidoNo: Int,
  var recebidoNome: String,
  var recebidoSNome: String,
  val produtos: List<ReposicaoProduto>
) {
  fun countCD() = produtos.count { it.marca == EMarcaReposicao.CD.num }
  fun countSEP() = produtos.count { it.marca == EMarcaReposicao.SEP.num }
  companion object {
    fun findAll(filtro: FiltroReposicao): List<Reposicao> {
      return saci.findResposicaoProduto(filtro).groupBy { "${it.loja}:${it.numero}:${it.localizacao}" }.map {
        val produtos = it.value
        val first = produtos.firstOrNull()
        Reposicao(
          loja = first?.loja ?: 0,
          numero = first?.numero ?: 0,
          data = first?.data ?: LocalDate.now(),
          localizacao = first?.localizacao ?: "",
          marca = first?.marca ?: 0,
          observacao = first?.observacao ?: "",
          entregueNo = first?.entregueNo ?: 0,
          entregueNome = first?.entregueNome ?: "",
          entregueSNome = first?.entregueSNome ?: "",
          recebidoNo = first?.recebidoNo ?: 0,
          recebidoNome = first?.recebidoNome ?: "",
          recebidoSNome = first?.recebidoSNome ?: "",
          produtos = produtos
        )
      }
    }
  }
}

data class FiltroReposicao(
  val loja: Int,
  val pesquisa: String,
  val marca: EMarcaReposicao,
)

enum class EMarcaReposicao(val num: Int) {
  CD(0),
  SEP(1)
}