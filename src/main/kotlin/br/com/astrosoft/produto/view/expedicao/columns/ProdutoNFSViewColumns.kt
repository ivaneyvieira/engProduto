package br.com.astrosoft.produto.view.expedicao.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.ProdutoNFS
import com.vaadin.flow.component.grid.Grid

object ProdutoNFNFSViewColumns {
  fun Grid<ProdutoNFS>.produtoNFUsuarioNameCD() = columnGrid(ProdutoNFS::usuarioNameCD) {
    this.setHeader("Usuário CD")
  }

  fun Grid<ProdutoNFS>.produtoNFUsuarioNameExp() = columnGrid(ProdutoNFS::usuarioNameExp) {
    this.setHeader("Usuário Exp")
  }

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
    this.setHeader("Loc")
  }

  fun Grid<ProdutoNFS>.produtoNFBarcode() = columnGrid(ProdutoNFS::barcodeStrList) {
    this.setHeader("Código de Barras")
  }

  fun Grid<ProdutoNFS>.produtoNFDescricao() = columnGrid(ProdutoNFS::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoNFS>.produtoNFVendno() = columnGrid(ProdutoNFS::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoNFS>.produtoNFFornecedor() = columnGrid(ProdutoNFS::fornecedor) {
    this.setHeader("Nome Fornecedor")
  }

  fun Grid<ProdutoNFS>.produtoNFTypeNo() = columnGrid(ProdutoNFS::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoNFS>.produtoNFTypeName() = columnGrid(ProdutoNFS::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<ProdutoNFS>.produtoNFClno() = columnGrid(ProdutoNFS::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<ProdutoNFS>.produtoNFClName() = columnGrid(ProdutoNFS::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<ProdutoNFS>.produtoNFAltura() = columnGrid(ProdutoNFS::altura) {
    this.setHeader("Altura")
  }

  fun Grid<ProdutoNFS>.produtoNFComprimento() = columnGrid(ProdutoNFS::comprimento) {
    this.setHeader("Comprimento")
  }

  fun Grid<ProdutoNFS>.produtoNFLargura() = columnGrid(ProdutoNFS::largura) {
    this.setHeader("Largura")
  }

  fun Grid<ProdutoNFS>.produtoNFPrecoCheio() = columnGrid(ProdutoNFS::precoCheio) {
    this.setHeader("Preço a vista")
  }

  fun Grid<ProdutoNFS>.produtoNFNcm() = columnGrid(ProdutoNFS::ncm) {
    this.setHeader("NCM")
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