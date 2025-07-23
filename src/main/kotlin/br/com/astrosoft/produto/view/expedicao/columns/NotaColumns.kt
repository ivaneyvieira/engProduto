package br.com.astrosoft.produto.view.expedicao.columns

import br.com.astrosoft.framework.view.vaadin.helper.center
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.NotaSaida
import com.vaadin.flow.component.grid.Grid

object NotaColumns {
  fun Grid<NotaSaida>.colunaNFLoja() = columnGrid(NotaSaida::loja) {
    this.setHeader("Loja")
  }

  fun Grid<NotaSaida>.colunaSeparado() = columnGrid(NotaSaida::separadoStr) {
    this.setHeader("Separado")
    this.center()
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

  fun Grid<NotaSaida>.colunaNFValor() = columnGrid(NotaSaida::valorNota) {
    this.setHeader("Valor")
  }

  fun Grid<NotaSaida>.colunaNFSituacao() = columnGrid(NotaSaida::situacao) {
    this.setHeader("Situação")
  }

  fun Grid<NotaSaida>.colunaNFTipo() = columnGrid(NotaSaida::tipoNotaSaidaDesc, width = "120px") {
    this.setHeader("Tipo")
  }

  fun Grid<NotaSaida>.colunaNFEntregaRetira() = columnGrid(NotaSaida::tipo) {
    this.setHeader("Ent/Ret")
  }

  fun Grid<NotaSaida>.colunaNomeCliente() = columnGrid(NotaSaida::nomeCliente) {
    this.setHeader("Nome Cliente")
  }

  //fun Grid<NotaSaida>.colunaNomeVendedor() = columnGrid(NotaSaida::nomeVendedor) {
  //  this.setHeader("Nome Vendedor")
  //}

  fun Grid<NotaSaida>.colunaHora() = columnGrid(NotaSaida::hora) {
    this.setHeader("Hora")
    this.setComparator { t ->
      t.hotaTime
    }
  }

  fun Grid<NotaSaida>.colunaCD() = columnGrid(NotaSaida::cd5A) {
    this.setHeader("CD")
  }

  fun Grid<NotaSaida>.colunaRota() = columnGrid(NotaSaida::rota) {
    this.setHeader("Rota")
  }

  fun Grid<NotaSaida>.colunaAgendado() = columnGrid(NotaSaida::agendado) {
    this.setHeader("Agendado")
  }

  fun Grid<NotaSaida>.colunaPedido() = columnGrid(NotaSaida::pedido) {
    this.setHeader("Pedido")
  }

  fun Grid<NotaSaida>.colunaEntrega() = columnGrid(NotaSaida::entrega) {
    this.setHeader("Entrega")
  }

  fun Grid<NotaSaida>.colunaMotoristaSing() = columnGrid(NotaSaida::nomeMotorista) {
    this.setHeader("Motorista")
  }

  fun Grid<NotaSaida>.colunaImpresso() = columnGrid(NotaSaida::usuarioPrint) {
    this.setHeader("Impresso")
  }

  fun Grid<NotaSaida>.colunaImpressoSep() = columnGrid(NotaSaida::usuarioSep) {
    this.setHeader("Impresso")
  }

  fun Grid<NotaSaida>.colunaLoginEnt() = columnGrid(NotaSaida::usuarioEntrega) {
    this.setHeader("Login")
  }
}