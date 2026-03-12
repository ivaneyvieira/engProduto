package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.recebimento.FormValidade
import br.com.astrosoft.produto.viewmodel.produto.ITabAtacado
import br.com.astrosoft.produto.viewmodel.produto.TabAtacadoViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class TabAtacado(val viewModel: TabAtacadoViewModel) :
  TabPanelGrid<ProdutoSaldoAtacado>(ProdutoSaldoAtacado::class),
  ITabAtacado {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtProduto: TextField
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
  private lateinit var edtSaldo: IntegerField

  override fun HorizontalLayout.toolBarConfig() {
    verticalLayout {
      this.isSpacing = false
      this.isPadding = false
      this.isMargin = false
      horizontalLayout {
        this.isPadding = false
        this.isMargin = false

        edtPesquisa = textField("Pesquisa") {
          this.width = "10rem"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtProduto = textField("Produto") {
          this.width = "6rem"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtFornecedor = integerField("Fornecedor") {
          this.width = "6rem"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtTributo = textField("CST") {
          this.width = "6rem"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtRotulo = textField("Rotulo") {
          this.width = "6rem"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtTipo = integerField("Tipo") {
          this.width = "6rem"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtCl = integerField("C Lucro") {
          this.width = "6rem"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }
      }
      horizontalLayout {
        cmbCartacer = select("Caracter") {
          this.width = "6rem"
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
          this.width = "6rem"
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
          this.width = "6rem"
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
        cmdEstoque = select("Estoque") {
          this.width = "6rem"
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
          this.width = "6rem"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          this.value = null
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          addValueChangeListener {
            viewModel.updateView()
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

  override fun Grid<ProdutoSaldoAtacado>.gridPanel() {
    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.addColumnSeq("Seq", width = "50px")
    columnGrid(ProdutoSaldoAtacado::codigo, header = "Código").right()
    columnGrid(ProdutoSaldoAtacado::descricao, header = "Descrição").expand()
    columnGrid(ProdutoSaldoAtacado::gradeProduto, header = "Grade")
    columnGrid(ProdutoSaldoAtacado::quantSaldoSaida, header = "Fiscal Q")
    columnGrid(ProdutoSaldoAtacado::valorTotalSaldoSaida, header = "Fiscal V")

    columnGrid(ProdutoSaldoAtacado::estoqueDSAtacado, header = "Atac DS")
    columnGrid(ProdutoSaldoAtacado::custoDSAtacado, header = "V Atac DS")
    columnGrid(ProdutoSaldoAtacado::estoqueMRAtacado, header = "Atac MR")
    columnGrid(ProdutoSaldoAtacado::custoMRAtacado, header = "V Atac MR")
    columnGrid(ProdutoSaldoAtacado::estoqueMFAtacado, header = "Atac MF")
    columnGrid(ProdutoSaldoAtacado::custoMFAtacado, header = "V Atac MF")
    columnGrid(ProdutoSaldoAtacado::estoquePKAtacado, header = "Atac PK")
    columnGrid(ProdutoSaldoAtacado::custoPKAtacado, header = "V Atac PK")
    columnGrid(ProdutoSaldoAtacado::estoqueTMAtacado, header = "Atac TM")
    columnGrid(ProdutoSaldoAtacado::custoTMAtacado, header = "V Atac TM")

    columnGrid(ProdutoSaldoAtacado::estoqueLojasAtacado, header = "Atac Lojas")
    columnGrid(ProdutoSaldoAtacado::custoLojasAtacado, header = "V Atac Lojas")
    columnGrid(ProdutoSaldoAtacado::tributacao, header = "CST")
    columnGrid(ProdutoSaldoAtacado::rotulo, header = "Rotulo")
    columnGrid(ProdutoSaldoAtacado::fornecedor, header = "For")
    columnGrid(ProdutoSaldoAtacado::tipo, header = "Tipo")
    columnGrid(ProdutoSaldoAtacado::cl, header = "C Lucro")
  }

  override fun filtro(): FiltroProdutoSaldoAtacado {
    return FiltroProdutoSaldoAtacado(
      pesquisa = edtPesquisa.value ?: "",
      produto = edtProduto.value ?: "",
      fornecedor = edtFornecedor.value ?: 0,
      tributacao = edtTributo.value ?: "",
      rotulo = edtRotulo.value ?: "",
      tipo = edtTipo.value ?: 0,
      cl = edtCl.value ?: 0,
      caracter = cmbCartacer.value ?: ECaracter.TODOS,
      letraDup = cmbLetraDup.value ?: ELetraDup.TODOS,
      grade = chkGrade.value,
      estoque = cmdEstoque.value ?: EEstoque.TODOS,
      saldo = edtSaldo.value ?: 0,
      consumo = cmbConsumo.value ?: EConsumo.TODOS,
    )
  }

  override fun updateProdutos(produtos: List<ProdutoSaldoAtacado>) {
    updateGrid(produtos)
    gridPanel
      .getColumnBy(ProdutoSaldoAtacado::custoDSAtacado)
      .setFooter(produtos.sumOf { it.custoDSAtacado ?: 0.00 }
        .format())
    gridPanel
      .getColumnBy(ProdutoSaldoAtacado::custoMRAtacado)
      .setFooter(produtos.sumOf { it.custoMRAtacado ?: 0.00 }
        .format())
    gridPanel
      .getColumnBy(ProdutoSaldoAtacado::custoMFAtacado)
      .setFooter(produtos.sumOf { it.custoMFAtacado ?: 0.00 }
        .format())
    gridPanel
      .getColumnBy(ProdutoSaldoAtacado::custoPKAtacado)
      .setFooter(produtos.sumOf { it.custoPKAtacado ?: 0.00 }
        .format())
    gridPanel
      .getColumnBy(ProdutoSaldoAtacado::custoTMAtacado)
      .setFooter(produtos.sumOf { it.custoTMAtacado ?: 0.00 }
        .format())
    gridPanel
      .getColumnBy(ProdutoSaldoAtacado::custoLojasAtacado)
      .setFooter(produtos.sumOf { it.custoLojasAtacado ?: 0.00 }
        .format())
    gridPanel.recalculateColumnWidths()
  }

  override fun produtosSelecionados(): List<ProdutoSaldoAtacado> {
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
    return username?.produtoAtacado == true
  }

  override val label: String
    get() = "Atacado"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraProduto.orEmpty().toList()
  }
}
