package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueSaldo
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueSaldoViewModel
import com.github.mvysny.karibudsl.v10.*
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
  private lateinit var edtFornecedor: TextField
  private lateinit var edtCentroLucro: IntegerField
  private lateinit var edtGrade: TextField
  private lateinit var cmbCaracter: Select<ECaracter>
  private lateinit var cmbInativo: Select<EInativo>
  private lateinit var edtLocalizacao: TextField
  private lateinit var cmdEstoque: Select<EEstoque>
  private lateinit var edtSaldo: IntegerField

  override fun HorizontalLayout.toolBarConfig() {
    verticalBlock {
      horizontalLayout {
        edtPesquisa = textField("Pesquisa") {
          this.width = "300px"
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtProduto = integerField("Produto") {
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
        edtFornecedor = textField("Fornecedor") {
          this.width = "150px"
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtCentroLucro = integerField("C. Lucro") {
          this.width = "100px"
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }
      }
      horizontalLayout {
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
        cmbInativo = select("Inativo") {
          this.setItems(EInativo.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = EInativo.TODOS
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
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          this.value = 0
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        this.button("Cópia") {
          val user = AppConfig.userLogin() as? UserSaci
          this.isVisible = user?.estoqueCopiaLoc == true
          this.icon = VaadinIcon.COPY.create()
          onClick {
            viewModel.copiaLocalizacao()
          }
        }
        this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "estoqueSaldo") {
          val produtos = itensSelecionados()
          viewModel.geraPlanilha(produtos)
        }
        this.button("Imprimir") {
          this.icon = VaadinIcon.PRINT.create()
          onClick {
            viewModel.imprimeProdutos()
          }
        }
      }
    }
  }

  override fun Grid<ProdutoEstoque>.gridPanel() {
    this.addClassName("styling")
    this.format()
    selectionMode = Grid.SelectionMode.MULTI

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
    columnGrid(ProdutoEstoque::locApp, header = "Loc App", width = "100px").apply {
      val user = AppConfig.userLogin() as? UserSaci
      if(user?.estoqueEditaLoc == true) {
        textFieldEditor()
      }
    }
    columnGrid(ProdutoEstoque::embalagem, header = "Emb")
    columnGrid(ProdutoEstoque::qtdEmbalagem, header = "Qtd Emb")
    columnGrid(ProdutoEstoque::saldo, header = "Estoque")
    columnGrid(ProdutoEstoque::codForn, header = "For Cod")
    columnGrid(ProdutoEstoque::fornecedor, header = "For Abr", width = "100px")
    columnGrid(ProdutoEstoque::dataInicial, header = "Data Inicial").dateFieldEditor()
  }

  override fun filtro(): FiltroProdutoEstoque {
    return FiltroProdutoEstoque(
      pesquisa = edtPesquisa.value ?: "",
      codigo = edtProduto.value ?: 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCaracter.value ?: ECaracter.TODOS,
      localizacao = edtLocalizacao.value ?: "",
      fornecedor = edtFornecedor.value ?: "",
      centroLucro = edtCentroLucro.value ?: 0,
      estoque = cmdEstoque.value ?: EEstoque.TODOS,
      saldo = edtSaldo.value ?: 0,
      inativo = cmbInativo.value ?: EInativo.TODOS
    )
  }

  override fun updateProduto(produtos: List<ProdutoEstoque>) {
    updateGrid(produtos)
  }

  override fun updateKardec() {
    dlgKardec?.update()
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