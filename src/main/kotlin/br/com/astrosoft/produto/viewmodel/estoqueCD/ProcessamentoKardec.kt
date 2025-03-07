package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.produto.model.beans.*
import java.time.LocalDate

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
      if (prd.dataUpdate != LocalDate.now()) {
        updateSaldoKardec(prd)
      }
    }
  }

  fun updateSaldoKardec(produto: ProdutoEstoque) {
    produto.dataUpdate = null
    val kardec = kardec(produto)
    produto.kardec = kardec.lastOrNull()?.saldo ?: 0
    produto.update()
  }

  private fun updateKardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    ProdutoKardec.deleteList(produto)
    val list = fetchKardec(produto)
    list.filter { produtoKardec: ProdutoKardec ->
      true // produtoKardec.data?.isBefore(LocalDate.now()) ?: true
    }.forEach { produtoKardec: ProdutoKardec ->
      produtoKardec.save()
    }
    produto.dataUpdate = LocalDate.now()
    produto.update()
    return list.ajustaOrdem()
  }

  fun kardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    if (produto.dataUpdate != LocalDate.now()) {
      val kardec = updateKardec(produto)
      produto.kardec = kardec.lastOrNull()?.saldo ?: 0
      produto.update()
      return kardec
    }
    val saldoKardec = ProdutoKardec.findKardec(produto)
    val saldoHoje = emptyList<ProdutoKardec>() //fetchKardecHoje (produto)
    return (saldoHoje + saldoKardec).ajustaOrdem()
  }

  fun updateKardec(produtos: List<ProdutoEstoque>) {
    produtos.forEach { produto ->
      updateSaldoKardec(produto)
    }
  }

  private fun fetchKardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    val date = produto.dataInicialDefault
    val lista: List<ProdutoKardec> =
        produto.recebimentos(date) +
        produto.ressuprimento(date) +
        produto.expedicao(date) +
        produto.reposicao(date) +
        produto.saldoAnterior(date) +
        produto.acertoEstoque(date)
    return lista.ajustaOrdem()
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