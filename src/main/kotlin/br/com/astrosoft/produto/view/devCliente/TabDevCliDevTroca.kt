package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.DevTroca
import br.com.astrosoft.produto.model.beans.FiltroDevTroca
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevCliDevTroca
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevCliDevTrocaViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import java.time.LocalDate

class TabDevCliDevTroca(val viewModel: TabDevCliDevTrocaViewModel) :
  TabPanelGrid<DevTroca>(DevTroca::class),
  ITabDevCliDevTroca {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

  init {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaVale != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaVale ?: 0) ?: Loja.lojaZero
  }

  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    return listOfNotNull(username?.impressoraDev)
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
    edtDataInicial = datePicker("Data inicial") {
      this.localePtBr()
      this.value = LocalDate.now()
      this.isVisible = AppConfig.userLogin()?.admin == true
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      this.value = LocalDate.now()
      this.isVisible = AppConfig.userLogin()?.admin == true
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<DevTroca>.gridPanel() {
    this.addClassName("styling")
    columnGrid(DevTroca::loja, header = "Loja")
    columnGrid(DevTroca::pdv, header = "PDV")
    columnGrid(DevTroca::xano, header = "Transação")
    columnGrid(DevTroca::data, header = "Data")
    columnGrid(DevTroca::nfVenda, header = "NF").right()
    columnGrid(DevTroca::vlTotal, header = "Valor")
    columnGrid(DevTroca::ni, header = "NI")
    columnGrid(DevTroca::cliente, header = "Cliente")
  }

  override fun filtro(): FiltroDevTroca {
    return FiltroDevTroca(
      loja = cmbLoja.value?.no ?: 0,
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
    )
  }

  override fun updateNotas(notas: List<DevTroca>) {
    updateGrid(notas)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devCliDevTroca == true
  }

  override val label: String
    get() = "Troca"

  override fun updateComponent() {
    viewModel.updateView()
  }
}