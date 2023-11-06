package br.com.astrosoft.produto.view.notaEntrada.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.ProdutoNFE
import com.vaadin.flow.component.grid.Grid

object ProdutoNFEViewColumns {
  fun Grid<ProdutoNFE>.produtoNFECodigo() = columnGrid(ProdutoNFE::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoNFE>.produtoNFEGrade() = columnGrid(ProdutoNFE::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoNFE>.produtoNFEUnidade() = columnGrid(ProdutoNFE::unidade) {
    this.setHeader("Unid")
  }

  fun Grid<ProdutoNFE>.produtoNFELocalizacao() = columnGrid(ProdutoNFE::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<ProdutoNFE>.produtoNFEBarcode() = columnGrid(ProdutoNFE::barcode) {
    this.setHeader("Código de Barras")
  }

  fun Grid<ProdutoNFE>.produtoNFEReferencia() = columnGrid(ProdutoNFE::referencia) {
    this.setHeader("Ref Forn")
  }

  fun Grid<ProdutoNFE>.produtoNFEMesesGarantia() = columnGrid(ProdutoNFE::mesesGarantia) {
    this.setHeader("Validade")
  }

  fun Grid<ProdutoNFE>.produtoNFEQuantidadePacote() = columnGrid(ProdutoNFE::quantidadePacote) {
    this.setHeader("Qnt Emb")
  }

  fun Grid<ProdutoNFE>.produtoNFEDescricao() = columnGrid(ProdutoNFE::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoNFE>.produtoNFEQuantidade() = columnGrid(ProdutoNFE::quantidade) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoNFE>.produtoNFEQuantidadeRef() = columnGrid(ProdutoNFE::qttyRef) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoNFE>.produtoNFEPrecoUnitario() = columnGrid(ProdutoNFE::preco) {
    this.setHeader("Preço")
  }

  fun Grid<ProdutoNFE>.produtoNFEPrecoTotal() = columnGrid(ProdutoNFE::total) {
    this.setHeader("Total")
  }
}