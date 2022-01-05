package br.com.astrosoft.produto.view.nota

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.produto.model.beans.FiltroNota
import br.com.astrosoft.produto.model.beans.NotaSaida
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFCliente
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFData
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFLoja
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFNota
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFVendedor
import br.com.astrosoft.produto.viewmodel.nota.ITabNotaBase
import br.com.astrosoft.produto.viewmodel.nota.TabNotaBaseViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaBase(val viewModel: TabNotaBaseViewModel) : TabPanelGrid<NotaSaida>(NotaSaida::class), ITabNotaBase {
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
      DlgProdutos(viewModel).showDialog(nota)
    }
    colunaNFLoja()
    colunaNFNota()
    colunaNFData()
    colunaNFCliente()
    colunaNFVendedor()
  }

  override fun filtro(): FiltroNota {
    val loja = (Config.user as? UserSaci)?.storeno ?: 0
    return FiltroNota(storeno = loja, nota = edtNota.value)
  }

  override fun updateProdutos(notas: List<NotaSaida>) {
    updateGrid(notas)
  }

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.notaBase == true
  }

  override val label: String
    get() = "Base"

  override fun updateComponent() {
    viewModel.updateView()
  }
}