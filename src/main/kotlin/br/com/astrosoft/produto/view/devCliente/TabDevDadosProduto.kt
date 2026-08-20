package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.DadosDevProduto
import br.com.astrosoft.produto.model.beans.FiltroDadosDev
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevDadosProduto
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevDadosProdutoViewModel
import com.flowingcode.vaadin.addons.gridhelpers.GridHelper
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.asc
import com.github.mvysny.kaributools.sort
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabDevDadosProduto(val viewModel: TabDevDadosProdutoViewModel) :
  TabPanelGrid<DadosDevProduto>(DadosDevProduto::class),
  ITabDevDadosProduto {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtData: DatePicker
  private lateinit var edtPesquisa: TextField

  fun init() {
    val listLojas = viewModel.findAllLojas()
    cmbLoja.setItems(listLojas)
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaVale != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaVale ?: 0) ?: listLojas.firstOrNull()
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
    edtData = datePicker("Data") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    button("Impressão") {
      icon = VaadinIcon.PRINT.create()
      onClick {
        viewModel.imprimeProdutos()
      }
    }
  }

  override fun Grid<DadosDevProduto>.gridPanel() {
    this.addClassName("styling")
    this.selectionMode = Grid.SelectionMode.MULTI
    columnGrid(DadosDevProduto::codigo, header = "Código").right()
    columnGrid(DadosDevProduto::descricao, header = "Descrição").expand()
    columnGrid(DadosDevProduto::grade, header = "Grade")
    columnGrid(DadosDevProduto::localizacao, header = "CD")
    columnGrid(DadosDevProduto::quantidadeTotal, header = "Quantidade")
    columnGrid(DadosDevProduto::obsNotaVenda, header = "Observação").expand()
    columnGrid(DadosDevProduto::produtoTipo, header = "Tipo")
    columnGrid(DadosDevProduto::userEntrega, header = "Entregador")
    columnGrid(DadosDevProduto::userRecebimento, header = "Recebedor")
    columnGrid(DadosDevProduto::ni, header = "NI")
    columnGrid(DadosDevProduto::nfDevolucao, header = "NF Dev")
    columnGrid(DadosDevProduto::dataDevolucao, header = "Data")
    GridHelper.setEnhancedSelectionEnabled(this, true)

    this.sort(DadosDevProduto::localizacao.asc, DadosDevProduto::descricao.asc)
  }

  override fun filtro(): FiltroDadosDev {
    val user = AppConfig.userLogin() as? UserSaci
    return FiltroDadosDev(
      loja = cmbLoja.value?.no ?: 0,
      dataInicial = edtData.value ?: LocalDate.now(),
      dataFinal = edtData.value ?: LocalDate.now(),
      pesquisa = edtPesquisa.value ?: "",
      localizacao = user?.localizacaoDev ?: setOf("TODOS"),
      devolvido = true,
      impresso = null
    )
  }

  override fun reloadGrid() {
    gridPanel.dataProvider.refreshAll()
  }

  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.impressoraDev.orEmpty().toList()
  }

  override fun updateProdutos(produtos: List<DadosDevProduto>) {
    updateGrid(produtos)
  }

  override fun produtosSelecionados(): List<DadosDevProduto> {
    return this.itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devDadosProduto == true
  }

  override val label: String
    get() = "Prd Devolvido"

  override fun updateComponent() {
    viewModel.updateView()
  }
}