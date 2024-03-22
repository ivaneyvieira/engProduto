package br.com.astrosoft.produto.view.notaSaida.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.NotaSaida
import com.vaadin.flow.component.grid.Grid

object NotaColumns {
  fun Grid<NotaSaida>.colunaNFLoja() = columnGrid(NotaSaida::loja) {
    this.setHeader("Loja")
  }

  fun Grid<NotaSaida>.colunaNFNota() = columnGrid(NotaSaida::nota) {
    this.setHeader("Nota")
  }

  fun Grid<NotaSaida>.colunaNFNotaEnt() = columnGrid(NotaSaida::notaEntrega) {
    this.setHeader("NF Ent")
  }

  fun Grid<NotaSaida>.colunaNFCliente() = columnGrid(NotaSaida::cliente) {
    this.setHeader("Cliente")
  }

  fun Grid<NotaSaida>.colunaNFData() = columnGrid(NotaSaida::data) {
    this.setHeader("Data")
  }

  fun Grid<NotaSaida>.colunaNFDataEnt() = columnGrid(NotaSaida::dataEntrega) {
    this.setHeader("Data")
  }

  fun Grid<NotaSaida>.colunaNFVendedor() = columnGrid(NotaSaida::vendedor) {
    this.setHeader("Vendedor")
  }

  fun Grid<NotaSaida>.colunaNFValor() = columnGrid(NotaSaida::totalProdutos) {
    this.setHeader("Valor")
  }

  fun Grid<NotaSaida>.colunaNFSituacao() = columnGrid(NotaSaida::situacao) {
    this.setHeader("Situação")
  }

  fun Grid<NotaSaida>.colunaNFTipo() = columnGrid(NotaSaida::tipoNotaSaida) {
    this.setHeader("Tipo")
  }

  fun Grid<NotaSaida>.colunaNomeCliente() = columnGrid(NotaSaida::nomeCliente) {
    this.setHeader("Nome Cliente")
  }

  fun Grid<NotaSaida>.colunaNomeVendedor() = columnGrid(NotaSaida::nomeVendedor) {
    this.setHeader("Nome Vendedor")
  }

  fun Grid<NotaSaida>.colunaHora() = columnGrid(NotaSaida::hora) {
    this.setHeader("Hora")
  }
}