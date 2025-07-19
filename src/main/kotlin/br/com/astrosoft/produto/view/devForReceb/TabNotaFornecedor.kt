package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaFornecedor
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaNulo
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaFornecedorViewModel
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaNuloViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaFornecedor(val viewModel: TabNotaFornecedorViewModel) :
  TabPanelGrid<FornecedorClass>(FornecedorClass::class), ITabNotaFornecedor {
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

  override fun Grid<FornecedorClass>.gridPanel() {
    this.addClassName("styling")
    this.format()

    columnGrid(FornecedorClass::no, header = "No")
    columnGrid(FornecedorClass::descricao, header = "Descrição")
    columnGrid(FornecedorClass::cnpjCpf, header = "CNPJ/CPF")
    columnGrid(FornecedorClass::classificacao, header = "Classificação")
  }

  override fun filtro(): FiltroFornecedor {
    return FiltroFornecedor(
      pesquisa = edtPesquisa.value ?: "",
    )
  }

  override fun updateFornecedor(fornecedore: List<FornecedorClass>) {
    this.updateGrid(fornecedore)
  }


  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devFor2NotaFornecedor == true
  }

  override val label: String
    get() = "Forn"

  override fun updateComponent() {
    viewModel.updateView()
  }
}