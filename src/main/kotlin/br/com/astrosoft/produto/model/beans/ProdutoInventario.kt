package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import kotlin.math.min

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
  var saidaVenda: Int?,
  var saidaTransf: Int?,
  var entradaCompra: Int?,
  var entradaTransf: Int?,
) {
  val saida: Int
    get() = (saidaVenda ?: 0) + (saidaTransf ?: 0)
  val entrada: Int
    get() = (entradaCompra ?: 0) + (entradaTransf ?: 0)

  val saldo: Int
    get() = (estoque ?: 0) - saida + entrada

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

  fun copy(block: ProdutoInventario.() -> Unit = {}): ProdutoInventario {
    val produto = ProdutoInventario(
      loja = loja,
      lojaAbrev = lojaAbrev,
      prdno = prdno,
      codigo = codigo,
      descricao = descricao,
      grade = grade,
      unidade = unidade,
      validade = validade,
      vendno = vendno,
      fornecedorAbrev = fornecedorAbrev,
      dataEntrada = dataEntrada,
      estoqueTotal = estoqueTotal,
      estoque = estoque,
      vencimento = vencimento,
      saidaVenda = saidaVenda,
      saidaTransf = saidaTransf,
      entradaCompra = entradaCompra,
      entradaTransf = entradaTransf,
    )
    block(produto)
    return produto
  }

  companion object {
    fun find(filtro: FiltroProdutoInventario): List<ProdutoInventario> {
      val produtos = saci.produtoValidade(filtro)
      val dataInicial = produtos.mapNotNull { it.dataEntrada }.minOrNull()
      val saidas = ProdutoSaida.findSaidas(filtro, dataInicial)

      val entradas = ProdutoRecebimento.findEntradas(filtro, dataInicial)

      //val produtosEntrada = produtoInventariosEntradas(produtos, entradas)

      val produtosSaida = produtoInventariosSaidas(entradas, saidas)

      return produtosSaida
        .filter { it.loja == filtro.storeno || filtro.storeno == 0 }
        .filter { it.entrada > 0 || it.saida > 0 || (it.estoque ?: 0) > 0 }
        .distinctBy { "${it.loja} ${it.prdno} ${it.grade} ${it.vencimento}" }
    }

    private fun produtoInventariosEntradas(
      produtos: List<ProdutoInventario>,
      entradas: List<ProdutoRecebimento>
    ): List<ProdutoInventario> {
      val produtosGrupo = produtos.groupBy { ChaveProdutoInventario(it.loja, it.prdno, it.grade, it.vencimento) }
      val entradasGrupo = entradas.groupBy { ChaveProdutoInventario(it.loja, it.prdno, it.grade, it.mesAno) }

      val chaveOnlyProdutos = produtosGrupo.keys - entradasGrupo.keys
      val chaveOnlyEntradas = entradasGrupo.keys - produtosGrupo.keys
      val chaveBoth = produtosGrupo.keys.intersect(entradasGrupo.keys)


      return sequence {
        chaveOnlyProdutos.forEach { chave ->
          val produtosList = produtosGrupo[chave].orEmpty()
          yieldAll(produtosList)
        }
        chaveOnlyEntradas.forEach { chave ->
          val entradasList = entradasGrupo[chave].orEmpty()
          yieldAll(entradasList.map { it.toProdutoInventario() })
        }
        chaveBoth.forEach { chave ->
          val produtosList = produtosGrupo[chave].orEmpty()
          val entradasList = entradasGrupo[chave].orEmpty()
          val entradaQtty = entradasList.sumOf { it.qtty ?: 0 }
          produtosList.forEach { produto ->
            produto.entradaCompra = entradaQtty
            yield(produto)
          }
        }

      }.toList()
    }

    private fun ProdutoRecebimento.toProdutoInventario(): ProdutoInventario {
      return ProdutoInventario(
        loja = loja,
        lojaAbrev = lojaAbrev,
        prdno = prdno,
        codigo = codigo,
        descricao = descricao,
        grade = grade,
        unidade = unidade,
        validade = validade,
        vendno = vendno,
        fornecedorAbrev = fornecedorAbrev,
        dataEntrada = date,
        estoqueTotal = qtty,
        estoque = qtty,
        vencimento = mesAno,
        saidaVenda = 0,
        saidaTransf = 0,
        entradaCompra = qtty,
        entradaTransf = 0,
      )
    }

    private fun produtoInventariosSaidas(
      produtos: List<ProdutoInventario>,
      saidas: List<ProdutoSaida>
    ): List<ProdutoInventario> {
      val produtosGroup = produtos.groupBy { ChaveMovimentacao(it.loja, it.prdno, it.grade) }
      val saidasGroup = saidas.groupBy { ChaveMovimentacao(it.lojaOrigem, it.prdno, it.grade) }

      val onlyChaveProdutos = produtosGroup.keys - saidasGroup.keys
      val onlyChaveSaidas = saidasGroup.keys - produtosGroup.keys
      val bothChave = produtosGroup.keys.intersect(saidasGroup.keys)

      return sequence {
        onlyChaveProdutos.forEach { chave ->
          val produtosList = produtosGroup[chave].orEmpty()
          yieldAll(produtosList)
        }
        onlyChaveSaidas.forEach { _ ->
          //val saidasList = saidasGroup[chave].orEmpty()
          //Não faz nada
        }
        bothChave.forEach { chave ->
          val produtosList = produtosGroup[chave].orEmpty().sortedBy { it.vencimento }
          val saidasList = saidasGroup[chave].orEmpty()
          var saidasQuant = saidasList.sumOf { it.qtty ?: 0 }
          produtosList.forEach { produto ->
            val qtty = min(saidasQuant, produto.estoque ?: 0)
            produto.saidaVenda = (produto.saidaVenda ?: 0) + qtty
            saidasQuant -= qtty
            yield(produto)

            val lojaDestino = saidasList.firstOrNull()?.lojaDestino
            if (lojaDestino != null) {
              val iventarioDestino = produtos.firstOrNull {
                it.loja == lojaDestino &&
                it.prdno == produto.prdno &&
                it.grade == produto.grade &&
                it.vencimento == produto.vencimento
              }
              if (iventarioDestino == null) {
                val novo = produto.copy {
                  loja = lojaDestino
                  lojaAbrev = saidasList.firstOrNull()?.abrevDestino
                  entradaTransf = qtty
                  entradaCompra = null
                  estoque = null
                  saidaVenda = null
                  saidaTransf = null
                }
                yield(novo)
              } else {
                iventarioDestino.entradaTransf = (iventarioDestino.entradaTransf ?: 0) + qtty
                yield(iventarioDestino)
              }
            }
          }
        }
      }.toList()
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
      saidaDS = produtos.filter { it.loja == 2 }.sumOf { it.saida },
      saidaMR = produtos.filter { it.loja == 3 }.sumOf { it.saida },
      saidaMF = produtos.filter { it.loja == 4 }.sumOf { it.saida },
      saidaPK = produtos.filter { it.loja == 5 }.sumOf { it.saida },
      saidaTM = produtos.filter { it.loja == 8 }.sumOf { it.saida },
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

data class ChaveProdutoInventario(
  val loja: Int?,
  val prdno: String?,
  val grade: String?,
  val vencimento: Int?,
)