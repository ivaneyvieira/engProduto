package br.com.astrosoft.produto.view.notaEntrada

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFEChave
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFEData
import br.com.astrosoft.produto.viewmodel.notaEntrada.ITabNotaEntradaPendente
import br.com.astrosoft.produto.viewmodel.notaEntrada.ITabNotaEntradaReceber
import br.com.astrosoft.produto.viewmodel.notaEntrada.TabNotaEntradaPendenteViewModel
import br.com.astrosoft.produto.viewmodel.notaEntrada.TabNotaEntradaReceberViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaEntradaPendente(val viewModel: TabNotaEntradaPendenteViewModel) : TabPanelGrid<NotaEntrada>
  (NotaEntrada::class), ITabNotaEntradaPendente {
  private  var dlgProduto: DlgProdutosPendente? = null

  override fun HorizontalLayout.toolBarConfig() {
  }

  override fun Grid<NotaEntrada>.gridPanel() {
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosPendente(viewModel,nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaNFEChave()
    colunaNFEData()
  }

  override fun updateNotas(notas: List<NotaEntrada>) {
    updateGrid(notas)
  }

  override fun notaSelecionada(): NotaEntrada? {
    return dlgProduto?.nota
  }

  override fun updateViewProduto() {
    dlgProduto?.update()
  }

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.notaEntradaPendente == true
  }

  override val label: String
    get() = "Pendente"

  override fun updateComponent() {
    viewModel.updateView()
  }
}