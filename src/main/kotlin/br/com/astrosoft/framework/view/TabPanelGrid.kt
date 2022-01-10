package br.com.astrosoft.framework.view

import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.isExpand
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant.LUMO_COMPACT
import com.vaadin.flow.component.grid.dataview.GridListDataView
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.ListDataProvider
import kotlin.reflect.KClass
import kotlin.streams.toList

abstract class TabPanelGrid<T : Any>(classGrid: KClass<T>) : ITabPanel {
  private var gridDataView: GridListDataView<T>
 // private val dataProviderPanel = ListDataProvider<T>(mutableListOf())
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
     // this.dataProvider = dataProviderPanel
      gridDataView = this.setItems()
      isExpand = true
      isMultiSort = true
      addThemeVariants(LUMO_COMPACT)
      gridPanel()
    }
    addAndExpand(gridPanel)
  }

  fun updateGrid(itens: List<T>) {
    gridPanel.deselectAll()
    //dataProviderPanel.updateItens(itens)
    gridDataView.removeItems(gridDataView.items.toList())
    gridDataView.addItems(itens)
  }

  fun listBeans() = gridDataView.items.toList()

  fun itensSelecionados() = gridPanel.selectedItems.toList()
}