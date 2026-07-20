package br.com.astrosoft.produto.view.cliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.DadosCredito
import br.com.astrosoft.produto.model.beans.FiltroDadosCredito
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.cliente.ITabCredito
import br.com.astrosoft.produto.viewmodel.cliente.TabCreditoViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabCredito(val viewModel: TabCreditoViewModel) :
  TabPanelGrid<DadosCredito>(DadosCredito::class), ITabCredito {

  lateinit var edtPesquisa: TextField

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "creditoCliente") {
      viewModel.geraPlanilha()
    }
  }

  override fun Grid<DadosCredito>.gridPanel() {
    this.addClassName("styling")
    this.format()

    this.setSelectionMode(Grid.SelectionMode.MULTI)

    addColumnSeq("Seq")
    columnGrid(DadosCredito::custno, header = "Número")
    columnGrid(DadosCredito::nome, header = "Nome").expand()
    columnGrid(DadosCredito::cpfCnpj, header = "CPF/CNPJ")
    columnGrid(DadosCredito::tipoCliente, header = "Tipo")
    columnGrid(DadosCredito::dataCredito, header = "Dara Crédito")
    columnGrid(DadosCredito::limiteCredito, header = "Limite Cŕedito")
    columnGrid(DadosCredito::ultCompra, header = "Ult Compra")
    columnGrid(DadosCredito::valorAberto, header = "V. Aberto")
    columnGrid(DadosCredito::valorAtrasado, header = "V. Atraso")
    columnGrid(DadosCredito::valorDisponivel, header = "V. Disponivel")
  }

  override fun filtro(): FiltroDadosCredito {
    return FiltroDadosCredito(
      pesquisa = edtPesquisa.value ?: ""
    )
  }

  override fun updateNotas(movManualList: List<DadosCredito>) {
    this.updateGrid(movManualList)
  }

  override fun clientesSelecionados(): List<DadosCredito> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci
    return userSaci?.clienteCredito == true
  }

  override val label: String
    get() = "Credito"

  override fun updateComponent() {
    viewModel.updateView()
  }
}