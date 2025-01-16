package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.columnGrid
import br.com.astrosoft.framework.view.vaadin.columnGroup
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.ProdutoInventarioResumo
import br.com.astrosoft.produto.model.beans.Produtos
import br.com.astrosoft.produto.viewmodel.produto.TabEstoqueValidadeLojaViewModel
import com.github.mvysny.kaributools.asc
import com.github.mvysny.kaributools.getColumnBy
import com.github.mvysny.kaributools.sort
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosValidadeLoja(
  val viewModel: TabEstoqueValidadeLojaViewModel,
  val produtos: Produtos
) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoInventarioResumo::class.java, false)

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
        this.columnGrid(ProdutoInventarioResumo::dataEntrada, "Data") {
          this.setFooter("Total")
        }
        this.columnGrid(ProdutoInventarioResumo::estoqueDS, "DS", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::estoqueMR, "MR", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::estoqueMF, "MF", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::estoquePK, "PK", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::estoqueTM, "TM", width = defaultWidth)
      }

      columnGroup("Estoque / Vencimento") {
        this.columnGrid(ProdutoInventarioResumo::estoqueTotal, "Sist", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::saldo, "Saldo", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::vencimentoStr, "Venc", width = defaultWidth) {
          this.setComparator(Comparator.comparingInt { produto -> produto.vencimento ?: 0 })
        }
      }

      columnGroup("Saldo por Loja") {
        this.columnGrid(ProdutoInventarioResumo::saldoDS, "DS", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::saldoMR, "MR", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::saldoMF, "MF", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::saldoPK, "PK", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::saldoTM, "TM", width = defaultWidth)
      }

      columnGroup("Dados Venda") {
        this.columnGrid(ProdutoInventarioResumo::saidaDS, "DS", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::saidaMR, "MR", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::saidaMF, "MF", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::saidaPK, "PK", width = defaultWidth)
        this.columnGrid(ProdutoInventarioResumo::saidaTM, "TM", width = defaultWidth)
      }
    }
    this.addAndExpand(gridDetail)
    gridDetail.sort(ProdutoInventarioResumo::vencimentoStr.asc)
    update()
  }

  fun produtosSelecionados(): List<ProdutoInventarioResumo> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = produtos.produtosInventarioResumo()
    gridDetail.setItems(listProdutos)
    gridDetail.getColumnBy(ProdutoInventarioResumo::estoqueDS)
      .setFooter(listProdutos.sumOf { it.estoqueDS ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::estoqueMR)
      .setFooter(listProdutos.sumOf { it.estoqueMR ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::estoqueMF)
      .setFooter(listProdutos.sumOf { it.estoqueMF ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::estoquePK)
      .setFooter(listProdutos.sumOf { it.estoquePK ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::estoqueTM)
      .setFooter(listProdutos.sumOf { it.estoqueTM ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::estoqueTotal)
      .setFooter(listProdutos.firstOrNull()?.estoqueTotal?.format() ?: "")
    gridDetail.getColumnBy(ProdutoInventarioResumo::saldo).setFooter(listProdutos.sumOf { it.saldo ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::saldoDS).setFooter(listProdutos.sumOf { it.saldoDS ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::saldoMR).setFooter(listProdutos.sumOf { it.saldoMR ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::saldoMF).setFooter(listProdutos.sumOf { it.saldoMF ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::saldoPK).setFooter(listProdutos.sumOf { it.saldoPK ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::saldoTM).setFooter(listProdutos.sumOf { it.saldoTM ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::saidaDS).setFooter(listProdutos.sumOf { it.saidaDS ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::saidaMR).setFooter(listProdutos.sumOf { it.saidaMR ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::saidaMF).setFooter(listProdutos.sumOf { it.saidaMF ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::saidaPK).setFooter(listProdutos.sumOf { it.saidaPK ?: 0 }.format())
    gridDetail.getColumnBy(ProdutoInventarioResumo::saidaTM).setFooter(listProdutos.sumOf { it.saidaTM ?: 0 }.format())
  }

  fun close() {
    onClose?.invoke()
    form?.close()
  }
}