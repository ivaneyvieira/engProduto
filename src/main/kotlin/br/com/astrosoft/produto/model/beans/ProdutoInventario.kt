package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoInventario(
  var loja: Int?,
  var lojaAbrev: String?,
  var prdno: String?,
  var codigo: String?,
  var descricao: String?,
  var grade: String?,
  var unidade: String?,
  var validade: Int?,
  var vendno: Int?,
  var fornecedorAbrev: String?,
  var dataEntrada: LocalDate?,
  var estoqueTotal: Int?,
  var estoque: Int?,
  var vencimento: Int?,
  var saida: Int?,
) {

  val saldo: Int
    get() = (estoque ?: 0) - (saida ?: 0)

  var vencimentoStr: String?
    get() = vencimentoToStr(vencimento)
    set(value) {
      vencimento = mesAno(value)
    }

  private fun mesAno(value: String?): Int {
    value ?: return 0
    val mes = value.substring(0, 2).toIntOrNull() ?: return 0
    val ano = value.substring(3, 5).toIntOrNull() ?: return 0
    return mes + (ano + 2000) * 100
  }

  private fun vencimentoToStr(vencimento: Int?): String {
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

  companion object {
    fun find(filtro: FiltroProdutoInventario): List<ProdutoInventario> {
      val produtos = saci.produtoValidade(filtro)
      val dataInicial = produtos.filter {
        it.estoque != 0
      }.mapNotNull { it.dataEntrada }.minOrNull() ?: return produtos
      val saidas = ProdutoInventarioSaida.find(dataInicial).groupBy { ChaveSaida(it.loja, it.prdno, it.grade) }

      val produtosGrupo = produtos.groupBy { "${it.prdno} ${it.grade} ${it.dataEntrada}" }
      val produtosNovos = produtosGrupo.flatMap { (_, produtosList) ->
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
          (it.estoque ?: 0) > 0 && (it.vencimento ?: 0) > 0 && it.loja == 2
        }.sortedBy { it.vencimento }

        val produtosList3 = produtosList.filter {
          (it.vencimento ?: 0) > 0 && it.loja == 3
        }.sortedBy { it.vencimento }

        val produtosList4 = produtosList.filter {
          (it.vencimento ?: 0) > 0 && it.loja == 4
        }.sortedBy { it.vencimento }

        val produtosList5 = produtosList.filter {
          (it.vencimento ?: 0) > 0 && it.loja == 5
        }.sortedBy { it.vencimento }

        val produtosList8 = produtosList.filter {
          (it.vencimento ?: 0) > 0 && it.loja == 8
        }.sortedBy { it.vencimento }

        produtosList2.forEach { produtoInventario ->
          val estoqueDS = produtoInventario.estoque ?: 0
          if (estoqueDS > 0) {
            if (saidaLoja2 > 0) {
              val saida = minOf(estoqueDS, saidaLoja2)
              produtoInventario.saida = saida
              saidaLoja2 -= saida
            }
          }
        }

        produtosList3.forEach { produtoInventario ->
          val estoqueMR = produtoInventario.estoque ?: 0
          if (estoqueMR > 0) {
            if (saidaLoja3 > 0) {
              val saida = minOf(estoqueMR, saidaLoja3)
              produtoInventario.saida = saida
              saidaLoja3 -= saida
            }
          }
        }

        produtosList4.forEach { produtoInventario ->
          val estoqueMF = produtoInventario.estoque ?: 0
          if (estoqueMF > 0) {
            if (saidaLoja4 > 0) {
              val saida = minOf(estoqueMF, saidaLoja4)
              produtoInventario.saida = saida
              saidaLoja4 -= saida
            }
          }
        }

        produtosList5.forEach { produtoInventario ->
          val estoquePK = produtoInventario.estoque ?: 0
          if (estoquePK > 0) {
            if (saidaLoja5 > 0) {
              val saida = minOf(estoquePK, saidaLoja5)
              produtoInventario.saida = saida
              saidaLoja5 -= saida
            }
          }
        }

        produtosList8.forEach { produtoInventario ->
          val estoqueTM = produtoInventario.estoque ?: 0
          if (estoqueTM > 0) {
            if (saidaLoja8 > 0) {
              val saida = minOf(estoqueTM, saidaLoja8)
              produtoInventario.saida = saida
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

fun List<ProdutoInventario>.resumo(): List<ProdutoInventarioResumo> {
  val produtosGroup = this.groupBy { "${it.prdno} ${it.grade} ${it.vencimento}" }

  return produtosGroup.map { (_, produtos) ->
    ProdutoInventarioResumo(
      prdno = produtos.firstOrNull()?.prdno ?: "",
      codigo = produtos.firstOrNull()?.codigo ?: "",
      grade = produtos.firstOrNull()?.grade ?: "",
      dataEntrada = produtos.firstOrNull()?.dataEntrada,
      estoqueTotal = produtos.firstOrNull()?.estoqueTotal,
      estoqueDS = produtos.filter { it.loja == 2 }.sumOf { it.estoque ?: 0 },
      estoqueMR = produtos.filter { it.loja == 3 }.sumOf { it.estoque ?: 0 },
      estoqueMF = produtos.filter { it.loja == 4 }.sumOf { it.estoque ?: 0 },
      estoquePK = produtos.filter { it.loja == 5 }.sumOf { it.estoque ?: 0 },
      estoqueTM = produtos.filter { it.loja == 8 }.sumOf { it.estoque ?: 0 },
      saldo = produtos.sumOf { it.saldo },
      vencimentoStr = produtos.firstOrNull()?.vencimentoStr,
      vencimento = produtos.firstOrNull()?.vencimento,
      saldoDS = produtos.filter { it.loja == 2 }.sumOf { it.saldo },
      saldoMR = produtos.filter { it.loja == 3 }.sumOf { it.saldo },
      saldoMF = produtos.filter { it.loja == 4 }.sumOf { it.saldo },
      saldoPK = produtos.filter { it.loja == 5 }.sumOf { it.saldo },
      saldoTM = produtos.filter { it.loja == 8 }.sumOf { it.saldo },
      saidaDS = produtos.filter { it.loja == 2 }.sumOf { it.saida ?: 0 },
      saidaMR = produtos.filter { it.loja == 3 }.sumOf { it.saida ?: 0 },
      saidaMF = produtos.filter { it.loja == 4 }.sumOf { it.saida ?: 0 },
      saidaPK = produtos.filter { it.loja == 5 }.sumOf { it.saida ?: 0 },
      saidaTM = produtos.filter { it.loja == 8 }.sumOf { it.saida ?: 0 },
    )
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
  val storeno: Int,
)

