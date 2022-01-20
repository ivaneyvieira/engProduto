package br.com.astrosoft.produto.view.ressuprimento.columns

import br.com.astrosoft.framework.view.*
import br.com.astrosoft.produto.model.beans.Ressuprimento
import com.vaadin.flow.component.grid.Grid

object RessuprimentoColumns {
  fun Grid<Ressuprimento>.colunaRessuprimentoNumero() = addColumnLong(Ressuprimento::numero) {
    this.setHeader("Número")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoChaveCD() = addColumnString(Ressuprimento::chaveNovaCD) {
    this.setHeader("Chave")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoData() = addColumnLocalDate(Ressuprimento::data) {
    this.setHeader("Data")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoNotaBaixa() = addColumnString(Ressuprimento::notaBaixa) {
    this.setHeader("Nota")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoDataBaixa() = addColumnLocalDate(Ressuprimento::dataBaixa) {
    this.setHeader("Data")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoComprador() = addColumnInt(Ressuprimento::comprador) {
    this.setHeader("Comprador")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoValor() = addColumnDouble(Ressuprimento::totalProdutos) {
    this.setHeader("Valor")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoSituacao() = addColumnString(Ressuprimento::situacao) {
    this.setHeader("Situação")
  }
}