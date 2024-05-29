package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoInventario(
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var grade: String?,
  var unidade: String?,
  var validade: Int?,
  var vendno: Int?,
  var fornecedorAbrev: String?,
  var dataEntrada: LocalDate?,
  var vencimento: Int?,
  var estoqueTotalDS: Int?,
  var estoqueTotalMR: Int?,
  var estoqueTotalMF: Int?,
  var estoqueTotalPK: Int?,
  var estoqueTotalTM: Int?,
  var estoqueTotal: Int?,
  var seq: Int?,
  var estoqueDS: Int?,
  var estoqueMR: Int?,
  var estoqueMF: Int?,
  var estoquePK: Int?,
  var estoqueTM: Int?,
  var vendasDS: Int? = null,
  var vendasMR: Int? = null,
  var vendasMF: Int? = null,
  var vendasPK: Int? = null,
  var vendasTM: Int? = null,
  var vencimentoDS: Int?,
  var vencimentoMR: Int?,
  var vencimentoMF: Int?,
  var vencimentoPK: Int?,
  var vencimentoTM: Int?,
) {
  val venda: Int
    get() = (vendasDS ?: 0) + (vendasMR ?: 0) + (vendasMF ?: 0) + (vendasPK ?: 0) + (vendasTM ?: 0)

  val saldo: Int
    get() = (estoqueDS ?: 0) + (estoqueMR ?: 0) + (estoqueMF ?: 0) + (estoquePK ?: 0) + (estoqueTM ?: 0) - venda

  var vencimentoStr: String?
    get() = vencimento(vencimento)
    set(value) {
      vencimento = mesAno(value)
    }

  var vencimentoDSStr: String?
    get() = vencimento(vencimentoDS)
    set(value) {
      vencimentoDS = mesAno(value)
    }

  var vencimentoMRStr: String?
    get() = vencimento(vencimentoMR)
    set(value) {
      vencimentoMR = mesAno(value)
    }

  var vencimentoMFStr: String?
    get() = vencimento(vencimentoMF)
    set(value) {
      vencimentoMF = mesAno(value)
    }

  var vencimentoPKStr: String?
    get() = vencimento(vencimentoPK)
    set(value) {
      vencimentoPK = mesAno(value)
    }

  var vencimentoTMStr: String?
    get() = vencimento(vencimentoTM)
    set(value) {
      vencimentoTM = mesAno(value)
    }

  fun mesAno(value: String?): Int {
    value ?: return 0
    val mes = value.substring(0, 2).toIntOrNull() ?: return 0
    val ano = value.substring(3, 5).toIntOrNull() ?: return 0
    return mes + (ano + 2000) * 100
  }

  fun vencimento(vencimento: Int?): String {
    vencimento ?: return ""
    val vencimentoStr = vencimento.toString()
    if (vencimentoStr.length != 6) return ""
    val mes = vencimentoStr.substring(4, 6)
    val ano = vencimentoStr.substring(2, 4)
    return "$mes/$ano"
  }

  fun update() {
    saci.updateProdutoValidade(this)
  }

  fun remove() {
    saci.removeProdutoValidade(this)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ProdutoInventario

    if (prdno != other.prdno) return false
    if (codigo != other.codigo) return false
    if (descricao != other.descricao) return false
    if (grade != other.grade) return false
    if (unidade != other.unidade) return false
    if (validade != other.validade) return false
    if (vendno != other.vendno) return false
    if (fornecedorAbrev != other.fornecedorAbrev) return false
    if (dataEntrada != other.dataEntrada) return false
    if (vencimento != other.vencimento) return false
    if (estoqueTotalDS != other.estoqueTotalDS) return false
    if (estoqueTotalMR != other.estoqueTotalMR) return false
    if (estoqueTotalMF != other.estoqueTotalMF) return false
    if (estoqueTotalPK != other.estoqueTotalPK) return false
    if (estoqueTotalTM != other.estoqueTotalTM) return false
    if (estoqueTotal != other.estoqueTotal) return false
    if (seq != other.seq) return false
    if (estoqueDS != other.estoqueDS) return false
    if (estoqueMR != other.estoqueMR) return false
    if (estoqueMF != other.estoqueMF) return false
    if (estoquePK != other.estoquePK) return false
    if (estoqueTM != other.estoqueTM) return false
    if (vendasDS != other.vendasDS) return false
    if (vendasMR != other.vendasMR) return false
    if (vendasMF != other.vendasMF) return false
    if (vendasPK != other.vendasPK) return false
    if (vendasTM != other.vendasTM) return false
    if (vencimentoDS != other.vencimentoDS) return false
    if (vencimentoMR != other.vencimentoMR) return false
    if (vencimentoMF != other.vencimentoMF) return false
    if (vencimentoPK != other.vencimentoPK) return false
    if (vencimentoTM != other.vencimentoTM) return false

    return true
  }

  override fun hashCode(): Int {
    var result = prdno?.hashCode() ?: 0
    result = 31 * result + (codigo ?: 0)
    result = 31 * result + (descricao?.hashCode() ?: 0)
    result = 31 * result + (grade?.hashCode() ?: 0)
    result = 31 * result + (unidade?.hashCode() ?: 0)
    result = 31 * result + (validade ?: 0)
    result = 31 * result + (vendno ?: 0)
    result = 31 * result + (fornecedorAbrev?.hashCode() ?: 0)
    result = 31 * result + (dataEntrada?.hashCode() ?: 0)
    result = 31 * result + (vencimento ?: 0)
    result = 31 * result + (estoqueTotalDS ?: 0)
    result = 31 * result + (estoqueTotalMR ?: 0)
    result = 31 * result + (estoqueTotalMF ?: 0)
    result = 31 * result + (estoqueTotalPK ?: 0)
    result = 31 * result + (estoqueTotalTM ?: 0)
    result = 31 * result + (estoqueTotal ?: 0)
    result = 31 * result + (seq ?: 0)
    result = 31 * result + (estoqueDS ?: 0)
    result = 31 * result + (estoqueMR ?: 0)
    result = 31 * result + (estoqueMF ?: 0)
    result = 31 * result + (estoquePK ?: 0)
    result = 31 * result + (estoqueTM ?: 0)
    result = 31 * result + (vendasDS ?: 0)
    result = 31 * result + (vendasMR ?: 0)
    result = 31 * result + (vendasMF ?: 0)
    result = 31 * result + (vendasPK ?: 0)
    result = 31 * result + (vendasTM ?: 0)
    result = 31 * result + (vencimentoDS ?: 0)
    result = 31 * result + (vencimentoMR ?: 0)
    result = 31 * result + (vencimentoMF ?: 0)
    result = 31 * result + (vencimentoPK ?: 0)
    result = 31 * result + (vencimentoTM ?: 0)
    return result
  }

  companion object {
    fun find(filtro: FiltroProdutoInventario): List<ProdutoInventario> {
      val produtos = saci.produtoValidade(filtro)
      val dataInicial = produtos.filter {
        it.estoqueDS != 0 || it.estoqueMR != 0 || it.estoqueMF != 0 || it.estoquePK != 0 || it.estoqueTM != 0
      }.mapNotNull { it.dataEntrada }.minOrNull() ?: return produtos
      val saidas = ProdutoInventarioSaida.find(dataInicial).groupBy { ChaveSaida(it.loja, it.prdno, it.grade) }

      val produtosGrupo = produtos.groupBy { "${it.prdno} ${it.grade} ${it.dataEntrada}" }
      val produtosNovos = produtosGrupo.flatMap { (chave, produtosList) ->
        val prdno = produtosList.first().prdno
        val grade = produtosList.first().grade
        val data = produtosList.first().dataEntrada

        var saidaLoja2 = saidas[ChaveSaida(2, prdno, grade)].orEmpty().filter {
          it.date.toSaciDate() >= data.toSaciDate()
        }.sumOf { it.qtty ?: 0 }
        var saidaLoja3 = saidas[ChaveSaida(3, prdno, grade)].orEmpty().filter {
          it.date.toSaciDate() >= data.toSaciDate()
        }.sumOf { it.qtty ?: 0 }
        var saidaLoja4 = saidas[ChaveSaida(4, prdno, grade)].orEmpty().filter {
          it.date.toSaciDate() >= data.toSaciDate()
        }.sumOf { it.qtty ?: 0 }
        var saidaLoja5 = saidas[ChaveSaida(5, prdno, grade)].orEmpty().filter {
          it.date.toSaciDate() >= data.toSaciDate()
        }.sumOf { it.qtty ?: 0 }
        var saidaLoja8 = saidas[ChaveSaida(8, prdno, grade)].orEmpty().filter {
          it.date.toSaciDate() >= data.toSaciDate()
        }.sumOf { it.qtty ?: 0 }

        val produtosList2 = produtosList.filter {
          (it.estoqueDS ?: 0) > 0 && (it.vencimentoDS ?: 0) > 0
        }.sortedByDescending { it.vencimentoDS }

        val produtosList3 = produtosList.filter {
          (it.estoqueMR ?: 0) > 0 && (it.vencimentoMR ?: 0) > 0
        }.sortedByDescending { it.vencimentoMR }

        val produtosList4 = produtosList.filter {
          (it.estoqueMF ?: 0) > 0 && (it.vencimentoMF ?: 0) > 0
        }.sortedByDescending { it.vencimentoMF }

        val produtosList5 = produtosList.filter {
          (it.estoquePK ?: 0) > 0 && (it.vencimentoPK ?: 0) > 0
        }.sortedByDescending { it.vencimentoPK }

        val produtosList8 = produtosList.filter {
          (it.estoqueTM ?: 0) > 0 && (it.vencimentoTM ?: 0) > 0
        }.sortedByDescending { it.vencimentoTM }

        produtosList2.forEach { produtoInventario ->
          val estoqueDS = produtoInventario.estoqueDS ?: 0
          if (estoqueDS > 0) {
            if (saidaLoja2 > 0) {
              val saida = minOf(estoqueDS, saidaLoja2)
              produtoInventario.vendasDS = saida
              saidaLoja2 -= saida
            }
          }
        }

        produtosList3.forEach { produtoInventario ->
          val estoqueMR = produtoInventario.estoqueMR ?: 0
          if (estoqueMR > 0) {
            if (saidaLoja3 > 0) {
              val saida = minOf(estoqueMR, saidaLoja3)
              produtoInventario.vendasDS = saida
              saidaLoja3 -= saida
            }
          }
        }

        produtosList4.forEach { produtoInventario ->
          val estoqueMF = produtoInventario.estoqueMF ?: 0
          if (estoqueMF > 0) {
            if (saidaLoja4 > 0) {
              val saida = minOf(estoqueMF, saidaLoja4)
              produtoInventario.vendasMF = saida
              saidaLoja4 -= saida
            }
          }
        }

        produtosList5.forEach { produtoInventario ->
          val estoquePK = produtoInventario.estoquePK ?: 0
          if (estoquePK > 0) {
            if (saidaLoja5 > 0) {
              val saida = minOf(estoquePK, saidaLoja5)
              produtoInventario.vendasPK = saida
              saidaLoja5 -= saida
            }
          }
        }

        produtosList8.forEach { produtoInventario ->
          val estoqueTM = produtoInventario.estoqueTM ?: 0
          if (estoqueTM > 0) {
            if (saidaLoja8 > 0) {
              val saida = minOf(estoqueTM, saidaLoja8)
              produtoInventario.vendasTM = saida
              saidaLoja8 -= saida
            }
          }
        }

        produtosList
      }
      return produtosNovos
    }
  }
}

