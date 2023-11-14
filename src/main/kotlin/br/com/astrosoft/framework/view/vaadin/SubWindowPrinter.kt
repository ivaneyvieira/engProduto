package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrinterCups
import br.com.astrosoft.framework.view.vaadin.helper.style
import br.com.astrosoft.produto.model.beans.Impressora
import br.com.astrosoft.produto.model.beans.UserSaci
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.select.Select

class SubWindowPrinter(text: String) : Dialog() {
  private var cmbImpressora: Select<Impressora>? = null

  private val divText = Div().apply {
    this.style("background-color", "#FFE4B5")
    this.style("font-family", "monospace")
    this.style("font-size", "12px")
    this.style("white-space", "pre-wrap")
    this.style("padding", "10px")
    this.style("margin", "10px")
    this.style("border", "1px solid black")
    this.style("border-radius", "5px")
    this.style("color", "black")
    this.html(toHtml(text))
  }

  private fun toHtml(text: String): String {
    val html =
        text.removerInicializer()
          .removerCodigoBarras()
          .removeFinalize()
          .removerNegrito()
          .replace("\n", "<br>")
          .replace(" ", "&nbsp;")

    return "<pre>$html</pre>"
  }

  init {
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
        cmbImpressora = select<Impressora>("Impressora") {
          val lista = Impressora.allTermica()
          val printerUser = (AppConfig.userLogin() as? UserSaci)?.impressora ?: ""
          setItems(lista)
          this.setItemLabelGenerator { it.name }

          this.value = lista.firstOrNull {
            it.name == printerUser
          }?: lista.firstOrNull()
        }
        this.button("Imprimir") {
          icon = VaadinIcon.PRINT.create()
          this.onLeftClick {
            val impressora = cmbImpressora?.value?.name ?: "Nenhuma impressora selecionada"
            val printer = PrinterCups(impressora)
            printer.print(text)
          }
        }
      }

      addAndExpand(Scroller(divText))
    }
    isCloseOnEsc = true
  }

  private fun String.removerInicializer(): String {
    val padrao = "\u001B\u0021\u0001".toRegex()
    return this.replace(padrao, "")
  }

  private fun String.removeFinalize(): String {
    val padrao = "\u000A\u000A\u000A\u001B\u0069".toRegex()
    return this.replace(padrao, "")
  }

  private fun String.removerCodigoBarras(): String {
    val padrao = "\u001D\u0068\u0050\u001D\u0077\u0004\u001D\u006B\u0049".toRegex()
    return this.replace(padrao, "")
  }

  private fun String.removerNegrito(): String {
    val padraoi = "\u001B\u0045".toRegex()
    val padraof = "\u001B\u0046".toRegex()
    return this.replace(padraoi, "<strong>").replace(padraof, "</strong>")
  }
}


