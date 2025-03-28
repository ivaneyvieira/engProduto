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
  var locApp: String? = null,
  var barcode: String? = null,
  var ref: String? = null,
  var grade: String? = null,
  var estoqueSis: Int? = null,
  var estoqueCD: Int? = null,
  var estoqueLoja: Int? = null,
  var processado: Boolean? = null,
  var transacao: String? = null,
  var gravadoLogin: Int? = 0,
  var gravado: Boolean? = false,
) {
  val diferenca: Int?
    get() {
      return (estoqueCD ?: return null) + (estoqueLoja ?: return null) - (estoqueSis ?: return null)
    }

  val saldoBarraRef: String
    get() {
      return "${barcode ?: ""}   |   ${ref ?: ""}"
    }

  val acertado
    get() = estoqueCD != null && estoqueLoja != null

  val estoqueReal: Int
    get() {
      return (estoqueSis ?: 0) + (diferenca ?: 0)
    }

  val estoqueRelatorio: String
    get() {
      val estSis = estoqueSis?.format() ?: ""
      val estCD = estoqueCD?.format() ?: ""
      val estLj = estoqueLoja?.format() ?: ""
      val estReal = estoqueReal.format()
      val linha = "       Est Sis: $estSis | Est CD: $estCD | Est Loja: $estLj | Est Real: $estReal"
      val linhaMenor = "       E Sis: $estSis | E CD: $estCD | E Loja: $estLj | E Real: $estReal"
      return if(linha.length> 64) linhaMenor else linha
    }

  fun save() {
    saci.acertoUpdate(this)
  }

  fun saveMobile() {
    saci.acertoUpdate(this, true)
  }

  fun jaGravado(): Boolean {
    return saci.jaGravado(this)
  }

  fun remove() {
    saci.removeAcertoProduto(this)
  }

  fun removeMobile() {
    saci.removeAcertoProduto(this, true)
  }

  val codigo
    get() = prdno?.trim()

  companion object {
    fun findAll(filtro: FiltroAcerto, mobile: Boolean = false): List<ProdutoEstoqueAcerto> {
      return saci.acertoFindAll(filtro, mobile)
    }

    fun findAllMobile(filtro: FiltroAcerto): List<ProdutoEstoqueAcerto> {
      return saci.acertoFindAll(filtro, true)
    }

    fun proximoNumero(numLoja: Int): Int {
      return saci.acertoProximo(numLoja)
    }
  }
}

data class FiltroAcerto(
  val numLoja: Int = 0,
  val dataInicial: LocalDate? = null,
  val dataFinal: LocalDate? = null,
  val numero: Int = 0,
)

fun List<ProdutoEstoque>.toAcerto(numero: Int): List<ProdutoEstoqueAcerto> {
  val user = AppConfig.userLogin()

  val numLoja = this.firstOrNull()?.loja ?: return emptyList()
  val novo = saci.acertoNovo(numero, numLoja) ?: return emptyList()
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
    )
  }
}

fun List<ProdutoEstoqueAcerto>.agrupa(): List<EstoqueAcerto> {
  val grupos = this.groupBy { "${it.numloja}${it.numero}" }
  return grupos.mapNotNull {
    val acerto = it.value.firstOrNull() ?: return@mapNotNull null

    EstoqueAcerto(
      numero = acerto.numero ?: return@mapNotNull null,
      numloja = acerto.numloja ?: return@mapNotNull null,
      lojaSigla = acerto.lojaSigla ?: return@mapNotNull null,
      data = acerto.data ?: return@mapNotNull null,
      hora = acerto.hora ?: return@mapNotNull null,
      login = acerto.login,
      usuario = acerto.usuario,
      processado = it.value.map { it.processado }.maxBy { it ?: false },
      transacaoEnt = it.value.firstOrNull { (it.diferenca ?: 0) > 0 }?.transacao,
      transacaoSai = it.value.firstOrNull { (it.diferenca ?: 0) < 0 }?.transacao,
      gravadoLogin = acerto.gravadoLogin,
      gravado = acerto.gravado,
    )
  }
}

class EstoqueAcerto(
  var numero: Int,
  var numloja: Int,
  var lojaSigla: String,
  var data: LocalDate,
  var hora: LocalTime,
  var login: String?,
  var usuario: String?,
  var processado: Boolean?,
  var transacaoEnt: String?,
  var transacaoSai: String?,
  var gravadoLogin: Int?,
  var gravado: Boolean?,
) {
  val processadoStr
    get() = if (processado == true) "Sim" else "Não"

  val gravadoLoginStr: String
    get() {
      return getUser(gravadoLogin ?: 0)?.login ?: ""
    }

  val gravadoStr: String
    get() = if (gravado == true) "Sim" else "Não"

  fun cancela() {
    saci.acertoCancela(this)
  }

  fun cancelaMobile() {
    saci.acertoCancela(this, true)
  }

  fun findProdutos(): List<ProdutoEstoqueAcerto> {
    val filtro = FiltroAcerto(
      numLoja = numloja,
      numero = numero
    )
    val produtos = ProdutoEstoqueAcerto.findAll(filtro)
    return produtos
  }

  fun findProdutosMobile(): List<ProdutoEstoqueAcerto> {
    val filtro = FiltroAcerto(
      numLoja = numloja,
      numero = numero
    )
    val produtos = ProdutoEstoqueAcerto.findAll(filtro, true)
    return produtos
  }

  companion object {
    private val listUserSaci = saci.findAllUser()

    private fun getUser(no: Int): UserSaci? {
      return listUserSaci.firstOrNull { it.no == no }
    }
  }
}