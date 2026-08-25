package br.com.astrosoft.produto.view.precificacao

import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.produto.model.beans.BeanForm
import br.com.astrosoft.produto.viewmodel.precificacao.TabPrecificacaoAbstractViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.value.ValueChangeMode
import kotlin.reflect.KMutableProperty1

class DialogPrecificacao(
  val viewModel: TabPrecificacaoAbstractViewModel,
  val loja: Int = 10,
  val bean: BeanForm,
  val cardEntrada: Boolean,
  val cardSaida: Boolean
) : Dialog() {
  private val binder = Binder(BeanForm::class.java)

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
      h2("Modificar Percentuais da Planilha de Precificação") {
        style.set("margin", "var(--lumo-space-m) 0 0 0").set("font-size", "1.5em").set("font-weight", "bold")
      }
      horizontalLayout {
        isSpacing = true
        isPadding = false
        alignItems = FlexComponent.Alignment.STRETCH
        if (cardEntrada) {
          panelCard("% de Entrada") {
            edtNumero("MVA", BeanForm::mvap) {
              this.min = 0.00
            }
            edtNumero("ICMS Ent", BeanForm::creditoICMS) {
              this.min = 0.00
            }
            edtNumero("P. Fab", BeanForm::pcfabrica) {
              this.min = 0.00
            }
            edtNumero("IPI", BeanForm::ipi) {
              this.min = 0.00
            }
            edtNumero("Emb", BeanForm::embalagem) {
              this.min = 0.00
            }
            edtNumero("IR ST", BeanForm::retido) {
              this.min = 0.00
            }
            edtNumero("C. ICMS", BeanForm::icmsp) {
              this.max = 0.00
            }
            edtNumero("Frete", BeanForm::frete) {
              this.min = 0.00
            }
            edtNumero("ICMS do Frete", BeanForm::freteICMS) {
              this.min = 0.00
            }
            edtNumero("Pis/Cofins", BeanForm::pisCofins) {
              this.max = 0.00
            }
          }
        }
        if (cardSaida) {
          panelCard("% de Saída") {
            edtNumero("ICM Sai", BeanForm::icms)
            edtNumero("FCP", BeanForm::fcp)
            edtNumero("Pis", BeanForm::pis)
            edtNumero("IR", BeanForm::ir)
            edtNumero("CS", BeanForm::contrib)
            edtNumero("CPMF", BeanForm::cpmf)
            edtNumero("Desp", BeanForm::fixa)
            edtNumero("Out", BeanForm::outras)
          }
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
                bean.loja = loja
                viewModel.updatePrecificacao(bean)
              }
              this@DialogPrecificacao.close()
            }catch (e: Exception) {
              DialogHelper.showWarning("A validação falhou em alguns campos")
            }
          }
        }
        button("Cancela") {
          addThemeVariants(ButtonVariant.LUMO_ERROR)
          onClick {
            this@DialogPrecificacao.close()
          }
        }
      }
    }
  }

  private fun FormLayout.edtNumero(
    label: String,
    prop: KMutableProperty1<BeanForm, out Double?>,
    block: NumberField.() -> Unit = {}
  ) {
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

