package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devFor2.ITabPedidoGarantia
import br.com.astrosoft.produto.viewmodel.devFor2.TabPedidoGarantiaViewModel
import br.com.astrosoft.produto.viewmodel.devFor2.TipoEstoque
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabPedidoGarantia(val viewModel: TabPedidoGarantiaViewModel) :
  TabPanelGrid<PedidoGarantia>(PedidoGarantia::class), ITabPedidoGarantia {
  private var dlgEstoque: DlgPedidoGarantia? = null
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDateIncial: DatePicker
  private lateinit var edtDateFinal: DatePicker
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbTipo: Select<ETipoDevolvidoGarantia>

  fun init() {
    val user = AppConfig.userLogin() as? UserSaci
    val itens = if (user?.admin == true) {
      listOf(Loja.lojaZero) + viewModel.findAllLojas()
    } else {
      viewModel.findAllLojas().filter { it.no == (user?.lojaConferencia ?: 0) }
    }
    cmbLoja.setItems(itens)
    cmbLoja.value = itens.firstOrNull()
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

    edtDateIncial = datePicker("Data Inicial") {
      this.value = LocalDate.of(2025, 4, 1)
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtDateFinal = datePicker("Data Final") {
      this.value = LocalDate.now()
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    cmbTipo = select("Tipo Garantia") {
      this.setItems(ETipoDevolvidoGarantia.entries)
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      this.value = ETipoDevolvidoGarantia.PENDENTE
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    button("Cancelar") {
      this.icon = VaadinIcon.CLOSE.create()
      onClick {
        viewModel.cancelarGarantia()
      }
    }
  }

  override fun Grid<PedidoGarantia>.gridPanel() {
    selectionMode = Grid.SelectionMode.MULTI

    this.withEditor(
      classBean = PedidoGarantia::class,
      openEditor = {
        val edit = getColumnBy(PedidoGarantia::observacao) as? Focusable<*>
        edit?.focus()
      },
      closeEditor = {
        viewModel.updateGarantia(it.bean)
      })

    columnGrid(PedidoGarantia::lojaSigla, header = "Loja")
    columnGrid(PedidoGarantia::numero, header = "Garantia")
    addColumnButton(VaadinIcon.FILE_TABLE, "Pedido") { garantia ->
      dlgEstoque = DlgPedidoGarantia(viewModel, garantia)
      dlgEstoque?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(PedidoGarantia::data, header = "Data")
    columnGrid(PedidoGarantia::hora, header = "Hora")
    columnGrid(PedidoGarantia::codFor, header = "For Cod")
    columnGrid(PedidoGarantia::nomeFor, header = "For Nome")
    columnGrid(PedidoGarantia::dataNfdGarantia, header = "Data").dateFieldEditor()
    columnGrid(PedidoGarantia::nfdGarantia, header = "NFD", width = "7rem").right().textFieldEditor()
    columnGrid(PedidoGarantia::valorTotal, header = "Valor", width = "7rem")

    columnGrid(PedidoGarantia::observacao, header = "Observação", isExpand = true).textFieldEditor()
  }

  override fun filtro(): FiltroGarantia {
    return FiltroGarantia(
      numLoja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDateIncial.value ?: LocalDate.now(),
      dataFinal = edtDateFinal.value ?: LocalDate.now(),
      processado = (cmbTipo.value ?: ETipoDevolvidoGarantia.PENDENTE).codigo,
    )
  }

  override fun updateProduto(produtos: List<PedidoGarantia>) {
    updateGrid(produtos)
    dlgEstoque?.updateGarantia(produtos)
  }

  override fun updateProduto() {
    dlgEstoque?.update()
  }

  override fun filtroVazio(): FiltroProdutoEstoque {
    val user = AppConfig.userLogin() as? UserSaci
    val listaUser = user?.listaEstoque.orEmpty().toList().ifEmpty {
      listOf("TODOS")
    }

    return FiltroProdutoEstoque(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = "",
      codigo = 0,
      grade = "",
      caracter = ECaracter.TODOS,
      localizacao = "",
      fornecedor = "",
      centroLucro = 0,
      estoque = EEstoque.TODOS,
      saldo = 0,
      inativo = EInativo.TODOS,
      uso = EUso.TODOS,
      listaUser = listaUser,
    )
  }

  override fun autorizaGarantia(block: (IUser) -> Unit) {
    val form = FormAutorizaGarantia()
    DialogHelper.showForm(caption = "Autoriza gravação do garantia", form = form) {
      val user = AppConfig.findUser(form.login, form.senha)
      if (user != null) {
        block(user)
      } else {
        DialogHelper.showWarning("Usuário ou senha inválidos")
      }
    }
  }

  override fun produtosSelecionado(): List<ProdutoPedidoGarantia> {
    return dlgEstoque?.produtosSelecionado().orEmpty()
  }

  override fun formSeleionaEstoque(block: (estoque: TipoEstoque?) -> Unit) {
    val form = FormSelecionaEstoque()
    DialogHelper.showForm(caption = "Seleciona Estoque", form = form) {
      block(form.selecionaEstoque())
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueGarantia == true
  }

  override val label: String
    get() = "Ped Garantia"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraEstoque.orEmpty().toList()
  }
}