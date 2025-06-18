package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.recebimento.FormValidade
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoList
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoListViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class TabProdutoList(val viewModel: TabProdutoListViewModel) :
  TabPanelGrid<ProdutoSaldo>(ProdutoSaldo::class),
  ITabProdutoList {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtFornecedor: IntegerField
  private lateinit var edtTributo: TextField
  private lateinit var edtRotulo: TextField
  private lateinit var edtTipo: IntegerField
  private lateinit var edtCl: IntegerField
  private lateinit var cmbCartacer: Select<ECaracter>
  private lateinit var cmbConsumo: Select<EConsumo>
  private lateinit var cmbLetraDup: Select<ELetraDup>
  private lateinit var chkGrade: Checkbox
  private lateinit var cmdEstoque: Select<EEstoque>
  private lateinit var cmbTipoSaldo: Select<ETipoSaldo>
  private lateinit var edtSaldo: IntegerField

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas())
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaProduto != 0
    val loja = user?.lojaProduto ?: 1
    val lojaEscolhida = if (loja == 0) 1 else loja
    cmbLoja.value = viewModel.findLoja(lojaEscolhida)
  }

  override fun HorizontalLayout.toolBarConfig() {
    verticalLayout {
      this.isSpacing = false
      this.isPadding = false
      this.isMargin = false
      horizontalLayout {
        this.isPadding = false
        this.isMargin = false
        cmbLoja = select("Loja") {
          this.setItemLabelGenerator { item ->
            item.descricao
          }

          addValueChangeListener {
            if (it.isFromClient) {
              viewModel.updateView()
            }
          }
        }
        init()
        edtPesquisa = textField("Pesquisa") {
          this.width = "300px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtFornecedor = integerField("Fornecedor") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtTributo = textField("CST") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtRotulo = textField("Rotulo") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtTipo = integerField("Tipo") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtCl = integerField("C Lucro") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }
      }
      horizontalLayout {
        cmbCartacer = select("Caracter") {
          this.setItems(ECaracter.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = ECaracter.TODOS
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        cmbConsumo = select("Consumo") {
          this.width = "100px"
          this.setItems(EConsumo.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = EConsumo.TODOS
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
        chkGrade = checkBox("Grade") {
          this.value = true
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        cmbTipoSaldo = select("Tipo Saldo") {
          this.setItems(ETipoSaldo.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = ETipoSaldo.TOTAL
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
          this.value = null
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        button("Cadastra Validade") {
          onClick {
            viewModel.cadastraValidade()
          }
        }

        this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "mov") {
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

  override fun Grid<ProdutoSaldo>.gridPanel() {
    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.addColumnSeq("Seq", width = "50px")
    columnGrid(ProdutoSaldo::loja, header = "Loja")
    columnGrid(ProdutoSaldo::codigo, header = "Código").right()
    columnGrid(ProdutoSaldo::descricao, header = "Descrição").expand()
    columnGrid(ProdutoSaldo::gradeProduto, header = "Grade")
    columnGrid(ProdutoSaldo::unidade, header = "Un")
    columnGrid(ProdutoSaldo::qttyVarejo, header = "Varejo")
    columnGrid(ProdutoSaldo::qttyAtacado, header = "Atacado")
    columnGrid(ProdutoSaldo::qttyTotal, header = "Total")
    columnGrid(ProdutoSaldo::estoqueLojas, header = "Est Lojas")
    columnGrid(ProdutoSaldo::tributacao, header = "CST")
    columnGrid(ProdutoSaldo::rotulo, header = "Rotulo")
    columnGrid(ProdutoSaldo::ncm, header = "NCM")
    columnGrid(ProdutoSaldo::fornecedor, header = "For")
    columnGrid(ProdutoSaldo::abrev, header = "Abrev")
    columnGrid(ProdutoSaldo::tipo, header = "Tipo")
    columnGrid(ProdutoSaldo::cl, header = "C Lucro")
    columnGrid(ProdutoSaldo::tipoValidade, header = "Tipo")
    columnGrid(ProdutoSaldo::mesesGarantia, header = "Val")
    columnGrid(ProdutoSaldo::codigoRel, header = "Relac").right()
  }

  override fun filtro(): FiltroProdutoSaldo {
    return FiltroProdutoSaldo(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      fornecedor = edtFornecedor.value ?: 0,
      tributacao = edtTributo.value ?: "",
      rotulo = edtRotulo.value ?: "",
      tipo = edtTipo.value ?: 0,
      cl = edtCl.value ?: 0,
      caracter = cmbCartacer.value ?: ECaracter.TODOS,
      letraDup = cmbLetraDup.value ?: ELetraDup.TODOS,
      grade = chkGrade.value,
      tipoSaldo = cmbTipoSaldo.value ?: ETipoSaldo.TOTAL,
      estoque = cmdEstoque.value ?: EEstoque.TODOS,
      saldo = edtSaldo.value ?: 0,
      consumo = cmbConsumo.value ?: EConsumo.TODOS,
      update = true
    )
  }

  override fun updateProdutos(produtos: List<ProdutoSaldo>) {
    updateGrid(produtos)
  }

  override fun produtosSelecionados(): List<ProdutoSaldo> {
    return itensSelecionados()
  }

  override fun openValidade(tipoValidade: Int, tempoValidade: Int, block: (ValidadeSaci) -> Unit) {
    val form = FormValidade(tipoValidade, tempoValidade)
    DialogHelper.showForm(caption = "Validade", form = form) {
      block(form.validadeSaci)
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.produtoList == true
  }

  override val label: String
    get() = "Produto"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraProduto.orEmpty().toList()
  }
}
