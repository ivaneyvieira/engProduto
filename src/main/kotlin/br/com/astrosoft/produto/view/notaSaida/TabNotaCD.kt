package br.com.astrosoft.produto.view.notaSaida

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaHora
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFChaveExp
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFCliente
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFData
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFLoja
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFNota
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFValor
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFVendedor
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNomeCliente
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNomeVendedor
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
import java.time.LocalDate

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
      val user = AppConfig.userLogin() as? UserSaci
      isVisible = user?.lojaSaidaCDOk() == 0
      value = user?.lojaSaidaCDOk()
      valueChangeMode = ValueChangeMode.LAZY

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
    colunaHora()
    colunaNFCliente()
    colunaNomeCliente()
    colunaNFVendedor()
    colunaNomeVendedor()
    colunaNFValor()
  }

  override fun filtro(marca: EMarcaNota): FiltroNota {
    return FiltroNota(
      storeno = edtLoja.value ?: 0,
      nota = edtNota.value,
      marca = marca,
      loja = edtLoja.value ?: 0,
      cliente = edtCliente.value ?: 0,
      vendedor = edtVendedor.value ?: "",
      dataInicial = LocalDate.now().minusDays(15),
      dataFinal = LocalDate.now().plusDays(30),
    )
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

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.notaCD == true
  }

  override val label: String
    get() = "CD"

  override fun updateComponent() {
    viewModel.updateView()
  }
}