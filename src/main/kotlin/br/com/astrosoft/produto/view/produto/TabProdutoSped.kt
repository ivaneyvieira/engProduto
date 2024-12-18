package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.center
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoSped
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoSpedViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabProdutoSped(val viewModel: TabProdutoSpedViewModel) :
  TabPanelGrid<ProdutoSped>(ProdutoSped::class),
  ITabProdutoSped {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtFornecedor: IntegerField
  private lateinit var edtTributo: TextField
  private lateinit var edtRotulo: TextField
  private lateinit var edtTipo: IntegerField
  private lateinit var edtCl: IntegerField
  private lateinit var cmbCartacer: Select<ECaracter>
  private lateinit var cmbConsumo: Select<EConsumo>
  private lateinit var cmbLetraDup: Select<ELetraDup>
  private lateinit var chkConfigSt: Checkbox
  private lateinit var chkPisCofN: Checkbox
  private lateinit var chkRotuloN: Checkbox

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
          valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtFornecedor = integerField("Fornecedor") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtTributo = textField("Tributo") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtRotulo = textField("Rotulo") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtTipo = integerField("Tipo") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtCl = integerField("C Lucro") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }
      }
      horizontalLayout {
        cmbCartacer = select("Caracter") {
          this.width = "100px"
          this.setItems(ECaracter.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = ECaracter.TODOS
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        cmbLetraDup = select("Letra Dup") {
          this.width = "100px"
          this.setItems(ELetraDup.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = ELetraDup.TODOS
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
        chkConfigSt = checkBox("PIS/ICMS N") {
          this.value = false
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        button("Conf PIS/ICMS") {
          this.icon = VaadinIcon.COG.create()
          onClick {
            viewModel.configProdutosSelecionados()
          }
        }
        chkPisCofN = checkBox("PIS/COF N") {
          this.value = false
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        chkRotuloN = checkBox("Rotulo N") {
          this.value = false
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "produtoSped") {
          val produtos = itensSelecionados()
          viewModel.planilha(produtos)
        }
      }
    }
  }

  override fun Grid<ProdutoSped>.gridPanel() {
    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.addColumnSeq("Seq", width = "50px")
    columnGrid(ProdutoSped::codigo, header = "Cód", width = "80px").right()
    columnGrid(ProdutoSped::descricao, header = "Descrição").expand()
    columnGrid(ProdutoSped::unidade, header = "Un")
    columnGrid(ProdutoSped::rotulo, header = "Rotulo")
    columnGrid(ProdutoSped::tributacao, header = "Trib").center()
    columnGrid(ProdutoSped::ctIpi, header = "IPI")
    columnGrid(ProdutoSped::ctPis, header = "PIS")
    columnGrid(ProdutoSped::ctIcms, header = "ICMS")
    columnGrid(ProdutoSped::pisCofOk, header = "PIS/COF")
    columnGrid(ProdutoSped::rotuloOk, header = "Rotulo")
    columnGrid(ProdutoSped::forn, header = "Forn")
    columnGrid(ProdutoSped::abrev, header = "Abrev")
    columnGrid(ProdutoSped::ncm, header = "NCM").right()
    columnGrid(ProdutoSped::tipo, header = "Tipo")
    columnGrid(ProdutoSped::clno, header = "CL", width = "80px")
    columnGrid(ProdutoSped::refForn, header = "Ref Forn", width = "150px").right()
    columnGrid(ProdutoSped::saldo, header = "Saldo", width = "80px").right()
  }

  override fun filtro(): FiltroProdutoSped {
    return FiltroProdutoSped(
      pesquisa = edtPesquisa.value ?: "",
      vendno = edtFornecedor.value ?: 0,
      taxno = edtTributo.value ?: "",
      rotulo = edtRotulo.value ?: "",
      typeno = edtTipo.value ?: 0,
      clno = edtCl.value ?: 0,
      caracter = cmbCartacer.value ?: ECaracter.TODOS,
      letraDup = cmbLetraDup.value ?: ELetraDup.TODOS,
      configSt = chkConfigSt.value ?: false,
      pisCofN = chkPisCofN.value ?: false,
      rotuloN = chkRotuloN.value ?: false,
      consumo = cmbConsumo.value ?: EConsumo.TODOS
    )
  }

  override fun updateProdutos(produtos: List<ProdutoSped>) {
    updateGrid(produtos)
  }

  override fun produtosSelecionados(): List<ProdutoSped> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.produtoSped == true
  }

  override val label: String
    get() = "Sped"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraProduto.orEmpty().toList()
  }
}
