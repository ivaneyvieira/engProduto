package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.vendaRef.ITabNotaCancelada
import br.com.astrosoft.produto.viewmodel.vendaRef.TabNotaCanceladaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabNotaCancelada(val viewModel: TabNotaCanceladaViewModel) :
    TabPanelGrid<NotaSolicitaCancelar>(NotaSolicitaCancelar::class), ITabNotaCancelada {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbNota: Select<ETipoNotaFiscal>
  private lateinit var cmbAutoriza: Select<ENotaAutorizada>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtPdv: IntegerField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private var dlgProduto: DlgProdutosSolicitaCancelar? = null
  
  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaVale != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaVale ?: 0) ?: Loja.lojaZero
  }
  
  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.impressoraDev.orEmpty().toList()
  }
  
  override fun HorizontalLayout.toolBarConfig() {
    cmbLoja = select("Loja") {
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient) viewModel.updateView()
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
      value = ETipoNotaFiscal.TODOS
      
      this.setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
        if (it.isFromClient) {
          viewModel.updateView()
        }
      }
    }
    cmbAutoriza = select {
      setItems(ENotaAutorizada.entries)
      this.setItemLabelGenerator {
        it.descricao
      }
      this.value = ENotaAutorizada.NAO
      addValueChangeListener {
        if (it.isFromClient) {
          viewModel.updateView()
        }
      }
    }
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.LAZY
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtPdv = integerField("PDV") {
      this.width = "3rem"
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      valueChangeMode = ValueChangeMode.LAZY
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data inicial") {
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
    button("Relatorio") {
      icon = VaadinIcon.PRINT.create()
      onClick {
        viewModel.imprimeRelatorio()
      }
    }
    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "vendas") {
      val vendas = itensSelecionados()
      viewModel.geraPlanilha(vendas)
    }
  }
  
  override fun Grid<NotaSolicitaCancelar>.gridPanel() {
    this.addClassName("styling")
    this.selectionMode = Grid.SelectionMode.MULTI
    
    addColumnSeq("Seq")
    columnGrid(NotaSolicitaCancelar::loja, header = "Loja")
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosSolicitaCancelar(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(NotaSolicitaCancelar::pedido, header = "Pedido")
    columnGrid(NotaSolicitaCancelar::pdv, header = "PDV")
    columnGrid(
      NotaSolicitaCancelar::data, header = "Data"
    )
    columnGrid(NotaSolicitaCancelar::nota, header = "NF")
    addColumnButton(
      iconButton = VaadinIcon.SIGN_IN,
      tooltip = "Autoriza Solicitação",
      header = "Solicitação",
    ) { nota ->
      execSolicitacoes(nota)
    }
    addColumnButton(iconButton = VaadinIcon.TRASH, tooltip = "Desfaz", header = "Desfaz") { nota ->
      execDesfazSolicitacoes(nota)
    }
    columnGrid(NotaSolicitaCancelar::motivoDescricao, header = "Motivo")
    columnGrid(NotaSolicitaCancelar::loginCancel, header = "Autorizado")
    columnGrid(NotaSolicitaCancelar::uf, header = "UF")
    columnGrid(NotaSolicitaCancelar::tipoNotaSaida, header = "Tipo NF")
    columnGrid(
      NotaSolicitaCancelar::hora, header = "Hora"
    )
    columnGrid(NotaSolicitaCancelar::numMetodo, header = "Met")
    columnGrid(
      NotaSolicitaCancelar::nomeMetodo, header = "Nome Met"
    )
    columnGrid(
      NotaSolicitaCancelar::documento, header = "Documento"
    )
    columnGrid(
      NotaSolicitaCancelar::tipoPgto, header = "Tipo Pgto"
    ) {
      this.setFooter(Html("<b><font size=4>Total</font></b>"))
    }
    val valorCol = columnGrid(
      NotaSolicitaCancelar::valor, header = "Valor NF"
    )
    columnGrid(NotaSolicitaCancelar::cliente, header = "Cód Cli")
    columnGrid(NotaSolicitaCancelar::nomeCliente, header = "Nome Cliente").expand()
    columnGrid(NotaSolicitaCancelar::vendedor, header = "Vendedor").expand()
    
    
    this.dataProvider.addDataProviderListener {
      val list = it.source.fetchAll()
      val totalValor = list.groupBy { nota ->
        "${nota.loja} ${nota.pdv} ${nota.transacao}"
      }.values.sumOf { t -> t.firstOrNull()?.valor ?: 0.0 }
      val totalValorTipo = list.sumOf { t -> t.valorTipo ?: 0.0 }
      valorCol.setFooter(Html("<b><font size=4>${totalValor.format()}</font></b>")) //valorTipoCol.setFooter(Html("<b><font size=4>${totalValorTipo.format()}</font></b>"))
    }
  }
  
  fun execDesfazSolicitacoes(nota: NotaSolicitaCancelar) {
    viewModel.desfazSolicitacao(nota)
  }
  
  private fun execSolicitacoes(nota: NotaSolicitaCancelar) {
    val form = FormSolicitacaoCancelamento(nota)
    
    DialogHelper.showForm(caption = "Autoriza Devolução", form = form) {
      val solicitacaoCancelamento = form.solicitacaoCancelamento()
      viewModel.autorizaSolicitacao(nota, solicitacaoCancelamento)
    }
  }
  
  override fun filtro(): FiltroSolicitaCancelar {
    return FiltroSolicitaCancelar(
      loja = cmbLoja.value?.no ?: 0,
      pdv = edtPdv.value ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      tipoNota = cmbNota.value ?: ETipoNotaFiscal.TODOS,
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      autorizada = cmbAutoriza.value ?: ENotaAutorizada.NAO
    )
  }
  
  override fun updateNotas(list: List<NotaSolicitaCancelar>) {
    this.updateGrid(list)
  }
  
  override fun itensNotasSelecionados(): List<NotaSolicitaCancelar> {
    return itensSelecionados()
  }
  
  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.tabSolicitaCancelar == true
  }
  
  override val label: String
    get() = "Nota Cancelada"
  
  override fun updateComponent() {
    viewModel.updateView()
  }
}