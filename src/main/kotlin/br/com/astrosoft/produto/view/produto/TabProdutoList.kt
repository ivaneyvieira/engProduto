package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.produto.model.beans.FiltroProduto
import br.com.astrosoft.produto.model.beans.Produto
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoCliente
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoClno
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoCodigo
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoDescricao
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoEmpno
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoEstSaci
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoGrade
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoLoja
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoNota
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoPedido
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoQuant
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoSaldo
import br.com.astrosoft.produto.view.produto.columns.ProdutoViewColumns.produtoTipo
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
  private lateinit var edtTipo: IntegerField
  private lateinit var edtCentroLucro: IntegerField
  private lateinit var edtFornecedor: IntegerField
  private lateinit var edtNota: TextField

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
    edtNota = textField("Nota") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<Produto>.gridPanel() {

    produtoLoja()
    produtoPedido()
    produtoNota()
    produtoTipo()
    produtoCliente()
    produtoEmpno()
    produtoCodigo()
    produtoDescricao()
    produtoGrade()
    produtoVendno()
    produtoTypeNo()
    produtoClno()
    produtoQuant()
    produtoEstSaci()
    produtoSaldo()
  }

  override fun filtro(): FiltroProduto {
    return FiltroProduto(codigo = edtProduto.value ?: "",
                         typeno = edtTipo.value ?: 0,
                         clno = edtCentroLucro.value ?: 0,
                         vendno = edtFornecedor.value ?: 0,
                         nota = edtNota.value ?: "")
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
