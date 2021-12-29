package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.produto.model.beans.FiltroProduto
import br.com.astrosoft.produto.model.beans.Produto
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoAltura
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoBarcode
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoClno
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoCodigo
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoComprimento
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoDescricao
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoGrade
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoLargura
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoLocalizacao
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoNcm
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoPrecoCheio
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoTypeNo
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoVendno
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoList
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoListViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabProdutoList(val viewModel: TabProdutoListViewModel) : TabPanelGrid<Produto>(Produto::class), ITabProdutoList {
  private lateinit var edtProduto: TextField
  private lateinit var edtLocalizacao: TextField
  private lateinit var edtTipo: IntegerField
  private lateinit var edtCentroLucro: IntegerField
  private lateinit var edtFornecedor: IntegerField

  override fun HorizontalLayout.toolBarConfig() {
    edtProduto = textField("Produto") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtTipo = integerField("Tipo") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtCentroLucro = integerField("CL") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtFornecedor = integerField("Fornecedor") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtLocalizacao = textField("Localização") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<Produto>.gridPanel() {
    produtoCodigo()
    produtoBarcode()
    produtoDescricao()
    produtoGrade()
    produtoLocalizacao()
    produtoVendno()
    produtoTypeNo()
    produtoClno()
    produtoAltura()
    produtoComprimento()
    produtoLargura()
    produtoPrecoCheio()
    produtoNcm()
  }

  override fun filtro(): FiltroProduto {
    return FiltroProduto(loja = 0,
                         codigo = edtProduto.value ?: "",
                         typeno = edtTipo.value ?: 0,
                         clno = edtCentroLucro.value ?: 0,
                         vendno = edtFornecedor.value ?: 0,
                         localizacao = edtLocalizacao.value ?: "",
                         nota = "")
  }

  override fun updateProdutos(produtos: List<Produto>) {
    updateGrid(produtos)
  }

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.produtoList == true
  }

  override val label: String
    get() = "Produto"

  override fun updateComponent() {
    viewModel.updateView()
  }
}