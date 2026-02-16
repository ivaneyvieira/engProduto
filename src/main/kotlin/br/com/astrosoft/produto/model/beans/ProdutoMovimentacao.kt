package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class ProdutoMovimentacao(
  var numero: Int? = null,
  var numloja: Int? = null,
  var lojaSigla: String? = null,
  var data: LocalDate? = null,
  var hora: LocalTime? = null,
  var noLogin: Int? = null,
  var login: String? = null,
  var usuario: String? = null,
  var codFor: Int? = null,
  var prdno: String? = null,
  var descricao: String? = null,
  var locApp: String? = null,
  var barcode: String? = null,
  var ref: String? = null,
  var grade: String? = null,
  var noGravado: Int? = 0,
  var gravadoLogin: String? = null,
  var noEntregue: Int? = 0,
  var entregue: String? = null,
  var entregueNome: String? = null,
  var noRecebido: Int? = 0,
  var recebido: String? = null,
  var recebidoNome: String? = null,
  var movimentacao: Int? = null,
  var estoque: Int? = null
) {
  val localAbrev
    get() = locApp?.substring(0, 4) ?: ""

  val saldoBarraRef: String
    get() {
      return "${barcode ?: ""}   |   ${ref ?: ""}"
    }

  fun save() {
    saci.updateMovimentacao(this)
  }

  fun jaGravado(): Boolean {
    return saci.jaGravado(this)
  }

  fun remove() {
    saci.removeMovimentacao(this)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ProdutoMovimentacao

    if (numero != other.numero) return false
    if (numloja != other.numloja) return false
    if (prdno != other.prdno) return false
    if (grade != other.grade) return false

    return true
  }

  override fun hashCode(): Int {
    var result = numero ?: 0
    result = 31 * result + (numloja ?: 0)
    result = 31 * result + (prdno?.hashCode() ?: 0)
    result = 31 * result + (grade?.hashCode() ?: 0)
    return result
  }

  val codigo
    get() = prdno?.trim()?.toIntOrNull() ?: 0

  companion object {
    fun findAll(filtro: FiltroMovimentacao): List<ProdutoMovimentacao> {
      return saci.movimentacaoFindAll(filtro)
    }

    fun proximoNumero(numLoja: Int): Int {
      return saci.movimentacaoProximo(numLoja)
    }

    fun updateProduto(produtos: List<ProdutoMovimentacao>) {
      produtos.forEach { produto ->
        produto.save()
      }
    }
  }
}

data class FiltroMovimentacao(
  val numLoja: Int = 0,
  val dataInicial: LocalDate? = null,
  val dataFinal: LocalDate? = null,
  val numero: Int = 0,
)

fun List<ProdutoMovimentacao>.agrupa(): List<Movimentacao> {
  val grupos = this.groupBy { "${it.numloja}${it.numero}" }
  return grupos.mapNotNull { mapPedido ->
    val pedido = mapPedido.value.firstOrNull() ?: return@mapNotNull null
    val lista = mapPedido.value

    Movimentacao(
      numero = pedido.numero ?: return@mapNotNull null,
      numloja = pedido.numloja ?: return@mapNotNull null,
      lojaSigla = pedido.lojaSigla ?: return@mapNotNull null,
      data = pedido.data ?: return@mapNotNull null,
      hora = pedido.hora ?: return@mapNotNull null,
      noLogin = pedido.noLogin ?: 0,
      login = pedido.login ?: "",
      usuario = pedido.usuario ?: "",
      noGravado = pedido.noGravado ?: 0,
      gravadoLogin = pedido.gravadoLogin ?: "",
      noEntregue = pedido.noEntregue ?: 0,
      entregue = pedido.entregue ?: "",
      entregueNome = pedido.entregueNome ?: "",
      noRecebido = pedido.noRecebido ?: 0,
      recebido = pedido.recebido ?: "",
      recebidoNome = pedido.recebidoNome ?: ""
    )
  }
}

class Movimentacao(
  var numero: Int,
  var numloja: Int,
  var lojaSigla: String,
  var data: LocalDate,
  var hora: LocalTime,
  var noLogin: Int,
  var login: String,
  var usuario: String,
  var noGravado: Int,
  var gravadoLogin: String,
  var noEntregue: Int,
  var entregue: String,
  var entregueNome: String,
  var noRecebido: Int,
  var recebido: String,
  var recebidoNome: String
) {
  fun findProdutos(): List<ProdutoMovimentacao> {
    val filtro = FiltroMovimentacao(
      numLoja = numloja,
      numero = numero,
    )
    val produtos = ProdutoMovimentacao.findAll(filtro)
    return produtos
  }

  companion object {
    private val listUserSaci = saci.findAllUser()

    private fun getUser(no: Int): UserSaci? {
      return listUserSaci.firstOrNull { it.no == no }
    }
  }
}