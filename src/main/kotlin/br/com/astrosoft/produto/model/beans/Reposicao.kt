package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
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
  fun countSEP() = produtos.count { it.marca == EMarcaReposicao.SEP.num }
  fun countENT() = produtos.count { it.marca == EMarcaReposicao.ENT.num }

  fun produtosSEP() = produtos.filter { it.marca == EMarcaReposicao.SEP.num }
  fun produtosENT() = produtos.filter { it.marca == EMarcaReposicao.ENT.num }

  fun chave() = "${loja}:${numero}:${localizacao}"

  fun entregue(user: UserSaci) {
    this.entregueNo = user.no
    this.salva()
  }

  fun recebe(funcionario: Funcionario) {
    this.recebidoNo = funcionario.codigo ?: 0
    this.salva()
  }

  fun salva() {
    saci.updateReposicao(this)
  }

  fun expiraPedido() {
    saci.statusPedido(this, EStatusPedido.Expirado)
  }

  val usuarioApp: String?
    get() {
      val user = AppConfig.userLogin() as? UserSaci
      return user?.login
    }

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
  val localizacao: List<String>,
  val dataInicial: LocalDate? = null,
  val dataFinal: LocalDate? = null,
)

enum class EMarcaReposicao(val num: Int) {
  SEP(0),
  ENT(1)
}