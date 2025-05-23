package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.FiltroNotaVenda
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaVenda
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevVenda
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevCliVendaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabDevVenda(val viewModel: TabDevCliVendaViewModel) :
  TabPanelGrid<NotaVenda>(NotaVenda::class),
  ITabDevVenda {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

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
        if (it.isFromClient)
          viewModel.updateView()
      }
    }
    init()
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
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

  override fun Grid<NotaVenda>.gridPanel() {
    this.addClassName("styling")
    this.setSelectionMode(Grid.SelectionMode.MULTI)

    addColumnSeq("Seq")
    columnGrid(NotaVenda::loja, header = "Loja")
    columnGrid(NotaVenda::pedido, header = "Pedido")
    columnGrid(NotaVenda::pdv, header = "PDV")
    columnGrid(NotaVenda::data, header = "Data")
    columnGrid(NotaVenda::transacao, header = "Transação")
    columnGrid(NotaVenda::nota, header = "NF")
    columnGrid(NotaVenda::uf, header = "UF")
    columnGrid(NotaVenda::tipoNf, header = "Tipo NF")
    columnGrid(NotaVenda::hora, header = "Hora")
    columnGrid(NotaVenda::numeroInterno, header = "NI", width = "100px")
    columnGrid(NotaVenda::tipoPgto, header = "Tipo Pgto") {
      this.setFooter(Html("<b><font size=4>Total</font></b>"))
    }
    val valorCol = columnGrid(NotaVenda::valor, header = "Valor NF")
    val valorTipoCol = columnGrid(NotaVenda::valorTipo, header = "Valor TP")
    columnGrid(NotaVenda::cliente, header = "Cód Cli")
    columnGrid(NotaVenda::nomeCliente, header = "Nome Cliente").expand()
    columnGrid(NotaVenda::vendedor, header = "Vendedor").expand()

    this.dataProvider.addDataProviderListener {
      val list = it.source.fetchAll()
      val totalValor = list.groupBy { nota ->
        "${nota.loja} ${nota.pdv} ${nota.transacao}"
      }
        .values.sumOf { t -> t.firstOrNull()?.valor ?: 0.0 }
      val totalValorTipo = list.sumOf { t -> t.valorTipo ?: 0.0 }
      valorCol.setFooter(Html("<b><font size=4>${totalValor.format()}</font></b>"))
      valorTipoCol.setFooter(Html("<b><font size=4>${totalValorTipo.format()}</font></b>"))
    }
  }

  override fun filtro(): FiltroNotaVenda {
    return FiltroNotaVenda(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
    )
  }

  override fun updateNotas(notas: List<NotaVenda>) {
    this.updateGrid(notas)
  }

  override fun itensNotasSelecionados(): List<NotaVenda> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devCliVenda == true
  }

  override val label: String
    get() = "Venda"

  override fun updateComponent() {
    viewModel.updateView()
  }
}