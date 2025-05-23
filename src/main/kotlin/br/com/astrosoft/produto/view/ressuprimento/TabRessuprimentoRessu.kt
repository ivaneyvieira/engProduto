package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.ressuprimento.ITabRessuprimentoRessup
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoRessupViewModel
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

class TabRessuprimentoRessu(val viewModel: TabRessuprimentoRessupViewModel) :
  TabPanelGrid<DadosRessuprimento>(DadosRessuprimento::class), ITabRessuprimentoRessup {
  private var dlgProduto: DlgProdutosRessuEdit? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaRessu != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaRessu ?: 0) ?: Loja.lojaZero
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
    edtDataInicial = datePicker("Data Inicial") {
      value = LocalDate.now()
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      value = LocalDate.now()
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<DadosRessuprimento>.gridPanel() {
    this.addClassName("styling")
    this.format()

    columnGrid(DadosRessuprimento::lojaRessuprimento, "Loja")
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { ressuprimento ->
      dlgProduto = DlgProdutosRessuEdit(viewModel, ressuprimento)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(DadosRessuprimento::data, "Data")
    columnGrid(DadosRessuprimento::pedido, "Pedido")
    columnGrid(DadosRessuprimento::codFornecedor, "No Forn")
    columnGrid(DadosRessuprimento::totalPedido, "Total Pedido")
    columnGrid(DadosRessuprimento::observacao, "Observação").expand()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.ressuprimentoRessu == true
  }

  override val label: String
    get() = "Ressup"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun filtro(): FiltroDadosProdutosRessuprimento {
    return FiltroDadosProdutosRessuprimento(
      loja = cmbLoja.value.no,
      pesquisa = edtPesquisa.value,
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
    )
  }

  override fun updateRessuprimentos(ressuprimentos: List<DadosRessuprimento>) {
    this.updateGrid(ressuprimentos)
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelecionados(): List<DadosProdutosRessuprimento> {
    return dlgProduto?.produtosSelecionados() ?: emptyList()
  }
}

