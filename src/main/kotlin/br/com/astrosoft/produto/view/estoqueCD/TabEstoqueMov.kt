package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.reposicao.ReposicaoView
import br.com.astrosoft.produto.view.ressuprimento.RessuprimentoView
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueMov
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueMovViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabEstoqueMov(val viewModel: TabEstoqueMovViewModel) :
  TabPanelGrid<ProdutoEstoque>(ProdutoEstoque::class), ITabEstoqueMov {
  private lateinit var edtProduto: IntegerField
  private lateinit var edtPesquisa: TextField
  private lateinit var edtGrade: TextField
  private lateinit var cmbCaracter: Select<ECaracter>
  private lateinit var edtLocalizacao: TextField
  private lateinit var cmbInativo: Select<EInativo>

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtProduto = integerField("Produto") {
      this.width = "100px"
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
    edtLocalizacao = textField("Loc App") {
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
    addColumnButton(VaadinIcon.SHOP, "Ressuprimento", "Ressuprimento") { produto ->
      val codigo = produto.codigo?.toString() ?: ""
      val grade = produto.grade ?: ""
      val descricao = produto.descricao ?: ""
      val dlg = SubWindowForm("Ressuprimento|$codigo : $descricao : $grade") {
        val view = RessuprimentoView(false, codigo, grade)
        val tab = view.tabRessuprimentoEnt
        val form = tab.createComponent()
        tab.updateComponent()
        form
      }
      dlg.open()
    }
    addColumnButton(VaadinIcon.SIGNAL, "Reposição", "Reposição") { produto ->
      val codigo = produto.codigo?.toString() ?: ""
      val grade = produto.grade ?: ""
      val descricao = produto.descricao ?: ""
      val dlg = SubWindowForm("Reposição Loja|$codigo : $descricao : $grade") {
        val view = ReposicaoView(false, codigo, grade)
        val tab = view.tabReposicaoEnt
        val form = tab.createComponent()
        tab.updateComponent()
        form
      }
      dlg.open()
    }
    addColumnSeq("Seq")
    columnGrid(ProdutoEstoque::codigo, header = "Código")
    columnGrid(ProdutoEstoque::descricao, header = "Descrição").expand()
    columnGrid(ProdutoEstoque::grade, header = "Grade", width = "100px")
    columnGrid(ProdutoEstoque::unidade, header = "UN")
    columnGrid(ProdutoEstoque::locSaci, header = "Loc Saci")
    columnGrid(ProdutoEstoque::locApp, header = "Loc App")
    columnGrid(ProdutoEstoque::embalagem, header = "Emb")
    columnGrid(ProdutoEstoque::qtdEmbalagem, header = "Qtd Emb")
    columnGrid(ProdutoEstoque::estoque, header = "Estoque")
    columnGrid(ProdutoEstoque::saldo, header = "Saldo")
  }

  override fun filtro(): FiltroProdutoEstoque {
    return FiltroProdutoEstoque(
      loja = 4,
      pesquisa = edtPesquisa.value ?: "",
      codigo = edtProduto.value ?: 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCaracter.value ?: ECaracter.TODOS,
      localizacao = edtLocalizacao.value ?: "",
      fornecedor = "",
      inativo = cmbInativo.value ?: EInativo.TODOS
    )
  }


  override fun updateProduto(produtos: List<ProdutoEstoque>) {
    updateGrid(produtos)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueMov == true
  }

  override val label: String
    get() = "Movimentação"

  override fun updateComponent() {
    viewModel.updateView()
  }
}