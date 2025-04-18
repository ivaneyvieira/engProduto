package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class ProdutoEstoqueGarantia(
  var numero: Int? = null,
  var numloja: Int? = null,
  var lojaSigla: String? = null,
  var data: LocalDate? = null,
  var hora: LocalTime? = null,
  var usuario: String? = null,
  var prdno: String? = null,
  var descricao: String? = null,
  var locApp: String? = null,
  var barcode: String? = null,
  var ref: String? = null,
  var grade: String? = null,
  var estoqueLoja: Int? = null,
  var estoqueLojas: Int? = null,
  var estoqueDev: Int? = null,
  var observacao: String? = null,
  var lojaReceb: Int? = null,
  var niReceb: Int? = null,
  var nfoReceb: String? = null,
  var entradaReceb: LocalDate? = null,
  var forReceb: Int? = null,
  var cfopReceb: String? = null,
) {

  val saldoBarraRef: String
    get() {
      return "${barcode ?: ""}   |   ${ref ?: ""}"
    }

  fun saveGarantia() {
    saci.garantiaUpdate(this)
  }

  fun jaGravadoGarantia(): Boolean {
    return saci.jaGravadoGarantia(this)
  }

  fun remove() {
    saci.removeGarantiaProduto(this)
  }

  val codigo
    get() = prdno?.trim()

  companion object {
    fun findAll(filtro: FiltroGarantia): List<ProdutoEstoqueGarantia> {
      return saci.garantiaFindAll(filtro)
    }

    fun proximoNumero(numLoja: Int): Int {
      return saci.garantiaProximo(numLoja)
    }
  }
}

data class FiltroGarantia(
  val numLoja: Int = 0,
  val dataInicial: LocalDate? = null,
  val dataFinal: LocalDate? = null,
  val numero: Int = 0,
)

fun List<ProdutoEstoque>.toGarantia(numero: Int): List<ProdutoEstoqueGarantia> {
  val user = AppConfig.userLogin()

  val numLoja = this.firstOrNull()?.loja ?: return emptyList()
  val lojaSigla = this.firstOrNull()?.lojaSigla ?: return emptyList()

  return this.map {
    ProdutoEstoqueGarantia(
      numero = numero,
      numloja = numLoja,
      lojaSigla = lojaSigla,
      data = LocalDate.now(),
      hora = LocalTime.now(),
      usuario = user?.name,
      prdno = it.prdno,
      descricao = it.descricao,
      grade = it.grade,
      estoqueLoja = it.saldo,
      observacao = it.observacao,
    )
  }
}

fun List<ProdutoEstoqueGarantia>.agrupaGarantia(): List<EstoqueGarantia> {
  val grupos = this.groupBy { "${it.numloja}${it.numero}" }
  return grupos.mapNotNull {
    val garantia = it.value.firstOrNull() ?: return@mapNotNull null

    EstoqueGarantia(
      numero = garantia.numero ?: return@mapNotNull null,
      numloja = garantia.numloja ?: return@mapNotNull null,
      lojaSigla = garantia.lojaSigla ?: return@mapNotNull null,
      data = garantia.data ?: return@mapNotNull null,
      hora = garantia.hora ?: return@mapNotNull null,
      usuario = garantia.usuario,
      observacao = garantia.observacao,
    )
  }
}

class EstoqueGarantia(
  var numero: Int,
  var numloja: Int,
  var lojaSigla: String,
  var data: LocalDate,
  var hora: LocalTime,
  var usuario: String?,
  var observacao: String?,
) {
  fun cancelaGarantia() {
    saci.garantiaCancela(this)
  }

  fun findProdutos(): List<ProdutoEstoqueGarantia> {
    val filtro = FiltroGarantia(
      numLoja = numloja,
      numero = numero
    )
    val produtos = ProdutoEstoqueGarantia.findAll(filtro)
    return produtos
  }

  fun saveGarantia() {
    saci.updateGarantia(this)
  }

  companion object {
    private val listUserSaci = saci.findAllUser()

    private fun getUser(no: Int): UserSaci? {
      return listUserSaci.firstOrNull { it.no == no }
    }
  }
}