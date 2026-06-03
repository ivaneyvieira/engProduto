package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.produto.model.beans.ETipoKardec
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoKardex
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
    val listaKardec = updateKardex(produto = produto, loja = loja, dataIncial = produto.dataInicialDefault())
    produto.dataUpdate = LocalDate.now()
    val listaOrdenada = listaKardec.ajustaOrdem()
    produto.kardec = listaOrdenada.lastOrNull()?.saldo ?: 0
    produto.updateKardec()
  }

  fun updateSaldoKardecMov(produto: ProdutoEstoque, doc: String) {
    val loja = produto.loja ?: 4
    produto.dataUpdate = null
    ProdutoKardex.deleteKardecMov(produto, doc)
    val listaKardec = produto.movimentacaoEstoque(loja, produto.dataInicialDefault())
    listaKardec.forEach { produto ->
      produto.save()
    }

    val kardecProduto = ProdutoKardex.findKardec(produto).ajustaOrdem()
    kardecProduto.forEach { produto ->
      produto.save()
    }

    produto.dataUpdate = LocalDate.now()
    produto.kardec = kardecProduto.ajustaOrdem().lastOrNull()?.saldo ?: 0
    produto.updateKardec()
  }

  private fun updateKardex(produto: ProdutoEstoque, loja: Int, dataIncial: LocalDate): List<ProdutoKardex> {
    return runBlocking {
      ProdutoKardex.deleteKardec(produto)
      val listBuild = fetchKardec(produto, loja, dataIncial)
      listBuild.forEach { produtoKardec: ProdutoKardex ->
        produtoKardec.save()
      }
      listBuild
    }
  }

  private fun updateControleKardec(produto: ProdutoEstoque): List<ProdutoKardex> {
    return runBlocking {
      ProdutoKardex.deleteKardec(produto)
      val listBuild = fetchControleKardec(produto)
      listBuild.forEachIndexed { index, produtoKardec: ProdutoKardex ->
        produtoKardec.save()
        println(index)
      }
      listBuild
    }
  }

  fun kardec(produto: ProdutoEstoque, dataIncial: LocalDate?): List<ProdutoKardex> {
    val data = dataIncial ?: produto.dataInicialDefault()
    val listaBruta = ProdutoKardex.findKardec(produto)
    val lista = listaBruta.ajustaOrdem()
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

  private fun saldoAnterior(produto: ProdutoEstoque, ultimoMov: ProdutoKardex?): List<ProdutoKardex> {
    ultimoMov ?: return emptyList()
    return listOf(
      ProdutoKardex(
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

  fun updateKardex(produtos: List<ProdutoEstoque>) {
    produtos.forEach { produto ->
      updateSaldoKardec(produto)
    }
  }

  fun updateKardecMov(produtos: List<ProdutoEstoque>, doc: String) {
    produtos.forEach { produto ->
      updateSaldoKardecMov(produto, doc)
    }
  }

  fun updateControleKardec(produtos: List<ProdutoEstoque>) {
    produtos.forEach { produto ->
      updateSaldoControleKardec(produto)
    }
  }

  fun fetchKardec(produto: ProdutoEstoque, loja: Int, dataInicial: LocalDate): List<ProdutoKardex> = runBlocking {
    println("Início do processamento do produto ${produto.codigo} na data $dataInicial")

    val recebimento = async { produto.recebimentos(loja, dataInicial) }
    val expedicao = async { produto.expedicao2(loja, dataInicial) }
    val reposicao = async { produto.reposicao(loja, dataInicial) }
    val saldoInicial = async { produto.saldoInicial(loja, dataInicial) }
    val acertoEstoque = async { produto.acertoEstoque(loja, dataInicial) }
    val movimentacaoEstoque = async { produto.movimentacaoEstoque(loja, dataInicial) }
    val devolucao = async { produto.devolucao(loja, dataInicial) }

    recebimento.await() + expedicao.await() + reposicao.await() + saldoInicial.await() + acertoEstoque.await() +
    movimentacaoEstoque.await() + devolucao.await()
  }

  fun fetchControleKardec(produto: ProdutoEstoque): List<ProdutoKardex> {
    println("Início do processamento do produto ${produto.codigo}")
    return produto.controleKardec()
  }
}

fun List<ProdutoKardex>.ajustaOrdem(): List<ProdutoKardex> {
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