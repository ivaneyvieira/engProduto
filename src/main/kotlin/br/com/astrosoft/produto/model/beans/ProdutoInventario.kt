package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
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
  var estoqueLoja: Int?,
  var entradaDS: Int? = null,
  var entradaMR: Int? = null,
  var entradaMF: Int? = null,
  var entradaPK: Int? = null,
  var entradaTM: Int? = null,
  var saidaDS: Int? = null,
  var saidaMR: Int? = null,
  var saidaMF: Int? = null,
  var saidaPK: Int? = null,
  var saidaTM: Int? = null,
  var vencimentoDS: Int?,
  var vencimentoMR: Int?,
  var vencimentoMF: Int?,
  var vencimentoPK: Int?,
  var vencimentoTM: Int?,
  var vencimentoLoja: Int?,
) {
  val venda: Int
    get() = (saidaDS ?: 0) + (saidaMR ?: 0) + (saidaMF ?: 0) + (saidaPK ?: 0) + (saidaTM ?: 0)

  val saldo: Int
    get() = (estoqueDS ?: 0) + (estoqueMR ?: 0) + (estoqueMF ?: 0) + (estoquePK ?: 0) + (estoqueTM ?: 0) - venda

  val saldoDS: Int
    get() = (estoqueDS ?: 0) - (saidaDS ?: 0)
  val saldoMR: Int
    get() = (estoqueMR ?: 0) - (saidaMR ?: 0)
  val saldoMF: Int
    get() = (estoqueMF ?: 0) - (saidaMF ?: 0)
  val saldoPK: Int
    get() = (estoquePK ?: 0) - (saidaPK ?: 0)
  val saldoTM: Int
    get() = (estoqueTM ?: 0) - (saidaTM ?: 0)

  var vencimentoStr: String?
    get() = vencimentoToStr(vencimento)
    set(value) {
      vencimento = mesAno(value)
    }

  var vencimentoDSStr: String?
    get() = vencimentoToStr(vencimentoDS)
    set(value) {
      vencimentoDS = mesAno(value)
    }

  var vencimentoMRStr: String?
    get() = vencimentoToStr(vencimentoMR)
    set(value) {
      vencimentoMR = mesAno(value)
    }

  var vencimentoMFStr: String?
    get() = vencimentoToStr(vencimentoMF)
    set(value) {
      vencimentoMF = mesAno(value)
    }

  var vencimentoPKStr: String?
    get() = vencimentoToStr(vencimentoPK)
    set(value) {
      vencimentoPK = mesAno(value)
    }

  var vencimentoTMStr: String?
    get() = vencimentoToStr(vencimentoTM)
    set(value) {
      vencimentoTM = mesAno(value)
    }

  fun mesAno(value: String?): Int {
    value ?: return 0
    val mes = value.substring(0, 2).toIntOrNull() ?: return 0
    val ano = value.substring(3, 5).toIntOrNull() ?: return 0
    return mes + (ano + 2000) * 100
  }

  fun vencimentoToStr(vencimento: Int?): String {
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
    val user = AppConfig.userLogin() as? UserSaci ?: return
    saci.removeProdutoValidade(this, user.lojaProduto ?: 0)
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
        }.sortedBy { it.vencimentoDS }

        val produtosList3 = produtosList.filter {
          (it.estoqueMR ?: 0) > 0 && (it.vencimentoMR ?: 0) > 0
        }.sortedBy { it.vencimentoMR }

        val produtosList4 = produtosList.filter {
          (it.estoqueMF ?: 0) > 0 && (it.vencimentoMF ?: 0) > 0
        }.sortedBy { it.vencimentoMF }

        val produtosList5 = produtosList.filter {
          (it.estoquePK ?: 0) > 0 && (it.vencimentoPK ?: 0) > 0
        }.sortedBy { it.vencimentoPK }

        val produtosList8 = produtosList.filter {
          (it.estoqueTM ?: 0) > 0 && (it.vencimentoTM ?: 0) > 0
        }.sortedBy { it.vencimentoTM }

        produtosList2.forEach { produtoInventario ->
          val estoqueDS = produtoInventario.estoqueDS ?: 0
          if (estoqueDS > 0) {
            if (saidaLoja2 > 0) {
              val saida = minOf(estoqueDS, saidaLoja2)
              produtoInventario.saidaDS = saida
              saidaLoja2 -= saida
            }
          }
        }

        produtosList3.forEach { produtoInventario ->
          val estoqueMR = produtoInventario.estoqueMR ?: 0
          if (estoqueMR > 0) {
            if (saidaLoja3 > 0) {
              val saida = minOf(estoqueMR, saidaLoja3)
              produtoInventario.saidaDS = saida
              saidaLoja3 -= saida
            }
          }
        }

        produtosList4.forEach { produtoInventario ->
          val estoqueMF = produtoInventario.estoqueMF ?: 0
          if (estoqueMF > 0) {
            if (saidaLoja4 > 0) {
              val saida = minOf(estoqueMF, saidaLoja4)
              produtoInventario.saidaMF = saida
              saidaLoja4 -= saida
            }
          }
        }

        produtosList5.forEach { produtoInventario ->
          val estoquePK = produtoInventario.estoquePK ?: 0
          if (estoquePK > 0) {
            if (saidaLoja5 > 0) {
              val saida = minOf(estoquePK, saidaLoja5)
              produtoInventario.saidaPK = saida
              saidaLoja5 -= saida
            }
          }
        }

        produtosList8.forEach { produtoInventario ->
          val estoqueTM = produtoInventario.estoqueTM ?: 0
          if (estoqueTM > 0) {
            if (saidaLoja8 > 0) {
              val saida = minOf(estoqueTM, saidaLoja8)
              produtoInventario.saidaTM = saida
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
    val vencDS = if ((produto.vencimentoDS ?: 0) > 0 && (produto.estoqueDS ?: 0) > 0) produto.vencimentoDS else null
    val vencMR = if ((produto.vencimentoMR ?: 0) > 0 && (produto.estoqueMR ?: 0) > 0) produto.vencimentoMR else null
    val vencMF = if ((produto.vencimentoMF ?: 0) > 0 && (produto.estoqueMF ?: 0) > 0) produto.vencimentoMF else null
    val vencPK = if ((produto.vencimentoPK ?: 0) > 0 && (produto.estoquePK ?: 0) > 0) produto.vencimentoPK else null
    val vencTM = if ((produto.vencimentoTM ?: 0) > 0 && (produto.estoqueTM ?: 0) > 0) produto.vencimentoTM else null

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
        saidaDS = if (vencDS == null) 0 else produto.saidaDS,
        saidaMR = if (vencMR == null) 0 else produto.saidaMR,
        saidaMF = if (vencMF == null) 0 else produto.saidaMF,
        saidaPK = if (vencPK == null) 0 else produto.saidaPK,
        saidaTM = if (vencTM == null) 0 else produto.saidaTM,
        vencimentoDS = vencDS,
        vencimentoMR = vencMR,
        vencimentoMF = vencMF,
        vencimentoPK = vencPK,
        vencimentoTM = vencTM,
        vencimentoLoja = null,
        estoqueLoja = null,
      )
    }
  }
  return produtosList.distinctBy { prd ->
    "${prd.prdno} ${prd.grade} ${prd.dataEntrada} ${prd.vencimento}"
  }
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

