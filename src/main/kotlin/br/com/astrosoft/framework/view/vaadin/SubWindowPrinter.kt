package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrinterCups
import br.com.astrosoft.framework.model.printText.PrinterToHtml
import br.com.astrosoft.framework.model.printText.TextBuffer
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.style
import br.com.astrosoft.produto.model.beans.*
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.select.Select
import java.io.File

class SubWindowPrinter(
  text: TextBuffer,
  showPrinter: Boolean = true,
  printerUser: List<String>,
  rota: Rota?,
  loja: Int,
  val actionSave: Runnable?,
  val printEvent: (impressora: String) -> Unit
) :
  Dialog() {
  private var cmbImpressora: Select<String>? = null

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
    this.html(PrinterToHtml.toHtml(text.printHtml()))
  }

  private fun imprimeText(text: TextBuffer, impressora: String, loja: Int) {
    val printer = PrinterCups(impressora, loja)
    printer.print(text)
  }

  init {
    File("/tmp/relatorio.txt").writeText(text.printEspPos())

    height = "100%"
    verticalLayout {
      isPadding = false
      horizontalLayout {
        button("Fechar") {
          icon = VaadinIcon.CLOSE.create()
          onClick {
            close()
          }
        }
        if (showPrinter) {
          cmbImpressora = this.select("Impressora") {
            val userSaci = AppConfig.userLogin() as? UserSaci
            val allPrinter =
                if (rota == null) {
                  Impressora.allTermica().map { it.name }
                } else {
                  Impressora.allTermica()
                    .map { it.name } + ETipoRota.impressoraLojas().map { it.name }
                }
            val lista =
                when {
                  userSaci?.admin == true                    -> allPrinter
                  printerUser.contains(ETipoRota.TODAS.nome) -> allPrinter
                  printerUser.isEmpty()                      -> emptyList()
                  else                                       -> printerUser
                }
            setItems(lista)

            this.value = lista.firstOrNull()
          }
          if (actionSave != null) {
            this.button("Grava") {
              icon = VaadinIcon.DISC.create()
              this.onClick {
                actionSave.run()
              }
            }
          }
          this.button("Imprimir") {
            icon = VaadinIcon.PRINT.create()
            this.onClick {
              DialogHelper.showQuestion("Confirma a impressão?") {
                val impressoraName = cmbImpressora?.value ?: "Nenhuma impressora selecionada"
                val impressora = Impressora.findImpressora(impressoraName)
                val tipoRota = impressora?.tipoRota()
                if (tipoRota != null) {
                  val impressoraOrigem = Impressora.findImpressora(rota?.lojaOrigem, tipoRota)?.name ?: ""
                  val impressoraDestino = Impressora.findImpressora(rota?.lojaDestino, tipoRota)?.name ?: ""
                  val printerRota = listOf(impressoraOrigem, impressoraDestino)
                  printerRota.forEach { printer ->
                    imprimeText(text, printer, loja)
                  }
                  printEvent(printerRota.firstOrNull() ?: "")
                } else {
                  imprimeText(text, impressoraName, loja)
                  printEvent(impressoraName)
                }
                this@SubWindowPrinter.close()
                //TODO verificar se a impressão foi realizada
              }
            }
          }
        }
      }

      addAndExpand(Scroller(divText))
    }
    isCloseOnEsc = true
  }
}