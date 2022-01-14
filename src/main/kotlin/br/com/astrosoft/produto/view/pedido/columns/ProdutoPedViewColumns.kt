package br.com.astrosoft.produto.view.pedido.columns

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.ProdutoPedidoVenda
import com.vaadin.flow.component.grid.Grid

object ProdutoPedViewColumns {
  fun Grid<ProdutoPedidoVenda>.produtoPedidoUsuarioNameCD() = addColumnString(ProdutoPedidoVenda::usuarioNameCD) {
    this.setHeader("Usuário CD")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoCodigo() = addColumnString(ProdutoPedidoVenda::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoGrade() = addColumnString(ProdutoPedidoVenda::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoGradeAlternativa() = addColumnString(ProdutoPedidoVenda::gradeAlternativa) {
    this.setHeader("Grade Editada")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoLocalizacao() = addColumnString(ProdutoPedidoVenda::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTipo() = addColumnString(ProdutoPedidoVenda::statusStr) {
    this.setHeader("Status")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoBarcode() = addColumnString(ProdutoPedidoVenda::barcode) {
    this.setHeader("Código de Barras")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoDescricao() = addColumnString(ProdutoPedidoVenda::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoVendno() = addColumnInt(ProdutoPedidoVenda::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoFornecedor() = addColumnString(ProdutoPedidoVenda::fornecedor) {
    this.setHeader("Nome Fornecedor")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTypeNo() = addColumnInt(ProdutoPedidoVenda::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoTypeName() = addColumnString(ProdutoPedidoVenda::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoClno() = addColumnString(ProdutoPedidoVenda::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoClName() = addColumnString(ProdutoPedidoVenda::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoAltura() = addColumnInt(ProdutoPedidoVenda::altura) {
    this.setHeader("Altura")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoComprimento() = addColumnInt(ProdutoPedidoVenda::comprimento) {
    this.setHeader("Comprimento")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoLargura() = addColumnInt(ProdutoPedidoVenda::largura) {
    this.setHeader("Largura")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoPrecoCheio() = addColumnDouble(ProdutoPedidoVenda::precoCheio) {
    this.setHeader("Preço a vista")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoNcm() = addColumnString(ProdutoPedidoVenda::ncm) {
    this.setHeader("NCM")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoQuantidade() = addColumnInt(ProdutoPedidoVenda::quantidade) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoEstoque() = addColumnInt(ProdutoPedidoVenda::estoque) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoPrecoUnitario() = addColumnDouble(ProdutoPedidoVenda::preco) {
    this.setHeader("Preço")
  }

  fun Grid<ProdutoPedidoVenda>.produtoPedidoPrecoTotal() = addColumnDouble(ProdutoPedidoVenda::total) {
    this.setHeader("Total")
  }
}