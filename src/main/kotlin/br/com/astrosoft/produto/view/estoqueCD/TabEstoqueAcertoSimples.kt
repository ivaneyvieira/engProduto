package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueAcertoSimples
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoSimplesViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
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

class TabEstoqueAcertoSimples(val viewModel: TabEstoqueAcertoSimplesViewModel) :
  TabPanelGrid<EstoqueAcerto>(EstoqueAcerto::class), ITabEstoqueAcertoSimples {
  private var dlgEstoque: DlgEstoqueAcertoSimples? = null
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
        viewModel.cancelarAcerto()
      }
    }

    button("Novo Pedido") {
      this.icon = VaadinIcon.NOTEBOOK.create()
      onClick {
        val loja = cmbLoja?.value
        viewModel.novoPedido(loja?.no ?: 0)
      }
    }
  }

  override fun Grid<EstoqueAcerto>.gridPanel() {
    selectionMode = Grid.SelectionMode.MULTI

    this.withEditor(
      classBean = EstoqueAcerto::class,
      openEditor = {
        val edit = getColumnBy(EstoqueAcerto::observacao) as? Focusable<*>
        edit?.focus()
      },
      closeEditor = {
        viewModel.updateAcerto(it.bean)
      })

    columnGrid(EstoqueAcerto::lojaSigla, header = "Loja")
    columnGrid(EstoqueAcerto::numero, header = "Acerto")
    addColumnButton(VaadinIcon.FILE_TABLE, "Pedido") { acerto ->
      dlgEstoque = DlgEstoqueAcertoSimples(viewModel, acerto)
      dlgEstoque?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(EstoqueAcerto::data, header = "Data")
    columnGrid(EstoqueAcerto::hora, header = "Hora")
    columnGrid(EstoqueAcerto::login, header = "Usuário", width = "80px")
    columnGrid(EstoqueAcerto::transacaoEnt, header = "Trans Ent")
    columnGrid(EstoqueAcerto::transacaoSai, header = "Trans Sai")
    columnGrid(EstoqueAcerto::gravadoStr, header = "Gravado")
    columnGrid(EstoqueAcerto::gravadoLoginStr, header = "Usuário", width = "200px")
    columnGrid(EstoqueAcerto::processadoStr, header = "Processado")
    columnGrid(EstoqueAcerto::observacao, header = "Observação", isExpand = true).textFieldEditor()
  }

  override fun filtro(): FiltroAcerto {
    return FiltroAcerto(
      numLoja = cmbLoja.value?.no ?: 0,
      numero = edtNumero.value ?: 0,
      dataInicial = edtDateIncial.value ?: LocalDate.now(),
      dataFinal = edtDateFinal.value ?: LocalDate.now(),
      simples = true,
    )
  }

  override fun updateProduto(produtos: List<EstoqueAcerto>) {
    updateGrid(produtos)
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

  override fun autorizaAcerto(block: (IUser) -> Unit) {
    val form = FormAutorizaAcerto()
    DialogHelper.showForm(caption = "Autoriza gravação do acerto", form = form) {
      val user = AppConfig.findUser(form.login, form.senha)
      if (user != null) {
        block(user)
      } else {
        DialogHelper.showWarning("Usuário ou senha inválidos")
      }
    }
  }

  override fun produtosSelecionado(): List<ProdutoEstoqueAcerto> {
    return dlgEstoque?.produtosSelecionado().orEmpty()
  }

  override fun adicionaAcerto(acerto: EstoqueAcerto) {
    val list = gridPanel.dataProvider.fetchAll() + acerto
    updateGrid(list)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueAcertoSimples == true
  }

  override val label: String
    get() = "Acerto 2"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraEstoque.orEmpty().toList()
  }
}