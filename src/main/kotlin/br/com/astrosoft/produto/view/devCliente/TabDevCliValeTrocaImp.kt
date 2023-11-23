package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.EntradaDevCli
import br.com.astrosoft.produto.model.beans.FiltroEntradaDevCli
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevCliValeTrocaImp
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevCliValeTrocaImpViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabDevCliValeTrocaImp(val viewModel: TabDevCliValeTrocaImpViewModel) :
  TabPanelGrid<EntradaDevCli>(EntradaDevCli::class),
  ITabDevCliValeTrocaImp {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

  init {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isVisible = user?.storeno == 0
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
  }

  override fun Grid<EntradaDevCli>.gridPanel() {
    this.addClassName("styling")
    columnGrid(EntradaDevCli::loja, header = "Loja")
    addColumnButton(VaadinIcon.PRINT, "Imprimir vale troca", "Imprimir") { nota ->
      viewModel.imprimeValeTroca(nota)
    }
    columnGrid(EntradaDevCli::invno, header = "NI")
    columnGrid(EntradaDevCli::notaFiscal, header = "Nota Devolução")
    columnGrid(EntradaDevCli::data, header = "Data")
    columnGrid(EntradaDevCli::vendno, header = "Cód For")
    columnGrid(EntradaDevCli::fornecedor, header = "Fornecedor")
    columnGrid(EntradaDevCli::valor, header = "Valor Devolução")
    columnGrid(EntradaDevCli::nfVenda, header = "Nota Venda")
    columnGrid(EntradaDevCli::nfData, header = "Data")
    columnGrid(EntradaDevCli::custno, header = "Cód Cliente")
    columnGrid(EntradaDevCli::cliente, header = "Nome do Cliente")
    columnGrid(EntradaDevCli::nfValor, header = "Valor Venda")
  }

  override fun filtro(): FiltroEntradaDevCli {
    return FiltroEntradaDevCli(
      loja = cmbLoja.value?.no ?: 0,
      query = edtPesquisa.value ?: "",
      dataI = edtDataInicial.value,
      dataF = edtDataFinal.value,
      impresso = true,
    )
  }

  override fun updateNotas(notas: List<EntradaDevCli>) {
    updateGrid(notas)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devCliValeTrocaImp == true
  }

  override val label: String
    get() = "Impresso"

  override fun updateComponent() {
    viewModel.updateView()
  }
}