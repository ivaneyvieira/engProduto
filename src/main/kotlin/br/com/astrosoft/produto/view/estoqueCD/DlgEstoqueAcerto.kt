package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.EstoqueAcerto
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoViewModel
import com.github.mvysny.karibudsl.v10.button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgEstoqueAcerto(val viewModel: TabEstoqueAcertoViewModel, val acerto: EstoqueAcerto) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoEstoqueAcerto::class.java, false)

  fun showDialog(onClose: () -> Unit = {}) {
    this.onClose = onClose
    val numero = acerto.numero
    val loja = acerto.lojaSigla

    form = SubWindowForm(
      "Produtos do Acerto $numero - Loja $loja",
      toolBar = {
        button("Pedido") {
          this.icon = VaadinIcon.PRINT.create()
          this.addClickListener {
            viewModel.imprimirPedido(acerto)
          }
        }
        button("Acerto") {
          this.icon = VaadinIcon.PRINT.create()
          this.addClickListener {
            viewModel.imprimirAcerto(acerto)
          }
        }
        button("Relatório") {
          this.icon = VaadinIcon.FILE_TEXT.create()
          this.addClickListener {
            viewModel.imprimirRelatorio(acerto)
          }
        }

        this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "acertoEstoque") {
          val produtos = estoqueAcertos()
          viewModel.geraPlanilha(produtos)
        }
      },
      onClose = {
        closeForm()
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

      columnGrid(ProdutoEstoqueAcerto::codigo, "Código")
      columnGrid(ProdutoEstoqueAcerto::descricao, "Descrição")
      columnGrid(ProdutoEstoqueAcerto::grade, "Grade")
      addColumnButton(VaadinIcon.DATE_INPUT, "Conferência", "Conf") { produto ->
        val user = AppConfig.userLogin()
        when {
          acerto.login != user?.login -> {
            DialogHelper.showWarning("Usuário não é o responsável pelo acerto")
          }

          acerto.processado == "Sim"  -> {
            DialogHelper.showWarning("Acerto já processado")
          }

          else                        -> {
            val dlgConferencia = DlgConferenciaAcerto(viewModel, produto) {
              gridDetail.dataProvider.refreshAll()
            }
            dlgConferencia.open()
          }
        }
      }
      columnGrid(ProdutoEstoqueAcerto::estoqueSis, "Est Sist")
      columnGrid(ProdutoEstoqueAcerto::estoqueCD, "Est CD")
      columnGrid(ProdutoEstoqueAcerto::estoqueLoja, "Est Loja")
      columnGrid(ProdutoEstoqueAcerto::diferenca, "Diferença")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<ProdutoEstoqueAcerto> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val produtos = estoqueAcertos()
    gridDetail.setItems(produtos)
  }

  private fun estoqueAcertos(): List<ProdutoEstoqueAcerto> {
    return acerto.findProdutos()
  }

  private fun closeForm() {
    onClose?.invoke()
    form?.close()
  }
}