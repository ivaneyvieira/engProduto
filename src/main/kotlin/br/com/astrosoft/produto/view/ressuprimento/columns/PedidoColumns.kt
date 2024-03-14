package br.com.astrosoft.produto.view.ressuprimento.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.Ressuprimento
import com.vaadin.flow.component.grid.Grid

object RessuprimentoColumns {
  fun Grid<Ressuprimento>.colunaRessuprimentoNumero() = columnGrid(Ressuprimento::numero) {
    this.setHeader("Número")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoData() = columnGrid(Ressuprimento::data) {
    this.setHeader("Data")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoNotaBaixa() = columnGrid(Ressuprimento::notaBaixa) {
    this.setHeader("Nota")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoDataBaixa() = columnGrid(Ressuprimento::dataBaixa) {
    this.setHeader("Data")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoComprador() = columnGrid(Ressuprimento::comprador) {
    this.setHeader("Comprador")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoObservacao() = columnGrid(Ressuprimento::observacao) {
    this.setHeader("Observação")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoUsuarioApp() = columnGrid(Ressuprimento::usuarioApp) {
    this.setHeader("Login")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoSing() = columnGrid(Ressuprimento::entregueSPor) {
    this.setHeader("Entregue")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoTransportadorPor() = columnGrid(Ressuprimento::transportadoSPor) {
    this.setHeader("Transportado")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoRecebidoPor() = columnGrid(Ressuprimento::recebidoSPor) {
    this.setHeader("Recebido")
  }
}