package br.com.astrosoft.produto.view.pedidoTransf.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.ProdutoPedidoVenda
import com.vaadin.flow.component.grid.Grid

object ProdutoPedTransfViewColumns {
  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfUsuarioNameCD() = columnGrid(ProdutoPedidoVenda::usuarioNameCD) {
    this.setHeader("Usuário CD")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfCodigo() = columnGrid(ProdutoPedidoVenda::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfGrade() = columnGrid(ProdutoPedidoVenda::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfGradeAlternativa() = columnGrid(ProdutoPedidoVenda::gradeAlternativa) {
    this.setHeader("Grade Editada")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfLocalizacao() = columnGrid(ProdutoPedidoVenda::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfTipo() = columnGrid(ProdutoPedidoVenda::statusStr) {
    this.setHeader("Status")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfBarcode() = columnGrid(ProdutoPedidoVenda::barcode) {
    this.setHeader("Código de Barras")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfDescricao() = columnGrid(ProdutoPedidoVenda::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfVendno() = columnGrid(ProdutoPedidoVenda::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfFornecedor() = columnGrid(ProdutoPedidoVenda::fornecedor) {
    this.setHeader("Nome Fornecedor")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfTypeNo() = columnGrid(ProdutoPedidoVenda::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfTypeName() = columnGrid(ProdutoPedidoVenda::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfClno() = columnGrid(ProdutoPedidoVenda::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfClName() = columnGrid(ProdutoPedidoVenda::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfAltura() = columnGrid(ProdutoPedidoVenda::altura) {
    this.setHeader("Altura")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfComprimento() = columnGrid(ProdutoPedidoVenda::comprimento) {
    this.setHeader("Comprimento")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfLargura() = columnGrid(ProdutoPedidoVenda::largura) {
    this.setHeader("Largura")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfPrecoCheio() = columnGrid(ProdutoPedidoVenda::precoCheio) {
    this.setHeader("Preço a vista")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfQuantidade() = columnGrid(ProdutoPedidoVenda::quantidade) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfEstoque() = columnGrid(ProdutoPedidoVenda::estoque) {
    this.setHeader("Estoque")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfPrecoUnitario() = columnGrid(ProdutoPedidoVenda::preco) {
    this.setHeader("Preço")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTransfPrecoTotal() = columnGrid(ProdutoPedidoVenda::total) {
    this.setHeader("Total")
  }
}