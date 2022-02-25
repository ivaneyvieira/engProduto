package br.com.astrosoft.produto.view.notaEntrada.columns

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.ProdutoNFE
import com.vaadin.flow.component.grid.Grid

object ProdutoNFEViewColumns {
  fun Grid<ProdutoNFE>.produtoNFECodigo() = addColumnString(ProdutoNFE::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoNFE>.produtoNFEGrade() = addColumnString(ProdutoNFE::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoNFE>.produtoNFELocalizacao() = addColumnString(ProdutoNFE::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<ProdutoNFE>.produtoNFEBarcode() = addColumnString(ProdutoNFE::barcode) {
    this.setHeader("Código de Barras")
  }

  fun Grid<ProdutoNFE>.produtoNFEReferencia() = addColumnString(ProdutoNFE::referencia) {
    this.setHeader("Ref Forn")
  }

  fun Grid<ProdutoNFE>.produtoNFEMesesGarantia() = addColumnInt(ProdutoNFE::mesesGarantia) {
    this.setHeader("Validade")
  }

  fun Grid<ProdutoNFE>.produtoNFEQuantidadePacote() = addColumnInt(ProdutoNFE::quantidadePacote) {
    this.setHeader("Qnt Emb")
  }

  fun Grid<ProdutoNFE>.produtoNFEDescricao() = addColumnString(ProdutoNFE::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoNFE>.produtoNFEQuantidade() = addColumnInt(ProdutoNFE::quantidade) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoNFE>.produtoNFEQuantidadeRef() = addColumnInt(ProdutoNFE::qttyRef) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoNFE>.produtoNFEPrecoUnitario() = addColumnDouble(ProdutoNFE::preco) {
    this.setHeader("Preço")
  }

  fun Grid<ProdutoNFE>.produtoNFEPrecoTotal() = addColumnDouble(ProdutoNFE::total) {
    this.setHeader("Total")
  }
}