package br.com.astrosoft.produto.view.pedidoTransf.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.ProdutoPedidoTransf
import com.vaadin.flow.component.grid.Grid

object ProdutoPedTransfViewColumns {
  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfUsuarioNameCD() = columnGrid(ProdutoPedidoTransf::usuarioNameCD) {
    this.setHeader("Usuário CD")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfCodigo() = columnGrid(ProdutoPedidoTransf::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfGrade() = columnGrid(ProdutoPedidoTransf::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfGradeAlternativa() = columnGrid(ProdutoPedidoTransf::gradeAlternativa) {
    this.setHeader("Grade Editada")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfLocalizacao() = columnGrid(ProdutoPedidoTransf::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfTipo() = columnGrid(ProdutoPedidoTransf::statusStr) {
    this.setHeader("Status")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfBarcode() = columnGrid(ProdutoPedidoTransf::barcode) {
    this.setHeader("Código de Barras")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfDescricao() = columnGrid(ProdutoPedidoTransf::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfVendno() = columnGrid(ProdutoPedidoTransf::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfFornecedor() = columnGrid(ProdutoPedidoTransf::fornecedor) {
    this.setHeader("Nome Fornecedor")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfTypeNo() = columnGrid(ProdutoPedidoTransf::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfTypeName() = columnGrid(ProdutoPedidoTransf::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfClno() = columnGrid(ProdutoPedidoTransf::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfClName() = columnGrid(ProdutoPedidoTransf::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfAltura() = columnGrid(ProdutoPedidoTransf::altura) {
    this.setHeader("Altura")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfComprimento() = columnGrid(ProdutoPedidoTransf::comprimento) {
    this.setHeader("Comprimento")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfLargura() = columnGrid(ProdutoPedidoTransf::largura) {
    this.setHeader("Largura")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfPrecoCheio() = columnGrid(ProdutoPedidoTransf::precoCheio) {
    this.setHeader("Preço a vista")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfQuantidade() = columnGrid(ProdutoPedidoTransf::quantidade) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfEstoque() = columnGrid(ProdutoPedidoTransf::estoque) {
    this.setHeader("Estoque")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfPrecoUnitario() = columnGrid(ProdutoPedidoTransf::preco) {
    this.setHeader("Preço")
  }

  fun Grid<ProdutoPedidoTransf>.produtoPedidoTransfPrecoTotal() = columnGrid(ProdutoPedidoTransf::total) {
    this.setHeader("Total")
  }
}