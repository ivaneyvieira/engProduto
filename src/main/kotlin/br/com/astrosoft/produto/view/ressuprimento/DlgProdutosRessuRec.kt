package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.EMarcaRessuprimento
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import br.com.astrosoft.produto.model.beans.Ressuprimento
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoBarcode
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoCodigo
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoDescricao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoGrade
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoLocalizacao
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoQtEntregue
import br.com.astrosoft.produto.view.ressuprimento.columns.ProdutoRessuViewColumns.produtoRessuprimentoQtRecebido
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoRecViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosRessuRec(val viewModel: TabRessuprimentoRecViewModel, val ressuprimento: Ressuprimento) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoRessuprimento::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos de Ressuprimento ${ressuprimento.numero}", toolBar = {
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
      button("Recebido") {
        val user = AppConfig.userLogin() as? UserSaci
        icon = VaadinIcon.ARROW_RIGHT.create()
        onLeftClick {
          viewModel.marcaRec()
        }
      }
      button("Desmarcar") {
        val user = AppConfig.userLogin() as? UserSaci
        icon = VaadinIcon.ARROW_LEFT.create()
        onLeftClick {
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
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)

      this.withEditor(classBean = ProdutoRessuprimento::class,
        openEditor = {
          this.focusEditor(ProdutoRessuprimento::qtRecebido)
        },
        closeEditor = {
          viewModel.saveQuant(it.bean)
        }
      )

      produtoRessuprimentoCodigo()
      produtoRessuprimentoBarcode()
      produtoRessuprimentoDescricao()
      produtoRessuprimentoGrade()
      produtoRessuprimentoLocalizacao()
      produtoRessuprimentoQtEntregue()
      produtoRessuprimentoQtRecebido().integerFieldEditor()
      this.columnGrid(ProdutoRessuprimento::selecionadoOrdem, "Selecionado") {
        this.isVisible = false
      }
      this.columnGrid(ProdutoRessuprimento::posicao, "Posicao") {
        this.isVisible = false
      }

      this.setPartNameGenerator {
        if (it.selecionado == true) {
          "amarelo"
        } else null
      }
      gridDetail.isMultiSort = true
      gridDetail.sort(
        gridDetail.getColumnBy(ProdutoRessuprimento::selecionadoOrdem).asc,
        gridDetail.getColumnBy(ProdutoRessuprimento::posicao).desc,
      )
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<ProdutoRessuprimento> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = ressuprimento.produtos(EMarcaRessuprimento.ENT)
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { it.barcode == codigoBarra }
  }

  fun updateProduto(produto: ProdutoRessuprimento) {
    gridDetail.dataProvider.refreshItem(produto)
    gridDetail.isMultiSort = true
    gridDetail.sort(
      gridDetail.getColumnBy(ProdutoRessuprimento::selecionadoOrdem).asc,
      gridDetail.getColumnBy(ProdutoRessuprimento::posicao).desc,
    )
    update()
    val index = gridDetail.list().indexOf(produto)
    gridDetail.scrollToIndex(index)
    gridDetail.select(produto)
  }

  fun produtosMarcados(): List<ProdutoRessuprimento> {
    return gridDetail.list().filter { it.marca == EMarcaRessuprimento.ENT.num }
  }

  fun itensSelecionados(): List<ProdutoRessuprimento> {
    return gridDetail.selectedItems.toList()
  }
}