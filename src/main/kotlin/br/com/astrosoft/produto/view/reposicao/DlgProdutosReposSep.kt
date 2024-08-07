package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.EMarcaReposicao
import br.com.astrosoft.produto.model.beans.Reposicao
import br.com.astrosoft.produto.model.beans.ReposicaoProduto
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoSepViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosReposSep(val viewModel: TabReposicaoSepViewModel, private val reposicoes: List<Reposicao>) {
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
      textField("Código de barras") {
        this.valueChangeMode = ValueChangeMode.ON_CHANGE
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.selecionaProdutos(it.value)
            this@textField.value = ""
            this@textField.focus()
          }
        }
      }
      button("Entregue") {
        icon = VaadinIcon.ARROW_RIGHT.create()
        onClick {
          viewModel.marca()
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
      setSelectionMode(Grid.SelectionMode.MULTI)

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
    update(reposicoes)
  }

  fun produtosSelecionados(): List<ReposicaoProduto> {
    return gridDetail.selectedItems.toList()
  }

  fun update(reposicoesNovas: List<Reposicao>) {
    val reposicoesFiltradas = reposicoesNovas.filter {nova ->
      val chave = nova.chave()
      reposicoes.any { it.chave() == chave }
    }

    val listProdutosFiltradas = reposicoesFiltradas.flatMap {
      it.produtosSEP()
    }

    val listProdutos = reposicoes.flatMap {
      it.produtosSEP()
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