package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.toSaciDate
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
  var estoqueLoja: Int?,
  var estoque: Int?,
  var compras: Int?,
  var vencimento: Int?,
  var vencimentoEdit: Int?,
  var saidaVenda: Int?,
  var saidaTransf: Int?,
  var entradaCompra: Int?,
  var entradaTransf: Int?,
) {
  val saida: Int
    get() = (saidaVenda ?: 0) + (saidaTransf ?: 0)

  val entrada: Int
    get() = (entradaCompra ?: 0) + (entradaTransf ?: 0) + (compras ?: 0)

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

  private fun vencimentoToStr(vencimentoPar: Int?): String {
    val venc = vencimentoPar ?: 0
    val vencimentoStr = venc.toString()
    if (vencimentoStr.length != 6) {
      return when (venc) {
        0 -> "Saida"

        1 -> "Transf"

        else -> ""
      }
    } else {
      val mes = vencimentoStr.substring(4, 6)
      val ano = vencimentoStr.substring(2, 4)
      return "$mes/$ano"
    }
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
      compras = compras,
      estoqueLoja = estoqueLoja,
      vencimentoEdit = vencimentoEdit,
    )
    block(produto)
    return produto
  }

  companion object {
    fun find(filtro: FiltroProdutoInventario): List<ProdutoInventario> {
      val produtos = saci.produtoValidade(filtro)
      val dataInicial = produtos.mapNotNull { it.dataEntrada }.minOrNull()
      val saidas = ProdutoSaida.findSaidas(filtro, dataInicial)

      //val entradas = ProdutoRecebimento.findEntradas(filtro, dataInicial)

      //val produtosEntrada = produtoInventariosEntradas(produtos, entradas)
      //val produtosSaida = produtoInventariosSaidas(produtos, saidas)
      //return produtosSaida
      //  .filter { it.loja == filtro.storeno || filtro.storeno == 0 }
      //  .distinctBy { "${it.loja} ${it.prdno} ${it.grade} ${it.vencimento}" }

      return produtosInventarioCompras(produtos, saidas)
        .distinctBy { "${it.loja} ${it.prdno} ${it.grade} ${it.vencimentoStr}" }
        .filter { it.loja == filtro.storeno || filtro.storeno == 0 }
    }

    private fun produtosInventarioCompras(
      produtos: List<ProdutoInventario>,
      saidas: List<ProdutoSaida>
    ): List<ProdutoInventario> {
      return sequence {
        yieldAll(produtos)
        produtos.groupBy { "${it.loja} ${it.prdno} ${it.grade}" }.forEach { (_, produtos) ->
          val produtoCompra = produtos.firstOrNull { it.vencimento == 0 }
          if (produtoCompra == null) {
            val produto = produtos.firstOrNull()

            val copy = produto?.copy {
              estoque = null
              saidaVenda = null
              saidaTransf = null
              entradaTransf = null
              entradaCompra = null
              vencimento = 0
              vencimentoEdit = 0
              dataEntrada = null
              compras = null
            }
            if (copy != null)
              yield(copy)
          } else {
            val saidasProduto = saidas.filter {
              it.lojaOrigem == produtoCompra.loja
              && it.prdno == produtoCompra.prdno
              && it.grade == produtoCompra.grade
              && it.date.toSaciDate() >= produtoCompra.dataEntrada.toSaciDate()
            }
            val quantSaidas = saidasProduto.sumOf { it.qtty ?: 0 }
            produtoCompra.saidaVenda = quantSaidas
          }

          //Transferencias
          val produto = produtos.firstOrNull()
          val loja = produto?.loja ?: 0
          val prdno = produto?.prdno ?: ""
          val grade = produto?.grade ?: ""
          val dataEntradaTransf = produtos.mapNotNull { it.dataEntrada }.minOrNull()

          val saidasProduto = saidas.filter {
            it.lojaDestino == loja
            && it.prdno == prdno
            && it.grade == grade
            && it.date.toSaciDate() >= dataEntradaTransf.toSaciDate()
          }
          val quantSaidas = saidasProduto.sumOf { it.qtty ?: 0 }

          if (quantSaidas > 0) {
            val produtoTransferencia = produtos.firstOrNull { it.vencimento == 1 }

            if (produtoTransferencia == null) {
              val copy = produto?.copy {
                estoque = null
                saidaVenda = null
                saidaTransf = null
                entradaTransf = null
                entradaCompra = null
                vencimento = 1
                vencimentoEdit = 1
                dataEntrada = dataEntradaTransf
                compras = quantSaidas
              }
              if (copy != null)
                yield(copy)
            } else {
              produtoTransferencia.compras = quantSaidas
            }
          }
        }
      }.toList()
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
        compras = 0,
        estoqueLoja = 0,
        vencimentoEdit = mesAno,
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
          saidasList.forEach { saida ->
            produtosList.forEach { produto ->
              val dataSaida = saida.date.toSaciDate()
              val dataProduto = produto.dataEntrada.toSaciDate()
              var saidasQuant = 0

              if (dataSaida != 0 && dataProduto != 0) {
                saidasQuant = if (dataSaida >= dataProduto) saida.qtty ?: 0 else 0
              }

              val lojaDestino = saida.lojaDestino ?: 0
              val qtty = min(saidasQuant, produto.estoque ?: 0)
              if (lojaDestino == 0)
                produto.saidaVenda = (produto.saidaVenda ?: 0) + qtty
              else
                produto.saidaTransf = (produto.saidaTransf ?: 0) + qtty
              saidasQuant -= qtty
              yield(produto)
              /*
                            if (lojaDestino != 0) {
                              val inventarioDestino = produtos.firstOrNull {
                                it.loja == lojaDestino &&
                                it.prdno == produto.prdno &&
                                it.grade == produto.grade &&
                                it.vencimento == produto.vencimento
                              }
                              if (inventarioDestino == null) {
                                val novo = produto.copy {
                                  loja = saida.lojaDestino
                                  lojaAbrev = saida.abrevDestino
                                  entradaTransf = saida.qtty
                                  entradaCompra = null
                                  estoque = null
                                  saidaVenda = null
                                  saidaTransf = null
                                }
                                yield(novo)
                              } else {
                                inventarioDestino.entradaTransf = (inventarioDestino.entradaTransf ?: 0) + qtty
                                yield(inventarioDestino)
                              }
                            }
                            */
            }
          }
        }
      }.toList()
    }

    fun atualizaTabelas() {
      saci.atualizarTabelas()
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