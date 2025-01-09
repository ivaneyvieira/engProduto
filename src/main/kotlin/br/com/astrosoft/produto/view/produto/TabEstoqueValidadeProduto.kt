package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.dateFieldEditor
import br.com.astrosoft.framework.view.vaadin.helper.focusEditor
import br.com.astrosoft.framework.view.vaadin.helper.integerFieldEditor
import br.com.astrosoft.framework.view.vaadin.helper.shiftSelect
import br.com.astrosoft.framework.view.vaadin.helper.withEditor
import br.com.astrosoft.produto.model.beans.EEstoqueList
import br.com.astrosoft.produto.model.beans.Produtos
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.model.beans.Validade
import br.com.astrosoft.produto.viewmodel.produto.ITabEstoqueValidadeViewModel
import br.com.astrosoft.produto.viewmodel.produto.TabEstoqueValidadeViewModel
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_DS_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_MF_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_MR_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_PK_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_TM_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_Unidade
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_abrev
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_codigo
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_descricao
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_forn
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_grade
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_quantVenda
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_total
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_val
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.data.value.ValueChangeMode

class TabEstoqueValidadeProduto(viewModel: TabEstoqueValidadeViewModel) :
  TabAbstractProduto<ITabEstoqueValidadeViewModel>(viewModel, showDatas = false), ITabEstoqueValidadeViewModel {
  private lateinit var cmbEstoqueFiltro: Select<EEstoqueList>
  private lateinit var edtSaldo: IntegerField

  override fun isAuthorized() = (AppConfig.userLogin() as? UserSaci)?.produtoEstoqueValidade ?: false

  override val label: String
    get() = "Validade"

  override fun HorizontalLayout.addAditionaisFields() {
    cmbEstoqueFiltro = select("Estoque") {
      setItems(EEstoqueList.entries)
      value = EEstoqueList.TODOS
      this.setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
        viewModel.updateView()
      }
      this.width = "8em"
    }

    edtSaldo = integerField("Saldo") {
      this.isAutofocus = true
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      this.value = 1
      addValueChangeListener {
        viewModel.updateView()
      }
      this.width = "5em"
    }

    button("Relatório") {
      icon = VaadinIcon.PRINT.create()
      this.addClickListener {
        viewModel.geraRelatorio()
      }
    }

    edtTributacao.isVisible = false
    edtType.isVisible = false
    edtCl.isVisible = false
    edtCompra.isVisible = false
    edtCompra.value = null
  }

  override fun Grid<Produtos>.colunasGrid() {
    this.setSelectionMode(Grid.SelectionMode.MULTI)

    this.withEditor(Produtos::class,
      openEditor = {
        this.focusEditor(Produtos::qtty01)
      },
      closeEditor = {
        viewModel.salvaValidades(it.bean)
      })

    this.shiftSelect()
    addColumnSeq("Seq")
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { produto ->
      val dlgProduto = DlgProdutosValidade(viewModel, produto)
      dlgProduto.showDialog {
        viewModel.updateView()
      }
    }
    produto_codigo()
    produto_descricao()
    produto_grade()
    produto_Unidade()
    produto_forn()
    produto_abrev()
    produto_total()
    produto_quantVenda()
    produto_val()

    val user = AppConfig.userLogin() as? UserSaci

    val lojaProduto = user?.lojaProduto ?: 0

    if (lojaProduto == 2 || lojaProduto == 0) {
      produto_DS_TT()
    }
    if (lojaProduto == 3 || lojaProduto == 0) {
      produto_MR_TT()
    }
    if (lojaProduto == 4 || lojaProduto == 0) {
      produto_MF_TT()
    }
    if (lojaProduto == 5 || lojaProduto == 0) {
      produto_PK_TT()
    }
    if (lojaProduto == 8 || lojaProduto == 0) {
      produto_TM_TT()
    }

    columnGrid(Produtos::qtty01, "QTD 1").integerFieldEditor()
    columnGrid(Produtos::venc01, "Vence 1").dateFieldEditor()
    columnGrid(Produtos::qtty02, "QTD 2").integerFieldEditor()
    columnGrid(Produtos::venc02, "Vence 2").dateFieldEditor()
    columnGrid(Produtos::qtty03, "QTD 3").integerFieldEditor()
    columnGrid(Produtos::venc03, "Vence 3").dateFieldEditor()
    columnGrid(Produtos::qtty04, "QTD 4").integerFieldEditor()
    columnGrid(Produtos::venc04, "Vence 4").dateFieldEditor()
  }

  override fun estoque(): EEstoqueList {
    return cmbEstoqueFiltro.value ?: EEstoqueList.TODOS
  }

  override fun saldo(): Int {
    return edtSaldo.value ?: 0
  }

  override fun temValidade(): Boolean {
    return true
  }
}
