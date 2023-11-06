package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoComprador
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoData
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoDataBaixa
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoLocalizacao
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoNotaBaixa
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoNumero
import br.com.astrosoft.produto.viewmodel.ressuprimento.ITabRessuprimentoCD
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoCDViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.data.value.ValueChangeMode

class TabRessuprimentoCD(val viewModel: TabRessuprimentoCDViewModel) :
        TabPanelGrid<Ressuprimento>(Ressuprimento::class), ITabRessuprimentoCD {
  private var dlgProduto: DlgProdutosRessuCD? = null
  private lateinit var edtRessuprimento: IntegerField

  override fun HorizontalLayout.toolBarConfig() {
    edtRessuprimento = integerField("Número") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<Ressuprimento>.gridPanel() {
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { pedido ->
      dlgProduto = DlgProdutosRessuCD(viewModel, pedido)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaRessuprimentoNumero()
    colunaRessuprimentoData()
    colunaRessuprimentoNotaBaixa()
    colunaRessuprimentoDataBaixa()
    colunaRessuprimentoLocalizacao()
    colunaRessuprimentoComprador()
  }

  override fun filtro(marca: EMarcaRessuprimento): FiltroRessuprimento {
    return FiltroRessuprimento(numero = edtRessuprimento.value ?: 0, marca = marca)
  }

  override fun updateRessuprimentos(ressuprimentos: List<Ressuprimento>) {
    updateGrid(ressuprimentos)
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelcionados(): List<ProdutoRessuprimento> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun produtosMarcados(): List<ProdutoRessuprimento> {
    return dlgProduto?.produtosMarcados().orEmpty()
  }

  override fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento? {
    return dlgProduto?.produtosCodigoBarras(codigoBarra)
  }

  override fun findRessuprimento(): Ressuprimento? {
    return dlgProduto?.ressuprimento
  }

  override fun updateProduto(produto: ProdutoRessuprimento) {
    dlgProduto?.updateProduto(produto)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.pedidoCD == true
  }

  override val label: String
    get() = "CD"

  override fun updateComponent() {
    viewModel.updateView()
  }
}