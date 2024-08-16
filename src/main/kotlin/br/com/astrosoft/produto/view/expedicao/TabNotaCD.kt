package br.com.astrosoft.produto.view.expedicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaHora
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFCliente
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFData
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFEntregaRetira
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFLoja
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFNota
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFTipo
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFValor
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFVendedor
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNomeCliente
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNomeVendedor
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaRota
import br.com.astrosoft.produto.viewmodel.expedicao.ITabNotaCD
import br.com.astrosoft.produto.viewmodel.expedicao.TabNotaCDViewModel
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
  private var colRota: Grid.Column<NotaSaida>? = null
  private var dlgProduto: DlgProdutosCD? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbNota: Select<ETipoNotaFiscal>
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var edtPesquisa: TextField

  fun init() {
    val user = AppConfig.userLogin() as? UserSaci
    val loja = user?.lojaNota ?: 0
    val lojaSelecionada = viewModel.findAllLojas().firstOrNull { it.no == loja }
    cmbLoja.isReadOnly = (user?.admin == false) && (lojaSelecionada?.no != 0)
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
    cmbNota = select("Nota") {
      val user = AppConfig.userLogin() as? UserSaci
      val tiposNota = user?.tipoNotaExpedicao.let { tipo ->
        if (tipo == null) ETipoNotaFiscal.entries
        else {
          if (tipo.contains(ETipoNotaFiscal.TODOS)) ETipoNotaFiscal.entries
          else tipo.ifEmpty { ETipoNotaFiscal.entries }
        }
      }
      setItems(tiposNota)
      value = tiposNota.firstOrNull()

      this.setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateView()
        colRota?.isVisible = it.value == ETipoNotaFiscal.ENTRE_FUT
      }
    }
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
  }

  override fun Grid<NotaSaida>.gridPanel() {
    colunaNFLoja()
    addColumnButton(VaadinIcon.PRINT, "Etiqueta", "Etiqueta") { nota ->
      viewModel.printEtiquetaExp(nota)
    }
    addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { nota ->
      viewModel.formEntregue(nota)
    }
    columnGrid(NotaSaida::usuarioSing, "Entregue")
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosCD(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaNFNota()
    colunaNFData()
    colunaHora()
    colRota = colunaRota()
    colunaNFCliente()
    colunaNomeCliente()
    colunaNFVendedor()
    colunaNomeVendedor()
    colunaNFTipo()
    colunaNFEntregaRetira()

    colunaNFValor()
  }

  override fun filtro(): FiltroNota {
    return FiltroNota(
      marca = EMarcaNota.CD,
      tipoNota = cmbNota.value ?: ETipoNotaFiscal.TODOS,
      loja = cmbLoja.value?.no ?: 0,
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      pesquisa = edtPesquisa.value ?: "",
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

  override fun formEntregue(nota: NotaSaida) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Entregue", form = form) {
      viewModel.entreguePedido(nota, form.login, form.senha)
    }
  }
}

