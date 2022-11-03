package br.com.astrosoft.produto.view.notaSaida.columns

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.ProdutoNFS
import com.vaadin.flow.component.grid.Grid

object ProdutoNFNFSViewColumns {
  fun Grid<ProdutoNFS>.produtoNFUsuarioNameCD() = addColumnString(ProdutoNFS::usuarioNameCD) {
    this.setHeader("Usuário CD")
  }

  fun Grid<ProdutoNFS>.produtoNFUsuarioNameExp() = addColumnString(ProdutoNFS::usuarioNameExp) {
    this.setHeader("Usuário Exp")
  }

  fun Grid<ProdutoNFS>.produtoNFCodigo() = addColumnString(ProdutoNFS::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoNFS>.produtoNFGrade() = addColumnString(ProdutoNFS::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoNFS>.produtoNFGradeAlternativa() = addColumnString(ProdutoNFS::gradeAlternativa) {
    this.setHeader("Grade Editada")
  }

  fun Grid<ProdutoNFS>.produtoNFLocalizacao() = addColumnString(ProdutoNFS::local) {
    this.setHeader("Loc")
  }

  fun Grid<ProdutoNFS>.produtoNFBarcode() = addColumnString(ProdutoNFS::barcode) {
    this.setHeader("Código de Barras")
  }

  fun Grid<ProdutoNFS>.produtoNFDescricao() = addColumnString(ProdutoNFS::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoNFS>.produtoNFVendno() = addColumnInt(ProdutoNFS::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoNFS>.produtoNFFornecedor() = addColumnString(ProdutoNFS::fornecedor) {
    this.setHeader("Nome Fornecedor")
  }

  fun Grid<ProdutoNFS>.produtoNFTypeNo() = addColumnInt(ProdutoNFS::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoNFS>.produtoNFTypeName() = addColumnString(ProdutoNFS::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<ProdutoNFS>.produtoNFClno() = addColumnString(ProdutoNFS::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<ProdutoNFS>.produtoNFClName() = addColumnString(ProdutoNFS::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<ProdutoNFS>.produtoNFAltura() = addColumnInt(ProdutoNFS::altura) {
    this.setHeader("Altura")
  }

  fun Grid<ProdutoNFS>.produtoNFComprimento() = addColumnInt(ProdutoNFS::comprimento) {
    this.setHeader("Comprimento")
  }

  fun Grid<ProdutoNFS>.produtoNFLargura() = addColumnInt(ProdutoNFS::largura) {
    this.setHeader("Largura")
  }

  fun Grid<ProdutoNFS>.produtoNFPrecoCheio() = addColumnDouble(ProdutoNFS::precoCheio) {
    this.setHeader("Preço a vista")
  }

  fun Grid<ProdutoNFS>.produtoNFNcm() = addColumnString(ProdutoNFS::ncm) {
    this.setHeader("NCM")
  }

  fun Grid<ProdutoNFS>.produtoNFQuantidade() = addColumnInt(ProdutoNFS::quantidade) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoNFS>.produtoNFPrecoUnitario() = addColumnDouble(ProdutoNFS::preco) {
    this.setHeader("Preço")
  }

  fun Grid<ProdutoNFS>.produtoNFPrecoTotal() = addColumnDouble(ProdutoNFS::total) {
    this.setHeader("Total")
  }
}