package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.EMarcaReposicao
import br.com.astrosoft.produto.model.beans.Reposicao
import br.com.astrosoft.produto.model.beans.ReposicaoProduto
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoRetornoViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.kaributools.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosReposRetorno(val viewModel: TabReposicaoRetornoViewModel, private val reposicao: Reposicao) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ReposicaoProduto::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val reposicaoTitle = "${reposicao.numero}     ${reposicao.data.format()}"
    form = SubWindowForm("Produtos do reposicao $reposicaoTitle", toolBar = {
      button("Entregue") {
        icon = VaadinIcon.ARROW_RIGHT.create()
        onClick {
          viewModel.marca(reposicao)
        }
      }
      button("Desmarcar") {
        icon = VaadinIcon.ARROW_LEFT.create()
        onClick {
          viewModel.desmarcar()
        }
      }
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
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.MULTI

      this.withEditor(classBean = ReposicaoProduto::class,
        openEditor = {
          this.focusEditor(ReposicaoProduto::qtRecebido)
        },
        closeEditor = {
          viewModel.saveQuant(it.bean)
        }
      )

      columnGrid(ReposicaoProduto::codigo, "Código")
      columnGrid(ReposicaoProduto::barcode, "Código de Barras")
      columnGrid(ReposicaoProduto::descricao, "Descrição")
      columnGrid(ReposicaoProduto::grade, "Grade")
      columnGrid(ReposicaoProduto::localizacao, "Loc")
      columnGrid(ReposicaoProduto::quantidade, "Quant")
      columnGrid(ReposicaoProduto::qtEstoque, "Estoque")

      this.columnGrid(ReposicaoProduto::selecionadoOrdemENT, "Selecionado") {
        this.isVisible = false
      }
      this.columnGrid(ReposicaoProduto::posicao, "Posicao") {
        this.isVisible = false
      }

      this.setPartNameGenerator {
        if (it.selecionado == EMarcaReposicao.ENT.num) {
          "amarelo"
        } else null
      }
      gridDetail.isMultiSort = true
      gridDetail.sort(
        gridDetail.getColumnBy(ReposicaoProduto::selecionadoOrdemENT).asc,
        gridDetail.getColumnBy(ReposicaoProduto::posicao).desc,
      )
    }
    this.addAndExpand(gridDetail)
    update(reposicao)
  }

  fun produtosSelecionados(): List<ReposicaoProduto> {
    return gridDetail.selectedItems.toList()
  }

  fun update(reposicoesNovas: Reposicao) {
    gridDetail.setItems(reposicoesNovas.produtos)
  }

  fun produtosCodigoBarras(codigoBarra: String): ReposicaoProduto? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { it.barcode == codigoBarra }
  }

  fun updateProduto(produto: ReposicaoProduto) {
    gridDetail.dataProvider.refreshItem(produto)
    gridDetail.isMultiSort = true
    gridDetail.sort(
      gridDetail.getColumnBy(ReposicaoProduto::selecionadoOrdemENT).asc,
      gridDetail.getColumnBy(ReposicaoProduto::posicao).desc,
    )
    update(reposicao)
    val index = gridDetail.list().indexOf(produto)
    gridDetail.scrollToIndex(index)
    gridDetail.select(produto)
  }
}