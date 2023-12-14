package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevCliCredito
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevCliValeTrocaProduto
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevCliCreditoViewModel
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevCliValeTrocaProdutoViewModel
import com.flowingcode.vaadin.addons.gridhelpers.GridHelper
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabDevCliCredito(val viewModel: TabDevCliCreditoViewModel) :
  TabPanelGrid<CreditoCliente>(CreditoCliente::class),
  ITabDevCliCredito {
  private lateinit var edtPesquisa: TextField

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<CreditoCliente>.gridPanel() {
    this.addClassName("styling")
    columnGrid(CreditoCliente::codigo, header = "Cód Cliente").right()
    columnGrid(CreditoCliente::nome, header = "Nome Cliente").expand()
    columnGrid(CreditoCliente::vlCredito, header = "Valor Crédito")
  }

  override fun filtro(): FiltroCreditoCliente {
    return FiltroCreditoCliente(
      pesquisa = edtPesquisa.value ?: ""
    )
  }

  override fun updateClientes(clientes: List<CreditoCliente>) {
    this.updateGrid(clientes)
  }

  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    return listOfNotNull(username?.impressoraDev)
  }


  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devCliCredito == true
  }

  override val label: String
    get() = "Crédito"

  override fun updateComponent() {
    viewModel.updateView()
  }
}