package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.produto.viewmodel.estoqueCD.TipoEstoque
import com.github.mvysny.karibudsl.v10.passwordField
import com.github.mvysny.karibudsl.v10.radioButtonGroup
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField

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