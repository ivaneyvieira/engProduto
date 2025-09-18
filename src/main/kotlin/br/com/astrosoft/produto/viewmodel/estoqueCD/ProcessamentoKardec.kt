package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.produto.model.beans.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import org.apache.poi.ss.formula.functions.T
import java.time.LocalDate
import kotlin.system.measureTimeMillis

object ProcessamentoKardec {
  fun updateAll() {

    val filtro = FiltroProdutoEstoque(
      pesquisa = "",
      codigo = 0,
      grade = "",
      caracter = ECaracter.TODOS,
      localizacao = "",
      fornecedor = "",
      centroLucro = 0,
      estoque = EEstoque.TODOS,
      saldo = 0,
      inativo = EInativo.TODOS,
      listaUser = listOf("TODOS"),
    )
    val produtos = ProdutoEstoque.findProdutoEstoque(filtro)
    produtos.forEach { prd ->
      if (!prd.isUpdated()) {
        updateSaldoKardec(prd)
      }
    }
  }

  fun updateSaldoKardec(produto: ProdutoEstoque) {
    produto.dataUpdate = null
    val listaKardec = updateKardec(produto)
    produto.dataUpdate = LocalDate.now()
    produto.kardec = listaKardec.ajustaOrdem().lastOrNull()?.saldo ?: 0
    produto.update()
  }

  private fun updateKardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    return runBlocking {
      ProdutoKardec.deleteKarde(produto)
      val list = fetchKardecFlow(produto)
      buildList {
        list.collect { produtoKardec: ProdutoKardec ->
          produtoKardec.save()
          add(produtoKardec)
        }
      }
    }
  }

  fun kardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    val listaKardec = if (produto.isUpdated()) {
      ProdutoKardec.findKardec(produto)
    } else {
      updateKardec(produto)
    }

    produto.dataUpdate = LocalDate.now()
    produto.kardec = listaKardec.ajustaOrdem().lastOrNull()?.saldo ?: 0
    produto.update()
    return listaKardec.ajustaOrdem()
  }

  fun updateKardec(produtos: List<ProdutoEstoque>) {
    produtos.forEach { produto ->
      updateSaldoKardec(produto)
    }
  }

  private fun fetchKardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    val date = produto.dataInicialDefault()
    val lista: List<ProdutoKardec> =
        produto.recebimentos(date) +
        //produto.ressuprimento(date) +
        produto.expedicao(date) +
        produto.reposicao(date) +
        produto.saldoInicial(date) +
        produto.acertoEstoque(date)
    return lista
  }

  suspend fun fetchKardecParallel(produto: ProdutoEstoque): List<ProdutoKardec> = coroutineScope {
    val date = produto.dataInicialDefault()

    // Dispare tudo em paralelo
    val recebimentosDefer = async { callSafely("Recebimento") { produto.recebimentos(date) } }
    // val ressuprimentoDefer = async { callSafely { produto.ressuprimento(date) } } // se/quando reativar
    val expedicaoDefer = async { callSafely("Expedição") { produto.expedicao(date) } }
    val reposicaoDefer = async { callSafely("Reposicao") { produto.reposicao(date) } }
    val saldoInicialDefer = async { callSafely("Saldo Inicial") { produto.saldoInicial(date) } }
    val acertoEstoqueDefer = async { callSafely("Acerto") { produto.acertoEstoque(date) } }

    val listaDeffer =
        listOf(
          recebimentosDefer,
          // ressuprimentoDefer,
          expedicaoDefer,
          reposicaoDefer,
          saldoInicialDefer,
          acertoEstoqueDefer
        )
    listaDeffer.awaitAll()                 // List<List<ProdutoKardec>>
      .flatten()
      .ajustaOrdem()// List<ProdutoKardec>
  }

  fun fetchKardecFlow(produto: ProdutoEstoque): Flow<ProdutoKardec> = channelFlow {
    val date = produto.dataInicialDefault()
    println("Início do processamento do produto ${produto.codigo} na data $date")

    fun launchSource(tipo: String, block: suspend () -> List<ProdutoKardec>) = launch {
      val itens = withContext(Dispatchers.IO) {
        var ret: List<ProdutoKardec>
        val tempo = measureTimeMillis {
          ret = runCatching { block() }.getOrElse { emptyList() }
        }
        println("Tempo $tipo = ${T::class.simpleName} ${tempo / 1000} s")
        ret
      }
      for (item in itens) trySend(item)
    }

    launchSource("Recebimento") { produto.recebimentos(date) }
    // launchSource { produto.ressuprimento(date) }
    launchSource("Expedicao") { produto.expedicao(date) }
    launchSource("Reposicao") { produto.reposicao(date) }
    launchSource("Saldo Inicial") { produto.saldoInicial(date) }
    launchSource("Acerto") { produto.acertoEstoque(date) }
  }

  // Helper para isolar IO e tratar falhas sem derrubar tudo
  private suspend inline fun <reified T> callSafely(tipo: String, crossinline block: suspend () -> T): T =
      withContext(Dispatchers.IO) {
        try {
          var ret: T
          val tempo = measureTimeMillis {
            ret = block()
          }
          println("Tempo $tipo = ${T::class.simpleName} ${tempo / 1000} s")
          ret
        } catch (e: Exception) {
          @Suppress("UNCHECKED_CAST")
          when (val empty = emptyList<Any>()) {
            is T -> empty
            else -> throw e // se não for lista, repropague
          }
        }
      }
}

fun List<ProdutoKardec>.ajustaOrdem(): List<ProdutoKardec> {
  var saldoAcumulado = 0
  return this.distinctBy { "${it.loja}${it.prdno}${it.grade}${it.data}${it.doc}${it.tipo}" }
    .sortedWith(compareBy({ it.data }, { it.loja }, { it.doc })).map {
      saldoAcumulado += (it.qtde ?: 0)
      it.copy(saldo = saldoAcumulado)
    }
}

fun main() {
  val home = System.getenv("HOME")
  val fileName = System.getenv("EBEAN_PROPS") ?: "$home/ebean.properties"
  System.setProperty("ebean.props.file", fileName)
  ProcessamentoKardec.updateAll()
}