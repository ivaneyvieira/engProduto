package br.com.astrosoft.produto.view.nota

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFCliente
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFData
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFLoja
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFNota
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFVendedor
import br.com.astrosoft.produto.viewmodel.nota.ITabNotaEnt
import br.com.astrosoft.produto.viewmodel.nota.TabNotaEntViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaEnt(val viewModel: TabNotaEntViewModel) : TabPanelGrid<NotaSaida>(NotaSaida::class), ITabNotaEnt {
  private lateinit var dlgProduto: DlgProdutosEnt
  private lateinit var edtNota: TextField

  override fun HorizontalLayout.toolBarConfig() {
    edtNota = textField("Nota") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<NotaSaida>.gridPanel() {
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosEnt(viewModel)
      dlgProduto.showDialog(nota)
    }
    colunaNFLoja()
    colunaNFNota()
    colunaNFData()
    colunaNFCliente()
    colunaNFVendedor()
  }

  override fun filtro(marca : EMarcaNota): FiltroNota {
    val loja = (Config.user as? UserSaci)?.storeno ?: 0
    return FiltroNota(storeno = loja, nota = edtNota.value, marca)
  }

  override fun updateProdutos(notas: List<NotaSaida>) {
    updateGrid(notas)
  }

  override fun produtosSelcionados(): List<ProdutoNF> {
    return dlgProduto.itensSelecionados()
  }

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.notaEnt == true
  }

  override val label: String
    get() = "Entregue"

  override fun updateComponent() {
    viewModel.updateView()
  }
}