package br.com.astrosoft.produto.view.notaSaida

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFChaveExp
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFCliente
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFData
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFLoja
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFNota
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFValor
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFVendedor
import br.com.astrosoft.produto.viewmodel.notaSaida.ITabNotaCD
import br.com.astrosoft.produto.viewmodel.notaSaida.TabNotaCDViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaCD(val viewModel: TabNotaCDViewModel) : TabPanelGrid<NotaSaida>(NotaSaida::class), ITabNotaCD {
  private var dlgProduto: DlgProdutosCD? = null
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
    addColumnButton(VaadinIcon.PRINT, "Etiqueta", "Etiqueta") { nota ->
      viewModel.printEtiquetaExp(nota)
    }
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosCD(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaNFChaveExp()
    colunaNFNota()
    colunaNFData()
    colunaNFCliente()
    colunaNFVendedor()
    colunaNFValor()
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

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelcionados(): List<ProdutoNFS> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun produtosCodigoBarras(codigoBarra: String): ProdutoNFS? {
    return dlgProduto?.produtosCodigoBarras(codigoBarra)
  }

  override fun findNota(): NotaSaida? {
    return dlgProduto?.nota
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