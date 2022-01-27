package br.com.astrosoft.produto.view.notaEntrada

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFEDataEmissao
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFEDataEntrada
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFEFornecedor
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFELoja
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFENI
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFENomeFornecedor
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFENota
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFEValor
import br.com.astrosoft.produto.viewmodel.notaEntrada.ITabNotaEntradaRecebido
import br.com.astrosoft.produto.viewmodel.notaEntrada.TabNotaEntradaRecebidoViewModel
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class TabNotaEntradaRecebido(val viewModel: TabNotaEntradaRecebidoViewModel) :
        TabPanelGrid<NotaEntrada>(NotaEntrada::class), ITabNotaEntradaRecebido {
  private var dlgProduto: DlgProdutosRecebido? = null

  override fun HorizontalLayout.toolBarConfig() {
  }

  override fun Grid<NotaEntrada>.gridPanel() {
    colunaNFELoja()
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosRecebido(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaNFENI()
    colunaNFENota()
    colunaNFEDataEmissao()
    colunaNFEDataEntrada()
    colunaNFEFornecedor()
    colunaNFENomeFornecedor()
    colunaNFEValor()
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
    return username?.notaEntradaRecebido == true
  }

  override val label: String
    get() = "Recebido"

  override fun updateComponent() {
    viewModel.updateView()
  }
}