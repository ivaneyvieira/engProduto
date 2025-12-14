package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabControleLoja
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabControleLojaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabControleLoja(val viewModel: TabControleLojaViewModel) :
  TabPanelGrid<ProdutoControle>(ProdutoControle::class), ITabControleLoja {
  private var dlgKardec: DlgControleKardecConferenciaLoja? = null
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
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbLetraDup: Select<ELetraDup>
  private lateinit var edtDataInicial: DatePicker

  fun init() {
    val user = AppConfig.userLogin() as? UserSaci
    val lojaConferencia = user?.lojaConferencia ?: 0
    val itens = viewModel.findAllLojas().filter {
      it.no == lojaConferencia || lojaConferencia == 0 || user?.admin == true
    }
    cmbLoja.setItems(itens)
    cmbLoja.value = itens.firstOrNull {
      it.no == 4
    } ?: itens.firstOrNull()
  }

  override fun HorizontalLayout.toolBarConfig() {
    verticalBlock {
      horizontalLayout {
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
          this.width = "90px"
          this.setItems(ECaracter.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = ECaracter.NAO
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        cmbLetraDup = select("Letra Dup") {
          this.width = "90px"
          this.setItems(ELetraDup.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = ELetraDup.TODOS
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        cmbInativo = select("Inativo") {
          this.width = "90px"
          this.setItems(EInativo.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = EInativo.NAO
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        this.button("Kardex") {
          this.icon = VaadinIcon.FILE_TABLE.create()
          onClick {
            viewModel.updateKardec()
          }
        }

        this.button("Imprimir") {
          this.icon = VaadinIcon.PRINT.create()
          onClick {
            viewModel.imprimeProdutos()
          }
        }

        cmdEstoque = select("Estoque") {
          this.width = "80px"
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
          this.width = "80px"
          //this.isClearButtonVisible = true
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          this.value = 0
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtDataInicial = datePicker("Data Inv. Inicial") {
          this.localePtBr()
          this.isClearButtonVisible = true
          this.value = LocalDate.now().withDayOfMonth(1)
        }

      }
    }
  }

  override fun Grid<ProdutoControle>.gridPanel() {
    this.addClassName("styling")
    this.format()
    selectionMode = Grid.SelectionMode.MULTI

    columnGroup("Produto") {
      this.addColumnSeq("Seq")
      this.addColumnButton(VaadinIcon.FILE_TABLE, "Kardec", "Kardec") { produto: ProdutoControle ->
        val dataIncial: LocalDate? = edtDataInicial.value
        dlgKardec = DlgControleKardecConferenciaLoja(viewModel, produto, dataIncial)
        dlgKardec?.showDialog {
          viewModel.updateView()
        }
      }
      this.columnGrid(ProdutoControle::codigo, header = "Código")
      this.columnGrid(ProdutoControle::descricao, header = "Descrição").expand()
      this.columnGrid(ProdutoControle::grade, header = "Grade", width = "80px")
      this.columnGrid(ProdutoControle::unidade, header = "UN")
    }

    columnGroup("Inventário") {
      this.columnGrid(ProdutoControle::saldo, header = "Sistema", width = "75px")
      this.columnGrid(ProdutoControle::dataInicial, header = "Início Inv", width = "100px")
      this.columnGrid(ProdutoControle::estoqueLoja, header = "Est Loja", width = "75px").right()
      val user = AppConfig.userLogin() as? UserSaci
      if (user?.estoqueEditaInventario == true) {
        this.addColumnButton(VaadinIcon.DATE_INPUT, "Edita", "Edita") { produto: ProdutoControle ->
          val dlgControleLoja = DlgControleSaldo(viewModel, produto) {
            gridPanel.dataProvider.refreshAll()
          }
          dlgControleLoja.open()
        }
      }
      this.columnGrid(ProdutoControle::qtdEmbalagem, header = "Emb Sist", pattern = "0.##", width = "80px")
    }

    columnGroup("Outras Informações") {
      this.columnGrid(ProdutoControle::embalagem, header = "Emb")
      this.columnGrid(ProdutoControle::codForn, header = "For Cod")
      this.columnGrid(ProdutoControle::valorEstoque, header = "Valor Est")
    }
  }

  override fun filtro(): FiltroProdutoControle {
    val user = AppConfig.userLogin() as? UserSaci

    return FiltroProdutoControle(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      codigo = edtProduto.value ?: 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCaracter.value ?: ECaracter.TODOS,
      fornecedor = edtFornecedor.value ?: "",
      centroLucro = edtCentroLucro.value ?: 0,
      estoque = cmdEstoque.value ?: EEstoque.TODOS,
      saldo = edtSaldo.value ?: 0,
      inativo = cmbInativo.value ?: EInativo.TODOS,
      letraDup = cmbLetraDup.value ?: ELetraDup.TODOS,
      cl = 61101,
    )
  }

  override fun updateProduto(produtos: List<ProdutoControle>) {
    updateGrid(produtos)
  }

  override fun updateKardec() {
    dlgKardec?.update()
  }

  override fun reloadGrid() {
    gridPanel.dataProvider.refreshAll()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.controleLoja == true
  }

  override val label: String
    get() = "Controle Loja"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraEstoque.orEmpty().toList()
  }
}