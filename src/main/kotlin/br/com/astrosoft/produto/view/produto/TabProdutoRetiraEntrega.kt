package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.produto.model.beans.FiltroProduto
import br.com.astrosoft.produto.model.beans.ProdutoRetiraEntrega
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoCliente
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoClno
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoCodigo
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoData
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoDescricao
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoEmpno
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoEstSaci
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoGrade
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoLoja
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoNota
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoPedido
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoQuant
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoSaldo
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoTipo
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoTypeNo
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.produtoVendno
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoRetiraEntrega
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoRetiraEntregaViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabProdutoRetiraEntrega(val viewModel: TabProdutoRetiraEntregaViewModel) : TabPanelGrid<ProdutoRetiraEntrega>(ProdutoRetiraEntrega::class), ITabProdutoRetiraEntrega {
  private lateinit var edtProduto: TextField
  private lateinit var edtTipo: IntegerField
  private lateinit var edtCentroLucro: IntegerField
  private lateinit var edtFornecedor: IntegerField
  private lateinit var edtNota: TextField

  override fun HorizontalLayout.toolBarConfig() {
    edtProduto = textField("ProdutoRetiraEntrega") {
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

  override fun Grid<ProdutoRetiraEntrega>.gridPanel() {
    produtoLoja()
    produtoPedido()
    produtoData()
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

  override fun updateProdutos(produtos: List<ProdutoRetiraEntrega>) {
    updateGrid(produtos)
  }

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.produtoList == true
  }

  override val label: String
    get() = "ProdutoRetiraEntrega"

  override fun updateComponent() {
    viewModel.updateView()
  }
}
