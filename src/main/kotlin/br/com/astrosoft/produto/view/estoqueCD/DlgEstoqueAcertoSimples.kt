package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.EstoqueAcerto
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoSimplesViewModel
import com.github.mvysny.karibudsl.v10.button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgEstoqueAcertoSimples(val viewModel: TabEstoqueAcertoSimplesViewModel, val acerto: EstoqueAcerto) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoEstoqueAcerto::class.java, false)

  fun showDialog(onClose: () -> Unit = {}) {
    this.onClose = onClose
    val numero = acerto.numero
    val loja = acerto.lojaSigla
    val gravado = if (acerto.gravado == true) "(Gravado ${acerto.gravadoLoginStr})" else ""

    form = SubWindowForm(
      "Produtos do Acerto $numero - Loja $loja $gravado",
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

        button("Grava Acerto") {
          this.icon = VaadinIcon.CHECK.create()
          this.addClickListener {
            viewModel.gravaAcerto(acerto)
            closeForm()
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

        this.button("Adiciona") {
          this.icon = VaadinIcon.PLUS.create()
          this.addClickListener {
            if (acerto.processado == true) {
              DialogHelper.showWarning("Acerto já processado")
              return@addClickListener
            }
            val dlg = DlgAdicionaAcertoSimples(viewModel, acerto) {
              gridDetail.dataProvider.refreshAll()
            }
            dlg.open()
          }
        }

        this.button("Remove") {
          this.icon = VaadinIcon.TRASH.create()
          this.addClickListener {
            viewModel.removeAcerto()
          }
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
      this.setSelectionMode(Grid.SelectionMode.MULTI)
      isMultiSort = false

      columnGrid(ProdutoEstoqueAcerto::codigo, "Código")
      columnGrid(ProdutoEstoqueAcerto::descricao, "Descrição", width = "300px")
      columnGrid(ProdutoEstoqueAcerto::grade, "Grade", width = "120px")
      addColumnButton(VaadinIcon.DATE_INPUT, "Conferência", "Conf") { produto ->
        val user = AppConfig.userLogin()
        when {
          acerto.login != user?.login -> {
            DialogHelper.showWarning("Usuário não é o responsável pelo acerto")
          }

          acerto.processado == true   -> {
            DialogHelper.showWarning("Acerto já processado")
          }

          else                        -> {
            val dlgConferencia = DlgConferenciaAcertoSimples(viewModel, produto) {
              gridDetail.dataProvider.refreshAll()
            }
            dlgConferencia.open()
          }
        }
      }
      columnGrid(ProdutoEstoqueAcerto::estoqueSis, "Est Sist")
      //columnGrid(ProdutoEstoqueAcerto::estoqueCD, "Est CD")
      //columnGrid(ProdutoEstoqueAcerto::estoqueLoja, "Est Loja")
      columnGrid(ProdutoEstoqueAcerto::diferencaAcerto, "Diferença")
      columnGrid(ProdutoEstoqueAcerto::estoqueReal, "Est Real")
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
    return acerto.findProdutos(true)
  }

  private fun closeForm() {
    onClose?.invoke()
    form?.close()
  }

  fun produtosSelecionado(): List<ProdutoEstoqueAcerto> {
    return gridDetail.selectedItemsSort()
  }

  fun updateAcerto(acertos: List<EstoqueAcerto>) {
    val acerto = acertos.firstOrNull {
      it.numloja == this.acerto.numloja && it.numero == this.acerto.numero
    }
    gridDetail.setItems(acerto?.findProdutos().orEmpty())
  }
}