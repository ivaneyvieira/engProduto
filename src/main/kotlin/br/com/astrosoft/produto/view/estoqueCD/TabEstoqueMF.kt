package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.produto.model.beans.FiltroProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueMF
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueMFViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabEstoqueMF(val viewModel: TabEstoqueMFViewModel) :
  TabPanelGrid<ProdutoEstoque>(ProdutoEstoque::class), ITabEstoqueMF {
  private lateinit var edtPesquisa: TextField

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<ProdutoEstoque>.gridPanel() {
    this.addClassName("styling")
    columnGrid(ProdutoEstoque::codigo, header = "Código")
    columnGrid(ProdutoEstoque::descricao, header = "Descrição").expand()
    columnGrid(ProdutoEstoque::grade, header = "Grade")
    columnGrid(ProdutoEstoque::unidade, header = "UN")
    columnGrid(ProdutoEstoque::embalagem, header = "Emb")
    columnGrid(ProdutoEstoque::qtdEmbalagem, header = "Embalagem")
    columnGrid(ProdutoEstoque::estoque, header = "Estoque")
  }

  override fun filtro(): FiltroProdutoEstoque {
    return FiltroProdutoEstoque(
      loja = 4,
      pesquisa = edtPesquisa.value ?: "",
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
    get() = "MF"

  override fun updateComponent() {
    viewModel.updateView()
  }
}