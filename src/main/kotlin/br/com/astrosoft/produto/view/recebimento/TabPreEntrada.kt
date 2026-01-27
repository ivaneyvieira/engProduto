package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.produto.model.beans.Agenda
import br.com.astrosoft.produto.model.beans.ETipoAgenda
import br.com.astrosoft.produto.model.beans.FiltroAgenda
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.recebimento.ITabPreEntrada
import br.com.astrosoft.produto.viewmodel.recebimento.TabPreEntradaViewModel
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabPreEntrada(val viewModel: TabPreEntradaViewModel) :
  TabPanelGrid<Agenda>(Agenda::class), ITabPreEntrada {
  private lateinit var edtPesquisa: TextField
  private lateinit var cmbTipoAgenda: Select<ETipoAgenda>

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    cmbTipoAgenda = select("Agendamento") {
      setItems(*ETipoAgenda.entries.toTypedArray())
      setItemLabelGenerator {
        it.descricao
      }
      value = ETipoAgenda.TODOS
      addValueChangeListener { viewModel.updateView() }
    }
  }

  override fun Grid<Agenda>.gridPanel() {
    this.addClassName("styling")

    columnGrid(Agenda::loja, "Loja")
    columnGrid(Agenda::emissao, "Emissão", width = "6rem")
    columnGrid(Agenda::nf, "NF")
    columnGrid(Agenda::fornecedor, "For")
    columnGrid(Agenda::abreviacao, "Abrev")
    columnGrid(Agenda::total, "Valor NF")
    columnGrid(Agenda::frete, "Frete")
    columnGrid(Agenda::transp, "Transp")
    columnGrid(Agenda::nome, "Nome")
    addColumnButton(VaadinIcon.EDIT, "Agendamento", "Agd") { agenda ->
      DlgAgendamento(viewModel).edtAgendamento(agenda)
    }
    columnGrid(Agenda::dias, "Dias")
    columnGrid(Agenda::data, "Agendado", width = "6rem")
    columnGrid(Agenda::hora, "Hora", pattern = "HH:mm")
    columnGrid(Agenda::conhecimento, "CT-e").right()
    columnGrid(Agenda::emissaoConhecimento, "Emissão CTe", width = "7rem")
    columnGrid(Agenda::volume, "Vol").right()
    columnGrid(Agenda::invno, "Ord")
    columnGrid(Agenda::pedido, "Pedido")
    columnGrid(Agenda::recebedor, "Recebido")

    this.setPartNameGenerator { agend ->
      if (agend.empno == 0) {
        null
      } else {
        "amarelo"
      }
    }
  }

  override fun filtro(): FiltroAgenda {
    return FiltroAgenda(
      loja = 0,
      pesquisa = edtPesquisa.value ?: "",
      tipoAgenda = cmbTipoAgenda.value ?: ETipoAgenda.PENDENTE
    )
  }

  override fun updateNota(notas: List<Agenda>) {
    this.updateGrid(notas)
    gridPanel.getColumnBy(Agenda::abreviacao).setFooter("Total")
    gridPanel.getColumnBy(Agenda::total).setFooter(notas.sumOf { it.total }.format())
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