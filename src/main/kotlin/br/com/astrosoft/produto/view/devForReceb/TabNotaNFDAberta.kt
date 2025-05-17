package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaNFDAberta
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaNFDAbertaViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabNotaNFDAberta(val viewModel: TabNotaNFDAbertaViewModel) : TabPanelGrid<NotaSaidaDev>(NotaSaidaDev::class),
  ITabNotaNFDAberta {
  //private var colRota: Grid.Column<NotaSaida>? = null
  private var dlgProduto: DlgProdutosNFDAberta? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var edtPesquisa: TextField

  fun init() {
    val user = AppConfig.userLogin() as? UserSaci
    val loja = user?.lojaNota ?: 0
    val lojaSelecionada = viewModel.findAllLojas().firstOrNull { it.no == loja }
    cmbLoja.isReadOnly = (user?.admin == false) && (lojaSelecionada?.no != 0)
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    cmbLoja.value = lojaSelecionada ?: Loja.lojaZero
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
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data Inicial") {
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

  override fun Grid<NotaSaidaDev>.gridPanel() {
    this.addClassName("styling")
    this.format()

    columnGrid(NotaSaidaDev::loja) {
      this.setHeader("Loja")
    }

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosNFDAberta(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(NotaSaidaDev::nota) {
      this.setHeader("Nota")
    }
    columnGrid(NotaSaidaDev::data) {
      this.setHeader("Data")
    }
    columnGrid(NotaSaidaDev::cliente) {
    }
    columnGrid(NotaSaidaDev::nomeCliente) {
      this.setHeader("Nome Cliente")
    }
    columnGrid(NotaSaidaDev::valorNota, width="120px") {
      this.setHeader("Valor")
    }
    columnGrid(NotaSaidaDev::situacaoDup) {
      this.setHeader("Status Dup")
    }
  }

  override fun filtro(): FiltroNotaDev {
    return FiltroNotaDev(
      loja = cmbLoja.value?.no ?: 0,
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      pesquisa = edtPesquisa.value ?: "",
    )
  }

  override fun updateNotas(notas: List<NotaSaidaDev>) {
    updateGrid(notas)
    val colValor = gridPanel.getColumnBy(NotaSaidaDev::valorNota)
    colValor.setFooter(notas.sumOf { it.valorNota ?: 0.00 }.format())
  }

  override fun findNota(): NotaSaidaDev? {
    return dlgProduto?.nota
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelcionados(): List<ProdutoNFS> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.notaNFDAberta == true
  }

  override val label: String
    get() = "NFD Aberta"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraNotaTermica?.toList().orEmpty()
  }
}