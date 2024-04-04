package br.com.astrosoft.produto.view.notaSaida

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaHora
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
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabNotaCD(val viewModel: TabNotaCDViewModel) : TabPanelGrid<NotaSaida>(NotaSaida::class), ITabNotaCD {
  private var dlgProduto: DlgProdutosCD? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbNota: Select<ETipoNota>
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var edtPesquisa: TextField

  fun init() {
    val user = AppConfig.userLogin() as? UserSaci
    val loja = user?.lojaNota ?: 0
    val lojaSelecionada = viewModel.findAllLojas().firstOrNull { it.no == loja }
    cmbLoja.isReadOnly = lojaSelecionada != null
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    cmbLoja.value = lojaSelecionada ?: Loja.lojaZero
  }

  override fun HorizontalLayout.toolBarConfig() {
    cmbLoja = select("Loja") {
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateView()
      }
    }
    init()
    edtPesquisa = textField("Pesquisa") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data Inicial") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    cmbNota = select("Nota") {
      val user = AppConfig.userLogin() as? UserSaci
      setItems(ETipoNota.entries)
      value = ETipoNota.TODOS
      val tipoNota = ETipoNota.entries.firstOrNull { it.num == user?.tipoNota }
      this.isReadOnly = tipoNota != null && tipoNota.num != ETipoNota.TODOS.num
      this.setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
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
      marca = marca,
      tipoNota = cmbNota.value ?: ETipoNota.TODOS,
      loja = cmbLoja.value?.no ?: 0,
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      pesquisa = edtPesquisa.value ?: ""
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