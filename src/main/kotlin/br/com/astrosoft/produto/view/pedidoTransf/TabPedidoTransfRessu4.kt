package br.com.astrosoft.produto.view.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.pedidoTransf.ITabPedidoTransfRessu4
import br.com.astrosoft.produto.viewmodel.pedidoTransf.TabPedidoTransfRessu4ViewModel
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

class TabPedidoTransfRessu4(val viewModel: TabPedidoTransfRessu4ViewModel) : TabPanelGrid<TransfRessu4>(TransfRessu4::class),
  ITabPedidoTransfRessu4 {
  private var dlgProduto: DlgProdutosPedTransfRessu4? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

  init {
    cmbLoja.setItems(viewModel.findAllLojas())
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isVisible = user?.storeno == 0
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

  override fun Grid<TransfRessu4>.gridPanel() {
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { pedido ->
      dlgProduto = DlgProdutosPedTransfRessu4(viewModel, pedido)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(TransfRessu4::lojaOrigem, "Loja Orig")
    columnGrid(TransfRessu4::lojaDestino, "Loja Dest")
    columnGrid(TransfRessu4::cliente, "Cliente")
    columnGrid(TransfRessu4::data, "Loja Data")
    columnGrid(TransfRessu4::notaTransf, "NF Transf")
    columnGrid(TransfRessu4::valorTransf, "Valor")
    columnGrid(TransfRessu4::usuario, "Usuário")
    columnGrid(TransfRessu4::observacaoTransf, "Observação").expand()
  }

  override fun filtro(): FiltroPedidoRessu4 {
    return FiltroPedidoRessu4(
      storeno = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
    )
  }

  override fun updatePedidos(pedidos: List<TransfRessu4>) {
    updateGrid(pedidos)
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelcionados(): List<ProdutoTransfRessu4> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.pedidoTransfRessu4 == true
  }

  override val label: String
    get() = "Ressu4"

  override fun updateComponent() {
    viewModel.updateView()
  }
}