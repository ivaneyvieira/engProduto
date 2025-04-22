package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueGarantia
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueGarantiaViewModel
import br.com.astrosoft.produto.viewmodel.estoqueCD.TipoEstoque
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabEstoqueGarantia(val viewModel: TabEstoqueGarantiaViewModel) :
  TabPanelGrid<EstoqueGarantia>(EstoqueGarantia::class), ITabEstoqueGarantia {
  private var dlgEstoque: DlgEstoqueGarantia? = null
  private lateinit var edtNumero: IntegerField
  private lateinit var edtDateIncial: DatePicker
  private lateinit var edtDateFinal: DatePicker
  private lateinit var cmbLoja: Select<Loja>

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

    edtNumero = integerField("Número") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtDateIncial = datePicker("Data") {
      this.value = LocalDate.now()
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtDateFinal = datePicker("Data") {
      this.value = LocalDate.now()
      this.localePtBr()
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

  override fun Grid<EstoqueGarantia>.gridPanel() {
    selectionMode = Grid.SelectionMode.MULTI

    this.withEditor(
      classBean = EstoqueGarantia::class,
      openEditor = {
        val edit = getColumnBy(EstoqueGarantia::observacao) as? Focusable<*>
        edit?.focus()
      },
      closeEditor = {
        viewModel.updateGarantia(it.bean)
      })

    columnGrid(EstoqueGarantia::lojaSigla, header = "Loja")
    columnGrid(EstoqueGarantia::numero, header = "Garantia")
    addColumnButton(VaadinIcon.FILE_TABLE, "Pedido") { garantia ->
      dlgEstoque = DlgEstoqueGarantia(viewModel, garantia)
      dlgEstoque?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(EstoqueGarantia::data, header = "Data")
    columnGrid(EstoqueGarantia::hora, header = "Hora")
    columnGrid(EstoqueGarantia::codFor, header = "For Cod")
    columnGrid(EstoqueGarantia::nomeFor, header = "For Nome")

    columnGrid(EstoqueGarantia::observacao, header = "Observação", isExpand = true).textFieldEditor()
  }

  override fun filtro(): FiltroGarantia {
    return FiltroGarantia(
      numLoja = cmbLoja.value?.no ?: 0,
      numero = edtNumero.value ?: 0,
      dataInicial = edtDateIncial.value ?: LocalDate.now(),
      dataFinal = edtDateFinal.value ?: LocalDate.now()
    )
  }

  override fun updateProduto(produtos: List<EstoqueGarantia>) {
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

  override fun produtosSelecionado(): List<ProdutoEstoqueGarantia> {
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
    get() = "Garantia"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraEstoque.orEmpty().toList()
  }
}