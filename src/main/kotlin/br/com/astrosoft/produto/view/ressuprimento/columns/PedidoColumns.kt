package br.com.astrosoft.produto.view.ressuprimento.columns

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnLocalDate
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.Ressuprimento
import com.vaadin.flow.component.grid.Grid

object RessuprimentoColumns {
  fun Grid<Ressuprimento>.colunaRessuprimentoLoja() = addColumnInt(Ressuprimento::loja) {
    this.setHeader("Loja")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoNumero() = addColumnInt(Ressuprimento::ordno) {
    this.setHeader("Ressuprimento")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoChaveCD() = addColumnString(Ressuprimento::chaveNovaCD) {
    this.setHeader("Chave")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoCliente() = addColumnInt(Ressuprimento::cliente) {
    this.setHeader("Cliente")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoData() = addColumnLocalDate(Ressuprimento::data) {
    this.setHeader("Data")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoVendedor() = addColumnInt(Ressuprimento::vendedor) {
    this.setHeader("Vendedor")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoValor() = addColumnDouble(Ressuprimento::totalProdutos) {
    this.setHeader("Valor")
  }

  fun Grid<Ressuprimento>.colunaRessuprimentoSituacao() = addColumnString(Ressuprimento::situacao) {
    this.setHeader("Situação")
  }
}