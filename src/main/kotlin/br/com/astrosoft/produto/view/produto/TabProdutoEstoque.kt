package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.recebimento.FormValidade
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoEstoque
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoEstoqueViewModel
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

class TabProdutoEstoque(val viewModel: TabProdutoEstoqueViewModel) :
  TabPanelGrid<ProdutoLoja>(ProdutoLoja::class),
  ITabProdutoEstoque {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtTributo: TextField
  private lateinit var edtRotulo: TextField
  private lateinit var edtNCM: TextField
  private lateinit var edtFornecedor: IntegerField
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

        edtNCM = textField("NCM") {
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

  override fun Grid<ProdutoLoja>.gridPanel() {
    this.addClassName("styling")
    selectionMode = Grid.SelectionMode.MULTI
    this.addColumnSeq("Seq", width = "50px")
    columnGrid(ProdutoLoja::codigo, header = "Código").right()
    columnGrid(ProdutoLoja::descricao, header = "Descrição").expand()
    columnGrid(ProdutoLoja::gradeProduto, header = "Grade")
    columnGrid(ProdutoLoja::unidade, header = "Un")
    columnGrid(ProdutoLoja::estoqueTotal, header = "Est Total")
    columnGrid(ProdutoLoja::estoqueDS, header = "DS")
    columnGrid(ProdutoLoja::estoqueMR, header = "MR")
    columnGrid(ProdutoLoja::estoqueMF, header = "MF")
    columnGrid(ProdutoLoja::estoquePK, header = "PK")
    columnGrid(ProdutoLoja::estoqueTM, header = "TM")
    columnGrid(ProdutoLoja::tributacao, header = "CST")
    columnGrid(ProdutoLoja::rotulo, header = "Rotulo")
    columnGrid(ProdutoLoja::ncm, header = "NCM")
    columnGrid(ProdutoLoja::fornecedor, header = "For")
    columnGrid(ProdutoLoja::tipo, header = "Tipo")
    columnGrid(ProdutoLoja::cl, header = "C Lucro")
    //columnGrid(ProdutoLoja::codigoRel, header = "Relac").right()
  }

  override fun filtro(): FiltroProdutoLoja {
    return FiltroProdutoLoja(
      pesquisa = edtPesquisa.value ?: "",
      fornecedor = edtFornecedor.value ?: 0,
      tipo = edtTipo.value ?: 0,
      cl = edtCl.value ?: 0,
      caracter = cmbCartacer.value ?: ECaracter.TODOS,
      letraDup = cmbLetraDup.value ?: ELetraDup.TODOS,
      grade = chkGrade.value,
      estoque = cmdEstoque.value ?: EEstoque.TODOS,
      saldo = edtSaldo.value ?: 0,
      consumo = cmbConsumo.value ?: EConsumo.TODOS,
      tributacao = edtTributo.value ?: "",
      rotulo = edtRotulo.value ?: "",
      ncm = edtNCM.value ?: "",
      update = true
    )
  }

  override fun updateProdutos(produtos: List<ProdutoLoja>) {
    updateGrid(produtos)
  }

  override fun produtosSelecionados(): List<ProdutoLoja> {
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
    return username?.produtoEstoque == true
  }

  override val label: String
    get() = "Estoque"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraProduto.orEmpty().toList()
  }
}
