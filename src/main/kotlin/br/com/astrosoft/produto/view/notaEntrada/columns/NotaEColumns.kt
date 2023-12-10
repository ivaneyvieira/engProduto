package br.com.astrosoft.produto.view.notaEntrada.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.NotaEntrada
import com.vaadin.flow.component.grid.Grid

object NotaEColumns {
  fun Grid<NotaEntrada>.colunaNFEChave() = columnGrid(NotaEntrada::chave) {
    this.setHeader("Chave")
  }

  fun Grid<NotaEntrada>.colunaNFEDataEntrada() = columnGrid(NotaEntrada::entrada) {
    this.setHeader("Entrada")
  }

  fun Grid<NotaEntrada>.colunaNFEDataEmissao() = columnGrid(NotaEntrada::emissao) {
    this.setHeader("Emissao")
  }

  fun Grid<NotaEntrada>.colunaNFELoja() = columnGrid(NotaEntrada::loja) {
    this.setHeader("Loja")
  }

  fun Grid<NotaEntrada>.colunaNFENI() = columnGrid(NotaEntrada::ni) {
    this.setHeader("NI")
  }

  fun Grid<NotaEntrada>.colunaNFENota() = columnGrid(NotaEntrada::nota) {
    this.setHeader("Nota")
  }

  fun Grid<NotaEntrada>.colunaNFEFornecedor() = columnGrid(NotaEntrada::fornecedor) {
    this.setHeader("Fornecedor")
  }

  fun Grid<NotaEntrada>.colunaNFENomeFornecedor() = columnGrid(NotaEntrada::nomeForn) {
    this.setHeader("Nome For")
  }

  fun Grid<NotaEntrada>.colunaNFEValor() = columnGrid(NotaEntrada::valorNota) {
    this.setHeader("Valor")
  }
}