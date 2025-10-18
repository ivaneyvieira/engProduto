package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueForn
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueFornViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabEstoqueForn(val viewModel: TabEstoqueFornViewModel) :
  TabPanelGrid<FornecedorLoja>(FornecedorLoja::class), ITabEstoqueForn {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDateInicial: DatePicker
  private lateinit var edtDateFinal: DatePicker

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtDateInicial = datePicker("Data Inicial") {
      this.localePtBr()
      this.isClearButtonVisible = true
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtDateFinal = datePicker("Data Final") {
      this.localePtBr()
      this.isClearButtonVisible = true
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<FornecedorLoja>.gridPanel() {
    selectionMode = Grid.SelectionMode.SINGLE

    this.withEditor(
      classBean = FornecedorLoja::class,
      openEditor = {
        val edit = getColumnBy(FornecedorLoja::dataDS) as? Focusable<*>
        edit?.focus()
      },
      closeEditor = {
        viewModel.saveForn(it.bean)
      })

    columnGrid(FornecedorLoja::vendno, header = "Codigo")
    columnGrid(FornecedorLoja::abrev, header = "Nome", width = "200px")
    columnGrid(FornecedorLoja::dataDS, header = "Data DS", width = "160px").dateFieldEditor()
    columnGrid(FornecedorLoja::dataMR, header = "Data MR", width = "160px").dateFieldEditor()
    columnGrid(FornecedorLoja::dataMF, header = "Data MF", width = "160px").dateFieldEditor()
    columnGrid(FornecedorLoja::dataPK, header = "Data PK", width = "160px").dateFieldEditor()
    columnGrid(FornecedorLoja::dataTM, header = "Data TM", width = "160px").dateFieldEditor()
  }

  override fun filtro(): FiltroFornecedorLoja {
    return FiltroFornecedorLoja(
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDateInicial.value,
      dataFinal = edtDateFinal.value
    )
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueForn == true
  }

  override val label: String
    get() = "Fornecedor"

  override fun updateComponent() {
    viewModel.updateView()
  }
}