fun List<ProdutoInventario>.organiza(): List<ProdutoInventario> {
  val produtosList = this.flatMap { produto ->
    val vencDS = if ((produto.vencimentoDS ?: 0) > 0) produto.vencimentoDS else null
    val vencMR = if ((produto.vencimentoMR ?: 0) > 0) produto.vencimentoMR else null
    val vencMF = if ((produto.vencimentoMF ?: 0) > 0) produto.vencimentoMF else null
    val vencPK = if ((produto.vencimentoPK ?: 0) > 0) produto.vencimentoPK else null
    val vencTM = if ((produto.vencimentoTM ?: 0) > 0) produto.vencimentoTM else null

    val vencList = listOfNotNull(vencDS, vencMR, vencMF, vencPK, vencTM)

    vencList.map { venc ->
      ProdutoInventario(
        prdno = produto.prdno,
        codigo = produto.codigo,
        descricao = produto.descricao,
        grade = produto.grade,
        unidade = produto.unidade,
        validade = produto.validade,
        vendno = produto.vendno,
        fornecedorAbrev = produto.fornecedorAbrev,
        dataEntrada = produto.dataEntrada,
        vencimento = venc,
        estoqueTotalDS = produto.estoqueTotalDS,
        estoqueTotalMR = produto.estoqueTotalMR,
        estoqueTotalMF = produto.estoqueTotalMF,
        estoqueTotalPK = produto.estoqueTotalPK,
        estoqueTotalTM = produto.estoqueTotalTM,
        estoqueTotal = produto.estoqueTotal,
        seq = produto.seq,
        estoqueDS = if (vencDS == null) 0 else produto.estoqueDS,
        estoqueMR = if (vencMR == null) 0 else produto.estoqueMR,
        estoqueMF = if (vencMF == null) 0 else produto.estoqueMF,
        estoquePK = if (vencPK == null) 0 else produto.estoquePK,
        estoqueTM = if (vencTM == null) 0 else produto.estoqueTM,
        vendasDS = if (vencDS == null) 0 else produto.vendasDS,
        vendasMR = if (vencMR == null) 0 else produto.vendasMR,
        vendasMF = if (vencMF == null) 0 else produto.vendasMF,
        vendasPK = if (vencPK == null) 0 else produto.vendasPK,
        vendasTM = if (vencTM == null) 0 else produto.vendasTM,
        vencimentoDS = vencDS,
        vencimentoMR = vencMR,
        vencimentoMF = vencMF,
        vencimentoPK = vencPK,
        vencimentoTM = vencTM,
      )
    }
  }
  return produtosList.distinct()
}

data class FiltroProdutoInventario(
  val pesquisa: String,
  val codigo: String,
  val validade: Int,
  val grade: String,
  val caracter: ECaracter,
  val mes: Int,
  val ano: Int,
  val loja: Int,
  val organiza: Boolean,
)

