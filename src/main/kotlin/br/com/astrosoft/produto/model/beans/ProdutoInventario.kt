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
  var dataEntradaEdit: LocalDate?,
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
      dataEntradaEdit = dataEntradaEdit,
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

  override fun toString(): String {
    return "ProdutoInventario(loja=$loja, lojaAbrev=$lojaAbrev, prdno=$prdno, codigo=$codigo, descricao=$descricao, grade=$grade, unidade=$unidade, validade=$validade, vendno=$vendno, fornecedorAbrev=$fornecedorAbrev, dataEntrada=$dataEntrada, dataEntradaEdit=$dataEntradaEdit, estoqueTotal=$estoqueTotal, estoqueLoja=$estoqueLoja, vencimento=$vencimento, vencimentoEdit=$vencimentoEdit, movimento=$movimento, tipo=$tipo, tipoEdit=$tipoEdit)"
  }

  companion object {
    fun find(filtro: FiltroProdutoInventario): List<ProdutoInventario> {
      val produtos = saci.produtoValidade(filtro)
      val dataInicial = LocalDate.of(2024, 6, 1)
      val saidas = ProdutoSaida.findSaidas(filtro, dataInicial)
      val entradas = ProdutoRecebimento.findEntradas(filtro, dataInicial)

      val produtosSaida = produtos.produtosInventarioSaida(saidas)
      val produtosEntrada = produtosSaida.produtoInventariosEntradas(entradas)

      return produtosEntrada
        .filter { it.loja == filtro.storeno || filtro.storeno == 0 }
        .distinctBy { "${it.loja} ${it.prdno} ${it.grade} ${it.vencimentoStr} ${it.tipo} ${it.dataEntrada.toSaciDate()}" }
    }

    fun findAgrupado(filtro: FiltroProdutoInventario): List<ProdutoInventario> {
      val dataInicial = LocalDate.of(2024, 6, 1)
      val entradas = ProdutoRecebimento.findEntradas(filtro, dataInicial)
      val dataInicialSaida = entradas.mapNotNull { it.date }.minOrNull()
      val saidas = ProdutoSaida.findSaidas(filtro, dataInicial)

      val produtosEntrada = entradas.map { it.toProdutoInventario() }.agrupar()
      val produtosSaida = produtosEntrada.saidasAgrupadas(saidas).agrupar()

      return produtosSaida
    }

    private fun List<ProdutoInventario>.saidasAgrupadas(saidas: List<ProdutoSaida>): List<ProdutoInventario>{
      return sequence {
        yieldAll(this@saidasAgrupadas)
        this@saidasAgrupadas.groupBy { "${it.prdno} ${it.grade}" }.forEach {(_, produtoList)->
          val produto = produtoList.firstOrNull()
          if(produto != null) {
            saidas.filter {
              it.lojaDestino == 0
              && it.prdno == produto.prdno
              && it.grade == produto.grade
              && it.date.toSaciDate() >= produto.dataEntrada.toSaciDate()
            }.forEach {saida ->
              val produtoCopy = produto.copy {
                movimento = saida.qtty ?: 0
                vencimento = 0
                vencimentoEdit = 0
                dataEntrada = saida.date
                dataEntradaEdit = saida.date
                eTipo = ETipo.SAI
                tipoEdit = eTipo?.tipo
              }
              yield(produtoCopy)
            }
          }
        }
      }.toList()
    }

    private fun List<ProdutoInventario>.agrupar(): List<ProdutoInventario> {
      return this.filter { produto ->
        produto.eTipo != ETipo.TRA
      }.groupBy { "${it.prdno} ${it.grade}" }.flatMap { (_, produtoList) ->
        val list = produtoList.groupBy { it.loja }.map {
          it.value.firstOrNull()?.estoqueLoja ?: 0
        }

        val estoqueLoja = list.sum()

        produtoList.groupBy { "${it.vencimento} ${it.tipo} ${it.dataEntrada.toSaciDate()}" }
          .mapNotNull { (_, produtosVenc) ->
            val produtoVenc = produtosVenc.firstOrNull()
            produtoVenc?.estoqueLoja = estoqueLoja
            produtoVenc?.movimento = produtosVenc.sumOf { it.movimento ?: 0 }
            produtoVenc?.loja = 0
            produtoVenc?.lojaAbrev = "Todas"
            produtoVenc
          }
      }
    }

    private fun List<ProdutoInventario>.produtosInventarioSaida(
      saidas: List<ProdutoSaida>
    ): List<ProdutoInventario> {
      return sequence {
        yieldAll(this@produtosInventarioSaida)
        this@produtosInventarioSaida.groupBy { "${it.loja} ${it.prdno} ${it.grade}" }.forEach { (_, produtos) ->
          val produto = produtos.firstOrNull()
          val loja = produto?.loja ?: 0
          val prdno = produto?.prdno ?: ""
          val grade = produto?.grade ?: ""

          val produtoValidadeSaida = produtos.firstOrNull { it.eTipo == ETipo.SAI }
          val dataSaida = produtoValidadeSaida?.dataEntrada ?: LocalDate.of(2024, 6, 1)
          val saidasProduto = saidas.filter {
            it.lojaOrigem == loja
            && it.prdno == prdno
            && it.grade == grade
            && it.date.toSaciDate() >= dataSaida.toSaciDate()
          }
          val quantSaidas = saidasProduto.sumOf { it.qtty ?: 0 }
          if (produtoValidadeSaida == null) {
            val copy = produto?.copy {
              movimento = quantSaidas
              vencimento = 0
              vencimentoEdit = 0
              dataEntrada = dataSaida
              dataEntradaEdit = dataSaida
              eTipo = ETipo.SAI
              tipoEdit = eTipo?.tipo
            }
            if (copy != null)
              yield(copy)
          } else {
            produtoValidadeSaida.movimento = quantSaidas
          }

          val produtosTransf = saidas.filter {
            it.lojaDestino == loja
            && it.prdno == prdno
            && it.grade == grade
          }
          produtosTransf.forEach { produtoTransf ->
            val produtosValidadeTrans = this@produtosInventarioSaida.firstOrNull {
              it.loja == produtoTransf.lojaDestino
              && it.prdno == produtoTransf.prdno
              && it.grade == produtoTransf.grade
              && it.dataEntradaEdit.toSaciDate() == produtoTransf.date.toSaciDate()
              && it.tipo == ETipo.TRA.tipo
            }

            val quantSaidasTransf = produtoTransf.qtty ?: 0
            if (quantSaidasTransf > 0) {
              if (produtosValidadeTrans == null) {
                val dataSaidaTransf = produtoTransf.date
                val copy = produtos.firstOrNull()?.copy {
                  movimento = quantSaidasTransf
                  vencimento = 0
                  vencimentoEdit = 0
                  dataEntrada = dataSaidaTransf
                  dataEntradaEdit = dataSaidaTransf
                  eTipo = ETipo.TRA
                  tipoEdit = eTipo?.tipo
                }
                if (copy != null)
                  yield(copy)
              } else {
                produtosValidadeTrans.movimento = quantSaidasTransf
              }
            }
          }
        }
      }.toList()
    }

    private fun List<ProdutoInventario>.produtoInventariosEntradas(
      entradas: List<ProdutoRecebimento>
    ): List<ProdutoInventario> {
      return sequence {
        yieldAll(this@produtoInventariosEntradas)

        entradas.forEach { entrada ->
          val produtoValidade = this@produtoInventariosEntradas.firstOrNull {
            it.loja == entrada.loja
            && it.prdno == entrada.prdno
            && it.grade == entrada.grade
            && it.tipo == ETipo.REC.tipo
          }

          if (produtoValidade == null) {
            val produto = entrada.toProdutoInventario()
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
        dataEntradaEdit = date,
        estoqueTotal = qtty,
        movimento = qtty,
        vencimento = mesAno,
        tipo = ETipo.REC.tipo,
        tipoEdit = ETipo.REC.tipo,
        estoqueLoja = estoqueLoja,
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
  val dataEntrada: Int?,
)

enum class ETipo(val tipo: String, val descricao: String) {
  SAI("SAI", "Saída"),
  TRA("TRA", "Trans"),
  INV("INV", "Inv"),
  REC("REC", "Receb"),
}