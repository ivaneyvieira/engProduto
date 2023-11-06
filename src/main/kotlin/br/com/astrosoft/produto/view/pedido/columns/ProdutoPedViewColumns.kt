package br.com.astrosoft.produto.view.pedido.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.ProdutoPedidoVenda
import com.vaadin.flow.component.grid.Grid

object ProdutoPedViewColumns {
  fun Grid<ProdutoPedidoVenda>.produtoPedidoUsuarioNameCD() = columnGrid(ProdutoPedidoVenda::usuarioNameCD) {
    this.setHeader("Usuário CD")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoCodigo() = columnGrid(ProdutoPedidoVenda::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoGrade() = columnGrid(ProdutoPedidoVenda::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoGradeAlternativa() = columnGrid(ProdutoPedidoVenda::gradeAlternativa) {
    this.setHeader("Grade Editada")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoLocalizacao() = columnGrid(ProdutoPedidoVenda::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTipo() = columnGrid(ProdutoPedidoVenda::statusStr) {
    this.setHeader("Status")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoBarcode() = columnGrid(ProdutoPedidoVenda::barcode) {
    this.setHeader("Código de Barras")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoDescricao() = columnGrid(ProdutoPedidoVenda::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoVendno() = columnGrid(ProdutoPedidoVenda::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoFornecedor() = columnGrid(ProdutoPedidoVenda::fornecedor) {
    this.setHeader("Nome Fornecedor")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTypeNo() = columnGrid(ProdutoPedidoVenda::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTypeName() = columnGrid(ProdutoPedidoVenda::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoClno() = columnGrid(ProdutoPedidoVenda::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoClName() = columnGrid(ProdutoPedidoVenda::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoAltura() = columnGrid(ProdutoPedidoVenda::altura) {
    this.setHeader("Altura")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoComprimento() = columnGrid(ProdutoPedidoVenda::comprimento) {
    this.setHeader("Comprimento")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoLargura() = columnGrid(ProdutoPedidoVenda::largura) {
    this.setHeader("Largura")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoPrecoCheio() = columnGrid(ProdutoPedidoVenda::precoCheio) {
    this.setHeader("Preço a vista")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoQuantidade() = columnGrid(ProdutoPedidoVenda::quantidade) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoEstoque() = columnGrid(ProdutoPedidoVenda::estoque) {
    this.setHeader("Estoque")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoPrecoUnitario() = columnGrid(ProdutoPedidoVenda::preco) {
    this.setHeader("Preço")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoPrecoTotal() = columnGrid(ProdutoPedidoVenda::total) {
    this.setHeader("Total")
  }
}