package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.vendaRef.ITabEntFut
import br.com.astrosoft.produto.viewmodel.vendaRef.TabEntFutViewModel
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

class TabEntFut(val viewModel: TabEntFutViewModel) : TabPanelGrid<NotaVendaRef>(NotaVendaRef::class), ITabEntFut {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbTipoNF: Select<ETipoNFEntFut>
  private lateinit var cmbTemEntrega: Select<ETemEntrega>
  private lateinit var cmbImpressao: Select<EImpressao>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtPdv: IntegerField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private var dlgProduto: DlgProdutosEntFut? = null
  
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
      this.width = "7rem"
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient) viewModel.updateView()
      }
    }
    init()
    edtPesquisa = textField("Pesquisa") {
      this.width = "12rem"
      valueChangeMode = ValueChangeMode.LAZY
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    cmbTipoNF = select("Tipo NF") {
      this.width = "7rem"
      this.setItems(ETipoNFEntFut.entries)
      this.value = ETipoNFEntFut.Entrega
      this.setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    cmbTemEntrega = select("NF Ent") {
      this.width = "5rem"
      this.setItems(ETemEntrega.entries)
      this.value = ETemEntrega.NAO
      this.setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    cmbImpressao = select("Impresso") {
      this.width = "7rem"
      this.setItems(EImpressao.entries)
      this.value = EImpressao.NAO
      this.setItemLabelGenerator {
        it.descricao
      }
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
      this.width = "7rem"
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      this.width = "7rem"
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    
    button("Romaneio") {
      icon = VaadinIcon.PRINT.create()
      onClick {
        viewModel.imprimeRomaneio()
      }
    }
    
    button("Relatório") {
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
  
  override fun Grid<NotaVendaRef>.gridPanel() {
    this.addClassName("styling")
    this.selectionMode = Grid.SelectionMode.MULTI
    
    addColumnSeq("Seq")
    columnGrid(NotaVendaRef::loja, header = "Loja")
    columnGrid(NotaVendaRef::tipoNome, header = "Tipo NF")
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosEntFut(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(NotaVendaRef::pedido, header = "Pedido")
    columnGrid(NotaVendaRef::pdv, header = "PDV")
    columnGrid(NotaVendaRef::area, header = "Área")
    columnGrid(NotaVendaRef::rota, header = "Rota")
    columnGrid(NotaVendaRef::data, header = "Data", width = null)
    columnGrid(NotaVendaRef::nota, header = "NF")
    columnGrid(NotaVendaRef::uf, header = "UF")
    columnGrid(NotaVendaRef::hora, header = "Hora", pattern = "HH:mm")
    columnGrid(NotaVendaRef::impressoSN, header = "Imp")
    columnGrid(NotaVendaRef::nfEntrega, header = "NF Ent")
    columnGrid(NotaVendaRef::dataEntrega, header = "Data") {
      this.setFooter(Html("<b><font size=4>Total</font></b>"))
    }
    val valorCol = columnGrid(NotaVendaRef::valor, header = "Valor NF")
    columnGrid(NotaVendaRef::cliente, header = "Cód Cli")
    columnGrid(NotaVendaRef::nomeCliente, header = "Nome Cliente").expand()
    columnGrid(NotaVendaRef::vendedor, header = "Vendedor").expand()
    columnGrid(NotaVendaRef::transacao, header = "Transação")
    columnGrid(NotaVendaRef::nomeMetodo, header = "Nome Met")
    columnGrid(NotaVendaRef::tipoPgto, header = "Tipo Pgto")
    
    this.dataProvider.addDataProviderListener {
      val list = it.source.fetchAll()
      val totalValor = list.groupBy { nota ->
        "${nota.loja} ${nota.pdv} ${nota.transacao}"
      }.values.sumOf { t ->
        t.firstOrNull()?.valor ?: 0.0
      }
      valorCol.setFooter(Html("<b><font size=4>${totalValor.format()}</font></b>")) //valorTipoCol.setFooter(Html("<b><font size=4>${totalValorTipo.format()}</font></b>"))
    }
  }
  
  override fun filtro(): FiltroNotaVendaRef {
    return FiltroNotaVendaRef(
      loja = cmbLoja.value?.no ?: 0,
      pdv = edtPdv.value ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      tipoNF = "ENTRE FUT",
      tipoNFEntFut = cmbTipoNF.value ?: ETipoNFEntFut.Todos,
      temEntrega = cmbTemEntrega.value ?: ETemEntrega.NAO,
      impresso = cmbImpressao.value ?: EImpressao.NAO
    )
  }
  
  override fun updateNotas(notas: List<NotaVendaRef>) {
    this.updateGrid(notas)
  }
  
  override fun itensNotasSelecionados(): List<NotaVendaRef> {
    return itensSelecionados()
  }
  
  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.tabEntFut == true
  }
  
  override val label: String
    get() = "Ent Fut"
  
  override fun updateComponent() {
    viewModel.updateView()
  }
}