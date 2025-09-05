package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.printText.TextBuffer
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.ITabPanel
import br.com.astrosoft.framework.view.vaadin.helper.style
import br.com.astrosoft.framework.view.vaadin.helper.tabPanel
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.produto.model.beans.Rota
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.tab
import com.github.mvysny.karibudsl.v23.tabSheet
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.ColumnTextAlign.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.TabSheet
import com.vaadin.flow.component.tabs.TabSheetVariant
import com.vaadin.flow.component.tabs.TabsVariant
import com.vaadin.flow.router.*
import org.vaadin.stefan.LazyDownloadButton
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class ViewLayout<VM : ViewModel<*>> : VerticalLayout(), IView, BeforeLeaveObserver, BeforeEnterObserver,
  AfterNavigationObserver {
  abstract val viewModel: VM

  init {
    this.setSizeFull()
    this.isMargin = false
    this.isPadding = false
  }

  abstract fun isAccept(): Boolean

  fun addTabSheat(viewModel: VM, indexTab: Int = 0) {
    tabSheet {
      setSizeFull()
      addThemeVariants(
        TabSheetVariant.MATERIAL_BORDERED,
       // TabSheetVariant.LUMO_TABS_MINIMAL,
        //TabSheetVariant.LUMO_TABS_SMALL,
        //TabSheetVariant.LUMO_NO_PADDING
      )
      val tabs = viewModel.tabsAuthorized()
      tabs.forEach { tab ->
        val panel = tab as ITabPanel
        tabPanel(panel)
      }
      if (indexTab == 0)
        tabs.firstOrNull()?.updateComponent()
      else
        tabs.getOrNull(indexTab)?.updateComponent()
      if (tabs.size < indexTab)
        this.selectedIndex = indexTab
    }
  }

  override fun showError(msg: String) {
    DialogHelper.showError(msg)
  }

  override fun showWarning(msg: String) {
    DialogHelper.showWarning(msg)
  }

  override fun showInformation(msg: String) {
    DialogHelper.showInformation(msg)
  }

  fun showForm(caption: String, form: FormLayout, runConfirm: (() -> Unit)) {
    DialogHelper.showForm(caption, form, runConfirm)
  }

  override fun showQuestion(msg: String, execYes: () -> Unit) {
    showQuestion(msg, execYes) {}
  }

  private fun showQuestion(msg: String, execYes: () -> Unit, execNo: () -> Unit) {
    DialogHelper.showQuestion(msg, execYes, execNo)
  }

  override fun showReport(chave: String, report: ByteArray) {
    DialogHelper.showReport(chave, report)
  }

  override fun showPrintText(
    text: TextBuffer,
    showPrinter: Boolean,
    printerUser: List<String>,
    rota: Rota?,
    loja: Int,
    showPrintBunton: Boolean,
    actionSave: ((SubWindowPrinter) -> Unit)?,
    printEvent: (impressora: String) -> Unit
  ) {
    DialogHelper.showPrintText(text, showPrinter, printerUser, rota, loja, showPrintBunton, actionSave, printEvent)
  }

  override fun beforeLeave(event: BeforeLeaveEvent?) {
  }

  override fun beforeEnter(event: BeforeEnterEvent?) {
  }

  override fun afterNavigation(event: AfterNavigationEvent?) {
  }

  fun VerticalLayout.form(title: String, componentes: KFormLayout.() -> Unit = {}) {
    formLayout {
      isExpand = true

      em(title) {
        colspan = 2
      }
      componentes()
    }
  }

  fun HasComponents.toolbar(compnentes: HorizontalLayout.() -> Unit) {
    this.horizontalLayout {
      width = "100%"
      compnentes()
    }
  }
}

fun <T> (@VaadinDsl Grid.Column<T>).right() {
  this.textAlign = END
}

fun <T> (@VaadinDsl Grid.Column<T>).left() {
  this.textAlign = START
}

fun <T> (@VaadinDsl Grid.Column<T>).center() {
  this.textAlign = CENTER
}

fun Component.background(color: String) {
  this.style("background", color)
}

fun <T : Any> TabSheet.tabGrid(label: String, painelGrid: PainelGrid<T>) = tab {
  add(painelGrid)
}.apply {
  val button = Button(label) {
    painelGrid.blockUpdate()
  }
  button.addThemeVariants(ButtonVariant.LUMO_SMALL)
  this.addComponentAsFirst(button)
}

@VaadinDsl
fun (@VaadinDsl HasComponents).buttonPlanilha(
  text: String,
  icon: Component,
  chave: String,
  blockByteArray: () -> ByteArray
): LazyDownloadButton {
  val lazyDownloadButton = LazyDownloadButton(text, icon, { filename(chave) }) {
    ByteArrayInputStream(blockByteArray())
  }
  return init(lazyDownloadButton)
}

private fun filename(chave: String): String {
  val sdf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
  val textTime = LocalDateTime.now().format(sdf)
  return "$chave$textTime.xlsx"
}