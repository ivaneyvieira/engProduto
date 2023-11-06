package br.com.astrosoft.produto.view.produto.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.Produto
import com.vaadin.flow.component.grid.Grid

object ProdutoViewColumns {
  fun Grid<Produto>.produtoCodigo() = columnGrid(Produto::codigo) {
    this.setHeader("Código")
  }

  fun Grid<Produto>.produtoGrade() = columnGrid(Produto::grade) {
    this.setHeader("Grade")
  }

  fun Grid<Produto>.produtoLocalizacao() = columnGrid(Produto::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<Produto>.produtoBarcode() = columnGrid(Produto::barcode) {
    this.setHeader("Código de Barras")
  }

  fun Grid<Produto>.produtoDescricao() = columnGrid(Produto::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<Produto>.produtoVendno() = columnGrid(Produto::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<Produto>.produtoFornecedor() = columnGrid(Produto::fornecedor) {
    this.setHeader("Nome Fornecedor")
  }

  fun Grid<Produto>.produtoTypeNo() = columnGrid(Produto::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<Produto>.produtoTypeName() = columnGrid(Produto::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<Produto>.produtoClno() = columnGrid(Produto::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<Produto>.produtoClName() = columnGrid(Produto::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<Produto>.produtoAltura() = columnGrid(Produto::altura) {
    this.setHeader("Altura")
  }

  fun Grid<Produto>.produtoComprimento() = columnGrid(Produto::comprimento) {
    this.setHeader("Comprimento")
  }

  fun Grid<Produto>.produtoLargura() = columnGrid(Produto::largura) {
    this.setHeader("Largura")
  }

  fun Grid<Produto>.produtoPrecoCheio() = columnGrid(Produto::precoCheio) {
    this.setHeader("Preço a vista")
  }

  fun Grid<Produto>.produtoNcm() = columnGrid(Produto::ncm) {
    this.setHeader("NCM")
  }
}