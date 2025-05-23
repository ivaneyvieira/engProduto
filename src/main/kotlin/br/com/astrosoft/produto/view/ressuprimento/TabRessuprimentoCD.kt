package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoData
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoDataBaixa
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoNotaBaixa
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoNumero
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoObservacao
import br.com.astrosoft.produto.viewmodel.ressuprimento.ITabRessuprimentoCD
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoCDViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabRessuprimentoCD(val viewModel: TabRessuprimentoCDViewModel) :
  TabPanelGrid<Ressuprimento>(Ressuprimento::class), ITabRessuprimentoCD {
  private var dlgProduto: DlgProdutosRessuCD? = null
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
      isVisible = false
      value = null
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      isVisible = false
      value = null
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    val user = AppConfig.userLogin() as? UserSaci

    button("Exclui") {
      this.isVisible = user?.ressuprimentoExclui == true
      icon = VaadinIcon.TRASH.create()
      onClick {
        viewModel.excluiRessuprimento()
      }
    }

    button("Produtos") {
      this.icon = VaadinIcon.FILE_TABLE.create()
      onClick {
        viewModel.processamentoProdutos()
      }
    }
  }

  override fun Grid<Ressuprimento>.gridPanel() {
    this.addClassName("styling")
    this.format()
    this.withEditor(
      classBean = Ressuprimento::class,
      openEditor = {
        this.focusEditor(Ressuprimento::observacao)
      },
      closeEditor = {
        viewModel.saveObservacao(it.bean)
      }
    )
    addColumnButton(VaadinIcon.PRINT, "Preview", "Preview") { pedido ->
      viewModel.previewPedido(pedido)
    }
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { pedido ->
      dlgProduto = DlgProdutosRessuCD(viewModel, listOf(pedido))
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    this.setSelectionMode(Grid.SelectionMode.MULTI)
    colunaRessuprimentoNumero()
    colunaRessuprimentoData()
    colunaRessuprimentoNotaBaixa()
    colunaRessuprimentoDataBaixa()
    colunaRessuprimentoObservacao().textFieldEditor()

    this.setPartNameGenerator {
      val marca = it.countNot ?: 0
      if (marca > 0) {
        "amarelo"
      } else null
    }
  }

  override fun filtro(marca: EMarcaRessuprimento): FiltroRessuprimento {
    val user = AppConfig.userLogin() as? UserSaci
    return FiltroRessuprimento(
      numero = edtRessuprimento.value ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      marca = marca,
      temNota = ETemNota.TODOS,
      lojaRessu = cmbLoja.value?.no ?: user?.lojaRessu ?: 0,
      dataPedidoInicial = edtDataInicial.value,
      dataPedidoFinal = edtDataFinal.value,
    )
  }

  override fun updateRessuprimentos(ressuprimentos: List<Ressuprimento>) {
    updateGrid(ressuprimentos)
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelecionados(): List<ProdutoRessuprimento> {
    return dlgProduto?.produtosSelecionados().orEmpty()
  }

  override fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento? {
    return dlgProduto?.produtosCodigoBarras(codigoBarra)
  }

  override fun updateProduto(produto: ProdutoRessuprimento) {
    dlgProduto?.updateProduto(produto)
  }

  override fun ressuprimentosSelecionados(): List<Ressuprimento> {
    return this.itensSelecionados()
  }

  override fun showDlgProdutos(ressuprimentos: List<Ressuprimento>) {
    dlgProduto = DlgProdutosRessuCD(viewModel, ressuprimentos)
    dlgProduto?.showDialog {
      viewModel.updateView()
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.ressuprimentoCD == true
  }

  override val label: String
    get() = "Separar"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    val impressoraRessu = user?.impressoraRessu ?: return emptyList()
    return if (impressoraRessu.contains("TODOS")) emptyList() else impressoraRessu.toList()
  }
}