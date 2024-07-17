package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.integerField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.IntegerField
import java.time.LocalDate

class FormFuncionario(private val numeroI: Int? = null, private val dataI: LocalDate? = null) : FormLayout() {
  private var edtNumero: IntegerField? = null
  private var edtDate: DatePicker? = null

  init {
    edtNumero = integerField("Número do Funcionário") {
      this.width = "300px"
      this.isClearButtonVisible = true
      this.value = numeroI
    }
    edtDate = datePicker("Data de Entrada") {
      this.localePtBr()
      this.isClearButtonVisible = true
      this.value = dataI
    }
  }

  val numero: Int?
    get() = edtNumero?.value

  val data: LocalDate?
    get() = edtDate?.value
}