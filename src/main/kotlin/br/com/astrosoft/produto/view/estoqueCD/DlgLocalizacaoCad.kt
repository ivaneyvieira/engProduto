package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.produto.viewmodel.estoqueCD.TabControleCDViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.TextField

class DlgLocalizacaoCad(
    val locInicial: String, val viewModel: TabControleCDViewModel, val onClose: (
        localizacao: String) -> Unit = {}) : Dialog() {
  private var edtLocalizacao: TextField? = null
  
  init {
    this.isModal = true
    this.headerTitle = "Localização"
    this.footer.toolBar()
    
    verticalLayout {
      setSizeFull()
      horizontalLayout {
        button("Cadastra") {
          this.icon = VaadinIcon.PLUS.create()
          onClick {
            val localizacao = edtLocalizacao?.value ?: return@onClick
            viewModel.addLocalizacao(localizacao)
            onClose(localizacao)
            close()
          }
        }
        button("Remove") {
          this.icon = VaadinIcon.TRASH.create()
          onClick {
            viewModel.delLocalizacao()
            onClose("")
            close()
          }
        }
      }
      edtLocalizacao = textField {
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
          this@DlgLocalizacaoCad.close()
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