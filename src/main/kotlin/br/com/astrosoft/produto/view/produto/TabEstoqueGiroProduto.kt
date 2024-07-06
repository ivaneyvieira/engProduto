package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.shiftSelect
import br.com.astrosoft.produto.model.beans.EEstoqueList
import br.com.astrosoft.produto.model.beans.Produtos
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.produto.ITabEstoqueGiroViewModel
import br.com.astrosoft.produto.viewmodel.produto.TabEstoqueGiroViewModel
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_DS_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_MF_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_MR_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_Ncm
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_PK_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_Rotulo
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_TM_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_Unidade
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_abrev
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_cl
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_codigo
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_descricao
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_total
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_forn
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_grade
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_quantCompra
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_quantVenda
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_tipo
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_tributacao
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

class TabEstoqueGiroProduto(viewModel: TabEstoqueGiroViewModel) :
  TabAbstractProduto<ITabEstoqueGiroViewModel>(viewModel, showDatas = false), ITabEstoqueGiroViewModel {
  private lateinit var cmbEstoqueFiltro: Select<EEstoqueList>
  private lateinit var edtSaldo: IntegerField

  override fun isAuthorized() = (AppConfig.userLogin() as? UserSaci)?.produtoEstoqueGiro ?: false

  override val label: String
    get() = "Giro"

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
  }

  override fun Grid<Produtos>.colunasGrid() {
    this.setSelectionMode(Grid.SelectionMode.MULTI)
    this.shiftSelect()
    addColumnSeq("Seq")
    produto_codigo()
    produto_descricao()
    produto_grade()
    produto_Unidade()
    produto_val()
    produto_total()
    produto_quantCompra()
    produto_quantVenda()
    produto_DS_TT()
    produto_MR_TT()
    produto_MF_TT()
    produto_PK_TT()
    produto_TM_TT()
    produto_forn()
    produto_abrev()
    produto_tributacao()
    produto_Rotulo()
    produto_tipo()
    produto_cl()
    produto_Ncm()
  }


  override fun estoque(): EEstoqueList {
    return cmbEstoqueFiltro.value ?: EEstoqueList.TODOS
  }

  override fun saldo(): Int {
    return edtSaldo.value ?: 0
  }

  override fun temValidade(): Boolean {
    return false
  }
}
