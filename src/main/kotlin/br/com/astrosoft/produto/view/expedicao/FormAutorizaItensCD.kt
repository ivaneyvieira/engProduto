package br.com.astrosoft.produto.view.expedicao

import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoNFS
import br.com.astrosoft.produto.viewmodel.expedicao.TabNotaExpViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.combobox.ComboBoxVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant

class FormAutorizaItensCD(val viewModel: TabNotaExpViewModel, val lista: List<ProdutoNFS>) : VerticalLayout() {
  private var edtLogin: TextField? = null
  private var edtSenha: PasswordField? = null

  init {
    this.isMargin = false
    this.isPadding = false
    this.isSpacing = false
    this.width = "700px"

    horizontalLayout {
      this.isMargin = false
      this.isPadding = false
      this.setWidthFull()

      edtLogin = textField("Login") {
        this.isExpand = true
      }
      edtSenha = passwordField("Senha") {
        this.isExpand = true
      }
    }
    h3("Produtos")
    verticalLayout {
      this.isMargin = false
      this.isPadding = false
      this.isSpacing = false

      lista.forEachIndexed { index, item ->
        linhaEdit(index, item, viewModel)
      }
    }
  }

  val login: String
    get() = edtLogin?.value ?: ""
  val senha: String
    get() = edtSenha?.value ?: ""
}

fun VerticalLayout.linhaEdit(index: Int, item: ProdutoNFS, viewModel: TabNotaExpViewModel): HorizontalLayout {
  val linha = HorizontalLayout().apply {
    this.isMargin = false
    this.isPadding = false

    this.setWidthFull()
    textField {
      if (index == 0) {
        this.label = "Código"
      }
      this.width = "5rem"
      value = item.codigo
      isReadOnly = true
      addThemeVariants(TextFieldVariant.LUMO_SMALL)
    }
    textField {
      if (index == 0) {
        this.label = "Descrição"
      }
      value = item.descricao
      isReadOnly = true
      this.width = "100%"
      addThemeVariants(TextFieldVariant.LUMO_SMALL)
    }
    comboBox<String> {
      if (index == 0) {
        this.label = "Grade"
      }
      this.width = "8rem"
      val list = mutableListOf<PrdGrade>()

      viewModel.findGrade(item) { prds ->
        list.addAll(prds)
      }
      this.style.set("--vaadin-combo-box-overlay-width", "300px")
      val grades = list.mapNotNull {
        if (it.grade == "") {
          null
        } else {
          it.grade
        }
      }
      this.setItems(grades)
      this.value = if (grades.isNotEmpty()) {
        item.grade
      } else {
        this.isReadOnly = true
        null
      }
      this.setItemLabelGenerator { grade ->
        val saldo = list.firstOrNull { it.grade == grade }?.saldo ?: 0
        if (grade == null) ""
        else "$grade Saldo: $saldo"
      }
      addThemeVariants(ComboBoxVariant.LUMO_SMALL)

      this.addValueChangeListener {
        item.gradeAlternativa = it.value
      }
    }
    integerField {
      if (index == 0) {
        this.label = "Quant"
      }
      this.width = "4rem"
      this.value = item.quantidadeNF
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT, TextFieldVariant.LUMO_SMALL)

      this.addValueChangeListener {
        item.quantidadeCD = it.value
      }
    }
  }
  this.addAndExpand(linha)
  return linha
}