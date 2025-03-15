package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.FiltroAcerto
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueAcerto
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.integerField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabEstoqueAcerto(val viewModel: TabEstoqueAcertoViewModel) :
  TabPanelGrid<ProdutoEstoqueAcerto>(ProdutoEstoqueAcerto::class), ITabEstoqueAcerto {
  private lateinit var edtNumero: IntegerField
  private lateinit var edtDate: DatePicker

  override fun HorizontalLayout.toolBarConfig() {
    edtNumero = integerField("Número") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtDate = datePicker("Data") {
      this.value = LocalDate.now()
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<ProdutoEstoqueAcerto>.gridPanel() {
    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)

    columnGrid(ProdutoEstoqueAcerto::lojaSigla, header = "Loja")
    columnGrid(ProdutoEstoqueAcerto::numero, header = "Acerto")
    addColumnButton(VaadinIcon.PRINT, "Imprimir") { acerto ->
      viewModel.imprimir(acerto)
    }
    columnGrid(ProdutoEstoqueAcerto::data, header = "Data")
    columnGrid(ProdutoEstoqueAcerto::hora, header = "Hora")
    columnGrid(ProdutoEstoqueAcerto::usuario, header = "Usuário", width = "200px")
  }

  override fun filtro(): FiltroAcerto {
    return FiltroAcerto(
      numLoja = 4,
      numero = edtNumero.value ?: 0,
      data = edtDate.value ?: LocalDate.now()
    )
  }

  override fun updateProduto(produtos: List<ProdutoEstoqueAcerto>) {
    updateGrid(produtos)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueAcerto == true
  }

  override val label: String
    get() = "Acerto"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraEstoque.orEmpty().toList()
  }
}