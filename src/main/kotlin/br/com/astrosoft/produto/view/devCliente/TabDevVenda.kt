package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.FiltroNotaVenda
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaVenda
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevVenda
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevVendaViewModel
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

class TabDevVenda(val viewModel: TabDevVendaViewModel) :
  TabPanelGrid<NotaVenda>(NotaVenda::class),
  ITabDevVenda {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
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

  override fun Grid<NotaVenda>.gridPanel() {
    this.addClassName("styling")
    this.setSelectionMode(Grid.SelectionMode.MULTI)

    addColumnSeq("Seq")
    columnGrid(NotaVenda::loja, header = "Loja")
    columnGrid(NotaVenda::pedido, header = "Pedido")
    columnGrid(NotaVenda::pdv, header = "PDV")
    columnGrid(NotaVenda::data, header = "Data")
    columnGrid(NotaVenda::nota, header = "NF")
    columnGrid(NotaVenda::tipoNf, header = "Tipo NF")
    columnGrid(NotaVenda::hora, header = "Hora")
    columnGrid(NotaVenda::tipoPgto, header = "Tipo Pgto")
    columnGrid(NotaVenda::valor, header = "Valor")
    columnGrid(NotaVenda::cliente, header = "Cód Cli")
    columnGrid(NotaVenda::nomeCliente, header = "Nome Cliente").expand()
    columnGrid(NotaVenda::vendedor, header = "Vendedor").expand()
  }

  override fun filtro(): FiltroNotaVenda {
    return FiltroNotaVenda(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
    )
  }

  override fun updateNotas(notas: List<NotaVenda>) {
    this.updateGrid(notas)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devVenda == true
  }

  override val label: String
    get() = "Venda"

  override fun updateComponent() {
    viewModel.updateView()
  }
}