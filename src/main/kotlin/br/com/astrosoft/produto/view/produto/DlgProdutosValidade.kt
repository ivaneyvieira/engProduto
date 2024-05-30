package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.columnGrid
import br.com.astrosoft.framework.view.vaadin.columnGroup
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.ProdutoInventario
import br.com.astrosoft.produto.model.beans.Produtos
import br.com.astrosoft.produto.viewmodel.produto.ITabEstoqueValidadeViewModel
import br.com.astrosoft.produto.viewmodel.produto.TabAbstractProdutoViewModel
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosValidade(
  val viewModel: TabAbstractProdutoViewModel<ITabEstoqueValidadeViewModel>,
  val produtos: Produtos
) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoInventario::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val codigo = produtos.codigo ?: 0
    val descricao = produtos.descricao ?: ""
    val grade = produtos.grade ?: ""

    form = SubWindowForm("$codigo $descricao $grade", toolBar = {
    }, onClose = {
      onClose()
    }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos()
      }
    }
    form?.open()
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      this.format()
      setSizeFull()

      val defaultWidth = "80px"

      columnGroup("Dados Entrada") {
        this.columnGrid(ProdutoInventario::dataEntrada, "Data") {
          this.setFooter("Total")
        }
        this.columnGrid(ProdutoInventario::estoqueDS, "DS", width = defaultWidth)
        this.columnGrid(ProdutoInventario::estoqueMR, "MR", width = defaultWidth)
        this.columnGrid(ProdutoInventario::estoqueMF, "MF", width = defaultWidth)
        this.columnGrid(ProdutoInventario::estoquePK, "PK", width = defaultWidth)
        this.columnGrid(ProdutoInventario::estoqueTM, "TM", width = defaultWidth)
      }

      columnGroup("Estoque / Vencimento") {
        this.columnGrid(ProdutoInventario::estoqueTotal, "Sist", width = defaultWidth)
        this.columnGrid(ProdutoInventario::saldo, "Saldo", width = defaultWidth)
        this.columnGrid(ProdutoInventario::vencimentoStr, "Venc", width = defaultWidth)
      }

      columnGroup("Saldo por Loja") {
        this.columnGrid(ProdutoInventario::saldoDS, "DS", width = defaultWidth)
        this.columnGrid(ProdutoInventario::saldoMR, "MR", width = defaultWidth)
        this.columnGrid(ProdutoInventario::saldoMF, "MF", width = defaultWidth)
        this.columnGrid(ProdutoInventario::saldoPK, "PK", width = defaultWidth)
        this.columnGrid(ProdutoInventario::saldoTM, "TM", width = defaultWidth)
      }

      columnGroup("Dados Venda") {
        this.columnGrid(ProdutoInventario::saidaDS, "DS", width = defaultWidth)
        this.columnGrid(ProdutoInventario::saidaMR, "MR", width = defaultWidth)
        this.columnGrid(ProdutoInventario::saidaMF, "MF", width = defaultWidth)
        this.columnGrid(ProdutoInventario::saidaPK, "PK", width = defaultWidth)
        this.columnGrid(ProdutoInventario::saidaTM, "TM", width = defaultWidth)
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<ProdutoInventario> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = produtos.produtosValidade()
    gridDetail.setItems(listProdutos)
    gridDetail.getColumnBy(ProdutoInventario::estoqueDS).setFooter(listProdutos.sumOf { it.estoqueDS ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventario::estoqueMR).setFooter(listProdutos.sumOf { it.estoqueMR ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventario::estoqueMF).setFooter(listProdutos.sumOf { it.estoqueMF ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventario::estoquePK).setFooter(listProdutos.sumOf { it.estoquePK ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventario::estoqueTM).setFooter(listProdutos.sumOf { it.estoqueTM ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventario::saldo).setFooter(listProdutos.sumOf { it.saldo }.format())
    gridDetail.getColumnBy(ProdutoInventario::saldoDS).setFooter(listProdutos.sumOf { it.saldoDS }.format())
    gridDetail.getColumnBy(ProdutoInventario::saldoMR).setFooter(listProdutos.sumOf { it.saldoMR }.format())
    gridDetail.getColumnBy(ProdutoInventario::saldoMF).setFooter(listProdutos.sumOf { it.saldoMF }.format())
    gridDetail.getColumnBy(ProdutoInventario::saldoPK).setFooter(listProdutos.sumOf { it.saldoPK }.format())
    gridDetail.getColumnBy(ProdutoInventario::saldoTM).setFooter(listProdutos.sumOf { it.saldoTM }.format())
    gridDetail.getColumnBy(ProdutoInventario::saidaDS).setFooter(listProdutos.sumOf { it.saidaDS ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventario::saidaMR).setFooter(listProdutos.sumOf { it.saidaMR ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventario::saidaMF).setFooter(listProdutos.sumOf { it.saidaMF ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventario::saidaPK).setFooter(listProdutos.sumOf { it.saidaPK ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventario::saidaTM).setFooter(listProdutos.sumOf { it.saidaTM ?: 0 }.format())
  }

  fun close() {
    onClose?.invoke()
    form?.close()
  }
}