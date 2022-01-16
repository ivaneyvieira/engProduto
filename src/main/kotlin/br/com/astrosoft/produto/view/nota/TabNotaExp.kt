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
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFSituacao
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFTipo
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFValor
import br.com.astrosoft.produto.view.nota.columns.NotaColumns.colunaNFVendedor
import br.com.astrosoft.produto.viewmodel.nota.ITabNotaExp
import br.com.astrosoft.produto.viewmodel.nota.TabNotaExpViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaExp(val viewModel: TabNotaExpViewModel) : TabPanelGrid<NotaSaida>(NotaSaida::class), ITabNotaExp {
  private lateinit var dlgProduto: DlgProdutosExp
  private lateinit var edtNota: TextField
  private lateinit var edtLoja: IntegerField
  private lateinit var edtCliente: IntegerField
  private lateinit var edtVendedor: TextField

  override fun HorizontalLayout.toolBarConfig() {
    edtNota = textField("Nota") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtLoja = integerField("Loja") {
      val user = Config.user as? UserSaci
      isVisible = user?.storeno == 0
      value = user?.storeno
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtCliente = integerField("Cliente") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtVendedor = textField("Vendedor") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<NotaSaida>.gridPanel() {
    colunaNFLoja()
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosExp(viewModel, nota)
      dlgProduto.showDialog {
        viewModel.updateView()
      }
    }
    colunaNFNota()
    colunaNFData()
    colunaNFCliente()
    colunaNFVendedor()
    colunaNFValor()
    colunaNFSituacao()
    colunaNFTipo()

    this.setClassNameGenerator {
      when {
        it.cancelada == "S" -> "cancelada"
        it.marca == 1       -> "cd"
        it.marca == 2       -> "entregue"
        else                -> null
      }
    }
  }

  override fun filtro(marca: EMarcaNota): FiltroNota {
    return FiltroNota(storeno = edtLoja.value ?: 0,
                      nota = edtNota.value,
                      marca = marca,
                      loja = edtLoja.value ?: 0,
                      cliente = edtCliente.value ?: 0,
                      vendedor = edtVendedor.value ?: "")
  }

  override fun updateNotas(notas: List<NotaSaida>) {
    updateGrid(notas)
  }

  override fun findNota(): NotaSaida {
    return dlgProduto.nota
  }

  override fun updateProdutos() {
    dlgProduto.update()
  }

  override fun produtosSelcionados(): List<ProdutoNF> {
    return dlgProduto.itensSelecionados()
  }

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.notaExp == true
  }

  override val label: String
    get() = "Exp"

  override fun updateComponent() {
    viewModel.updateView()
  }
}