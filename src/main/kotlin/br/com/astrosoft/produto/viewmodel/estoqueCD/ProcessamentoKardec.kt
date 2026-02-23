package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.produto.model.beans.ETipoKardec
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoKardec
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

object ProcessamentoKardec {
  fun updateSaldoControleKardec(produto: ProdutoEstoque) {
    produto.dataUpdate = null
    val listaKardec = updateControleKardec(produto = produto)
    produto.dataUpdate = LocalDate.now()
    produto.kardec = listaKardec.ajustaOrdem().lastOrNull()?.saldo ?: 0
    produto.updateKardec()
  }

  fun updateSaldoKardec(produto: ProdutoEstoque) {
    val loja = produto.loja ?: 4
    produto.dataUpdate = null
    val listaKardec = updateKardec(produto = produto, loja = loja, dataIncial = produto.dataInicialDefault())
    produto.dataUpdate = LocalDate.now()
    produto.kardec = listaKardec.ajustaOrdem().lastOrNull()?.saldo ?: 0
    produto.updateKardec()
  }

  private fun updateKardec(produto: ProdutoEstoque, loja: Int, dataIncial: LocalDate): List<ProdutoKardec> {
    return runBlocking {
      ProdutoKardec.deleteKardec(produto)
      val listBuild = fetchKardec(produto, loja, dataIncial)
      listBuild.forEachIndexed { index, produtoKardec: ProdutoKardec ->
        produtoKardec.save()
      }
      listBuild
    }
  }

  private fun updateControleKardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    return runBlocking {
      ProdutoKardec.deleteKardec(produto)
      val listBuild = fetchControleKardec(produto)
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

  fun updateControleKardec(produtos: List<ProdutoEstoque>) {
    produtos.forEach { produto ->
      updateSaldoControleKardec(produto)
    }
  }

  fun fetchKardec(produto: ProdutoEstoque, loja: Int, dataIncial: LocalDate): List<ProdutoKardec> = runBlocking {
    println("Início do processamento do produto ${produto.codigo} na data $dataIncial")

    val recebimento = async { produto.recebimentos(loja, dataIncial) }
    val expedicao = async { produto.expedicao(loja, dataIncial) }
    val reposicao = async { produto.reposicao(loja, dataIncial) }
    val saldoInicial = async { produto.saldoInicial(loja, dataIncial) }
    val acertoEstoque = async { produto.acertoEstoque(loja, dataIncial) }
    val movimentacaoEstoque = async { produto.movimentacaoEstoque(loja, dataIncial) }
    recebimento.await() + expedicao.await() + reposicao.await() + saldoInicial.await() + acertoEstoque.await() + movimentacaoEstoque.await()
  }

  fun fetchControleKardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    println("Início do processamento do produto ${produto.codigo}")
    return produto.controleKardec()
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