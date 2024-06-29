package br.com.astrosoft.produto.view.cliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.DadosCliente
import br.com.astrosoft.produto.model.beans.FiltroDadosCliente
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.cliente.ITabCadastro
import br.com.astrosoft.produto.viewmodel.cliente.TabCadastroViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabCadastro(val viewModel: TabCadastroViewModel) :
  TabPanelGrid<DadosCliente>(DadosCliente::class), ITabCadastro {

  lateinit var edtPesquisa: TextField

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.LAZY
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "creditoCliente") {
      viewModel.geraPlanilha()
    }
  }

  override fun Grid<DadosCliente>.gridPanel() {
    this.addClassName("styling")
    this.format()

    this.setSelectionMode(Grid.SelectionMode.MULTI)

    addColumnSeq("Seq")
    columnGrid(DadosCliente::custno, header = "Número")
    columnGrid(DadosCliente::nome, header = "Nome").expand()
    columnGrid(DadosCliente::cpfCnpj, header = "CPF/CNPJ")
    columnGrid(DadosCliente::rg, header = "RG")
    columnGrid(DadosCliente::endereco, header = "Endereço").expand()
    columnGrid(DadosCliente::bairro, header = "Bairro")
    columnGrid(DadosCliente::rota, header = "Rota")
    columnGrid(DadosCliente::cidade, header = "Cidade")
    columnGrid(DadosCliente::estado, header = "Estado")
    columnGrid(DadosCliente::fone, header = "Fone")
  }

  override fun filtro(): FiltroDadosCliente {
    return FiltroDadosCliente(
      pesquisa = edtPesquisa.value ?: ""
    )
  }

  override fun updateNotas(movManualList: List<DadosCliente>) {
    this.updateGrid(movManualList)
  }

  override fun clientesSelecionados(): List<DadosCliente> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci
    return userSaci?.clienteCadastro == true
  }

  override val label: String
    get() = "Cadastro"

  override fun updateComponent() {
    viewModel.updateView()
  }
}