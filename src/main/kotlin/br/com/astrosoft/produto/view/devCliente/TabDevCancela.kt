package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevCancela
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevCancelaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabDevCancela(val viewModel: TabDevCancelaViewModel) : TabPanelGrid<EntradaDevCli>(EntradaDevCli::class),
  ITabDevCancela {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaVale != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaVale ?: 0) ?: Loja.lojaZero
  }

  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.impressoraDev.orEmpty().toList()
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
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
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
    button("Desfaz Devolução") {
      this.icon = VaadinIcon.TRASH.create()
      onClick {
        viewModel.desfazTroca()
      }
    }
  }

  override fun Grid<EntradaDevCli>.gridPanel() {
    this.addClassName("styling")
    this.setSelectionMode(Grid.SelectionMode.MULTI)

    columnGrid(EntradaDevCli::loja, header = "Loja")
    columnGrid(EntradaDevCli::loginSolicitacao, header = "Autorização")
    columnGrid(EntradaDevCli::loginAutorizacao, header = "Assina Troca")
    columnGrid(EntradaDevCli::fezTrocaCol, header = "Troca")
    columnGrid(EntradaDevCli::invno, header = "NI")
    columnGrid(EntradaDevCli::notaFiscal, header = "NF Dev").right()
    columnGrid(EntradaDevCli::data, header = "Data", width = "6rem")
    columnGrid(EntradaDevCli::vendno, header = "Cód For")
    columnGrid(EntradaDevCli::fornecedor, header = "Fornecedor")
    columnGrid(EntradaDevCli::valor, header = "Valor Dev")
    columnGrid(EntradaDevCli::observacao, header = "Observação").expand()
    columnGrid(EntradaDevCli::tipoObs, header = "Tipo")
    columnGrid(EntradaDevCli::nfVenda, header = "NF Venda").right()
    columnGrid(EntradaDevCli::nfData, header = "Data", width = "6rem")
    columnGrid(EntradaDevCli::custnoVend, header = "Cód Cli")
    columnGrid(EntradaDevCli::cliente, header = "Nome do Cliente").expand()
    columnGrid(EntradaDevCli::nfValor, header = "Valor Venda")
    columnGrid(EntradaDevCli::impressora, header = "Impressora")
    columnGrid(EntradaDevCli::userName, header = "Usuário")

  }

  override fun filtro(): FiltroEntradaDevCli {
    val user = AppConfig.userLogin() as? UserSaci
    return FiltroEntradaDevCli(
      loja = cmbLoja.value?.no ?: 0,
      query = edtPesquisa.value ?: "",
      dataI = edtDataInicial.value,
      dataF = edtDataFinal.value,
      impresso = null,
      dataLimiteInicial = LocalDate.of(2023, 12, 1),
      tipo = ETipoDevCli.COM,
      dataCorte = user?.dataVendaDevolucao,
      cancelado = true
    )
  }

  override fun updateNotas(notas: List<EntradaDevCli>) {
    this.updateGrid(notas)
  }

  override fun notasSelecionados(): List<EntradaDevCli> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devCliCancela == true
  }

  override val label: String
    get() = "Cancela"

  override fun updateComponent() {
    viewModel.updateView()
  }
}