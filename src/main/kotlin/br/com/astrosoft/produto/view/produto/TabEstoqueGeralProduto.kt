package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.shiftSelect
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.produto.ITabEstoqueTotalViewModel
import br.com.astrosoft.produto.viewmodel.produto.TabEstoqueTotalViewModel
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_DS_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_Localizacao
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_MF_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_MR_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_PK_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_Rotulo
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_TM_TT
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_Unidade
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_abrev
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_cl
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_codBar
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_codigo
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_descricao
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_estoque
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_forn
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_grade
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_qtPedido
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_quantCompra
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_quantVenda
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_tipo
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_tributacao
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.data.value.ValueChangeMode

class TabEstoqueTotalProduto(viewModel: TabEstoqueTotalViewModel) :
  TabAbstractProduto<ITabEstoqueTotalViewModel>(viewModel, showDatas = false), ITabEstoqueTotalViewModel {
  private lateinit var cmbEstoque: Select<EEstoqueTotal>
  private lateinit var cmbEstoqueFiltro: Select<EEstoqueList>
  private lateinit var edtSaldo: IntegerField
  private lateinit var cmbLoja: Select<Loja>

  override fun isAuthorized() = (AppConfig.userLogin() as? UserSaci)?.produtoEstoqueTotal ?: false

  override val label: String
    get() = "Estoque Total"

  override fun HorizontalLayout.addAditionaisFields() {
    cmbLoja = select("Loja") {
      val lojaTodas = Loja(0, "Todas", "Todas")
      val lojas = listOf(lojaTodas) + viewModel.allLojas()
      setItems(lojas)
      value = lojaTodas
      this.setItemLabelGenerator {
        it.sname
      }
      addValueChangeListener {
        viewModel.updateView()
      }
      this.width = "5em"
    }
    cmbEstoque = select("Estoque Total") {
      setItems(EEstoqueTotal.values().toList())
      value = EEstoqueTotal.TODOS
      this.setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
        viewModel.updateView()
      }
      this.width = "8em"
    }

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
    produto_estoque()
    produto_quantCompra()
    produto_quantVenda()
    produto_DS_TT()
    produto_MR_TT()
    produto_MF_TT()
    produto_PK_TT()
    produto_TM_TT()
    produto_qtPedido()
    produto_Localizacao()
    produto_forn()
    produto_abrev()
    produto_tributacao()
    produto_Rotulo()
    produto_tipo()
    produto_cl()
    produto_codBar()
  }

  override fun estoqueTotal(): EEstoqueTotal {
    return cmbEstoque.value ?: EEstoqueTotal.TODOS
  }

  override fun lojaEstoque(): Int {
    return cmbLoja.value?.no ?: 0
  }

  override fun estoque(): EEstoqueList {
    return cmbEstoqueFiltro.value ?: EEstoqueList.TODOS
  }

  override fun saldo(): Int {
    return edtSaldo.value ?: 0
  }
}
