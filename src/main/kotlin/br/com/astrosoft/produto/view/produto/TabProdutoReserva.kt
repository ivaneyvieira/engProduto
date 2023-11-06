package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.produto.model.beans.FiltroProduto
import br.com.astrosoft.produto.model.beans.Pedido
import br.com.astrosoft.produto.model.beans.ProdutoReserva
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaClno
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaCodigo
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaData
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaDescricao
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaGrade
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaLocalizacao
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaLoja
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaPedido
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaReserva
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaSaldo
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaSaldoSaci
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaTypeNo
import br.com.astrosoft.produto.view.produto.columns.ProdutoReservaColumns.produtoReservaVendno
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoReserva
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoReservaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabProdutoReserva(val viewModel: TabProdutoReservaViewModel) :
        TabPanelGrid<ProdutoReserva>(ProdutoReserva::class), ITabProdutoReserva {
  private lateinit var edtProduto: TextField
  private lateinit var edtLocalizacao: TextField
  private lateinit var edtTipo: IntegerField
  private lateinit var edtLoja: IntegerField
  private lateinit var edtCentroLucro: IntegerField
  private lateinit var edtFornecedor: IntegerField

  override fun HorizontalLayout.toolBarConfig() {
    edtLoja = integerField("Loja") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
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
    button("Expira pedido") {
      onLeftClick {
        viewModel.expiraPedidosSelecionados()
      }
    }
  }

  override fun Grid<ProdutoReserva>.gridPanel() {
    setSelectionMode(Grid.SelectionMode.MULTI)

    produtoReservaLoja()
    produtoReservaPedido()
    produtoReservaData()
    produtoReservaCodigo()
    produtoReservaDescricao()
    produtoReservaGrade()
    produtoReservaLocalizacao()
    produtoReservaVendno()
    produtoReservaTypeNo()
    produtoReservaClno()
    produtoReservaSaldoSaci()
    produtoReservaReserva()
    produtoReservaSaldo()
  }

  override fun filtro(): FiltroProduto {
    return FiltroProduto(loja = edtLoja.value ?: 0,
                         codigo = edtProduto.value ?: "",
                         typeno = edtTipo.value ?: 0,
                         clno = edtCentroLucro.value ?: 0,
                         vendno = edtFornecedor.value ?: 0,
                         localizacao = edtLocalizacao.value ?: "",
                         nota = "")
  }

  override fun updateProdutos(produtos: List<ProdutoReserva>) {
    updateGrid(produtos)
  }

  override fun pedidosSelecionado(): List<Pedido> {
    return itensSelecionados().map { it.pedido() }.distinct()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.produtoReserva == true
  }

  override val label: String
    get() = "Reserva"

  override fun updateComponent() {
    viewModel.updateView()
  }
}