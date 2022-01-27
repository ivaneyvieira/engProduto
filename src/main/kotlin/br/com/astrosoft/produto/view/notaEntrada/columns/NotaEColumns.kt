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

  fun Grid<NotaEntrada>.colunaNFEDataEntrada() = addColumnLocalDate(NotaEntrada::entrada) {
    this.setHeader("Entrada")
  }

  fun Grid<NotaEntrada>.colunaNFEDataEmissao() = addColumnLocalDate(NotaEntrada::emissao) {
    this.setHeader("Emissao")
  }

  fun Grid<NotaEntrada>.colunaNFELoja() = addColumnInt(NotaEntrada::loja) {
    this.setHeader("Loja")
  }

  fun Grid<NotaEntrada>.colunaNFENI() = addColumnInt(NotaEntrada::ni) {
    this.setHeader("NI")
  }

  fun Grid<NotaEntrada>.colunaNFENota() = addColumnString(NotaEntrada::nota) {
    this.setHeader("Nota")
  }

  fun Grid<NotaEntrada>.colunaNFEFornecedor() = addColumnInt(NotaEntrada::fornecedor) {
    this.setHeader("Fornecedor")
  }

  fun Grid<NotaEntrada>.colunaNFENomeFornecedor() = addColumnString(NotaEntrada::nomeForn) {
    this.setHeader("Nome For")
  }

  fun Grid<NotaEntrada>.colunaNFEValor() = addColumnDouble(NotaEntrada::valorNota) {
    this.setHeader("Valor")
  }
}