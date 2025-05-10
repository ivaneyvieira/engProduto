package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.nfd.DlgProdutosDevFor
import br.com.astrosoft.produto.view.nfd.columns.NotaColumns.colunaNFCliente
import br.com.astrosoft.produto.view.nfd.columns.NotaColumns.colunaNFData
import br.com.astrosoft.produto.view.nfd.columns.NotaColumns.colunaNFLoja
import br.com.astrosoft.produto.view.nfd.columns.NotaColumns.colunaNFNota
import br.com.astrosoft.produto.view.nfd.columns.NotaColumns.colunaNFTipo
import br.com.astrosoft.produto.view.nfd.columns.NotaColumns.colunaNFValor
import br.com.astrosoft.produto.view.nfd.columns.NotaColumns.colunaNomeCliente
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaNFDAberta
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaNFDAbertaViewModel
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

class TabNotaNFDAberta(val viewModel: TabNotaNFDAbertaViewModel) : TabPanelGrid<NotaSaida>(NotaSaida::class), ITabNotaNFDAberta {
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

  override fun Grid<NotaSaida>.gridPanel() {
    this.addClassName("styling")
    this.format()

    colunaNFLoja()

    columnGrid(NotaSaida::usuarioSingExp, "Autoriza")

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosNFDAberta(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaNFNota()
    colunaNFData()
    //colunaHora()
    //colRota = colunaRota()
    colunaNFCliente()
    colunaNomeCliente()
    //colunaNFVendedor()
    //colunaNomeVendedor()
    colunaNFValor()
    colunaNFTipo()
    //colunaNFEntregaRetira()
    //colunaNFSituacao()

    this.setPartNameGenerator {
      val countEnt = it.countEnt ?: 0
      val countImp = it.countImp ?: 0
      val cancelada = it.cancelada ?: "N"
      when {
        cancelada == "S" -> "vermelho"

        countImp > 0 -> "azul"

        countEnt > 0 -> "amarelo"

        else -> null
      }
    }
  }

  override fun filtro(marca: EMarcaNota): FiltroNota {
    return FiltroNota(
      marca = marca,
      tipoNota = ETipoNotaFiscal.DEVOLUCAO,
      loja = cmbLoja.value?.no ?: 0,
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      todosLocais = true,
      pesquisa = edtPesquisa.value ?: "",
    )
  }

  override fun updateNotas(notas: List<NotaSaida>) {
    updateGrid(notas)
  }

  override fun findNota(): NotaSaida? {
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
    return username?.nfdDevFor == true
  }

  override val label: String
    get() = "Dev For"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraNotaTermica?.toList().orEmpty()
  }
}