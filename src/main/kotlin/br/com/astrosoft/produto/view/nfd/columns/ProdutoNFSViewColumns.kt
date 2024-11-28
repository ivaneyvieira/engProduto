package br.com.astrosoft.produto.view.nfd.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.produto.model.beans.ProdutoNFS
import com.vaadin.flow.component.grid.Grid

object ProdutoNFNFSViewColumns {
  fun Grid<ProdutoNFS>.produtoNFCodigo() = columnGrid(ProdutoNFS::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoNFS>.produtoNFGrade() = columnGrid(ProdutoNFS::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoNFS>.produtoNFGradeAlternativa() = columnGrid(ProdutoNFS::gradeAlternativa) {
    this.setHeader("Grade Editada")
  }

  fun Grid<ProdutoNFS>.produtoNFLocalizacao() = columnGrid(ProdutoNFS::local) {
    this.setHeader("Loc App")
  }

  fun Grid<ProdutoNFS>.produtoNFBarcode() = columnGrid(ProdutoNFS::barcodeStrList) {
    this.setHeader("Código de Barras")
    this.right()
  }

  fun Grid<ProdutoNFS>.produtoAutorizacaoExp() = columnGrid(ProdutoNFS::usuarioExp) {
    this.setHeader("Expedicao")
  }


  fun Grid<ProdutoNFS>.produtoNFDescricao() = columnGrid(ProdutoNFS::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoNFS>.produtoNFQuantidade() = columnGrid(ProdutoNFS::quantidade) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoNFS>.produtoNFPrecoUnitario() = columnGrid(ProdutoNFS::preco) {
    this.setHeader("Preço")
  }

  fun Grid<ProdutoNFS>.produtoNFPrecoTotal() = columnGrid(ProdutoNFS::total) {
    this.setHeader("Total")
  }

  fun Grid<ProdutoNFS>.produtoNFUsuarioSep() = columnGrid(ProdutoNFS::usuarioSep) {
    this.setHeader("Impresso")
  }

  fun Grid<ProdutoNFS>.produtoNFEstoque() = columnGrid(ProdutoNFS::estoque) {
    this.setHeader("Estoque")
  }
}