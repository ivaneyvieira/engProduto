package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueLoja
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueLojaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabEstoqueLoja(val viewModel: TabEstoqueLojaViewModel) :
  TabPanelGrid<ProdutoEstoque>(ProdutoEstoque::class), ITabEstoqueLoja {
  private var dlgKardec: DlgProdutoKardecLoja? = null
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
                  viewModel.updateProduto(produto, false)
                }
                gridPanel.dataProvider.refreshAll()
              }
              dlg.open()
            }
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
      }
    }
  }

  override fun Grid<ProdutoEstoque>.gridPanel() {
    this.addClassName("styling")
    this.format()
    selectionMode = Grid.SelectionMode.MULTI

    val user = AppConfig.userLogin() as? UserSaci

    this.withEditor(
      classBean = ProdutoEstoque::class,
      isBuffered = false,
      openEditor = {
        this.focusEditor(ProdutoEstoque::qtConfEditLoja)
      },
      closeEditor = {
        viewModel.updateProduto(it.bean, false)
        if (!this.editorFinalizado) {
          abreProximo(it.bean)
        }
      },
      saveEditor = {
        viewModel.updateProduto(it.bean, false)
        if (!this.editorFinalizado) {
          abreProximo(it.bean)
        }
      }
    )

    columnGroup("Produto") {
      this.addColumnSeq("Seq")
      this.addColumnButton(VaadinIcon.FILE_TABLE, "Kardec", "Kardec") { produto: ProdutoEstoque ->
        dlgKardec = DlgProdutoKardecLoja(viewModel, produto)
        dlgKardec?.showDialog {
          viewModel.updateView()
        }
      }
      this.columnGrid(ProdutoEstoque::codigo, header = "Código")
      this.columnGrid(ProdutoEstoque::descricao, header = "Descrição").expand()
      this.columnGrid(ProdutoEstoque::grade, header = "Grade", width = "80px")
      this.columnGrid(ProdutoEstoque::unidade, header = "UN")
    }

    columnGroup("Estoque") {
      this.columnGrid(ProdutoEstoque::saldo, header = "Sistema", width = "75px")
      this.columnGrid(ProdutoEstoque::qtdDif, header = "Loja", pattern = "#,##0", width = "80px")
      this.columnGrid(ProdutoEstoque::kardec, header = "CD", width = "75px")
      this.columnGrid(ProdutoEstoque::qtConfEdit, header = "Conf", pattern = "#,##0", width = "80px")
        .integerFieldEditor()
    }

    columnGroup("Inventário") {
      this.columnGrid(ProdutoEstoque::qtConferencia, header = "Inv", width = "75px").right()
      if (user?.estoqueEditaConf == true) {
        this.addColumnButton(VaadinIcon.DATE_INPUT, "Edita", "Edita") { produto: ProdutoEstoque ->
          val dlgConferencia = DlgConferenciaSaldo(viewModel, produto) {
            gridPanel.dataProvider.refreshAll()
          }
          dlgConferencia.open()
        }
      }
      //columnGrid(ProdutoEstoque::dataConferencia, header = "Data Conf", width = "100px")
      this.columnGrid(ProdutoEstoque::dataInicial, header = "Início Inv", width = "100px")
      this.columnGrid(ProdutoEstoque::kardecEmb, header = "Emb CD", pattern = "0.##", width = "80px")
      this.columnGrid(ProdutoEstoque::qtdEmbalagem, header = "Emb Sist", pattern = "0.##", width = "80px")
    }

    columnGroup("Outras Informações") {
      this.columnGrid(ProdutoEstoque::embalagem, header = "Emb")
      this.columnGrid(ProdutoEstoque::locApp, header = "Loc App", width = "100px")/*.apply {
        if (user?.estoqueEditaLoc == true) {
          textFieldEditor()
        }
      }*/
      this.columnGrid(ProdutoEstoque::codForn, header = "For Cod")
      //columnGrid(ProdutoEstoque::fornecedor, header = "For Abr", width = "80px")
    }
  }

  private fun abreProximo(bean: ProdutoEstoque) {
    val items = gridPanel.list()
    val index = items.indexOf(bean)
    if (index >= 0) {
      val nextIndex = index + 1
      if (nextIndex < items.size) {
        val nextBean = items[nextIndex]
        //gridDetail.select(nextBean)
        gridPanel.editor.editItem(nextBean)
      } else {
        gridPanel.deselectAll()
      }
    }
  }

  override fun filtro(): FiltroProdutoEstoque {
    val user = AppConfig.userLogin() as? UserSaci
    val listaUser = user?.listaEstoque.orEmpty().toList().ifEmpty {
      listOf("TODOS")
    }

    return FiltroProdutoEstoque(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      codigo = edtProduto.value ?: 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCaracter.value ?: ECaracter.TODOS,
      localizacao = edtLocalizacao.value ?: "",
      fornecedor = edtFornecedor.value ?: "",
      centroLucro = edtCentroLucro.value ?: 0,
      estoque = cmdEstoque.value ?: EEstoque.TODOS,
      saldo = edtSaldo.value ?: 0,
      inativo = cmbInativo.value ?: EInativo.TODOS,
      listaUser = listaUser,
      letraDup = cmbLetraDup.value ?: ELetraDup.TODOS,
    )
  }

  override fun updateProduto(produtos: List<ProdutoEstoque>) {
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
    return username?.estoqueLoja == true
  }

  override val label: String
    get() = "Estoque Loja"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraEstoque.orEmpty().toList()
  }
}