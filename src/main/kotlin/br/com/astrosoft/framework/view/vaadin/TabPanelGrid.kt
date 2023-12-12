package br.com.astrosoft.framework.view.vaadin

import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.view.vaadin.helper.ITabPanel
import br.com.astrosoft.framework.view.vaadin.helper.updateItens
import br.com.astrosoft.produto.model.beans.Rota
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.ListDataProvider
import kotlin.reflect.KClass

abstract class TabPanelGrid<T : Any>(classGrid: KClass<T>) : ITabPanel {
  private val dataProviderPanel = ListDataProvider<T>(mutableListOf())
  protected val gridPanel: Grid<T> = Grid(classGrid.java, false)
  protected abstract fun HorizontalLayout.toolBarConfig()
  protected abstract fun Grid<T>.gridPanel()

  override val createComponent = VerticalLayout().apply {
    this.setSizeFull()
    isMargin = false
    isPadding = false
    horizontalLayout {
      setWidthFull()
      toolBarConfig()
    }

    gridPanel.apply {
      this.dataProvider = dataProviderPanel
      isExpand = true
      isMultiSort = true
      //addThemeVariants()
      gridPanel()
    }
    addAndExpand(gridPanel)
  }

  fun updateGrid(itens: List<T>) {
    gridPanel.deselectAll()
    dataProviderPanel.updateItens(itens)
  }

  fun listBeans() = dataProviderPanel.fetchAll()

  fun itensSelecionados() = gridPanel.selectedItems.toList()

  override fun printerPreview(
    showPrinter: Boolean,
    rota: Rota?,
    printEvent: (impressora: String) -> Unit
  ): IPrinter {
    return PrinterPreview(showPrinter, printerUser(), rota, printEvent)
  }

  open fun printerUser(): List<String> = emptyList()
}