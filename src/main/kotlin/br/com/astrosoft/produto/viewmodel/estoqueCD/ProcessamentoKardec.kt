package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.produto.model.beans.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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
        updateSaldoKardec(produto = prd)
      }
    }
  }

  fun updateSaldoKardec(produto: ProdutoEstoque) {
    produto.dataUpdate = null
    val listaKardec = updateKardec(produto = produto, dataIncial = produto.dataInicialDefault())
    produto.dataUpdate = LocalDate.now()
    produto.kardec = listaKardec.ajustaOrdem().lastOrNull()?.saldo ?: 0
    produto.update()
  }

  private fun updateKardec(produto: ProdutoEstoque, dataIncial: LocalDate): List<ProdutoKardec> {
    return runBlocking {
      ProdutoKardec.deleteKarde(produto)
      val list = fetchKardecFlow(produto, dataIncial)
      buildList {
        list.collect { produtoKardec: ProdutoKardec ->
          produtoKardec.save()
          add(produtoKardec)
        }
      }
    }
  }

  fun kardec(produto: ProdutoEstoque, dataIncial: LocalDate?): List<ProdutoKardec> {
    val data = dataIncial ?: produto.dataInicialDefault()
    val lista = ProdutoKardec.findKardec(produto).ajustaOrdem()
    val listaAntes = lista.filter {
      val dataK = it.data ?: return@filter false
      dataK < data
    }
    val listaDepois = lista.filter {
      val dataK = it.data ?: return@filter false
      dataK >= data
    }

    val ultimoMov = listaAntes.lastOrNull()?.copy(data = data)
    val listaSaldo = saldoAnterior(produto, ultimoMov)
    return (listaSaldo + listaDepois).ajustaOrdem()
  }

  private fun saldoAnterior(produto: ProdutoEstoque, ultimoMov: ProdutoKardec?): List<ProdutoKardec> {
    ultimoMov ?: return emptyList()
    return listOf(
      ProdutoKardec(
        loja = produto.loja,
        prdno = produto.prdno,
        grade = produto.grade,
        data = ultimoMov.data,
        doc = "Estoque",
        tipo = ETipoKardec.INICIAL,
        qtde = ultimoMov.saldo,
        saldo = 0,
        userLogin = "ADM",
        observacao = ""
      )
    )
  }

  fun updateKardec(produtos: List<ProdutoEstoque>) {
    produtos.forEach { produto ->
      updateSaldoKardec(produto)
    }
  }

  fun fetchKardecFlow(produto: ProdutoEstoque, dataIncial: LocalDate): Flow<ProdutoKardec> = channelFlow {
    println("Início do processamento do produto ${produto.codigo} na data $dataIncial")

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

    launchSource("Recebimento") { produto.recebimentos(dataIncial) }
    // launchSource { produto.ressuprimento(date) }
    launchSource("Expedicao") { produto.expedicao(dataIncial) }
    launchSource("Reposicao") { produto.reposicao(dataIncial) }
    launchSource("Saldo Inicial") { produto.saldoInicial(dataIncial) }
    launchSource("Acerto") { produto.acertoEstoque(dataIncial) }
  }
}

fun List<ProdutoKardec>.ajustaOrdem(): List<ProdutoKardec> {
  var saldoAcumulado = 0
  return this.distinctBy { "${it.loja}${it.prdno}${it.grade}${it.data}${it.doc}${it.tipo?.num}" }
    .sortedWith(compareBy({ it.data }, { it.loja }, { it.tipo?.num }, { it.doc })).map {
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