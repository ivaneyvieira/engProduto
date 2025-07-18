package br.com.astrosoft.produto.view.expedicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaCD
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaHora
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFCliente
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFData
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFEntregaRetira
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFLoja
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFNota
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFSituacao
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFValor
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNomeCliente
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaPedido
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaRota
import br.com.astrosoft.produto.view.ressuprimento.FormFuncionario
import br.com.astrosoft.produto.viewmodel.expedicao.ITabNotaTipo
import br.com.astrosoft.produto.viewmodel.expedicao.TabNotaTipoViewModel
import com.github.mvysny.karibudsl.v10.button
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

class TabNotaTipo(val viewModel: TabNotaTipoViewModel) : TabPanelGrid<NotaSaida>(NotaSaida::class), ITabNotaTipo {
  private var dlgProduto: DlgProdutosTipo? = null
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
      value = tiposNota.firstOrNull {
        it == ETipoNotaFiscal.ENTRE_FUT
      } ?: tiposNota.firstOrNull()

      this.setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateView()
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
    button("Relatório") {
      this.icon = VaadinIcon.PRINT.create()
      addClickListener {
        viewModel.print()
      }
    }
  }

  override fun Grid<NotaSaida>.gridPanel() {
    this.addClassName("styling")
    this.format()
    this.selectionMode = Grid.SelectionMode.MULTI

    colunaNFLoja()
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosTipo(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaCD()
    colunaRota()
    colunaNFNota()
    colunaNFData()
    colunaHora()
    colunaPedido()
    colunaNFCliente()
    colunaNomeCliente()
    //colunaNomeVendedor()
    colunaNFValor()
    colunaNFEntregaRetira()
    colunaNFSituacao()

    this.setPartNameGenerator {
      val countImp = it.countImp ?: 0
      when {
        countImp > 0     -> "amarelo"
        else             -> null
      }
    }
  }

  override fun filtro(marca: EMarcaNota): FiltroNota {
    return FiltroNota(
      marca = marca,
      tipoNota = cmbNota.value ?: ETipoNotaFiscal.TODOS,
      loja = cmbLoja.value?.no ?: 0,
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      localizacaoNota = listOf("CD5A"),
      pesquisa = edtPesquisa.value ?: "",
    )
  }

  override fun updateNotas(notas: List<NotaSaida>) {
    updateGrid(notas)
  }

  override fun findNota(): NotaSaida? {
    return dlgProduto?.nota
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelecionados(): List<ProdutoNFS> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun formTransportado(nota: NotaSaida) {
    val form = FormFuncionario(numeroI = nota.empnoMotorista, dataI = nota.entrega)
    DialogHelper.showForm(caption = "Transportado Por", form = form) {
      viewModel.transportadoNota(nota, form.numero, form.data)
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.notaTipo == true
  }

  override val label: String
    get() = "CD5A"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraNotaTermica?.toList().orEmpty()
  }
}