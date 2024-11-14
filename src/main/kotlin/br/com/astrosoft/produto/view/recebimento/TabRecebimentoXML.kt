package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.recebimento.ITabRecebimentoXML
import br.com.astrosoft.produto.viewmodel.recebimento.TabRecebimentoXmlViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

@CssImport("./styles/gridTotal.css", themeFor = "vaadin-grid")
class TabRecebimentoXML(val viewModel: TabRecebimentoXmlViewModel) : ITabRecebimentoXML,
  TabPanelGrid<NotaEntradaXML>(NotaEntradaXML::class) {
  private var dialog: DlgXmlProduto? = null
  private lateinit var edtNota: IntegerField
  private lateinit var edtFornecedorNota: TextField
  private lateinit var edtQuery: TextField
  private lateinit var edtDataF: DatePicker
  private lateinit var edtDataI: DatePicker
  private lateinit var edtCNPJ: TextField
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbPreEntrada: Select<EPREEntradaXML>

  override fun getFiltro(): FiltroNotaEntradaXML {
    return FiltroNotaEntradaXML(
      loja = cmbLoja.value,
      dataInicial = edtDataI.value ?: LocalDate.now(),
      dataFinal = edtDataF.value ?: LocalDate.now(),
      numero = edtNota.value ?: 0,
      cnpj = edtCNPJ.value ?: "",
      fornecedor = edtFornecedorNota.value ?: "",
      preEntrada = cmbPreEntrada.value ?: EPREEntradaXML.TODOS,
      query = edtQuery.value ?: "",
    )
  }

  override fun updateList(list: List<NotaEntradaXML>) {
    updateGrid(list)
  }

  override fun HorizontalLayout.toolBarConfig() {
    cmbLoja = select("Loja") {
      val lojas = Loja.allLojas() + Loja.lojaZero
      setItems(lojas.sortedBy { it.no })
      value = Loja.lojaZero
      this.width = "100px"
      setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
        viewModel.updateViewBD()
      }
    }
    cmbPreEntrada = select("Pré Entrada") {
      setItems(EPREEntradaXML.entries)
      value = EPREEntradaXML.TODOS
      this.width = "100px"
      setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
        viewModel.updateViewBD()
      }
    }
    edtQuery = textField("Pesquisa") {
      valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000
      addValueChangeListener {
        viewModel.updateViewLocal()
      }
    }
    edtDataI = datePicker("Data Inicial") {
      localePtBr()
      addValueChangeListener {
        viewModel.updateViewBD()
      }
    }
    edtDataF = datePicker("Data Final") {
      localePtBr()
      addValueChangeListener {
        viewModel.updateViewBD()
      }
    }
    edtCNPJ = textField("CNPJ") {
      valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000
      addValueChangeListener {
        viewModel.updateViewBD()
      }
    }
    edtFornecedorNota = textField("Fornecedor Nota") {
      valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000
      addValueChangeListener {
        viewModel.updateViewBD()
      }
    }
    edtNota = integerField("Nota Fiscal") {
      valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000
      addValueChangeListener {
        viewModel.updateViewBD()
      }
    }
  }

  override fun Grid<NotaEntradaXML>.gridPanel() {
    setSelectionMode(Grid.SelectionMode.MULTI)
    addColumnSeq("Item")
    addColumnButton(iconButton = VaadinIcon.FILE_TABLE, tooltip = "Nota fiscal", header = "NF") { nota ->
      dialog = DlgXmlProduto(viewModel, nota)
      dialog?.showDialog {
        dialog = null
      }
    }

    columnGrid(NotaEntradaXML::loja) {
      this.setHeader("Loja")
    }

    columnGrid(NotaEntradaXML::pedido) {
      this.setHeader("Pedido")
      this.isResizable = true
    }

    columnGrid(NotaEntradaXML::notaFiscal) {
      this.setHeader("Número")
      this.isResizable = true
      this.right()
    }

    columnGrid(NotaEntradaXML::dataPedido) {
      this.setHeader("Data")
      this.isResizable = true
    }

    columnGrid(NotaEntradaXML::dataEmissao) {
      this.setHeader("Emissão")
      this.isResizable = true
    }

    columnGrid(NotaEntradaXML::fornecedorNota) {
      this.setHeader("Forn. Nota")
      this.isResizable = true
    }

    columnGrid(NotaEntradaXML::fornecedorCad) {
      this.setHeader("Forn. Cad")
      this.right()
      this.isResizable = true
    }

    columnGrid(NotaEntradaXML::nomeFornecedor) {
      this.setHeader("Fornecedor")
      this.isResizable = true
      this.isExpand = true
    }

    columnGrid(NotaEntradaXML::chave) {
      this.setHeader("Chave")
      this.isResizable = true
    }

    columnGrid(NotaEntradaXML::valorTotalProdutos) {
      this.setHeader("Valor Produtos")
      this.isResizable = true
    }

    columnGrid(NotaEntradaXML::valorTotal) {
      this.setHeader("Valor")
      this.isResizable = true
    }

    columnGrid(NotaEntradaXML::preEntrada) {
      this.setHeader("Pre Ent")
      this.isResizable = true
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.recebimentoXML == true
  }

  override val label: String
    get() = "XML"

  override fun updateComponent() {
    viewModel.updateViewBD()
  }
}
