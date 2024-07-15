package br.com.astrosoft.produto.view.ressuprimento

import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.integerField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.IntegerField
import java.time.LocalDate

class FormFuncionario() : FormLayout() {
  private var edtNumero: IntegerField? = null
  private var edtDate: DatePicker? = null

  init {
    edtNumero = integerField("Número do Funcionário") {
      this.width = "300px"
    }
    edtDate = datePicker("Data de Entrada")
  }

  val numero: Int
    get() = edtNumero?.value ?: 0

  val data: LocalDate?
    get() = edtDate?.value
}