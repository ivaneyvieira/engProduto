package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.produto.model.beans.EEstoqueList
import br.com.astrosoft.produto.model.beans.EEstoqueTotal
import br.com.astrosoft.produto.model.beans.Produtos
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_DS_AT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_DS_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_DS_VA
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_MF_AT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_MF_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_MF_VA
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_MR_AT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_MR_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_MR_VA
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_PK_AT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_PK_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_PK_VA
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_TM_AT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_TM_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_TM_VA
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_Unidade
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_abrev
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_cl
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_codBar
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_codigo
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_descricao
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_estoque
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_forn
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_grade
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_tipo
import br.com.astrosoft.promocao.viewmodel.produto.ITabEstoqueGeralViewModel
import br.com.astrosoft.promocao.viewmodel.produto.TabEstoqueGeralViewModel
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class TabEstoqueGeralProduto(viewModel: TabEstoqueGeralViewModel) :
  TabAbstractProduto<ITabEstoqueGeralViewModel>(viewModel, showDatas = false), ITabEstoqueGeralViewModel {
  override fun isAuthorized(): Boolean {
    val user = AppConfig.userLogin()
    return (user as? UserSaci)?.produtoEstoqueGeral ?: false
  }

  override val label: String
    get() = "Estoque Geral"

  override fun HorizontalLayout.addAditionaisFields() {
  }

  override fun Grid<Produtos>.colunasGrid() {
    this.setSelectionMode(Grid.SelectionMode.MULTI)
    addColumnSeq("Seq")
    produto_codigo()
    produto_descricao()
    produto_grade()
    produto_Unidade()
    produto_estoque()
    produto_forn()
    produto_abrev()
    produto_tipo()
    produto_cl()
    produto_codBar()

    produto_DS_VA()
    produto_DS_AT()
    produto_DS_TT()
    produto_MR_VA()
    produto_MR_AT()
    produto_MR_TT()
    produto_MF_VA()
    produto_MF_AT()
    produto_MF_TT()
    produto_PK_VA()
    produto_PK_AT()
    produto_PK_TT()
    produto_TM_VA()
    produto_TM_AT()
    produto_TM_TT()
  }

  override fun estoqueTotal(): EEstoqueTotal {
    return EEstoqueTotal.TODOS
  }

  override fun lojaEstoque() = 0
  override fun estoque(): EEstoqueList = EEstoqueList.TODOS
  override fun saldo(): Int = 0
}