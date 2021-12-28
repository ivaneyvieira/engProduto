package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.FiltroProduto
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoRetiraEntrega
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaCliente
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaClno
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaCodigo
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaData
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaDescricao
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaEmpno
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaEstSaci
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaGrade
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaLoja
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaNota
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaPedido
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaQuant
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaSaldo
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaTipo
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaTypeNo
import br.com.astrosoft.produto.view.produto.columns.ProdutoRetiraEntregaViewColumns.retiraEntregaVendno
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoRetiraEntrega
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoRetiraEntregaViewModel
import com.github.mvysny.karibudsl.v10.gridContextMenu
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.SortDirection
import com.vaadin.flow.data.value.ValueChangeMode

class TabProdutoRetiraEntrega(val viewModel: TabProdutoRetiraEntregaViewModel) :
        TabPanelGrid<ProdutoRetiraEntrega>(ProdutoRetiraEntrega::class), ITabProdutoRetiraEntrega {
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
    retiraEntregaLoja()
    retiraEntregaPedido()
    retiraEntregaData()
    retiraEntregaNota()
    retiraEntregaTipo()
    retiraEntregaCliente()
    retiraEntregaEmpno()
    retiraEntregaCodigo()
    retiraEntregaDescricao()
    retiraEntregaGrade()
    retiraEntregaVendno()
    retiraEntregaTypeNo()
    retiraEntregaClno()
    retiraEntregaQuant()
    retiraEntregaEstSaci()
    retiraEntregaSaldo()

    this.gridContextMenu {
      val gridGrade = Grid(PrdGrade::class.java, false).apply {
        this.addColumnString(PrdGrade::grade) {
          this.setHeader("Grade")
        }
        this.addColumnInt(PrdGrade::saldo) {
          this.setHeader("Saldo")
        }
        setWidth("200px")
        setHeight("200px")
      }

      this.add(gridGrade)

      this.setDynamicContentHandler { prd ->
        viewModel.findGrade(prd) { itens ->
          gridGrade.setItems(itens)
          gridGrade.sort(mutableListOf(GridSortOrder(gridGrade.getColumnBy(PrdGrade::saldo), SortDirection.ASCENDING)))
        }
        return@setDynamicContentHandler true
      }
    }
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
    get() = "Retira/Entrega"

  override fun updateComponent() {
    viewModel.updateView()
  }
}
