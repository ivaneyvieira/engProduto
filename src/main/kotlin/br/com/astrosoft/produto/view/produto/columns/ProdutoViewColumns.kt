package br.com.astrosoft.produto.view.produto.columns

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.Produto
import com.vaadin.flow.component.grid.Grid

object ProdutoViewColumns {
  fun Grid<Produto>.produtoCodigo() = addColumnString(Produto::codigo) {
    this.setHeader("Código")
  }

  fun Grid<Produto>.produtoGrade() = addColumnString(Produto::grade) {
    this.setHeader("Grade")
  }

  fun Grid<Produto>.produtoLocalizacao() = addColumnString(Produto::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<Produto>.produtoBarcode() = addColumnString(Produto::barcode) {
    this.setHeader("Código de Barras")
  }

  fun Grid<Produto>.produtoDescricao() = addColumnString(Produto::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<Produto>.produtoVendno() = addColumnInt(Produto::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<Produto>.produtoFornecedor() = addColumnString(Produto::fornecedor) {
    this.setHeader("Nome Fornecedor")
  }

  fun Grid<Produto>.produtoTypeNo() = addColumnInt(Produto::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<Produto>.produtoTypeName() = addColumnString(Produto::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<Produto>.produtoClno() = addColumnString(Produto::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<Produto>.produtoClName() = addColumnString(Produto::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<Produto>.produtoAltura() = addColumnInt(Produto::altura) {
    this.setHeader("Altura")
  }

  fun Grid<Produto>.produtoComprimento() = addColumnInt(Produto::comprimento) {
    this.setHeader("Comprimento")
  }

  fun Grid<Produto>.produtoLargura() = addColumnInt(Produto::largura) {
    this.setHeader("Largura")
  }

  fun Grid<Produto>.produtoPrecoCheio() = addColumnDouble(Produto::precoCheio) {
    this.setHeader("Preço a vista")
  }

  fun Grid<Produto>.produtoNcm() = addColumnString(Produto::ncm) {
    this.setHeader("NCM")
  }
}