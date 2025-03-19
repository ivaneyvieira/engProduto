package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class ProdutoEstoqueAcerto(
  var numero: Int? = null,
  var numloja: Int? = null,
  var lojaSigla: String? = null,
  var data: LocalDate? = null,
  var hora: LocalTime? = null,
  var login: String? = null,
  var usuario: String? = null,
  var prdno: String? = null,
  var descricao: String? = null,
  var grade: String? = null,
  var estoqueSis: Int? = null,
  var estoqueCD: Int? = null,
  var estoqueLoja: Int? = null,
  var diferenca: Int? = null,
  var processado: String? = null,
  var transacao: String? = null
) {
  val estoqueRelatorio: String
    get() {
      val estSis = estoqueSis?.format() ?: ""
      val estCD = estoqueCD?.format() ?: ""
      val estLj = estoqueLoja?.format() ?: ""
      return "       Est Sis: $estSis | Est CD: $estCD | Est Loja: $estLj"
    }

  fun save() {
    saci.acertoUpdate(this)
  }

  fun jaGravado(): Boolean {
    return saci.jaGravado(this).isNotEmpty()
  }

  fun cancela() {
    saci.acertoCancela(this)
  }

  val codigo
    get() = prdno?.trim()

  companion object {
    fun findAll(filtro: FiltroAcerto): List<ProdutoEstoqueAcerto> {
      return saci.acertoFindAll(filtro)
    }
  }
}

data class FiltroAcerto(
  val numLoja: Int = 0,
  val data: LocalDate? = null,
  val numero: Int = 0,
)

fun List<ProdutoEstoque>.toAcerto(): List<ProdutoEstoqueAcerto> {
  val user = AppConfig.userLogin()

  val numLoja = this.firstOrNull()?.loja ?: return emptyList()
  val novo = saci.acertoNovo(numLoja) ?: return emptyList()
  return this.map {
    ProdutoEstoqueAcerto(
      numero = novo.numero,
      numloja = novo.numloja,
      lojaSigla = novo.lojaSigla,
      data = novo.data,
      hora = novo.hora,
      login = user?.login,
      usuario = user?.name,
      prdno = it.prdno,
      descricao = it.descricao,
      grade = it.grade,
      estoqueSis = it.saldo,
      estoqueCD = it.estoqueCD,
      estoqueLoja = it.estoqueLoja,
      diferenca = it.estoqueDif
    )
  }
}

fun List<ProdutoEstoqueAcerto>.agrupa(): List<ProdutoEstoqueAcerto> {
  val grupos = this.groupBy { "${it.numloja}${it.numero}" }
  return grupos.mapNotNull {
    it.value.firstOrNull() ?: return@mapNotNull null
  }
}