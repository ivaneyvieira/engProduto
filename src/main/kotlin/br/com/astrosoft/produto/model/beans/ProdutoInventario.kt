package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
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
  var entrada: Int?,
) {
  val saldo: Int
    get() = (estoque ?: 0) - (saida ?: 0) + (entrada ?: 0)

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
      val saidas = ProdutoMovimentacao.findSaidas(dataInicial).groupBy {
        ChaveMovimentacao(
          lojaOrigem = it.lojaOrigem,
          prdno = it.prdno,
          grade = it.grade
        )
      }

      val produtosSaida = produtoInventariosSaidas(produtos, saidas)

      return produtosSaida.filter { it.loja == filtro.storeno || filtro.storeno == 0 }
    }

    private fun produtoInventariosSaidas(
      produtos: List<ProdutoInventario>,
      saidas: Map<ChaveMovimentacao, List<ProdutoMovimentacao>>
    ): List<ProdutoInventario> {
      val produtosGrupo = produtos.groupBy { "${it.loja} ${it.prdno} ${it.grade}" }
      val produtosNovos = produtosGrupo.flatMap { (_, produtosList) ->
        val loja = produtosList.firstOrNull()?.loja
        val prdno = produtosList.firstOrNull()?.prdno
        val grade = produtosList.firstOrNull()?.grade
        val data = produtosList.mapNotNull { it.dataEntrada }.minOrNull()

        val listaSaida = saidas[ChaveMovimentacao(loja, prdno, grade)].orEmpty().filter {
          it.date.toSaciDate() >= data.toSaciDate()
        }
        if (listaSaida.isEmpty()) {
          produtosList
        } else {
          listaSaida.flatMap { produtoMov ->
            var qttySaidaLoja = produtoMov.qtty ?: 0

            val produtosListFilter = produtosList.filter {
              (it.vencimento ?: 0) > 0 && it.loja == loja
            }.sortedBy { it.vencimento }

            sequence {
              produtosListFilter.forEach { produtoInventario ->
                val estoque = produtoInventario.saldo
                if (estoque > 0) {
                  if (qttySaidaLoja > 0) {
                    val saida = minOf(estoque, qttySaidaLoja)
                    produtoInventario.saida = saida
                    qttySaidaLoja -= saida
                    yield(produtoInventario)

                    val lojaDestino = produtoMov.lojaDestino
                    if (lojaDestino != null && lojaDestino != loja) {
                      val produtosEntrada = produtos.firstOrNull {
                        it.loja == lojaDestino &&
                        it.prdno == produtoInventario.prdno &&
                        it.grade == produtoInventario.grade &&
                        it.vencimento == produtoInventario.vencimento
                      }
                      if (produtosEntrada != null) {
                        produtosEntrada.entrada = (produtosEntrada.entrada ?: 0) + saida
                        yield(produtosEntrada)
                      } else {
                        val novo = ProdutoInventario(
                          loja = lojaDestino,
                          lojaAbrev = produtoMov.abrevDestino,
                          entrada = saida,
                          estoque = null,
                          saida = null,
                          prdno = produtoInventario.prdno,
                          codigo = produtoInventario.codigo,
                          descricao = produtoInventario.descricao,
                          grade = produtoInventario.grade,
                          unidade = produtoInventario.unidade,
                          validade = produtoInventario.validade,
                          vendno = produtoInventario.vendno,
                          fornecedorAbrev = produtoInventario.fornecedorAbrev,
                          dataEntrada = produtoInventario.dataEntrada,
                          estoqueTotal = produtoInventario.estoqueTotal,
                          vencimento = produtoInventario.vencimento
                        )
                        novo.update()
                        yield(novo)
                      }
                    }
                  }else{
                    yield(produtoInventario)
                  }
                }
              }
            }.toList()
          }
        }
      }
      return produtosNovos.distinctBy { "${it.loja} ${it.prdno} ${it.grade} ${it.vencimento}" }
    }
  }
}

fun List<ProdutoInventario>.resumo(): List<ProdutoInventarioResumo> {
  val produtosGroup = this.groupBy { "${it.prdno} ${it.grade} ${it.vencimento} ${it.dataEntrada?.format()}" }

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

