package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrinterCups
import br.com.astrosoft.framework.model.printText.PrinterToHtml
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.style
import br.com.astrosoft.produto.model.beans.Impressora
import br.com.astrosoft.produto.model.beans.UserSaci
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.select.Select
import java.io.File

class SubWindowPrinter(text: String, printerUser: String, val printEvent: (impressora: String) -> Unit) : Dialog() {
  private var cmbImpressora: Select<Impressora>? = null

  private val divText = Div().apply {
    this.style("background-color", "#FFE4B5")
    this.style("font-family", "monospace")
    this.style("font-size", "80%")
    this.style("white-space", "pre-wrap")
    this.style("padding", "10px")
    this.style("margin", "10px")
    this.style("border", "1px solid black")
    this.style("border-radius", "5px")
    this.style("color", "black")
    this.html(PrinterToHtml.toHtml(text))
  }

  init {
    File("/tmp/relatorio.txt").writeText(text)

    height = "100%"
    verticalLayout {
      isPadding = false
      horizontalLayout {
        button("Fechar") {
          icon = VaadinIcon.CLOSE.create()
          onLeftClick {
            close()
          }
        }
        cmbImpressora = select("Impressora") {
          val userSaci = AppConfig.userLogin() as? UserSaci
          val lista = Impressora.allTermica()
          setItems(lista)
          this.setItemLabelGenerator { it.name }

          this.value = lista.firstOrNull {
            it.name == printerUser
          } ?: lista.firstOrNull()
          this.isReadOnly = printerUser.isNotBlank() && userSaci?.admin != true
        }
        this.button("Imprimir") {
          icon = VaadinIcon.PRINT.create()
          this.onLeftClick {
            DialogHelper.showQuestion("Confirma a impressão?") {
              val impressora = cmbImpressora?.value?.name ?: "Nenhuma impressora selecionada"
              val printer = PrinterCups(impressora)
              printer.print(text)
              printEvent(impressora)
              this@SubWindowPrinter.close()
              //TODO verificar se a impressão foi realizada
            }
          }
        }
      }

      addAndExpand(Scroller(divText))
    }
    isCloseOnEsc = true
  }

}


