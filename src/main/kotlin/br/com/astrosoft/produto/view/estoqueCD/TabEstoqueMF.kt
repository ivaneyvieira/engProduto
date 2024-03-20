package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.produto.model.beans.ECaracter
import br.com.astrosoft.produto.model.beans.FiltroProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.reposicao.ReposicaoView
import br.com.astrosoft.produto.view.ressuprimento.RessuprimentoView
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueMF
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueMFViewModel
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabEstoqueMF(val viewModel: TabEstoqueMFViewModel) :
  TabPanelGrid<ProdutoEstoque>(ProdutoEstoque::class), ITabEstoqueMF {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtGrade: TextField
  private lateinit var cmbCaracter: Select<ECaracter>
  private lateinit var edtLocalizacao: TextField

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
      val dlg = SubWindowForm("Reposição|$codigo : $descricao : $grade") {
        val view = RessuprimentoView(false, codigo, grade)
        val tab = view.tabRessuprimentoEnt
        val form = tab.createComponent()
        tab.updateComponent()
        form
      }
      dlg.open()
    }
    addColumnButton(VaadinIcon.SIGNAL, "Reposicao", "Reposicao") { produto ->
      val codigo = produto.codigo?.toString() ?: ""
      val grade = produto.grade ?: ""
      val descricao = produto.descricao ?: ""
      val dlg = SubWindowForm("Reposição|$codigo : $descricao : $grade") {
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
    columnGrid(ProdutoEstoque::locApp, header = "Loc")
    columnGrid(ProdutoEstoque::embalagem, header = "Emb")
    columnGrid(ProdutoEstoque::qtdEmbalagem, header = "Qtd Emb")
    columnGrid(ProdutoEstoque::estoque, header = "Estoque")
    columnGrid(ProdutoEstoque::saldo, header = "Saldo")
  }

  override fun filtro(): FiltroProdutoEstoque {
    return FiltroProdutoEstoque(
      loja = 4,
      pesquisa = edtPesquisa.value ?: "",
      codigo = 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCaracter.value ?: ECaracter.TODOS,
      localizacao = edtLocalizacao.value ?: "",
      fornecedor = "",
    )
  }

  override fun updateProduto(produtos: List<ProdutoEstoque>) {
    updateGrid(produtos)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueMF == true
  }

  override val label: String
    get() = "Estoque"

  override fun updateComponent() {
    viewModel.updateView()
  }
}