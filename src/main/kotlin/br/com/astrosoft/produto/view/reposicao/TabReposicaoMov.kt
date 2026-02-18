package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.estoqueCD.FormAutorizaPedido
import br.com.astrosoft.produto.viewmodel.reposicao.ITabReposicaoMov
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoMovViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabReposicaoMov(val viewModel: TabReposicaoMovViewModel) :
  TabPanelGrid<Movimentacao>(Movimentacao::class), ITabReposicaoMov {
  private var dlgEstoque: DlgReposicaoMov? = null
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

    button("Novo Pedido") {
      this.icon = VaadinIcon.NOTEBOOK.create()
      onClick {
        val loja = cmbLoja?.value
        viewModel.novoPedido(loja?.no ?: 0)
      }
    }
  }

  override fun Grid<Movimentacao>.gridPanel() {
    selectionMode = Grid.SelectionMode.MULTI

    addColumnButton(iconButton = VaadinIcon.FILE_TABLE, tooltip = "Produto", header = "Produto") { pedido ->
      dlgEstoque = DlgReposicaoMov(viewModel, pedido)
      dlgEstoque?.showDialog {
        viewModel.updateView()
      }
    }

    addColumnButton(iconButton = VaadinIcon.PRINT, tooltip = "Preview", header = "Preview") { pedido ->
      viewModel.previewPedido(pedido)
    }

    columnGrid(Movimentacao::lojaSigla, header = "Loja")
    columnGrid(Movimentacao::numero, header = "Pedido")
    columnGrid(Movimentacao::data, header = "Data")
    columnGrid(Movimentacao::hora, header = "Hora")
    columnGrid(Movimentacao::login, header = "Usuário", width = "7rem")
    columnGrid(Movimentacao::gravadoLogin, header = "Gravado", width = "7rem")
    columnGrid(Movimentacao::entregue, header = "Entregador", width = "7rem")
    columnGrid(Movimentacao::recebido, header = "Recebedor", width = "7rem")
  }

  override fun filtro(): FiltroMovimentacao {
    return FiltroMovimentacao(
      numLoja = cmbLoja.value?.no ?: 0,
      numero = edtNumero.value ?: 0,
      dataInicial = edtDateIncial.value ?: LocalDate.now(),
      dataFinal = edtDateFinal.value ?: LocalDate.now(),
    )
  }

  override fun updatePedidos(pedidos: List<Movimentacao>) {
    updateGrid(pedidos)
    dlgEstoque?.update()
  }

  override fun updateProdutos() {
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

  override fun autorizaPedido(caption: String, block: (IUser) -> Unit) {
    val form = FormAutorizaPedido()
    DialogHelper.showForm(caption = caption, form = form) {
      val user = AppConfig.findUser(form.login, form.senha)
      if (user != null) {
        block(user)
      } else {
        DialogHelper.showWarning("Usuário ou senha inválidos")
      }
    }
  }

  override fun autorizaAssinatura(assunto: String, block: (empno: Int, senha: String) -> Unit) {
    val form = FormFuncionario()
    DialogHelper.showForm(caption = assunto, form = form) {
      val empno = form.numero
      val senha = form.senha
      block(empno, senha)
    }
  }

  override fun produtosSelecionado(): List<ProdutoMovimentacao> {
    return dlgEstoque?.produtosSelecionado().orEmpty()
  }

  override fun produtosNaoSelecionado(): List<ProdutoMovimentacao> {
    return dlgEstoque?.produtosNaoSelecionado().orEmpty()
  }

  override fun adicionaPedido(movimentacao: Movimentacao) {
    val list = gridPanel.dataProvider.fetchAll() + movimentacao
    updateGrid(list)
  }

  override fun gravaSelecao() {
    dlgEstoque?.gravaSelecao()
  }

  override fun closeForm() {
    dlgEstoque?.closeForm()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.reposicaoMov == true
  }

  override val label: String
    get() = "Movimentação"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraEstoque.orEmpty().toList()
  }
}