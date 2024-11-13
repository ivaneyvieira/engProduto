package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.devolucao.model.beans.FiltroNotaEntradaXML
import br.com.astrosoft.devolucao.model.beans.NotaEntradaXML
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.recebimento.ITabRecebimentoXML
import br.com.astrosoft.produto.viewmodel.recebimento.TabRecebimentoXml
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
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
class TabRecebimentoXML(val viewModel: TabRecebimentoXml) : ITabRecebimentoXML,
  TabPanelGrid<NotaEntradaXML>(NotaEntradaXML::class) {
  private lateinit var edtNota: IntegerField
  private lateinit var edtFornecedorNota: TextField
  private lateinit var edtQuery: TextField
  private lateinit var edtDataF: DatePicker
  private lateinit var edtDataI: DatePicker
  private lateinit var edtCNPJ: TextField
  private lateinit var cmbLoja: Select<Loja>

  override fun getFiltro(): FiltroNotaEntradaXML {
    return FiltroNotaEntradaXML(
      loja = cmbLoja.value,
      dataInicial = edtDataI.value ?: LocalDate.now(),
      dataFinal = edtDataF.value ?: LocalDate.now(),
      numero = edtNota.value ?: 0,
      cnpj = edtCNPJ.value ?: "",
      fornecedor = edtFornecedorNota.value ?: "",
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
      TODO()
    }
    columnGrid(NotaEntradaXML::loja) {
      this.setHeader("Loja")
    }
    columnGrid(NotaEntradaXML::notaFiscal) {
      this.setHeader("Número")
      this.isResizable = true
      this.right()
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
