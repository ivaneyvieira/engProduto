package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.EntradaDevCliProList
import br.com.astrosoft.produto.model.beans.FiltroEntradaDevCliProList
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.reposicao.FormAutoriza
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueDevProduto
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueDevProdutoViewModel
import com.flowingcode.vaadin.addons.gridhelpers.GridHelper
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.asc
import com.github.mvysny.kaributools.sort
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabEstoqueDevProduto(val viewModel: TabEstoqueDevProdutoViewModel) :
  TabPanelGrid<EntradaDevCliProList>(EntradaDevCliProList::class),
  ITabEstoqueDevProduto {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtData: DatePicker
  private lateinit var edtPesquisa: TextField

  fun init() {
    val listLojas = viewModel.findAllLojas()
    cmbLoja.setItems(listLojas)
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaVale != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaVale ?: 0) ?: listLojas.firstOrNull()
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
    edtData = datePicker("Data") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    this.button("Kardex") {
      this.icon = VaadinIcon.FILE_TABLE.create()
      onClick {
        viewModel.updateKardex()
      }
    }

    button("Autoriza Entrega") {
      icon = VaadinIcon.SIGN_IN.create()
      onClick {
        viewModel.autorizaEntrega()
      }
    }

    button("Autoriza Recebimento") {
      icon = VaadinIcon.SIGN_IN.create()
      onClick {
        viewModel.autorizaRecebimento()
      }
    }

    button("Impressão") {
      icon = VaadinIcon.PRINT.create()
      onClick {
        viewModel.imprimeProdutos()
      }
    }

    button("Desfazer Ass") {
      val user = AppConfig.userLogin() as? UserSaci
      this.isVisible = user?.admin == true
      icon = VaadinIcon.PRINT.create()
      onClick {
        viewModel.desfazerAutorizacao()
      }
    }
  }

  override fun Grid<EntradaDevCliProList>.gridPanel() {
    this.addClassName("styling")
    this.selectionMode = Grid.SelectionMode.MULTI
    columnGrid(EntradaDevCliProList::codigo, header = "Código").right()
    columnGrid(EntradaDevCliProList::descricao, header = "Descrição").expand()
    columnGrid(EntradaDevCliProList::grade, header = "Grade")
    columnGrid(EntradaDevCliProList::localizacao, header = "Loc CD")
    columnGrid(EntradaDevCliProList::kardec, header = "Est CD")
    columnGrid(EntradaDevCliProList::tipoQtdEfetiva, header = "Qtd Dev")
    columnGrid(EntradaDevCliProList::observacao01, header = "Observação").expand()
    columnGrid(EntradaDevCliProList::tipoNotaPre, header = "Tipo")
    columnGrid(EntradaDevCliProList::userEntrega, header = "Entregador")
    columnGrid(EntradaDevCliProList::userRecebimento, header = "Recebedor")
    columnGrid(EntradaDevCliProList::ni, header = "NI")
    columnGrid(EntradaDevCliProList::nota, header = "NF Dev")
    columnGrid(EntradaDevCliProList::data, header = "Data")
    GridHelper.setEnhancedSelectionEnabled(this, true)

    this.sort(EntradaDevCliProList::localizacao.asc, EntradaDevCliProList::descricao.asc)
  }

  override fun filtro(): FiltroEntradaDevCliProList {
    val user = AppConfig.userLogin() as? UserSaci
    return FiltroEntradaDevCliProList(
      loja = cmbLoja.value?.no ?: 0,
      data = edtData.value ?: LocalDate.now(),
      pesquisa = edtPesquisa.value ?: "",
      localizacao = user?.listaEstoque ?: setOf("TODOS")
    )
  }

  override fun reloadGrid() {
    gridPanel.dataProvider.refreshAll()
  }

  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.impressoraDev.orEmpty().toList()
  }

  override fun updateProdutos(produtos: List<EntradaDevCliProList>) {
    updateGrid(produtos)
  }

  override fun produtosSelecionados(): List<EntradaDevCliProList> {
    return this.itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueDevProduto == true
  }

  override val label: String
    get() = "Dev Cli"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun autorizaEntrega(
    produtos: List<EntradaDevCliProList>,
    block: (user: UserSaci, produtos: List<EntradaDevCliProList>) -> Unit
  ) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Entrega", form = form) {
      val login = form.login
      val senha = form.senha
      val user = viewModel.validaLogin(login, senha)
      if (user == null) {
        DialogHelper.showError("Usuário ou senha inválidos")
      } else {
        block(user, produtos)
      }
    }
  }

  override fun autorizaRecebimento(
    produtos: List<EntradaDevCliProList>,
    block: (user: UserSaci, produtos: List<EntradaDevCliProList>) -> Unit
  ) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Recebimento", form = form) {
      val login = form.login
      val senha = form.senha
      val user = viewModel.validaLogin(login, senha)
      if (user == null) {
        DialogHelper.showError("Usuário ou senha inválidos")
      } else {
        block(user, produtos)
      }
    }
  }
}