package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.produto.model.beans.ETipoKardec
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoKardec
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
  /*
   fun updateAll() {

   }
 */
  fun updateSaldoKardec(produto: ProdutoEstoque) {
    val loja = produto.loja ?: 4
    produto.dataUpdate = null
    val listaKardec = updateKardec(produto = produto, loja, dataIncial = produto.dataInicialDefault())
    produto.dataUpdate = LocalDate.now()
    produto.kardec = listaKardec.ajustaOrdem().lastOrNull()?.saldo ?: 0
    produto.updateKardec()
  }

  private fun updateKardec(produto: ProdutoEstoque, loja: Int, dataIncial: LocalDate): List<ProdutoKardec> {
    return runBlocking {
      ProdutoKardec.deleteKardec(produto)
      //val list = fetchKardecFlow(produto, loja, dataIncial)
      val listBuild = fetchKardec(produto, loja, dataIncial)
      listBuild.forEachIndexed { index, produtoKardec: ProdutoKardec ->
        produtoKardec.save()
        println(index)
      }
      listBuild
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

  fun fetchKardecFlow(produto: ProdutoEstoque, loja: Int, dataIncial: LocalDate): Flow<ProdutoKardec> = channelFlow {
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

    launchSource("Recebimento") { produto.recebimentos(loja, dataIncial) }
    // launchSource { produto.ressuprimento(date) }
    launchSource("Expedicao") { produto.expedicao(loja, dataIncial) }
    launchSource("Reposicao") { produto.reposicao(loja, dataIncial) }
    launchSource("Saldo Inicial") { produto.saldoInicial(loja, dataIncial) }
    launchSource("Acerto") { produto.acertoEstoque(loja, dataIncial) }
  }

  fun fetchKardec(produto: ProdutoEstoque, loja: Int, dataIncial: LocalDate): List<ProdutoKardec> {
    println("Início do processamento do produto ${produto.codigo} na data $dataIncial")

    val recebimento = produto.recebimentos(loja, dataIncial)
    val expedicao = produto.expedicao(loja, dataIncial)
    val reposicao = produto.reposicao(loja, dataIncial)
    val saldoInicial = produto.saldoInicial(loja, dataIncial)
    val acertoEstoque = produto.acertoEstoque(loja, dataIncial)
    return recebimento + expedicao + reposicao + saldoInicial + acertoEstoque
  }
}

fun List<ProdutoKardec>.ajustaOrdem(): List<ProdutoKardec> {
  var saldoAcumulado = 0
  return this.distinctBy { "${it.loja} ${it.prdno} ${it.grade} ${it.data} ${it.doc} ${it.tipo?.num}" }
    .sortedWith(comparator = compareBy({ it.data }, { it.tipo?.num }, { it.loja }, { it.doc })).map {
      saldoAcumulado += (it.qtde ?: 0)
      it.copy(saldo = saldoAcumulado)
    }
}

fun main() {
  val home = System.getenv("HOME")
  val fileName = System.getenv("EBEAN_PROPS") ?: "$home/ebean.properties"
  System.setProperty("ebean.props.file", fileName)
  //ProcessamentoKardec.updateAll()
}