package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.devolucao.model.beans.Agenda
import br.com.astrosoft.devolucao.model.beans.FiltroAgenda
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.recebimento.ITabAgenda
import br.com.astrosoft.produto.viewmodel.recebimento.TabAgendaViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabAgenda(val viewModel: TabAgendaViewModel) :
  TabPanelGrid<Agenda>(Agenda::class), ITabAgenda {
  private lateinit var edtPesquisa: TextField

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<Agenda>.gridPanel() {
    columnGrid(Agenda::loja, "Loja")
    columnGrid(Agenda::data, "Data")
    columnGrid(Agenda::hora, "Hora")
    columnGrid(Agenda::frete, "Frete")
    columnGrid(Agenda::emissao, "Emissão")
    columnGrid(Agenda::nf, "NF")
    columnGrid(Agenda::transp, "Transp")
    columnGrid(Agenda::nome, "Nome")
    columnGrid(Agenda::cnpj, "CNPJ")
    columnGrid(Agenda::volume, "Volume")
    columnGrid(Agenda::recebedor, "Recebedor")
    columnGrid(Agenda::invno, "Ord")
    columnGrid(Agenda::pedido, "Pedido")
    columnGrid(Agenda::fornecedor, "Fornecedor")
    columnGrid(Agenda::abreviacao, "Abrev")
  }

  override fun filtro(): FiltroAgenda {
    return FiltroAgenda(
      loja = 0,
      pesquisa = edtPesquisa.value ?: ""
    )
  }

  override fun updateNota(notas: List<Agenda>) {
    this.updateGrid(notas)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.recebimentoAgenda == true
  }

  override val label: String
    get() = "Pré-entrada"

  override fun updateComponent() {
    viewModel.updateView()
  }
}