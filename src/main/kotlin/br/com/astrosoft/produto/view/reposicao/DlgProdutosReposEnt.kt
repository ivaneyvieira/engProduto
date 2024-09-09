package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.EMarcaReposicao
import br.com.astrosoft.produto.model.beans.Reposicao
import br.com.astrosoft.produto.model.beans.ReposicaoProduto
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoEntViewModel
import com.github.mvysny.kaributools.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosReposEnt(
  val viewModel: TabReposicaoEntViewModel,
  private val reposicoes: List<Reposicao>,
  val filtroProduto: Boolean
) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ReposicaoProduto::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val reposicaoTitle = if (reposicoes.size == 1) {
      val reposicao = reposicoes.first()
      "${reposicao.numero}     ${reposicao.data.format()}"
    } else {
      val loja = reposicoes.map { it.loja }.distinct().joinToString(", ")
      val data = reposicoes.map { it.data.format() }.distinct().joinToString(", ")
      "Loja: $loja    Data: $data"
    }
    form = SubWindowForm("Produtos do reposicao $reposicaoTitle", toolBar = {

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
      setSelectionMode(Grid.SelectionMode.MULTI)

      if(filtroProduto) {
        this.withEditor(classBean = ReposicaoProduto::class,
          openEditor = {
            this.focusEditor(ReposicaoProduto::qtRecebido)
          },
          closeEditor = {
            viewModel.saveQuant(it.bean)
          }
        )
      }

      columnGrid(ReposicaoProduto::codigo, "Código")
      columnGrid(ReposicaoProduto::barcode, "Código de Barras")
      columnGrid(ReposicaoProduto::descricao, "Descrição")
      columnGrid(ReposicaoProduto::grade, "Grade")
      columnGrid(ReposicaoProduto::localizacao, "Loc")
      columnGrid(ReposicaoProduto::entregueSNome, "Autoriza")
      columnGrid(ReposicaoProduto::quantidade, "Quant")
      columnGrid(ReposicaoProduto::qtRecebido, "Recebido").integerFieldEditor()

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
    update(reposicoes)
  }

  fun produtosSelecionados(): List<ReposicaoProduto> {
    return gridDetail.selectedItems.toList()
  }

  fun update(reposicoesNovas: List<Reposicao>) {
    val reposicoesFiltradas = reposicoesNovas.filter { nova ->
      val chave = nova.chave()
      reposicoes.any { it.chave() == chave }
    }

    val listProdutosFiltradas = reposicoesFiltradas.flatMap {
      it.produtosENT()
    }

    val listProdutos = reposicoes.flatMap {
      it.produtosENT()
    }

    val listProdutosNovos = listProdutosFiltradas.filter { produto ->
      listProdutos.any { it.chave() == produto.chave() }
    }

    gridDetail.setItems(listProdutosNovos)
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
    update(reposicoes)
    val index = gridDetail.list().indexOf(produto)
    gridDetail.scrollToIndex(index)
    gridDetail.select(produto)
  }
}