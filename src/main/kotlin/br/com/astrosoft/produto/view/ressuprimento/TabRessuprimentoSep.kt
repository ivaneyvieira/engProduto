package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoData
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoNumero
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoSing
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoTransportadorPor
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoUsuarioApp
import br.com.astrosoft.produto.viewmodel.ressuprimento.ITabRessuprimentoSep
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoSepViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabRessuprimentoSep(val viewModel: TabRessuprimentoSepViewModel) :
  TabPanelGrid<Ressuprimento>(Ressuprimento::class), ITabRessuprimentoSep {
  private var dlgProduto: DlgProdutosRessuSep? = null
  private lateinit var edtRessuprimento: IntegerField
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var cmbLoja: Select<Loja>

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
    edtRessuprimento = integerField("Número") {
      this.isVisible = false
      this.value = 0
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
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
    edtDataInicial = datePicker("Data Inicial") {
      this.isVisible = false
      value = null
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.isVisible = false
      value = null
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    button("Produtos") {
      this.icon = VaadinIcon.FILE_TABLE.create()
      onClick {
        viewModel.processamentoProdutos()
      }
    }
    button("Exclui") {
      val user = AppConfig.userLogin() as? UserSaci
      this.isVisible = user?.ressuprimentoExclui == true || user?.admin == true
      icon = VaadinIcon.TRASH.create()
      onClick {
        viewModel.excluiRessuprimento()
      }
    }
  }

  override fun Grid<Ressuprimento>.gridPanel() {
    val user = AppConfig.userLogin() as? UserSaci
    this.addClassName("styling")
    this.format()
    this.selectionMode = Grid.SelectionMode.MULTI

    addColumnButton(VaadinIcon.PRINT, "Preview", "Preview") { pedido ->
      viewModel.previewPedido(pedido) {
        viewModel.marcaImpressao(pedido)
      }
    }

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { ressuprimento ->
      if (ressuprimento.recebidoPor.isNullOrBlank() && user?.admin != true) {
        DialogHelper.showError("O ressuprimento não foi assinado pelo recebedor")
      } else if (ressuprimento.notaBaixa.isNullOrBlank() && user?.admin != true) {
        DialogHelper.showError("O ressuprimento não tem nota de transferencia")
      } else {
        dlgProduto = DlgProdutosRessuSep(viewModel, listOf(ressuprimento))
        dlgProduto?.showDialog {
          viewModel.updateView()
        }
      }
    }

    colunaRessuprimentoNumero()
    colunaRessuprimentoData()
    if (user?.ressuprimentoRecebedor == false || user?.admin == true) {
      addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { pedido ->
        viewModel.formAutoriza(pedido)
      }
    }
    colunaRessuprimentoSing()
    if (user?.ressuprimentoRecebedor == false || user?.admin == true) {
      addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { pedido ->
        viewModel.formTransportado(pedido)
      }
    }
    colunaRessuprimentoTransportadorPor()
    colunaRessuprimentoUsuarioApp()
  }

  override fun filtro(marca: EMarcaRessuprimento): FiltroRessuprimento {
    return FiltroRessuprimento(
      numero = edtRessuprimento.value ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      marca = marca,
      temNota = ETemNota.SEM_NOTA,
      lojaRessu = cmbLoja.value?.no ?: 0,
      dataNotaInicial = edtDataInicial.value,
      dataNotaFinal = edtDataFinal.value,
    )
  }

  override fun updateRessuprimentos(ressuprimentos: List<Ressuprimento>) {
    updateGrid(ressuprimentos)
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelcionados(): List<ProdutoRessuprimento> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.ressuprimentoSep == true
  }

  override val label: String
    get() = "Separado"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun formAutoriza(pedido: Ressuprimento) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Entregue", form = form) {
      viewModel.autorizaPedido(pedido, form.login, form.senha)
    }
  }

  override fun formRecebido(pedido: Ressuprimento) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Entregue", form = form) {
      viewModel.recebidoPedido(pedido, form.login, form.senha)
    }
  }

  override fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento? {
    return dlgProduto?.produtosCodigoBarras(codigoBarra)
  }

  override fun updateProduto(produto: ProdutoRessuprimento) {
    dlgProduto?.updateProduto(produto)
  }

  override fun produtosSelecionados(): List<ProdutoRessuprimento> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun ressuprimentosSelecionados(): List<Ressuprimento> {
    return this.itensSelecionados()
  }

  override fun showDlgProdutos(ressuprimentos: List<Ressuprimento>) {
    dlgProduto = DlgProdutosRessuSep(viewModel, ressuprimentos)
    dlgProduto?.showDialog {
      viewModel.updateView()
    }
  }

  override fun formTransportado(pedido: Ressuprimento) {
    val form = FormFuncionario()
    DialogHelper.showForm(caption = "Transportado Por", form = form) {
      viewModel.transportadoPedido(pedido, form.numero ?: 0)
    }
  }

  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    val impressoraRessu = username?.impressoraRessu ?: return emptyList()
    return if (impressoraRessu.contains("TODOS")) emptyList() else impressoraRessu.toList()
  }
}