package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.produto.model.beans.ETipoNotaFiscal
import br.com.astrosoft.produto.model.beans.FiltroSolicitaCancelar
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaSolicitaCancelar
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.vendaRef.ITabSolicitaCancelar
import br.com.astrosoft.produto.viewmodel.vendaRef.TabSolicitaCancelarViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class TabSolicitaCancelar(val viewModel: TabSolicitaCancelarViewModel) :
    TabPanelGrid<NotaSolicitaCancelar>(NotaSolicitaCancelar::class), ITabSolicitaCancelar {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbNota: Select<ETipoNotaFiscal>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtPdv: IntegerField
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
      value = tiposNota.firstOrNull {
        it == ETipoNotaFiscal.ENTRE_FUT
      } ?: tiposNota.firstOrNull()
      
      this.setItemLabelGenerator {
        it.descricao
      }
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
    ) //columnGrid(NotaSolicitaCancelar::transacao, header = "Transação")
    columnGrid(NotaSolicitaCancelar::nota, header = "NF")
    columnGrid(NotaSolicitaCancelar::uf, header = "UF")
    columnGrid(NotaSolicitaCancelar::tipoNf, header = "Tipo NF")
    columnGrid(
      NotaSolicitaCancelar::hora, header = "Hora"
    ) //columnGrid(NotaSolicitaCancelar::numeroInterno, header = "NI", width = "100px")
    columnGrid(NotaSolicitaCancelar::numMetodo, header = "Met")
    columnGrid(
      NotaSolicitaCancelar::nomeMetodo, header = "Nome Met"
    ) //columnGrid(NotaSolicitaCancelar::mult, pattern = "#,##0.0000", header = "Mlt")
    columnGrid(
      NotaSolicitaCancelar::documento, header = "Documento"
    ) //columnGrid(NotaSolicitaCancelar::quantParcelas, header = "Parc")
    //columnGrid(NotaSolicitaCancelar::mediaPrazo, header = "Pz M")
    columnGrid(
      NotaSolicitaCancelar::tipoPgto, header = "Tipo Pgto"
    ) {
      this.setFooter(Html("<b><font size=4>Total</font></b>"))
    }
    val valorCol = columnGrid(
      NotaSolicitaCancelar::valor, header = "Valor NF"
    ) //val valorTipoCol = columnGrid(NotaSolicitaCancelar::valorTipo, header = "Valor TP")
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
  
  override fun filtro(): FiltroSolicitaCancelar {
    return FiltroSolicitaCancelar(
      loja = cmbLoja.value?.no ?: 0,
      pdv = edtPdv.value ?: 0,
      puspesquisa = edtPesquisa.value ?: "",
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
    get() = "Solicita Cancelar"
  
  override fun updateComponent() {
    viewModel.updateView()
  }
}