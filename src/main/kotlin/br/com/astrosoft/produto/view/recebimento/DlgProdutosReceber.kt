package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.MesAno
import br.com.astrosoft.produto.model.beans.NotaRecebimento
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProduto
import br.com.astrosoft.produto.model.beans.Validade
import br.com.astrosoft.produto.viewmodel.recebimento.TabReceberViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class DlgProdutosReceber(val viewModel: TabReceberViewModel, val nota: NotaRecebimento) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(NotaRecebimentoProduto::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val numeroNota = nota.nfEntrada ?: ""

    form = SubWindowForm("Produtos da nota $numeroNota", toolBar = {
      textField("Código de barras") {
        this.valueChangeMode = ValueChangeMode.ON_CHANGE
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.selecionaProdutos(nota, it.value)
            this@textField.value = ""
            this@textField.focus()
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

      this.withEditor(NotaRecebimentoProduto::class,
        openEditor = {
          this.focusEditor(NotaRecebimentoProduto::vencimento)
        },
        closeEditor = {
          viewModel.salvaNotaProduto(it.bean)
        })

      columnGrid(NotaRecebimentoProduto::codigo, "Código")
      columnGrid(NotaRecebimentoProduto::barcodeStrList, "Código de Barras")
      columnGrid(NotaRecebimentoProduto::descricao, "Descrição")
      columnGrid(NotaRecebimentoProduto::grade, "Grade")
      columnGrid(NotaRecebimentoProduto::localizacao, "Loc")
      columnGrid(NotaRecebimentoProduto::quant, "Quant")
      columnGrid(NotaRecebimentoProduto::estoque, "Estoque")
      columnGrid(
        NotaRecebimentoProduto::validade,
        "Val",
        width = "100px"
      )
      columnGrid(
        NotaRecebimentoProduto::vencimento,
        "Venc",
        width = "120px",
        pattern = "MMM/yyyy"
      ).comboFieldEditor<NotaRecebimentoProduto, LocalDate?> {
        val datas = MesAno.values().map { mesAno ->
          mesAno.lastDay
        }
        it.setItems(datas)
        it.isEmptySelectionAllowed = true
        it.setItemLabelGenerator { data ->
          data.format("MMM/yyyy")
        }
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<NotaRecebimentoProduto> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): NotaRecebimentoProduto? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { prd ->
      prd.containBarcode(codigoBarra)
    }
  }

  fun updateProduto(): NotaRecebimento? {
    val nota = nota.refreshProdutos()
    update()
    return nota
  }

  fun close() {
    onClose?.invoke()
    form?.close()
  }
}