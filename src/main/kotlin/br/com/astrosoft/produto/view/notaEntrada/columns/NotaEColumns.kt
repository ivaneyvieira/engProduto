package br.com.astrosoft.produto.view.notaEntrada.columns

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnLocalDate
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.NotaEntrada
import com.vaadin.flow.component.grid.Grid

object NotaEColumns {
  fun Grid<NotaEntrada>.colunaNFEChave() = addColumnString(NotaEntrada::chave) {
    this.setHeader("Chave")
  }

  fun Grid<NotaEntrada>.colunaNFEData() = addColumnLocalDate(NotaEntrada::data) {
    this.setHeader("Data")
  }

  fun Grid<NotaEntrada>.colunaNFELoja() = addColumnInt(NotaEntrada::loja) {
    this.setHeader("Loja")
  }

  fun Grid<NotaEntrada>.colunaNFENota() = addColumnString(NotaEntrada::nota) {
    this.setHeader("Nota")
  }

  fun Grid<NotaEntrada>.colunaNFEFornecedor() = addColumnInt(NotaEntrada::fornecedor) {
    this.setHeader("Fornecedor")
  }

  fun Grid<NotaEntrada>.colunaNFEValor() = addColumnDouble(NotaEntrada::valorNota) {
    this.setHeader("Valor")
  }
}