package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper.showError
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper.showWarning
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.reposicao.ITabReposicaoAcerto
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoAcertoViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import java.time.LocalDate

class TabReposicaoAcerto(val viewModel: TabReposicaoAcertoViewModel) :
  TabPanelGrid<Reposicao>(Reposicao::class), ITabReposicaoAcerto {
  private var dlgProduto: DlgProdutosReposAcerto? = null
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var cmbMetodo: Select<EMetodo>

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    cmbLoja.value = viewModel.findLoja(0) ?: Loja.lojaZero
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
    cmbMetodo = select("Tipo") {
      val user = AppConfig.userLogin() as? UserSaci
      val tipos = user?.tipoMetodo ?: setOf(EMetodo.TODOS)
      val metodos = listOf(EMetodo.ACERTO, EMetodo.RETORNO, EMetodo.TODOS)
      this.setItems(metodos.filter {
        it in tipos || EMetodo.TODOS in tipos || user?.admin == true
      })
      this.value = tipos.firstOrNull()
      this.setItemLabelGenerator { it.descricao }
      this.addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data Inicial") {
      this.value = LocalDate.now()
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.value = LocalDate.now()
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<Reposicao>.gridPanel() {
    this.addClassName("styling")
    this.format()

    this.withEditor(classBean = Reposicao::class,
      openEditor = {
        this.focusEditor(Reposicao::observacao)
      },
      closeEditor = {
        viewModel.salva(it.bean)
      }
    )

    val user = AppConfig.userLogin() as? UserSaci

    columnGridProduto()
    columnGrid(Reposicao::loja, "Loja")
    columnGrid(Reposicao::numero, "Pedido")
    columnGrid(Reposicao::tipoMetodo, "Tipo")
    columnGrid(Reposicao::data, "Data")
    columnGrid(Reposicao::localizacao, "Loc")
    if (user?.autorizaAcerto == true || user?.admin == true) {
      addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { pedido ->
        viewModel.formEntregue(pedido)
      }
      columnGrid(Reposicao::entregueSNome, "Autoriza")
    }
    columnGrid(Reposicao::observacao, "Observação", width = "200px").textFieldEditor()
  }

  private fun Grid<Reposicao>.columnGridProduto() {
    this.addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { reposicao ->
      val userEntregue = reposicao.entregueNo
      if (userEntregue == 0) {
        showError("Pedido não assinado")
      } else {
        dlgProduto = DlgProdutosReposAcerto(viewModel, listOf(reposicao))
        dlgProduto?.showDialog {
          viewModel.updateView()
        }
      }
    }
  }

  override fun filtro(): FiltroReposicao {
    return FiltroReposicao(
      loja = cmbLoja.value.no,
      pesquisa = edtPesquisa.value ?: "",
      marca = EMarcaReposicao.SEP,
      localizacao = listOf("TODOS"),
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      listMetodo = when (cmbMetodo.value) {
        EMetodo.ACERTO -> listOf(EMetodo.ACERTO)
        EMetodo.RETORNO -> listOf(EMetodo.RETORNO)
        else -> listOf(EMetodo.ACERTO, EMetodo.RETORNO)
      },
    )
  }

  override fun updateUsuarios(reposicoes: List<Reposicao>) {
    this.updateGrid(reposicoes)
  }

  override fun produtosCodigoBarras(codigoBarra: String?): ReposicaoProduto? {
    codigoBarra ?: return null
    return dlgProduto?.produtosCodigoBarras(codigoBarra)
  }

  override fun updateProduto(produto: ReposicaoProduto) {
    dlgProduto?.updateProduto(produto)
  }

  override fun produtosSelecionados(): List<ReposicaoProduto> {
    return dlgProduto?.produtosSelecionados().orEmpty()
  }

  override fun updateProdutos(reposicoes: List<Reposicao>) {
    dlgProduto?.update(reposicoes)
  }

  override fun formEntregue(pedido: Reposicao) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Entregue", form = form) {
      viewModel.entreguePedido(pedido, form.login, form.senha)
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.reposicaoAcerto == true
  }

  override val label: String
    get() = "Acerto"

  override fun updateComponent() {
    viewModel.updateView()
  }
}