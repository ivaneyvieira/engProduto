package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.ECaracter
import br.com.astrosoft.produto.model.beans.FiltroProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueCad
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueCadViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabEstoqueCad(val viewModel: TabEstoqueCadViewModel) :
  TabPanelGrid<ProdutoEstoque>(ProdutoEstoque::class), ITabEstoqueCad {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtGrade: TextField
  private lateinit var cmbCaracter: Select<ECaracter>
  private lateinit var edtLocalizacao: TextField
  private lateinit var edtFornecedor: TextField

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
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
    edtLocalizacao = textField("Loc") {
      this.width = "60px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      val userSaci = AppConfig.userLogin() as? UserSaci
      val local = userSaci?.localEstoque ?: ""
      if ((local != "TODOS") && (local != "")) {
        this.value = local
        this.isReadOnly = true
      }
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtFornecedor = textField("Fornecedor") {
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
    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "EstoqueCad") {
      val produtos = itensSelecionados()
      viewModel.geraPlanilha(produtos)
    }

    this.button("Cópia") {
      this.icon = VaadinIcon.COPY.create()
      onLeftClick {
        viewModel.copiaLocalizacao()
      }
    }
  }

  override fun Grid<ProdutoEstoque>.gridPanel() {
    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)

    this.withEditor(
      classBean = ProdutoEstoque::class,
      openEditor = {
        val edit = getColumnBy(ProdutoEstoque::locApp) as? Focusable<*>
        edit?.focus()
      },
      closeEditor = {
        viewModel.updateProduto(it.bean)
      })


    addColumnSeq("Seq")
    columnGrid(ProdutoEstoque::codigo, header = "Código")
    columnGrid(ProdutoEstoque::descricao, header = "Descrição").expand()
    columnGrid(ProdutoEstoque::grade, header = "Grade", width = "100px")
    columnGrid(ProdutoEstoque::unidade, header = "UN")
    columnGrid(ProdutoEstoque::locSaci, header = "Loc Saci", width = "100px")
    columnGrid(ProdutoEstoque::locApp, header = "Loc App", width = "100px").textFieldEditor()
    columnGrid(ProdutoEstoque::codForn, header = "For Cod")
    columnGrid(ProdutoEstoque::fornecedor, header = "For Abr").expand()
  }

  override fun filtro(): FiltroProdutoEstoque {
    return FiltroProdutoEstoque(
      loja = 4,
      pesquisa = edtPesquisa.value ?: "",
      grade = edtGrade.value ?: "",
      caracter = cmbCaracter.value ?: ECaracter.TODOS,
      localizacao = edtLocalizacao.value ?: "",
      fornecedor = edtFornecedor.value ?: "",
    )
  }

  override fun updateProduto(produtos: List<ProdutoEstoque>) {
    updateGrid(produtos)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueCad == true
  }

  override val label: String
    get() = "Cad Loc"

  override fun updateComponent() {
    viewModel.updateView()
  }
}