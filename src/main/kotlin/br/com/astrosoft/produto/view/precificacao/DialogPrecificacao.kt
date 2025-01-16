package br.com.astrosoft.produto.view.precificacao

import br.com.astrosoft.produto.model.beans.BeanForm
import br.com.astrosoft.produto.viewmodel.precificacao.TabPrecificacaoAbstractViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.value.ValueChangeMode
import java.math.BigDecimal
import kotlin.reflect.KMutableProperty1

class DialogPrecificacao(
  val viewModel: TabPrecificacaoAbstractViewModel,
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

  fun createDialogLayout() {
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
            edtNumero("MVA", BeanForm::mvap)
            edtNumero("ICMS Ent", BeanForm::creditoICMS)
            edtNumero("P. Fab", BeanForm::pcfabrica)
            edtNumero("IPI", BeanForm::ipi)
            edtNumero("Emb", BeanForm::embalagem)
            edtNumero("IR ST", BeanForm::retido)
            edtNumero("C. ICMS", BeanForm::icmsp)
            edtNumero("Frete", BeanForm::frete)
            edtNumero("ICMS do Frete", BeanForm::freteICMS)
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
          onLeftClick {
            binder.writeBean(bean)
            binder.validate()
            if (binder.isValid) {
              viewModel.updatePrecificacao(bean)
            }
            this@DialogPrecificacao.close()
          }
        }
        button("Cancela") {
          onLeftClick {
            this@DialogPrecificacao.close()
          }
        }
      }
    }
  }

  private fun FormLayout.edtNumero(label: String, prop: KMutableProperty1<BeanForm, out BigDecimal?>) {
    bigDecimalField(label) {
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      value = null
      this.valueChangeMode = ValueChangeMode.EAGER
      this.isClearButtonVisible = true
      this.isAutoselect = true
      this.bind(binder).bind(prop)
    }
  }

  private fun HorizontalLayout.panelCard(label: String, block: FormLayout.() -> Unit) {
    verticalLayout {
      style.set("border", "1px ridge").set("border-radius", "5px")
      isSpacing = false
      isPadding = true
      isMargin = false
      alignItems = FlexComponent.Alignment.STRETCH

      label(label) {
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

