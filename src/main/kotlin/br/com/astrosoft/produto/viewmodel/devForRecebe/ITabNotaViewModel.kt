package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.saci

interface ITabNotaViewModel {
  fun findProdutos(codigo: String): List<PrdGrade>
  fun addProduto(produto: NotaRecebimentoProdutoDev?)
  fun updateProduto(produto: NotaRecebimentoProdutoDev, grade: String?, ni: Int?)
  fun updateAcertoProduto(produto: NotaRecebimentoProdutoDev) {
    produto.updateAcertoProduto()
  }

  fun niToNF(ni: Int): String {
    return saci.niToNF(ni)
  }

  fun nfToNI(loja: Int, nfno: String, nfse: String): Int?{
    return saci.nfToNI(loja, nfno, nfse)
  }

  fun refToCodigo(ref: String): String?{
    return saci.refToCodigo(ref)
  }

  fun codigoToRef(codigo: String): String? {
    return saci.codigoToRef(codigo)
  }
}