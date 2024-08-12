package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueSaldo
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueSaldoViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class TabEstoqueSaldo(val viewModel: TabEstoqueSaldoViewModel) :
  TabPanelGrid<ProdutoEstoque>(ProdutoEstoque::class), ITabEstoqueSaldo {
  private var dlgKardec: DlgProdutoKardec? = null
  private lateinit var edtProduto: IntegerField
  private lateinit var edtPesquisa: TextField
  private lateinit var edtGrade: TextField
  private lateinit var cmbCaracter: Select<ECaracter>
  private lateinit var edtLocalizacao: TextField
  private lateinit var cmdEstoque: Select<EEstoque>
  private lateinit var edtSaldo: IntegerField

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtProduto = integerField("Produto") {
      this.width = "100px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtGrade = textField("Grade") {
      this.width = "100px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtLocalizacao = textField("Loc App") {
      this.width = "100px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    cmbCaracter = select("Caracter") {
      this.setItems(ECaracter.entries)
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      this.value = ECaracter.TODOS
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    cmdEstoque = select("Estoque") {
      this.setItems(EEstoque.entries)
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      this.value = EEstoque.TODOS
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtSaldo = integerField("Saldo") {
      this.width = "100px"
      this.isClearButtonVisible = true
      valueChangeMode = ValueChangeMode.TIMEOUT
      this.value = 0
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "estoqueSaldo") {
      val produtos = itensSelecionados()
      viewModel.geraPlanilha(produtos)
    }
  }

  override fun Grid<ProdutoEstoque>.gridPanel() {
    this.addClassName("styling")
    this.format()
    setSelectionMode(Grid.SelectionMode.MULTI)

    val user = AppConfig.userLogin() as? UserSaci

    if (user?.admin == true) {
      this.withEditor(
        classBean = ProdutoEstoque::class,
        openEditor = {
          val edit = getColumnBy(ProdutoEstoque::dataInicial) as? Focusable<*>
          edit?.focus()
        },
        closeEditor = {
          viewModel.updateProduto(it.bean)
        })
    }

    addColumnSeq("Seq")
    addColumnButton(VaadinIcon.FILE_TABLE, "Kardec", "Kardec") { produto: ProdutoEstoque ->
      dlgKardec = DlgProdutoKardec(viewModel, produto)
      dlgKardec?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(ProdutoEstoque::codigo, header = "Código")
    columnGrid(ProdutoEstoque::descricao, header = "Descrição").expand()
    columnGrid(ProdutoEstoque::grade, header = "Grade", width = "100px")
    columnGrid(ProdutoEstoque::unidade, header = "UN")
    columnGrid(ProdutoEstoque::locSaci, header = "Loc Saci")
    columnGrid(ProdutoEstoque::locApp, header = "Loc App")
    columnGrid(ProdutoEstoque::embalagem, header = "Emb")
    columnGrid(ProdutoEstoque::qtdEmbalagem, header = "Qtd Emb")
    columnGrid(ProdutoEstoque::saldo, header = "Estoque")
    columnGrid(ProdutoEstoque::dataInicial, header = "Data Inicial").dateFieldEditor()
  }

  override fun filtro(): FiltroProdutoEstoque {
    return FiltroProdutoEstoque(
      loja = 4,
      pesquisa = edtPesquisa.value ?: "",
      codigo = edtProduto.value ?: 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCaracter.value ?: ECaracter.TODOS,
      localizacao = edtLocalizacao.value ?: "",
      fornecedor = "",
      estoque = cmdEstoque.value ?: EEstoque.TODOS,
      saldo = edtSaldo.value ?: 0,
      user = AppConfig.userLogin() as? UserSaci
    )
  }

  override fun updateProduto(produtos: List<ProdutoEstoque>) {
    updateGrid(produtos)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueSaldo == true
  }

  override val label: String
    get() = "Estoque"

  override fun updateComponent() {
    viewModel.updateView()
  }
}