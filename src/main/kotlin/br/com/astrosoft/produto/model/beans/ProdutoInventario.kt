package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
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
  var estoqueLoja: Int?,
  var vencimento: Int?,
  var vencimentoEdit: Int?,
  var movimento: Int?,
  var tipo: String?,
  var tipoEdit: String?,
) {
  var eTipo: ETipo?
    get() = ETipo.entries.firstOrNull { it.tipo == tipo }
    set(value) {
      tipo = value?.tipo ?: ""
    }

  val tipoStr
    get() = eTipo?.descricao ?: ""

  val saldo: Int
    get() = when (eTipo) {
      ETipo.SAI -> -(movimento ?: 0)
      ETipo.TRA -> movimento ?: 0
      ETipo.INV -> movimento ?: 0
      ETipo.REC -> movimento ?: 0
      null      -> 0
    }

  val saida: Int
    get() = if (saldo < 0) saldo else 0

  val entrada: Int
    get() = if (saldo > 0) saldo else 0

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
      return ""
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
      movimento = movimento,
      vencimento = vencimento,
      estoqueLoja = estoqueLoja,
      vencimentoEdit = vencimentoEdit,
      tipo = tipo,
      tipoEdit = tipoEdit,
    )
    block(produto)
    return produto
  }

  companion object {
    fun find(filtro: FiltroProdutoInventario): List<ProdutoInventario> {
      val produtos = saci.produtoValidade(filtro)
      val dataInicial = LocalDate.of(2024, 6, 1)

      val saidas = ProdutoSaida.findSaidas(filtro, dataInicial)
      val entradas = ProdutoRecebimento.findEntradas(filtro, dataInicial)

      val produtosCompra = produtosInventarioSaida(produtos, saidas)
      val produtosEntrada = produtoInventariosEntradas(produtosCompra, entradas)
      //val produtosSaida = produtoInventariosSaidas(produtos, saidas)
      //return produtosSaida
      //  .filter { it.loja == filtro.storeno || filtro.storeno == 0 }
      //  .distinctBy { "${it.loja} ${it.prdno} ${it.grade} ${it.vencimento}" }
      return produtosEntrada
        .filter { it.loja == filtro.storeno || filtro.storeno == 0 }
        .distinctBy { "${it.loja} ${it.prdno} ${it.grade} ${it.vencimentoStr} ${it.tipo}" }
    }

    private fun produtosInventarioSaida(
      produtos: List<ProdutoInventario>,
      saidas: List<ProdutoSaida>
    ): List<ProdutoInventario> {
      return sequence {
        yieldAll(produtos)
        produtos.groupBy { "${it.loja} ${it.prdno} ${it.grade}" }.forEach { (_, produtos) ->
          val loja = produtos.firstOrNull()?.loja ?: 0
          val prdno = produtos.firstOrNull()?.prdno ?: ""
          val grade = produtos.firstOrNull()?.grade ?: ""
          val saidasProduto = saidas.filter {
            it.lojaOrigem == loja
            && it.prdno == prdno
            && it.grade == grade
          }
          val quantSaidas = saidasProduto.sumOf { it.qtty ?: 0 }
          val dataSaída = saidasProduto.mapNotNull { it.date }.maxOrNull()
          val produtoSaida = produtos.firstOrNull { it.eTipo == ETipo.SAI && it.vencimento == 0 }
          if (produtoSaida == null) {
            val produto = produtos.firstOrNull()

            val copy = produto?.copy {
              movimento = quantSaidas
              vencimento = 0
              vencimentoEdit = 0
              dataEntrada = dataSaída
              eTipo = ETipo.SAI
              tipoEdit = eTipo?.tipo
            }
            if (copy != null)
              yield(copy)
          } else {
            produtoSaida.movimento = quantSaidas
            produtoSaida.dataEntrada = dataSaída
          }

          val saidaProdutoTransf = saidas.filter {
            it.lojaDestino == loja
            && it.prdno == prdno
            && it.grade == grade
          }
          val quantSaidasTransf = saidaProdutoTransf.sumOf { it.qtty ?: 0 }
          if (quantSaidasTransf > 0) {
            val dataSaídaTransf = saidaProdutoTransf.mapNotNull { it.date }.maxOrNull()
            val copy = produtos.firstOrNull()?.copy {
              movimento = quantSaidasTransf
              vencimento = 0
              vencimentoEdit = 0
              dataEntrada = dataSaídaTransf
              eTipo = ETipo.TRA
              tipoEdit = eTipo?.tipo
            }
            if (copy != null)
              yield(copy)
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
            produto.movimento = (produto.movimento ?: 0) + entradaQtty
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
        movimento = qtty,
        vencimento = mesAno,
        tipo = ETipo.INV.tipo,
        tipoEdit = ETipo.INV.tipo,
        estoqueLoja = 0,
        vencimentoEdit = mesAno,
      )
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
      estoqueDS = produtos.filter { it.loja == 2 }.sumOf { it.movimento ?: 0 },
      estoqueMR = produtos.filter { it.loja == 3 }.sumOf { it.movimento ?: 0 },
      estoqueMF = produtos.filter { it.loja == 4 }.sumOf { it.movimento ?: 0 },
      estoquePK = produtos.filter { it.loja == 5 }.sumOf { it.movimento ?: 0 },
      estoqueTM = produtos.filter { it.loja == 8 }.sumOf { it.movimento ?: 0 },
      saldo = produtos.sumOf { it.saldo },
      vencimentoStr = produtos.firstOrNull()?.vencimentoStr,
      vencimento = produtos.firstOrNull()?.vencimento,
      //Saldo
      saldoDS = produtos.filter { it.loja == 2 }.sumOf { it.saldo },
      saldoMR = produtos.filter { it.loja == 3 }.sumOf { it.saldo },
      saldoMF = produtos.filter { it.loja == 4 }.sumOf { it.saldo },
      saldoPK = produtos.filter { it.loja == 5 }.sumOf { it.saldo },
      saldoTM = produtos.filter { it.loja == 8 }.sumOf { it.saldo },
      //Saida
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

enum class ETipo(val tipo: String, val descricao: String) {
  SAI("SAI", "Saída"),
  TRA("TRA", "Trans"),
  INV("INV", "Inv"),
  REC("REC", "Receb"),
}