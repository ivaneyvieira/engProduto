package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.CreditoCliente
import br.com.astrosoft.produto.model.beans.FiltroCreditoCliente
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevCliCredito
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevCliCreditoViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

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
    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "creditoCliente") {
      val clientes = listBeans()
      viewModel.geraPlanilha(clientes)
    }
  }

  override fun Grid<CreditoCliente>.gridPanel() {
    this.addClassName("styling")
    columnGrid(CreditoCliente::loja, header = "Loja").right()
    addColumnButton(VaadinIcon.PRINT, "Imprimir vale troca", "Imprimir") { nota ->
      viewModel.imprimeCredito(nota)
    }
    columnGrid(CreditoCliente::ni, header = "NI").right()
    columnGrid(CreditoCliente::nfDev, header = "NF Dev").right()
    columnGrid(CreditoCliente::dtDev, header = "Data")
    columnGrid(CreditoCliente::vendno, header = "Cód For")
    columnGrid(CreditoCliente::fornecedor, header = "Fornecedor")
    columnGrid(CreditoCliente::valorDev, header = "Valor Dev")
    columnGrid(CreditoCliente::tipo, header = "Tipo")
    columnGrid(CreditoCliente::observacao, header = "Observação")
    columnGrid(CreditoCliente::codigo, header = "Cód Cliente").right()
    columnGrid(CreditoCliente::nome, header = "Nome do Cliente").expand()
    columnGrid(CreditoCliente::valorVenda, header = "Valor Venda")
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
    return username?.impressoraDev.orEmpty().toList()
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