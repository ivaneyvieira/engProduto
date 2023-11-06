package br.com.astrosoft.produto.view.produto.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.ProdutoReserva
import com.vaadin.flow.component.grid.Grid

object ProdutoReservaColumns {
  fun Grid<ProdutoReserva>.produtoReservaLoja() = columnGrid(ProdutoReserva::loja) {
    this.setHeader("Loja")
  }

  fun Grid<ProdutoReserva>.produtoReservaPedido() = columnGrid(ProdutoReserva::pedido) {
    this.setHeader("Pedido")
  }

  fun Grid<ProdutoReserva>.produtoReservaData() = columnGrid(ProdutoReserva::data) {
    this.setHeader("Data")
  }

  fun Grid<ProdutoReserva>.produtoReservaCodigo() = columnGrid(ProdutoReserva::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoReserva>.produtoReservaGrade() = columnGrid(ProdutoReserva::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoReserva>.produtoReservaLocalizacao() = columnGrid(ProdutoReserva::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<ProdutoReserva>.produtoReservaDescricao() = columnGrid(ProdutoReserva::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoReserva>.produtoReservaVendno() = columnGrid(ProdutoReserva::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoReserva>.produtoReservaTypeNo() = columnGrid(ProdutoReserva::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoReserva>.produtoReservaTypeName() = columnGrid(ProdutoReserva::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<ProdutoReserva>.produtoReservaClno() = columnGrid(ProdutoReserva::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<ProdutoReserva>.produtoReservaClName() = columnGrid(ProdutoReserva::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<ProdutoReserva>.produtoReservaSaldoSaci() = columnGrid(ProdutoReserva::estSaci) {
    this.setHeader("Estoque")
  }

  fun Grid<ProdutoReserva>.produtoReservaReserva() = columnGrid(ProdutoReserva::reserva) {
    this.setHeader("Reserva")
  }

  fun Grid<ProdutoReserva>.produtoReservaSaldo() = columnGrid(ProdutoReserva::saldo) {
    this.setHeader("Disponivel")
  }
}