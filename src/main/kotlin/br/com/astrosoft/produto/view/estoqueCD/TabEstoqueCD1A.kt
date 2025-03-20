package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueCD1A
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueCD1AViewModel
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabEstoqueCD1A(val viewModel: TabEstoqueCD1AViewModel) :
  TabPanelGrid<ProdutoEstoque>(ProdutoEstoque::class), ITabEstoqueCD1A {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtGrade: TextField
  private lateinit var cmbCaracter: Select<ECaracter>
  private lateinit var cmbInativo: Select<EInativo>

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
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
    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "estoqueMF") {
      val produtos = itensSelecionados()
      viewModel.geraPlanilha(produtos)
    }
  }

  override fun Grid<ProdutoEstoque>.gridPanel() {
    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)
    addColumnSeq("Seq")
    columnGrid(ProdutoEstoque::codigo, header = "Código")
    columnGrid(ProdutoEstoque::descricao, header = "Descrição").expand()
    columnGrid(ProdutoEstoque::grade, header = "Grade", width = "100px")
    columnGrid(ProdutoEstoque::unidade, header = "UN")
    columnGrid(ProdutoEstoque::locApp, header = "Loc")
    columnGrid(ProdutoEstoque::embalagem, header = "Emb")
    columnGrid(ProdutoEstoque::qtdEmbalagem, header = "Qtd Emb")
    columnGrid(ProdutoEstoque::estoque, header = "Estoque")
    columnGrid(ProdutoEstoque::saldo, header = "Saldo")
  }

  override fun filtro(): FiltroProdutoEstoque {
    val user = AppConfig.userLogin() as? UserSaci
    val listaUser = user?.listaEstoque.orEmpty().toList().ifEmpty {
      listOf("TODOS")
    }
    return FiltroProdutoEstoque(
      loja = 4,
      pesquisa = edtPesquisa.value ?: "",
      codigo = 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCaracter.value ?: ECaracter.TODOS,
      localizacao = "CD1A",
      fornecedor = "",
      inativo = cmbInativo.value ?: EInativo.TODOS,
      uso = EUso.TODOS,
      listaUser = listaUser
    )
  }

  override fun updateProduto(produtos: List<ProdutoEstoque>) {
    updateGrid(produtos)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueCD1A == true
  }

  override val label: String
    get() = "CD1A"

  override fun updateComponent() {
    viewModel.updateView()
  }
}