package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueConf
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueConfViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
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
import java.time.LocalDate

class TabEstoqueConf(val viewModel: TabEstoqueConfViewModel) :
  TabPanelGrid<ProdutoEstoque>(ProdutoEstoque::class), ITabEstoqueConf {
  private var dlgKardec: DlgProdutoKardec? = null
  private lateinit var edtProduto: IntegerField
  private lateinit var edtPesquisa: TextField
  private lateinit var edtFornecedor: TextField
  private lateinit var edtCentroLucro: IntegerField
  private lateinit var edtPedido: IntegerField
  private lateinit var edtGrade: TextField
  private lateinit var cmbCaracter: Select<ECaracter>
  private lateinit var cmbInativo: Select<EInativo>
  private lateinit var cmbUso: Select<EUso>
  private lateinit var edtLocalizacao: TextField
  private lateinit var cmdEstoque: Select<EEstoque>
  private lateinit var edtSaldo: IntegerField
  private lateinit var cmbLoja: Select<Loja>

  fun init() {
    val user = AppConfig.userLogin() as? UserSaci
    val itens = if (user?.admin == true) {
      viewModel.findAllLojas()
    } else {
      viewModel.findAllLojas().filter { it.no == (user?.lojaConferencia ?: 0) }
    }
    cmbLoja.setItems(itens)
    cmbLoja.value = if (user?.admin == true) {
      itens.firstOrNull { it.no == 4 }
    } else {
      itens.firstOrNull()
    }
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
            if (it.isFromClient) {
              val value = it.value ?: ""
              if (processaBarcode(value)) {
                edtPesquisa.value = ""
                reloadGridMarca()
              } else {
                viewModel.updateView()
              }
            }
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

        edtPedido = integerField("Pedido") {
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
          this.value = ECaracter.TODOS
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

        cmbUso = select("Uso") {
          this.width = "90px"
          this.setItems(EUso.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = EUso.NAO
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        this.button("Marca") {
          this.icon = VaadinIcon.CHECK.create()
          onClick {
            viewModel.marcaProduto()
          }
        }

        this.button("Grava Ped") {
          this.icon = VaadinIcon.PRINT.create()
          onClick {
            viewModel.imprimeProdutosEstoque()
          }
        }

        this.button("Acerto") {
          this.icon = VaadinIcon.PRINT.create()
          onClick {
            viewModel.processaAcerto()
          }
        }

        this.button("Desmarcar") {
          this.icon = VaadinIcon.CLOSE.create()
          onClick {
            viewModel.desmarcaProduto()
          }
        }

        cmdEstoque = select("Estoque") {
          this.width = "100px"
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
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
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
    }
  }

  private fun reloadGridMarca() {
    val userno = AppConfig.userLogin()?.no ?: 0
    val data = LocalDate.now()

    val produtos = gridPanel.dataProvider.fetchAll()

    updateGrid(produtos.sortedBy { !it.marcadoConf(userno, data) })
  }

  private fun processaBarcode(value: String): Boolean {
    return if (value.matches(Regex("[0-9]{13}"))) {
      val listaProduto = gridPanel.dataProvider.fetchAll()
      val filterList = listaProduto.filter { it.barcode == value }
      viewModel.marcaProduto(filterList)
      filterList.isNotEmpty()
    } else {
      false
    }
  }

  override fun Grid<ProdutoEstoque>.gridPanel() {
    this.addClassName("styling")
    this.format()
    selectionMode = Grid.SelectionMode.MULTI

    val user = AppConfig.userLogin() as? UserSaci

    if (user?.estoqueEditaLoc == true) {
      this.withEditor(
        classBean = ProdutoEstoque::class,
        openEditor = {
          val edit = getColumnBy(ProdutoEstoque::locApp) as? Focusable<*>
          edit?.focus()
        },
        closeEditor = {
          viewModel.updateProduto(it.bean, false)
        })
    }

    addColumnSeq("Seq")
    columnGrid(ProdutoEstoque::codigo, header = "Código")
    columnGrid(ProdutoEstoque::barcode, header = "Código de Barras")
    columnGrid(ProdutoEstoque::descricao, header = "Descrição").expand()
    columnGrid(ProdutoEstoque::grade, header = "Grade", width = "80px")
    columnGrid(ProdutoEstoque::unidade, header = "UN")
    columnGrid(ProdutoEstoque::preco, header = "Preço", width = "80px")
    columnGrid(ProdutoEstoque::saldo, header = "Estoque")
    columnGrid(ProdutoEstoque::numeroAcerto, header = "Pedido")
    columnGrid(ProdutoEstoque::locApp, header = "Loc App", width = "100px").apply {
      if (user?.estoqueEditaLoc == true) {
        textFieldEditor()
      }
    }
    columnGrid(ProdutoEstoque::codForn, header = "For Cod")
    val userno = AppConfig.userLogin()?.no ?: 0
    val data = LocalDate.now()

    this.setPartNameGenerator {
      if (it.marcadoConf(userno, data)) "amarelo" else null
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
      pedido = edtPedido.value ?: 0,
      estoque = cmdEstoque.value ?: EEstoque.TODOS,
      saldo = edtSaldo.value ?: 0,
      uso = cmbUso.value ?: EUso.TODOS,
      inativo = cmbInativo.value ?: EInativo.TODOS,
      listaUser = listaUser,
    )
  }

  override fun filtroVazio(): FiltroProdutoEstoque {
    val user = AppConfig.userLogin() as? UserSaci
    val listaUser = user?.listaEstoque.orEmpty().toList().ifEmpty {
      listOf("TODOS")
    }

    return FiltroProdutoEstoque(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = "",
      codigo = 0,
      grade = "",
      caracter = ECaracter.TODOS,
      localizacao = "",
      fornecedor = "",
      centroLucro = 0,
      estoque = EEstoque.TODOS,
      saldo = 0,
      inativo = EInativo.TODOS,
      uso = EUso.TODOS,
      listaUser = listaUser,
    )
  }

  override fun updateProduto(produtos: List<ProdutoEstoque>) {
    val userno = AppConfig.userLogin()?.no ?: 0
    val data = LocalDate.now()

    updateGrid(produtos.sortedBy { !it.marcadoConf(userno, data) })
  }

  override fun updateKardec() {
    dlgKardec?.update()
  }

  override fun reloadGrid() {
    gridPanel.dataProvider.refreshAll()
  }

  override fun autorizaAcerto(block: (user: UserSaci) -> Unit) {
    val form = FormAutorizaAcerto()
    DialogHelper.showForm(caption = "Autoriza gravação do acerto", form = form) {
      val user = AppConfig.findUser(form.login, form.senha) as? UserSaci
      if (user != null) {
        block(user)
      } else {
        DialogHelper.showWarning("Usuário ou senha inválidos")
      }
    }
  }

  override fun autorizaGarantia(block: (user: UserSaci) -> Unit) {
    val form = FormAutorizaAcerto()
    DialogHelper.showForm(caption = "Autoriza gravação da garantia", form = form) {
      val user = AppConfig.findUser(form.login, form.senha) as? UserSaci
      if (user != null) {
        block(user)
      } else {
        DialogHelper.showWarning("Usuário ou senha inválidos")
      }
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueConf == true
  }

  override val label: String
    get() = "Conferência"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraEstoque.orEmpty().toList()
  }
}