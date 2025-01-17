package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.EEstoqueList
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
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_forn
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_grade
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_qttyInv
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

  override fun isAuthorized() = true

  override val label: String
    get() = "Validade"

  override fun HorizontalLayout.addAditionaisFields() {
    cmbEstoqueFiltro = select("Estoque") {
      this.width = "100px"
      setItems(EEstoqueList.entries)
      value = EEstoqueList.TODOS
      this.setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtSaldo = integerField("Saldo") {
      this.isAutofocus = true
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      this.value = 0
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
    val user = AppConfig.userLogin() as? UserSaci

    this.selectionMode = Grid.SelectionMode.MULTI

    this.withEditor(
      Produtos::class,
      openEditor = {
        this.focusEditor(Produtos::qtty01)
      },
      closeEditor = {
        viewModel.salvaValidades(it.bean)
      })

    this.shiftSelect()
    addColumnSeq("Seq")
    if (lojaProduto() == 0) {
      addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { produto ->
        val dlgProduto = DlgProdutosValidade(viewModel, produto)
        dlgProduto.showDialog {
          viewModel.updateView()
        }
      }
    }
    produto_codigo()
    produto_descricao()
    produto_grade()
    produto_Unidade()
    if (user?.admin == true) {
      produto_total()
    }
    produto_quantVenda()
    produto_val()

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

    produto_qttyInv()

    columnGrid(Produtos::qtty01, "QTD 1").integerFieldEditor()
    columnGrid(Produtos::venc01, "Vence 1", width = "80px") {
      this.setComparator(Comparator.comparingInt { produto -> produto.venc01.toMesAno() })
    }.mesAnoFieldEditor()

    columnGrid(Produtos::qtty02, "QTD 2").integerFieldEditor()
    columnGrid(Produtos::venc02, "Vence 2", width = "80px") {
      this.setComparator(Comparator.comparingInt { produto -> produto.venc02.toMesAno() })
    }.mesAnoFieldEditor()

    columnGrid(Produtos::qtty03, "QTD 3").integerFieldEditor()
    columnGrid(Produtos::venc03, "Vence 3", width = "80px") {
      this.setComparator(Comparator.comparingInt { produto -> produto.venc03.toMesAno() })
    }.mesAnoFieldEditor()

    columnGrid(Produtos::qtty04, "QTD 4").integerFieldEditor()
    columnGrid(Produtos::venc04, "Vence 4", width = "80px") {
      this.setComparator(Comparator.comparingInt { produto -> produto.venc04.toMesAno() })
    }.mesAnoFieldEditor()

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

  fun String?.toMesAno(): Int {
    this ?: return 0
    val mes = this.substring(0, 2).toIntOrNull() ?: return 0
    val ano = this.substring(3, 5).toIntOrNull() ?: return 0
    return mes + (ano + 2000) * 100
  }
}
