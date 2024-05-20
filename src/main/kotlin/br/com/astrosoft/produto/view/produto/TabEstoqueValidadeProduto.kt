package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.shiftSelect
import br.com.astrosoft.produto.model.beans.EEstoqueList
import br.com.astrosoft.produto.model.beans.MesAno
import br.com.astrosoft.produto.model.beans.Produtos
import br.com.astrosoft.produto.model.beans.UserSaci
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
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_estoque
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_forn
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_grade
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_quantCompra
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_quantVenda
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
    this.shiftSelect()
    addColumnSeq("Seq")
    produto_codigo()
    produto_descricao()
    produto_grade()
    produto_Unidade()
    produto_estoque()
    produto_val()
    columnGrid(Produtos::fabricacao, "Fab", pattern = "MM/yy", width="80px")
    columnGrid(Produtos::vencimento, "Venc", pattern = "MM/yy", width="80px")
    produto_quantVenda()
    produto_DS_TT()
    produto_MR_TT()
    produto_MF_TT()
    produto_PK_TT()
    produto_TM_TT()
    //columnGrid(Produtos::mesesFabricacao, "M Fab")
    columnGrid(Produtos::entrada, "Entrada")
    columnGrid(Produtos::nfEntrada, "NF")
    columnGrid(Produtos::dataEntrada, "Data")
    produto_forn()
    produto_abrev()
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
