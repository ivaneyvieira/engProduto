package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.view.vaadin.helper.ITabPanel
import br.com.astrosoft.framework.view.vaadin.helper.UIThread
import br.com.astrosoft.framework.view.vaadin.helper.updateItens
import br.com.astrosoft.produto.model.beans.Rota
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.theme.lumo.LumoUtility
import kotlin.reflect.KClass

abstract class TabPanelGridMobile<T : Any>(classGrid: KClass<T>) : ITabPanel {
  private val dataProviderPanel = ListDataProvider<T>(mutableListOf())
  protected val gridPanel: VirtualList<T> = VirtualList()
  protected abstract fun HorizontalLayout.toolBarConfig()
  protected abstract fun VerticalLayout.renderCard(item: T)

  val renderGrid = ComponentRenderer<Component, T> { item: T ->
    VerticalLayout().apply {
      this.isSpacing = true
      this.setWidthFull()
      this.addClassNames(
        LumoUtility.BorderRadius.LARGE,
        LumoUtility.BoxShadow.LARGE,
        LumoUtility.Border.ALL,
        LumoUtility.BorderColor.CONTRAST_50
      )
      this.renderCard(item)
    }
  }

  override fun createComponent() = VerticalLayout().apply {
    this.setSizeFull()
    isMargin = false
    isPadding = false
    horizontalLayout {
      this.isSpacing = true
      this.isMargin = true
      this.isWrap = true
      setWidthFull()
      toolBarConfig()
    }

    gridPanel.apply {
      this.dataProvider = dataProviderPanel
      this.isExpand = true
      this.setRenderer(renderGrid)
    }
    addAndExpand(gridPanel)
  }

  fun updateGrid(itens: List<T>) {
    dataProviderPanel.updateItens(itens)
  }

  fun listBeans() = gridPanel.dataProvider.fetchAll()

  fun itensSelecionados(): List<T> {
    return emptyList()
  }

  override fun printerPreview(
    showPrinter: Boolean,
    rota: Rota?,
    loja: Int,
    showPrintBunton: Boolean,
    actionSave: ((SubWindowPrinter) -> Unit)?,
    printEvent: (impressora: String) -> Unit
  ): IPrinter {
    return PrinterPreview(showPrinter, printerUser(), rota, loja, showPrintBunton, actionSave, printEvent)
  }

  open fun printerUser(): List<String> = emptyList()

  override fun execThread(block: () -> Unit) {
    UIThread(UI.getCurrent(), block).start()
  }
}