package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.produto.model.beans.ProdutoCadastro
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoCadastroViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v10.bind
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.value.ValueChangeMode
import kotlin.reflect.KMutableProperty1

class DialogCadastro(val viewModel: TabProdutoCadastroViewModel, val bean: ProdutoCadastro) : Dialog() {
  private val binder = Binder(ProdutoCadastro::class.java)
  
  init {
    element.setAttribute("aria-label", "Create new employee")
    isModal = true
    createDialogLayout()
    binder.readBean(bean)
  }
  
  private fun createDialogLayout() {
    verticalLayout {
      isPadding = false
      alignItems = FlexComponent.Alignment.STRETCH
      style.set("width", "500px").set("max-width", "100%")
      h2("Modificar Informações do Cadastro") {
        style.set("margin", "var(--lumo-space-m) 0 0 0").set("font-size", "1.5em").set("font-weight", "bold")
      }
      horizontalLayout {
        isSpacing = true
        isPadding = false
        alignItems = FlexComponent.Alignment.STRETCH
        panelCard("Cadastro") {
          edtInteiro("For", ProdutoCadastro::forn) {
            this.min = 0
          }
          edtInteiro("Tipo", ProdutoCadastro::tipo) {
            this.min = 0
          }
          edtInteiro("C. Lucro", ProdutoCadastro::clno) {
            this.min = 0
          }
          edtTexto("NCM", ProdutoCadastro::ncm) {
            this.pattern = "[0-9]{8}"
          }
          comboBox("Rótulo") {
            setItems(viewModel.rotulos())
            this.bind(binder).bind(ProdutoCadastro::rotulo)
          }
          edtTexto("CST", ProdutoCadastro::tributacao)
        }
      }
      horizontalLayout {
        justifyContentMode = FlexComponent.JustifyContentMode.END
        button("Confirma") {
          addThemeVariants(ButtonVariant.LUMO_PRIMARY)
          onClick {
            try {
              binder.writeBean(bean)
              binder.validate()
              if (binder.isValid) {
                viewModel.updatePrecificacao(bean)
              }
              this@DialogCadastro.close()
            } catch (e: Exception) {
              DialogHelper.showWarning("A validação falhou em alguns campos")
            }
          }
        }
        button("Cancela") {
          addThemeVariants(ButtonVariant.LUMO_ERROR)
          onClick {
            this@DialogCadastro.close()
          }
        }
      }
    }
  }
  
  private fun FormLayout.edtNumero(
      label: String, prop: KMutableProperty1<ProdutoCadastro, out Double?>, block: NumberField.() -> Unit = {}) {
    numberField(label) {
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      value = null
      this.valueChangeMode = ValueChangeMode.EAGER
      this.isClearButtonVisible = true
      this.isAutoselect = true
      this.bind(binder).bind(prop)
      this.block()
      this.i18n = NumberField.NumberFieldI18n().setRequiredErrorMessage("O campo é requerido")
        .setBadInputErrorMessage("Formato de número inválido")
        .setMinErrorMessage("O valor deve ser maior que ${this.min}")
        .setMaxErrorMessage("O valor deve ser menor que ${this.max}")
    }
  }
  
  private fun FormLayout.edtInteiro(
      label: String, prop: KMutableProperty1<ProdutoCadastro, out Int?>, block: IntegerField.() -> Unit = {}) {
    integerField(label) {
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      this.valueChangeMode = ValueChangeMode.EAGER
      this.isClearButtonVisible = true
      this.isAutoselect = true
      this.bind(binder).bind(prop)
      this.block()
      this.i18n = IntegerField.IntegerFieldI18n().setRequiredErrorMessage("O campo é requerido")
        .setBadInputErrorMessage("Formato de número inválido")
        .setMinErrorMessage("O valor deve ser maior que ${this.min}")
        .setMaxErrorMessage("O valor deve ser menor que ${this.max}")
    }
  }
  
  private fun FormLayout.edtTexto(
      label: String,
      prop: KMutableProperty1<ProdutoCadastro, out String?>,
      block: TextField.() -> Unit = {}) {
    textField(label) {
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      this.valueChangeMode = ValueChangeMode.EAGER
      this.isClearButtonVisible = true
      this.isAutoselect = true
      this.bind(binder).bind(prop)
      this.block()
    }
  }
  
  private fun HorizontalLayout.panelCard(label: String, block: FormLayout.() -> Unit) {
    verticalLayout {
      style.set("border", "1px ridge").set("border-radius", "5px")
      isSpacing = false
      isPadding = true
      isMargin = false
      alignItems = FlexComponent.Alignment.STRETCH
      
      p(label) {
        style.set("font-weight", "bold")
      }
      formLayout {
        responsiveSteps { "0px"(2, top) }
        alignItems = FlexComponent.Alignment.STRETCH
        
        this.block()
      }
    }
  }
}

