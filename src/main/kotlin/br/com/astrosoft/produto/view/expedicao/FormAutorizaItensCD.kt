package br.com.astrosoft.produto.view.expedicao

import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoNFS
import br.com.astrosoft.produto.viewmodel.expedicao.TabNotaExpViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.select.SelectVariant
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

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
    select<String> {
      if (index == 0) {
        this.label = "Grade"
      }
      this.width = "9rem"
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
        grade ?: ""
      }
      addThemeVariants(SelectVariant.LUMO_SMALL)

      this.addValueChangeListener {
        item.gradeAlternativa = it.value
      }
    }
    integerField {
      if (index == 0) {
        this.label = "Quant CD"
      }
      this.width = "5rem"
      this.value = item.quantidadeNF ?: 0
      item.quantidadeCD = value
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT, TextFieldVariant.LUMO_SMALL)

      this.min = 1
      this.max = item.quantidadeNF ?: 0
      this.i18n = IntegerField.IntegerFieldI18n()
        .setMinErrorMessage("A quantidade deve ser maior que 0")
        .setMaxErrorMessage("A quantidade deve ser menor ou igual a ${item.quantidadeNF ?: 0}")

      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500

      this.addValueChangeListener {
        if (it.isFromClient) {
          if (this.isInvalid) {
            //Thread.sleep(2000)
            item.quantidadeCD = item.quantidadeNF ?: 0
            //this.value = item.quantidadeNF ?: 0
          } else {
            item.quantidadeCD = it.value
          }
        }
      }
    }
  }
  this.addAndExpand(linha)
  return linha
}