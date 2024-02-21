package br.com.astrosoft.produto.view.ressuprimento.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.Ressuprimento
import com.vaadin.flow.component.grid.Grid

object RessuprimentoColumns {
  fun Grid<Ressuprimento>.colunaRessuprimentoNumero() = columnGrid(Ressuprimento::numero) {
    this.setHeader("Número")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoChaveCD() = columnGrid(Ressuprimento::chaveNovaCD) {
    this.setHeader("Chave")
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

  fun Grid<Ressuprimento>.colunaRessuprimentoValor() = columnGrid(Ressuprimento::totalProdutos) {
    this.setHeader("Valor")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoSituacao() = columnGrid(Ressuprimento::situacao) {
    this.setHeader("Situação")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoLocalizacao() = columnGrid(Ressuprimento::localizacao) {
    this.setHeader("Loc")
  }


  fun Grid<Ressuprimento>.colunaRessuprimentoUsuarioCD() = columnGrid(Ressuprimento::usuarioLogin) {
    this.setHeader("Usuário")
  }
}