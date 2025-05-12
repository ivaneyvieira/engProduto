package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.DadosProdutosRessuprimento
import br.com.astrosoft.produto.model.beans.DadosRessuprimento
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoRessupViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosRessuEdit(val viewModel: TabRessuprimentoRessupViewModel, val ressuprimento: DadosRessuprimento) {
  private var edtPesquisa : TextField? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(DadosProdutosRessuprimento::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val pedido = "${ressuprimento.pedido}/${ressuprimento.loja}"
    form = SubWindowForm(
      title = "Pedido $pedido",
      toolBar = {
        edtPesquisa = textField("Pesquisa") {
          this.width = "300px"
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            update()
          }
        }
        this.button("Edita") {
          this.icon = VaadinIcon.EDIT.create()
          this.addClickListener {
            val item = gridDetail.list().firstOrNull()
            if (item != null) {
              gridDetail.deselectAll()
              gridDetail.editor.editItem(item)
            }
          }
        }
        this.button("Remove") {
          this.icon = VaadinIcon.TRASH.create()
          this.addClickListener {
            val produtos = gridDetail.selectedItems.toList()
            if (produtos.isNotEmpty()) {
              viewModel.removeProdutos(ressuprimento, produtos)
              update()
            }
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
    form?.isCloseOnEsc = true
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

      this.withEditor(
        classBean = DadosProdutosRessuprimento::class,
        isBuffered = false,
        openEditor = {
          this.focusEditor(DadosProdutosRessuprimento::qttyPedida)
        },
        closeEditor = {
          viewModel.saveProduto(it.bean)
          abreProximo(it.bean)
        },
        saveEditor = {
          viewModel.saveProduto(it.bean)
          abreProximo(it.bean)
        }
      )

      columnGrid(DadosProdutosRessuprimento::codigo, "Código").right()
      columnGrid(DadosProdutosRessuprimento::descricao, "Descrição", width = "220px")
      columnGrid(DadosProdutosRessuprimento::grade, "Grade")
      columnGrid(DadosProdutosRessuprimento::qttyVendaMes, "Venda no Mes")
      columnGrid(DadosProdutosRessuprimento::qttyVendaMesAnt, "Venda mes Ant")
      columnGrid(DadosProdutosRessuprimento::qttyVendaMedia, "Venda Media")
      columnGrid(DadosProdutosRessuprimento::estoque, "Estoque Atual")
      columnGrid(DadosProdutosRessuprimento::qttySugerida, "Sugestão")
      columnGrid(DadosProdutosRessuprimento::qttyPedida, "Qtde Pedida").integerFieldEditor()
      columnGrid(DadosProdutosRessuprimento::estoqueLJ, "Estoque MF")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  private fun abreProximo(bean: DadosProdutosRessuprimento) {
    val items = gridDetail.list()
    val index = items.indexOf(bean)
    if (index >= 0) {
      val nextIndex = index + 1
      if (nextIndex < items.size) {
        val nextBean = items[nextIndex]
        //gridDetail.select(nextBean)
        gridDetail.editor.editItem(nextBean)
      } else {
        gridDetail.deselectAll()
      }
    }
  }

  fun produtosSelecionados(): List<DadosProdutosRessuprimento> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val pesquisa = edtPesquisa?.value ?: ""
    val listProdutos = ressuprimento.produtos.filter {
      pesquisa == "" || it.codigo.toString() == pesquisa || (it.descricao?.contains(pesquisa) == true)
    }
    gridDetail.setItems(listProdutos)
  }

  fun itensSelecionados(): List<DadosProdutosRessuprimento> {
    return gridDetail.selectedItems.toList()
  }
}