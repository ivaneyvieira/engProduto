package br.com.astrosoft.produto.view.acertoEstoque

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.AcertoEntradaNota
import br.com.astrosoft.produto.model.beans.FiltroAcertoEntrada
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.acertoEstoque.ITabAcertoEstoqueEntrada
import br.com.astrosoft.produto.viewmodel.acertoEstoque.TabAcertoEstoqueEntradaViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabAcertoEstoqueEntrada(val viewModel: TabAcertoEstoqueEntradaViewModel) :
  TabPanelGrid<AcertoEntradaNota>(AcertoEntradaNota::class),
  ITabAcertoEstoqueEntrada {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.storeno != 0
    cmbLoja.value = viewModel.findLoja(user?.storeno ?: 0) ?: Loja.lojaZero
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
      valueChangeMode = ValueChangeMode.TIMEOUT
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
  }

  override fun Grid<AcertoEntradaNota>.gridPanel() {
    this.addClassName("styling")
    columnGrid(AcertoEntradaNota::loja, header = "Loja")
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      val dlgProduto = DlgProdutosEntrada(viewModel, nota)
      dlgProduto.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(AcertoEntradaNota::ni, header = "NI")
    columnGrid(AcertoEntradaNota::notaFiscal, header = "Nota Fiscal").right()
    columnGrid(AcertoEntradaNota::dataEmissao, header = "Data")
    columnGrid(AcertoEntradaNota::fornecedor, header = "Cód For")
    columnGrid(AcertoEntradaNota::nomeFornecedor, header = "Fornecedor").expand()
    columnGrid(AcertoEntradaNota::observacao, header = "Observação").expand()
    columnGrid(AcertoEntradaNota::valor, header = "Valor", width = "180px") {
      this.grid.dataProvider.addDataProviderListener {
        val total = listBeans().sumOf { it.valor ?: 0.0 }
        setFooter(Html("<b><font size=4>Total R$ &nbsp;&nbsp;&nbsp;&nbsp; ${total.format()}</font></b>"))
      }
    }
  }

  override fun filtro(): FiltroAcertoEntrada {
    return FiltroAcertoEntrada(
      loja = cmbLoja.value?.no ?: 0,
      query = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
    )
  }

  override fun updateNotas(notas: List<AcertoEntradaNota>) {
    updateGrid(notas)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.acertoEntrada == true
  }

  override val label: String
    get() = "Entrada"

  override fun updateComponent() {
    viewModel.updateView()
  }
}