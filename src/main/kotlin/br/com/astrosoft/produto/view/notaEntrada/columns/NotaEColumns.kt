package br.com.astrosoft.produto.view.notaEntrada.columns

import br.com.astrosoft.framework.view.addColumnDate
import br.com.astrosoft.framework.view.addColumnLocalDate
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.NotaSaida
import com.vaadin.flow.component.grid.Grid

object NotaEColumns {
  fun Grid<NotaEntrada>.colunaNFEChave() = addColumnString(NotaEntrada::chave) {
    this.setHeader("Chave")
  }

  fun Grid<NotaEntrada>.colunaNFEData() = addColumnLocalDate(NotaEntrada::data) {
    this.setHeader("Data")
  }
}