package br.com.astrosoft.produto.view.notaSaida

import com.github.mvysny.karibudsl.v10.integerField
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.IntegerField

class FormFuncionario() : FormLayout() {
  private var edtNumero: IntegerField? = null

  init {
    edtNumero = integerField("Número do Funcionário") {
      this.width = "300px"
    }
  }

  val numero: Int
    get() = edtNumero?.value ?: 0
}