package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.DadosProdutosRessuprimento
import br.com.astrosoft.produto.model.beans.DadosRessuprimento
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoRessupViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosRessuEdit(val viewModel: TabRessuprimentoRessupViewModel, val ressuprimento: DadosRessuprimento) {
  private var edtPesquisa: TextField? = null
  private var cmbOperador: Select<EOperador>? = null
  private var edtSaldo: IntegerField? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(DadosProdutosRessuprimento::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val pedido = "${ressuprimento.pedido}/${ressuprimento.loja}"
    form = SubWindowForm(
      title = "Pedido $pedido",
      toolBar = {
        edtPesquisa = textField("Pesquisa") {
          this.width = "220px"
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            update()
          }
        }
        cmbOperador = select("Est MF") {
          this.width = "80px"
          this.setItems(EOperador.entries)
          this.setItemLabelGenerator { e ->
            e.descricao
          }
          this.value = EOperador.TODOS

          this.addValueChangeListener {
            update()
          }
        }
        edtSaldo = integerField("Saldo") {
          this.value = 0
          this.width = "80px"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.addValueChangeListener {
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
      this.addClassName("negrito")
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

      columnGroup("Produto") {
        this.columnGrid(DadosProdutosRessuprimento::codigo, "Código").right()
        this.columnGrid(DadosProdutosRessuprimento::descricao, "Descrição", width = "260px")
        this.columnGrid(DadosProdutosRessuprimento::grade, "Grade")
      }
      columnGroup("Venda") {
        this.columnGrid(DadosProdutosRessuprimento::qttyVendaMes, "No Mês")
        this.columnGrid(DadosProdutosRessuprimento::qttyVendaMesAnt, "Mês Ant")
        this.columnGrid(DadosProdutosRessuprimento::qttyVendaMedia, "Média")
      }
      columnGroup("Estoque/Quantidade") {
        this.columnGrid(DadosProdutosRessuprimento::estoque, "Atual")
        this.columnGrid(DadosProdutosRessuprimento::qttySugerida, "Sugestão")
        this.columnGrid(DadosProdutosRessuprimento::qttyPedida, "Pedida").integerFieldEditor()
        this.columnGrid(DadosProdutosRessuprimento::estoqueLJ, "LJ MF")
      }
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
      ((pesquisa == "") || (it.codigo.toString() == pesquisa) || (it.descricao?.contains(pesquisa) == true)) &&
      ((cmbOperador?.value == EOperador.TODOS) || when (cmbOperador?.value) {
        EOperador.MAIOR -> (it.estoqueLJ ?: 0) > (edtSaldo?.value ?: 0)
        EOperador.MENOR -> (it.estoqueLJ ?: 0) < (edtSaldo?.value ?: 0)
        EOperador.IGUAL -> (it.estoqueLJ ?: 0) == (edtSaldo?.value ?: 0)
        else            -> false
      })
    }
    gridDetail.setItems(listProdutos)
  }

  fun itensSelecionados(): List<DadosProdutosRessuprimento> {
    return gridDetail.selectedItems.toList()
  }
}

enum class EOperador(val descricao: String) {
  MAIOR(">"),
  MENOR("<"),
  IGUAL("="),
  TODOS("Todos"),
}