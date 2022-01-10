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
import br.com.astrosoft.produto.viewmodel.nota.ITabNotaCD
import br.com.astrosoft.produto.viewmodel.nota.TabNotaCDViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaCD(val viewModel: TabNotaCDViewModel) : TabPanelGrid<NotaSaida>(NotaSaida::class), ITabNotaCD {
  private lateinit var dlgProduto: DlgProdutosCD
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
      dlgProduto = DlgProdutosCD(viewModel)
      dlgProduto.showDialog(nota)
    }
    colunaNFLoja()
    colunaNFNota()
    colunaNFData()
    colunaNFCliente()
    colunaNFVendedor()
  }

  override fun filtro(): FiltroNota {
    val loja = (Config.user as? UserSaci)?.storeno ?: 0
    return FiltroNota(storeno = loja, nota = edtNota.value, EMarcaNota.CD)
  }

  override fun updateProdutos(notas: List<NotaSaida>) {
    updateGrid(notas)
  }

  override fun produtosSelcionados(): List<ProdutoNF> {
    return dlgProduto.itensSelecionados()
  }

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.notaCD == true
  }

  override val label: String
    get() = "CD"

  override fun updateComponent() {
    viewModel.updateView()
  }
}