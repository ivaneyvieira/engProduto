package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.recebimento.ITabReceber
import br.com.astrosoft.produto.viewmodel.recebimento.TabReceberViewModel
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabReceber(val viewModel: TabReceberViewModel) :
  TabPanelGrid<NotaRecebimento>(NotaRecebimento::class), ITabReceber {
  private var dlgProduto: DlgProdutosReceber? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaRessu != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaRessu ?: 0) ?: Loja.lojaZero
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
  }

  override fun Grid<NotaRecebimento>.gridPanel() {
    this.addClassName("styling")
    this.format()

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosReceber(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }

    columnGrid(NotaRecebimento::loja, header = "Loja")
    columnGrid(NotaRecebimento::data, header = "Data")
    columnGrid(NotaRecebimento::ni, header = "NI")
    columnGrid(NotaRecebimento::nfEntrada, header = "NF Entrada")
    columnGrid(NotaRecebimento::custno, header = "For Cad")
    columnGrid(NotaRecebimento::vendno, header = "For NF")
    columnGrid(NotaRecebimento::fornecedor, header = "Nome Fornecedor")
    columnGrid(NotaRecebimento::valorNF, header = "Valor NF")
    columnGrid(NotaRecebimento::pedComp, header = "Ped Comp")
    columnGrid(NotaRecebimento::transp, header = "Transp")
    columnGrid(NotaRecebimento::cte, header = "CTe")
    columnGrid(NotaRecebimento::volume, header = "Volume")
    columnGrid(NotaRecebimento::peso, header = "Peso")
  }

  override fun filtro(): FiltroNotaRecebimentoProduto {
    return FiltroNotaRecebimentoProduto(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      marca = EMarcaRecebimento.RECEBER,
    )
  }

  override fun updateNota(notas: List<NotaRecebimento>) {
    this.updateGrid(notas)
  }

  override fun updateProduto(produto: NotaRecebimentoProduto) {
    dlgProduto?.updateProduto(produto)
  }

  fun showDlgProdutos(nota: NotaRecebimento) {
    dlgProduto = DlgProdutosReceber(viewModel, nota)
    dlgProduto?.showDialog {
      viewModel.updateView()
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.recebimentoReceber == true
  }

  override val label: String
    get() = "Receber"

  override fun updateComponent() {
    viewModel.updateView()
  }
}