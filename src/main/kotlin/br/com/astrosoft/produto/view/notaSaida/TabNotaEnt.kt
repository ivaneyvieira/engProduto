package br.com.astrosoft.produto.view.notaSaida

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.framework.view.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaHora
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFChaveCD
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFChaveExp
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFCliente
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFData
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFDataEnt
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFLoja
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFNota
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFNotaEnt
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFSituacao
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFTipo
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFValor
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFVendedor
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNomeCliente
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNomeVendedor
import br.com.astrosoft.produto.viewmodel.notaSaida.ITabNotaEnt
import br.com.astrosoft.produto.viewmodel.notaSaida.TabNotaEntViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaEnt(val viewModel: TabNotaEntViewModel) : TabPanelGrid<NotaSaida>(NotaSaida::class), ITabNotaEnt {
  private var dlgProduto: DlgProdutosEnt? = null
  private lateinit var edtNota: TextField
  private lateinit var edtLoja: IntegerField
  private lateinit var edtCliente: IntegerField
  private lateinit var edtVendedor: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

  override fun HorizontalLayout.toolBarConfig() {
    edtDataInicial = datePicker("Data Inicial") {
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtNota = textField("Nota") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtLoja = integerField("Loja") {
      val user = Config.user as? UserSaci
      isVisible = user?.lojaSaidaOk() == 0
      value = user?.lojaSaidaOk()
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
      viewModel.printEtiquetaEnt(nota)
    }
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosEnt(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaNFChaveExp()
    colunaNFChaveCD()
    colunaNFNota()
    colunaNFData()
    colunaHora()
    colunaNFNotaEnt()
    colunaNFDataEnt()
    colunaNFCliente()
    colunaNomeCliente()
    colunaNFVendedor()
    colunaNomeVendedor()
    colunaNFValor()
    colunaNFTipo()
    colunaNFSituacao()
  }

  override fun filtro(marca: EMarcaNota): FiltroNota {
    return FiltroNota(
      storeno = edtLoja.value ?: 0,
      nota = edtNota.value,
      marca = marca,
      loja = edtLoja.value ?: 0,
      cliente = edtCliente.value ?: 0,
      vendedor = edtVendedor.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
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