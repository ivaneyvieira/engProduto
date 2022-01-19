package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoCliente
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoData
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoLoja
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoNumero
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoVendedor
import br.com.astrosoft.produto.viewmodel.ressuprimento.ITabRessuprimentoCD
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoCDViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.data.value.ValueChangeMode

class TabRessuprimentoCD(val viewModel: TabRessuprimentoCDViewModel) :
  TabPanelGrid<Ressuprimento>(Ressuprimento::class),
  ITabRessuprimentoCD {
  private var dlgProduto: DlgProdutosRessuCD? = null
  private lateinit var edtLoja: IntegerField
  private lateinit var edtRessuprimento: IntegerField

  override fun HorizontalLayout.toolBarConfig() {
    edtLoja = integerField("Loja") {
      val user = Config.user as? UserSaci
      isVisible = user?.storeno == 0
      value = user?.storeno
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtRessuprimento = integerField("Ressuprimento") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<Ressuprimento>.gridPanel() {
    colunaRessuprimentoLoja()
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { pedido ->
      dlgProduto = DlgProdutosRessuCD(viewModel, pedido)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaRessuprimentoNumero()
    colunaRessuprimentoData()
    colunaRessuprimentoCliente()
    colunaRessuprimentoVendedor()
  }

  override fun filtro(marca: EMarcaRessuprimento): FiltroRessuprimento {
    return FiltroRessuprimento(storeno = edtLoja.value ?: 0, numero = edtRessuprimento.value ?: 0, marca = marca)
  }

  override fun updateRessuprimentos(pedido: List<Ressuprimento>) {
    updateGrid(pedido)
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelcionados(): List<ProdutoRessuprimento> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento? {
    return dlgProduto?.produtosCodigoBarras(codigoBarra)
  }

  override fun findRessuprimento(): Ressuprimento? {
    return dlgProduto?.ressuprimento
  }

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.pedidoCD == true
  }

  override val label: String
    get() = "CD"

  override fun updateComponent() {
    viewModel.updateView()
  }
}