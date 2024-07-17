package br.com.astrosoft.produto.view.notaSaida

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaAgendado
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaEntrega
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaHora
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaImpresso
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaMotoristaSing
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFCliente
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFData
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFEntregaRetira
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFLoja
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFNota
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFSituacao
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFTipo
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFValor
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNFVendedor
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNomeCliente
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaNomeVendedor
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaPedido
import br.com.astrosoft.produto.view.notaSaida.columns.NotaColumns.colunaRota
import br.com.astrosoft.produto.viewmodel.notaSaida.ITabNotaRota
import br.com.astrosoft.produto.viewmodel.notaSaida.TabNotaRotaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.selectionMode
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabNotaRota(val viewModel: TabNotaRotaViewModel) : TabPanelGrid<NotaSaida>(NotaSaida::class), ITabNotaRota {
  private var dlgProduto: DlgProdutosRota? = null
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
      value = if(tiposNota.contains(ETipoNotaFiscal.ENTRE_FUT)) {
        ETipoNotaFiscal.ENTRE_FUT
      } else {
        tiposNota.firstOrNull()
      }

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

    addColumnSeq("Item")
    colunaNFLoja()
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosRota(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaRota()
    colunaPedido()
    colunaNFNota()
    colunaNFData()
    colunaHora()
    colunaEntrega()
    colunaMotoristaSing()
    colunaImpresso()
    colunaNFCliente()
    colunaNomeCliente()
    colunaNomeVendedor()
    colunaNFValor()
    colunaNFEntregaRetira()
    colunaNFSituacao()

    this.setPartNameGenerator {
      val countEnt = it.countEnt ?: 0
      val countImp = it.countImp ?: 0
      val cancelada = it.cancelada ?: "N"
      when {
        cancelada == "S" -> "vermelho"

        countImp > 0     -> "azul"

        countEnt > 0     -> "amarelo"

        else             -> null
      }
    }
  }

  override fun filtro(marca: EMarcaNota): FiltroNota {
    return FiltroNota(
      marca = marca,
      tipoNota = cmbNota.value ?: ETipoNotaFiscal.TODOS,
      loja = cmbLoja.value?.no ?: 0,
      dataInicial = null,
      dataFinal = null,
      dataEntregaInicial = edtDataInicial.value,
      dataEntregaFinal = edtDataFinal.value,
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

  override fun produtosSelcionados(): List<ProdutoNFS> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.notaRota == true
  }

  override val label: String
    get() = "Rota"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraNotaTermica?.toList().orEmpty()
  }
}