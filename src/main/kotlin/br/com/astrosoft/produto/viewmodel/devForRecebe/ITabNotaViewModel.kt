package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoRef
import br.com.astrosoft.produto.model.saci

interface ITabNotaViewModel {
  fun addProduto(produto: NotaRecebimentoProdutoDev?)
  fun updateProduto(produto: NotaRecebimentoProdutoDev, grade: String?, ni: Int?)
  fun updateAcertoProduto(produto: NotaRecebimentoProdutoDev) {
    produto.updateAcertoProduto()
  }

  fun findProdutos(codigo: String): List<PrdGrade> {
    return saci.findProdutoGrades(codigo)
  }

  fun niToNF(ni: Int): String {
    return saci.niToNF(ni)
  }

  fun nfToNI(loja: Int, nfno: String, nfse: String): Int?{
    return saci.nfToNI(loja, nfno, nfse)
  }

  fun refToCodigo(ref: String): List<ProdutoRef> {
    return saci.refToCodigo(ref)
  }

  fun codigoToRef(codigo: String): List<ProdutoRef> {
    return saci.codigoToRef(codigo)
  }
}