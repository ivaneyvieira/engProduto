package br.com.astrosoft.produto.view.acertoEstoque

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.acertoEstoque.ITabAcertoMovAtacado
import br.com.astrosoft.produto.viewmodel.acertoEstoque.TabAcertoMovAtacadoViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabAcertoMovAtacado(val viewModel: TabAcertoMovAtacadoViewModel) :
  TabPanelGrid<MovAtacado>(MovAtacado::class),
  ITabAcertoMovAtacado {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.storeno != 0
    cmbLoja.value = viewModel.findLoja(user?.storeno ?: 0) ?: Loja.lojaZero
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
    init()
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data inicial") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    //this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "mov") {
    //val mov = itensSelecionados()
    //viewModel.geraPlanilha(mov)
    //}
  }

  override fun Grid<MovAtacado>.gridPanel() {
    this.addClassName("styling")
    this.setSelectionMode(Grid.SelectionMode.MULTI)
    columnGrid(MovAtacado::loja, header = "Loja")
    columnGrid(MovAtacado::codigo, header = "Código")
    columnGrid(MovAtacado::descricao, header = "Descrição").expand()
    columnGrid(MovAtacado::grade, header = "Grade")
    columnGrid(MovAtacado::data, header = "Data")
    columnGrid(MovAtacado::pedido, header = "Pedido")
    columnGrid(MovAtacado::transacao, header = "Transação")
    columnGrid(MovAtacado::tipo, header = "Tipo")
    columnGrid(MovAtacado::qtEntrada, header = "Entrada")
    columnGrid(MovAtacado::qtSaida, header = "Saída")
    columnGrid(MovAtacado::qtVarejo, header = "Varejo")
    columnGrid(MovAtacado::qtTotal, header = "Total")
  }

  override fun filtro(): MovManualFilter {
    return MovManualFilter(
      loja = cmbLoja.value?.no ?: 0,
      query = edtPesquisa.value ?: "",
      dataI = edtDataInicial.value,
      dataF = edtDataFinal.value,
      tipo = ETipoMovManul.TODOS
    )
  }

  override fun updateNotas(movManualList: List<MovAtacado>) {
    updateGrid(movManualList)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.acertoMovAtacado == true
  }

  override val label: String
    get() = "Atacado"

  override fun updateComponent() {
    viewModel.updateView()
  }
}