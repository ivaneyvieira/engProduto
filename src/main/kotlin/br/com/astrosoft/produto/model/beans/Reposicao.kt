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
  var finalizadoNo: Int,
  var finalizadoNome: String,
  var finalizadoSNome: String,
  var recebidoNo: Int,
  var recebidoNome: String,
  var recebidoSNome: String,
  var metodo: Int,
  val produtos: List<ReposicaoProduto>
) {
  val recebidoSNomeAjuste: String = when (metodo) {
    EMetodo.RETORNO.num -> {
      entregueSNome
    }

    EMetodo.ACERTO.num  -> {
      ""
    }

    else                -> {
      recebidoSNome
    }
  }
  val entregueSNomeAjuste: String = when (metodo) {
    EMetodo.RETORNO.num -> {
      recebidoSNome
    }

    EMetodo.ACERTO.num  -> {
      ""
    }

    else                -> {
      entregueSNome
    }
  }
  val tipoMetodo: String
    get() = EMetodo.entries.firstOrNull { it.num == metodo }?.descricao ?: ""

  fun countSep() = produtos.count { rep ->
    rep.isSep()
  }

  fun countNaoSelecionado() = produtos.count { rep ->
    !rep.isSelecionado()
  }

  fun countSepNaoAssinado() = produtos.count { rep ->
    rep.isSepNaoAssinado()
  }

  fun countNaoRecebido() = produtos.count { rep ->
    rep.isNaoRecebido()
  }

  fun countNaoFinalizado() = produtos.count { rep ->
    rep.isNaoFinalizado()
  }

  fun countNaoEntregue() = produtos.count { rep ->
    rep.isNaoEntregue()
  }

  fun produtosEnt() = produtos.filter { it.isEnt() }


  fun isProntoAssinar(): Boolean {
    return if (metodo == EMetodo.ACERTO.num) {
      (countNaoRecebido() > 0 || countNaoFinalizado() > 0)
    } else {
      (countNaoSelecionado() == 0) && (countNaoRecebido() > 0 || countNaoEntregue() > 0)
    }
  }

  fun chave() = "${loja}:${numero}:${localizacao}"

  fun entregue(user: UserSaci, produtosPar: List<ReposicaoProduto> = emptyList()) {
    this.entregueNo = user.no
    this.salva()
    val produtosList = produtosPar.ifEmpty { produtos }
    produtosList.forEach { produto ->
      produto.entregueNo = user.no
      produto.salva()
    }
  }

  fun finaliza(user: UserSaci, produtosPar: List<ReposicaoProduto> = emptyList()) {
    this.finalizadoNo = user.no
    this.salva()
    val produtosList = produtosPar.ifEmpty { produtos }
    produtosList.forEach { produto ->
      produto.finalizadoNo = user.no
      produto.salva()
    }
  }

  fun recebe(funcionario: Funcionario) {
    this.recebidoNo = funcionario.codigo
    this.salva()
    produtos.forEach { produto ->
      produto.recebidoNo = funcionario.codigo
      produto.salva()
    }
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
    fun findAll(codigo: String, grade: String): List<Reposicao> {
      val listBruto = findAll(
        FiltroReposicao(
          loja = 0,
          pesquisa = "",
          marca = EMarcaReposicao.ENT,
          localizacao = listOf("TODOS"),
          codigo = codigo,
          grade = grade,
          metodo = EMetodo.TODOS,
        )
      )
      val listLiquido = listBruto.flatMap { repo ->
        findAll(
          FiltroReposicao(
            loja = repo.loja,
            pesquisa = "${repo.numero}",
            marca = EMarcaReposicao.ENT,
            localizacao = listOf("TODOS"),
            metodo = EMetodo.TODOS,
          )
        )
      }
      return listLiquido
    }

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
          finalizadoNo = first?.finalizadoNo ?: 0,
          finalizadoNome = first?.finalizadoNome ?: "",
          finalizadoSNome = first?.finalizadoSNome ?: "",
          recebidoNo = first?.recebidoNo ?: 0,
          recebidoNome = first?.recebidoNome ?: "",
          recebidoSNome = first?.recebidoSNome ?: "",
          metodo = first?.metodo ?: 0,
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
  val codigo: String = "",
  val grade: String = "",
  val metodo: EMetodo,
)

enum class EMarcaReposicao(val num: Int) {
  SEP(0),
  ENT(1),
}

enum class EMetodo(val num: Int, val descricao: String) {
  REPOSICAO(431, "Reposição Loja"),
  RETORNO(432, "Retorno Loja"),
  ACERTO(433, "Acerto App"),
  TODOS(0, "Todos"),
}