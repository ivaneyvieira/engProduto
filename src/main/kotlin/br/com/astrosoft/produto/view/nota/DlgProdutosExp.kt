package br.com.astrosoft.produto.view.nota

import br.com.astrosoft.framework.view.SubWindowForm
import br.com.astrosoft.framework.view.comboFieldEditor
import br.com.astrosoft.framework.view.withEditor
import br.com.astrosoft.produto.model.beans.EMarcaNota
import br.com.astrosoft.produto.model.beans.NotaSaida
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoNF
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFCodigo
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFDescricao
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFGrade
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFGradeAlternativa
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFLocalizacao
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFPrecoTotal
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFPrecoUnitario
import br.com.astrosoft.produto.view.nota.columns.ProdutoNFNFSViewColumns.produtoNFQuantidade
import br.com.astrosoft.produto.viewmodel.nota.TabNotaExpViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select

class DlgProdutosExp(val viewModel: TabNotaExpViewModel, val nota: NotaSaida) {
  private val gridDetail = Grid(ProdutoNF::class.java, false)
  fun showDialog() {
    val form = SubWindowForm("Produtos da nota ${nota.nota} loja: ${nota.loja}", toolBar = {
      button("CD") {
        icon = VaadinIcon.ARROW_RIGHT.create()
        onLeftClick {
          viewModel.marcaCD()
          gridDetail.setItems(nota.produtos(EMarcaNota.TODOS))
        }
      }
    }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos()
      }
    }
    form.open()
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)

      withEditor(ProdutoNF::class, openEditor = {
        (getColumnBy(ProdutoNF::gradeAlternativa).editorComponent as? Focusable<*>)?.focus()
      }, closeEditor = { binder ->
        this.dataProvider.refreshItem(binder.bean)
      })

      addItemDoubleClickListener { e ->
        editor.editItem(e.item)
        val editorComponent: Component = e.column.editorComponent
        if (editorComponent is Focusable<*>) {
          (editorComponent as Focusable<*>).focus()
        }
      }

      produtoNFCodigo()
      produtoNFDescricao()
      produtoNFGrade()
      produtoNFGradeAlternativa().comboFieldEditor { combo: Select<String> ->
        combo.setItems("")
        editor.addOpenListener { e ->
          val produto = e.item
          val list = mutableListOf<PrdGrade>()

          viewModel.findGrade(produto) { prds ->
            list.addAll(prds)
          }
          combo.style.set("--vaadin-combo-box-overlay-width", "300px")
          combo.setItems(list.map { it.grade })
          combo.setItemLabelGenerator { grade ->
            val saldo = list.firstOrNull { it.grade == grade }?.saldo ?: 0
            "$grade Saldo: $saldo"
          }
        }
      }
      produtoNFLocalizacao()
      produtoNFQuantidade()
      produtoNFPrecoUnitario()
      produtoNFPrecoTotal()

      this.setClassNameGenerator {
        when (it.marca) {
          1    -> "marcaDiferenca"
          2    -> "marcaDiferenca2"
          else -> null
        }
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoNF> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos(EMarcaNota.TODOS)
    gridDetail.setItems(listProdutos)
  }
}