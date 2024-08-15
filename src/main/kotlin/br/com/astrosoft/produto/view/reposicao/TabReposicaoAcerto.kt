package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
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
      val tipo = user?.tipoMetodo ?: EMetodo.TODOS
      val metodos = listOf(EMetodo.ACERTO, EMetodo.RETORNO, EMetodo.TODOS)
      this.setItems(metodos.filter {
        it == tipo || tipo == EMetodo.TODOS || user?.admin == true
      })
      this.value = tipo
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

    columnGridProduto()
    columnGrid(Reposicao::loja, "Loja")
    columnGrid(Reposicao::numero, "Pedido")
    columnGrid(Reposicao::tipoMetodo, "Tipo")
    columnGrid(Reposicao::data, "Data")
    columnGrid(Reposicao::localizacao, "Loc")
    columnGrid(Reposicao::observacao, "Observação", width = "200px").textFieldEditor()
  }

  private fun Grid<Reposicao>.columnGridProduto() {
    this.addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { ressuprimento ->
      dlgProduto = DlgProdutosReposAcerto(viewModel, listOf(ressuprimento))
      dlgProduto?.showDialog {
        viewModel.updateView()
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
      listMetodo = when(cmbMetodo.value){
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

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.reposicaoAcerto == true
  }

  override val label: String
    get() = "Separar"

  override fun updateComponent() {
    viewModel.updateView()
  }
}