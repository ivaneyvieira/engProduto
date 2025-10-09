package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueCad
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueCadViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabEstoqueCad(val viewModel: TabEstoqueCadViewModel) :
  TabPanelGrid<ProdutoEstoque>(ProdutoEstoque::class), ITabEstoqueCad {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtGrade: TextField
  private lateinit var cmbCaracter: Select<ECaracter>
  private lateinit var edtLocalizacao: TextField
  private lateinit var edtFornecedor: TextField
  private lateinit var edtCodigo: IntegerField
  private lateinit var cmbInativo: Select<EInativo>

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtCodigo = integerField("Código") {
      this.width = "100px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtGrade = textField("Grade") {
      this.width = "100px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtLocalizacao = textField("Loc App") {
      this.width = "100px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    this.button("Localização") {
      this.icon = VaadinIcon.LOCATION_ARROW.create()
      onClick {
        val produtos = itensSelecionados()
        if (produtos.isEmpty()) {
          DialogHelper.showWarning("Nenhum item selecionado")
        } else {
          val localizacaoSel = produtos
                                 .asSequence()
                                 .mapNotNull { it.locApp }
                                 .groupBy { it }
                                 .map { Pair(it.key, it.value.size) }
                                 .sortedBy { -it.second }
                                 .map { it.first }
                                 .firstOrNull() ?: ""
          val dlg = DlgLocalizacao(localizacaoSel) { localizacao ->
            produtos.forEach { produto ->
              produto.locApp = localizacao
              viewModel.updateLocalizacao(produto)
            }
            gridPanel.dataProvider.refreshAll()
          }
          dlg.open()
        }
      }
    }

    edtFornecedor = textField("Fornecedor") {
      this.width = "100px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    cmbCaracter = select("Caracter") {
      this.width = "6rem"
      this.setItems(ECaracter.entries)
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      this.value = ECaracter.NAO
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    cmbInativo = select("Inativo") {
      this.width = "6rem"
      this.setItems(EInativo.entries)
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      this.value = EInativo.NAO
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "EstoqueCad") {
      val produtos = itensSelecionados()
      viewModel.geraPlanilha(produtos)
    }

    this.button("Cópia") {
      this.icon = VaadinIcon.COPY.create()
      onClick {
        viewModel.copiaLocalizacao()
      }
    }
  }

  override fun Grid<ProdutoEstoque>.gridPanel() {
    this.addClassName("styling")
    selectionMode = Grid.SelectionMode.MULTI

    this.withEditor(
      classBean = ProdutoEstoque::class,
      openEditor = {
        val edit = getColumnBy(ProdutoEstoque::locApp) as? Focusable<*>
        edit?.focus()
      },
      closeEditor = {
        viewModel.updateLocalizacao(it.bean)
      })

    addColumnSeq("Seq")
    columnGrid(ProdutoEstoque::codigo, header = "Código")
    columnGrid(ProdutoEstoque::descricao, header = "Descrição").expand()
    columnGrid(ProdutoEstoque::grade, header = "Grade", width = "100px")
    columnGrid(ProdutoEstoque::unidade, header = "UN")
    columnGrid(ProdutoEstoque::locNerus, header = "Loc Nerus", width = "100px")
    columnGrid(ProdutoEstoque::locApp, header = "Loc App", width = "100px").textFieldEditor()
    columnGrid(ProdutoEstoque::saldo, header = "Estoque")
    columnGrid(ProdutoEstoque::codForn, header = "For Cod")
    columnGrid(ProdutoEstoque::fornecedorAbrev, header = "For Abr").expand()
  }

  override fun filtro(): FiltroProdutoEstoque {
    val user = AppConfig.userLogin() as? UserSaci
    val listaUser = user?.listaEstoque.orEmpty().toList().ifEmpty {
      listOf("TODOS")
    }

    return FiltroProdutoEstoque(
      pesquisa = edtPesquisa.value ?: "",
      codigo = edtCodigo.value ?: 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCaracter.value ?: ECaracter.TODOS,
      localizacao = edtLocalizacao.value ?: "",
      fornecedor = edtFornecedor.value ?: "",
      inativo = cmbInativo.value ?: EInativo.TODOS,
      listaUser = listaUser
    )
  }

  override fun updateProduto(produtos: List<ProdutoEstoque>) {
    updateGrid(produtos)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueCad == true
  }

  override val label: String
    get() = "Cad Loc"

  override fun updateComponent() {
    viewModel.updateView()
  }
}