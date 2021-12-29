package br.com.astrosoft.produto.view.produto.columns

import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnLocalDate
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.ProdutoReserva
import com.vaadin.flow.component.grid.Grid

object ProdutoReservaColumns {
  fun Grid<ProdutoReserva>.produtoReservaLoja() = addColumnInt(ProdutoReserva::loja) {
    this.setHeader("Loja")
  }

  fun Grid<ProdutoReserva>.produtoReservaPedido() = addColumnInt(ProdutoReserva::pedido) {
    this.setHeader("Pedido")
  }

  fun Grid<ProdutoReserva>.produtoReservaData() = addColumnLocalDate(ProdutoReserva::data) {
    this.setHeader("Data")
  }

  fun Grid<ProdutoReserva>.produtoReservaCodigo() = addColumnString(ProdutoReserva::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoReserva>.produtoReservaGrade() = addColumnString(ProdutoReserva::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoReserva>.produtoReservaLocalizacao() = addColumnString(ProdutoReserva::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<ProdutoReserva>.produtoReservaDescricao() = addColumnString(ProdutoReserva::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoReserva>.produtoReservaVendno() = addColumnInt(ProdutoReserva::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoReserva>.produtoReservaTypeNo() = addColumnInt(ProdutoReserva::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoReserva>.produtoReservaTypeName() = addColumnString(ProdutoReserva::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<ProdutoReserva>.produtoReservaClno() = addColumnString(ProdutoReserva::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<ProdutoReserva>.produtoReservaClName() = addColumnString(ProdutoReserva::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<ProdutoReserva>.produtoReservaSaldoSaci() = addColumnInt(ProdutoReserva::estSaci) {
    this.setHeader("Estoque")
  }

  fun Grid<ProdutoReserva>.produtoReservaReserva() = addColumnInt(ProdutoReserva::reserva) {
    this.setHeader("Reserva")
  }

  fun Grid<ProdutoReserva>.produtoReservaSaldo() = addColumnInt(ProdutoReserva::saldo) {
    this.setHeader("Disponivel")
  }
}