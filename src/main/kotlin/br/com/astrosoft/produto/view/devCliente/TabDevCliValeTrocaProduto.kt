package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.EntradaDevCliProList
import br.com.astrosoft.produto.model.beans.FiltroEntradaDevCliProList
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevCliValeTrocaProduto
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevCliValeTrocaProdutoViewModel
import com.flowingcode.vaadin.addons.gridhelpers.GridHelper
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import java.time.LocalDate

class TabDevCliValeTrocaProduto(val viewModel: TabDevCliValeTrocaProdutoViewModel) :
  TabPanelGrid<EntradaDevCliProList>(EntradaDevCliProList::class),
  ITabDevCliValeTrocaProduto {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtData: DatePicker

  init {
    val listLojas = viewModel.findAllLojas()
    cmbLoja.setItems(listLojas)
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaVale != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaVale ?: 0) ?: listLojas.firstOrNull()
  }

  override fun HorizontalLayout.toolBarConfig() {
    cmbLoja = select("Loja") {
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateView()
      }
    }
    edtData = datePicker("Data") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    button("Impressão") {
      icon = VaadinIcon.PRINT.create()
      onLeftClick {
        viewModel.imprimeProdutos()
      }
    }
  }

  override fun Grid<EntradaDevCliProList>.gridPanel() {
    this.addClassName("styling")
    columnGrid(EntradaDevCliProList::codigo, header = "Código").right()
    columnGrid(EntradaDevCliProList::descricao, header = "Descrição").expand()
    columnGrid(EntradaDevCliProList::grade, header = "Grade")
    columnGrid(EntradaDevCliProList::quantidade, header = "Quantidade")
    GridHelper.setEnhancedSelectionEnabled(this, true)
  }

  override fun filtro(): FiltroEntradaDevCliProList {
    return FiltroEntradaDevCliProList(
      loja = cmbLoja.value?.no ?: 0,
      data = edtData.value ?: LocalDate.now(),
    )
  }

  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    return listOfNotNull(username?.impressoraDev)
  }

  override fun updateProdutos(produtos: List<EntradaDevCliProList>) {
    updateGrid(produtos)
  }

  override fun produtosSelecionados(): List<EntradaDevCliProList> {
    return this.listBeans()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devCliValeTrocaProduto == true
  }

  override val label: String
    get() = "Produto"

  override fun updateComponent() {
    viewModel.updateView()
  }
}