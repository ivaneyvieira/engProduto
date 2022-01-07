package br.com.astrosoft.produto.view.nota.columns

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.ProdutoNF
import com.vaadin.flow.component.grid.Grid

object ProdutoNFNFSViewColumns {
  fun Grid<ProdutoNF>.produtoNFCodigo() = addColumnString(ProdutoNF::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoNF>.produtoNFGrade() = addColumnString(ProdutoNF::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoNF>.produtoNFGradeAlternativa() = addColumnString(ProdutoNF::gradeAlternativa) {
    this.setHeader("Grade Editada")
  }

  fun Grid<ProdutoNF>.produtoNFLocalizacao() = addColumnString(ProdutoNF::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<ProdutoNF>.produtoNFBarcode() = addColumnString(ProdutoNF::barcode) {
    this.setHeader("Código de Barras")
  }

  fun Grid<ProdutoNF>.produtoNFDescricao() = addColumnString(ProdutoNF::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoNF>.produtoNFVendno() = addColumnInt(ProdutoNF::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoNF>.produtoNFFornecedor() = addColumnString(ProdutoNF::fornecedor) {
    this.setHeader("Nome Fornecedor")
  }

  fun Grid<ProdutoNF>.produtoNFTypeNo() = addColumnInt(ProdutoNF::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoNF>.produtoNFTypeName() = addColumnString(ProdutoNF::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<ProdutoNF>.produtoNFClno() = addColumnString(ProdutoNF::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<ProdutoNF>.produtoNFClName() = addColumnString(ProdutoNF::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<ProdutoNF>.produtoNFAltura() = addColumnInt(ProdutoNF::altura) {
    this.setHeader("Altura")
  }

  fun Grid<ProdutoNF>.produtoNFComprimento() = addColumnInt(ProdutoNF::comprimento) {
    this.setHeader("Comprimento")
  }

  fun Grid<ProdutoNF>.produtoNFLargura() = addColumnInt(ProdutoNF::largura) {
    this.setHeader("Largura")
  }

  fun Grid<ProdutoNF>.produtoNFPrecoCheio() = addColumnDouble(ProdutoNF::precoCheio) {
    this.setHeader("Preço a vista")
  }

  fun Grid<ProdutoNF>.produtoNFNcm() = addColumnString(ProdutoNF::ncm) {
    this.setHeader("NCM")
  }

  fun Grid<ProdutoNF>.produtoNFQuantidade() = addColumnInt(ProdutoNF::quantidade) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoNF>.produtoNFPrecoUnitario() = addColumnDouble(ProdutoNF::preco) {
    this.setHeader("Preço")
  }

  fun Grid<ProdutoNF>.produtoNFPrecoTotal() = addColumnDouble(ProdutoNF::total) {
    this.setHeader("Total")
  }
}