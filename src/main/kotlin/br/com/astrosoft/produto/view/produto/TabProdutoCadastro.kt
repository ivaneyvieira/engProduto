package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.center
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoCadastro
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoCadastroViewModel
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

class TabProdutoCadastro(val viewModel: TabProdutoCadastroViewModel) :
  TabPanelGrid<ProdutoCadastro>(ProdutoCadastro::class),
  ITabProdutoCadastro {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtFornecedor: IntegerField
  private lateinit var edtTributo: TextField
  private lateinit var edtRotulo: TextField
  private lateinit var edtTipo: IntegerField
  private lateinit var edtCl: IntegerField
  private lateinit var cmbCartacer: Select<ECaracter>
  private lateinit var cmbLetraDup: Select<ELetraDup>
  private lateinit var edtSaldo: IntegerField
  private lateinit var chkConfigSt: Checkbox

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
        edtSaldo = integerField("Saldo") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          this.value = 0
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        chkConfigSt = checkBox("Sem Sped") {
          this.value = false
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        button("Configura Sped") {
          this.icon = VaadinIcon.COG.create()
          onClick {
            viewModel.configProdutosSelecionados()
          }
        }
      }
    }
  }

  override fun Grid<ProdutoCadastro>.gridPanel() {
    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.addColumnSeq("Seq", width = "50px")
    columnGrid(ProdutoCadastro::codigo, header = "Cód", width = "80px").right()
    columnGrid(ProdutoCadastro::descricao, header = "Descrição").expand()
    columnGrid(ProdutoCadastro::unidade, header = "Un")
    columnGrid(ProdutoCadastro::rotulo, header = "Rotulo")
    columnGrid(ProdutoCadastro::tributacao, header = "Trib").center()
    columnGrid(ProdutoCadastro::forn, header = "Forn")
    columnGrid(ProdutoCadastro::abrev, header = "Abrev")
    columnGrid(ProdutoCadastro::ncm, header = "NCM").right()
    columnGrid(ProdutoCadastro::tipo, header = "Tipo")
    columnGrid(ProdutoCadastro::clno, header = "CL", width = "80px")
    columnGrid(ProdutoCadastro::refForn, header = "Ref Forn", width="150px").right()
    columnGrid(ProdutoCadastro::pesoBruto, header = "P.Bruto")
    columnGrid(ProdutoCadastro::uGar, header = "U.Gar")
    columnGrid(ProdutoCadastro::tGar, header = "T.Gar")
    columnGrid(ProdutoCadastro::emb, header = "Emb")
    columnGrid(ProdutoCadastro::foraLinha, header = "F.Linha")
    columnGrid(ProdutoCadastro::saldo, header = "Saldo", width="80px").right()
    columnGrid(ProdutoCadastro::configSt, header = "Conf St")
  }

  override fun filtro(): FiltroProdutoCadastro {
    return FiltroProdutoCadastro(
      pesquisa = edtPesquisa.value ?: "",
      vendno = edtFornecedor.value ?: 0,
      taxno = edtTributo.value ?: "",
      rotulo = edtRotulo.value ?: "",
      typeno = edtTipo.value ?: 0,
      clno = edtCl.value ?: 0,
      caracter = cmbCartacer.value ?: ECaracter.TODOS,
      letraDup = cmbLetraDup.value ?: ELetraDup.TODOS,
      saldo = edtSaldo.value ?: 0,
      configSt = chkConfigSt.value ?: false
    )
  }

  override fun updateProdutos(produtos: List<ProdutoCadastro>) {
    updateGrid(produtos)
  }

  override fun produtosSelecionados(): List<ProdutoCadastro> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.produtoCadastro == true
  }

  override val label: String
    get() = "Cadastro"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraProduto.orEmpty().toList()
  }
}
