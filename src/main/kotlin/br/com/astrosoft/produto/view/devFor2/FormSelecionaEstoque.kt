package br.com.astrosoft.produto.view.devFor2

import br.com.astrosoft.produto.viewmodel.devFor2.TipoEstoque
import com.github.mvysny.karibudsl.v10.radioButtonGroup
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup

class FormSelecionaEstoque : FormLayout() {
  private var selecionaEstoque : RadioButtonGroup<TipoEstoque>? = null

  init {
    selecionaEstoque = radioButtonGroup("Seleciona Estoque") {
      this.setItems(TipoEstoque.entries)
      this.setItemLabelGenerator { it.descricao }
      this.value = TipoEstoque.LOJA
    }
  }

  fun selecionaEstoque(): TipoEstoque? {
    return selecionaEstoque?.value
  }
}