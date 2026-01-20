package br.com.astrosoft.produto.view.recebimento

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.TextField

class DlgLocalizacao(val locInicial: String, val onClose: (localizacao: String) -> Unit = {}) : Dialog() {
  private var edtLocalizacao: TextField? = null

  init {
    this.isModal = true
    this.headerTitle = "Localização"
    this.footer.toolBar()

    verticalLayout {
      setSizeFull()
      edtLocalizacao = textField("Localização") {
        this.value = locInicial
        this.setWidthFull()
        this.isAutofocus = true
      }
    }
    this.width = "40%"
    this.height = "40%"
  }

  fun HasComponents.toolBar() {
    horizontalLayout {
      this.justifyContentMode = FlexComponent.JustifyContentMode.END
      button("Confirma") {
        this.setPrimary()
        onClick {
          closeForm()
        }
      }

      button("Cancelar") {
        this.addThemeVariants(ButtonVariant.LUMO_ERROR)
        onClick {
          this@DlgLocalizacao.close()
        }
      }
    }
  }

  private fun closeForm() {
    val localizacao = edtLocalizacao?.value ?: ""
    onClose.invoke(localizacao)
    this.close()
  }
}