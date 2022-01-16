package br.com.astrosoft.produto.view.nota.columns

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnLocalDate
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.NotaSaida
import com.vaadin.flow.component.grid.Grid

object NotaColumns {
  fun Grid<NotaSaida>.colunaNFLoja() = addColumnInt(NotaSaida::loja) {
    this.setHeader("Loja")
  }

  fun Grid<NotaSaida>.colunaNFNota() = addColumnString(NotaSaida::nota) {
    this.setHeader("Nota")
  }

  fun Grid<NotaSaida>.colunaNFChaveExp() = addColumnString(NotaSaida::chaveNovaExp) {
    this.setHeader("Chave Exp")
  }

  fun Grid<NotaSaida>.colunaNFChaveCD() = addColumnString(NotaSaida::chaveNovaCD) {
    this.setHeader("Chave CD")
  }

  fun Grid<NotaSaida>.colunaNFCliente() = addColumnInt(NotaSaida::cliente) {
    this.setHeader("Cliente")
  }

  fun Grid<NotaSaida>.colunaNFData() = addColumnLocalDate(NotaSaida::data) {
    this.setHeader("Data")
  }

  fun Grid<NotaSaida>.colunaNFVendedor() = addColumnInt(NotaSaida::vendedor) {
    this.setHeader("Vendedor")
  }

  fun Grid<NotaSaida>.colunaNFValor() = addColumnDouble(NotaSaida::totalProdutos) {
    this.setHeader("Valor")
  }

  fun Grid<NotaSaida>.colunaNFSituacao() = addColumnString(NotaSaida::situacao) {
    this.setHeader("Situação")
  }

  fun Grid<NotaSaida>.colunaNFTipo() = addColumnString(NotaSaida::tipoNotaSaida) {
    this.setHeader("Tipo")
  }
}