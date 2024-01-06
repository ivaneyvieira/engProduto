package br.com.astrosoft.produto.view.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.produto.model.beans.Impressora
import br.com.astrosoft.produto.model.beans.ProdutoTransfRessu4
import br.com.astrosoft.produto.model.beans.TransfRessu4
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.pedidoTransf.TabPedidoTransfRessu4ViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select

class DlgProdutosPedTransfRessu4(val viewModel: TabPedidoTransfRessu4ViewModel, val nota: TransfRessu4) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoTransfRessu4::class.java, false)
  private var cmbImpressora: Select<Impressora>? = null

  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("NF Transf ${nota.notaTransf} - ${nota.rota}", toolBar = {
      this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "planilhaPedidoTransfRessu4") {
        viewModel.geraPlanilha(nota)
      }
      this.button("Relatório") {
        icon = VaadinIcon.PRINT.create()
        onLeftClick {
          viewModel.imprimeRelatorio(nota)
        }
      }
      cmbImpressora = select<Impressora>("Impressora") {
        val lista = Impressora.allTermica()
        val printerUser = (AppConfig.userLogin() as? UserSaci)?.impressora ?: ""
        setItems(lista)
        this.setItemLabelGenerator { it.name }

        this.value = lista.firstOrNull {
          it.name == printerUser
        } ?: lista.firstOrNull()
      }
      this.button("Imprimir") {
        icon = VaadinIcon.PRINT.create()
        this.onLeftClick {
          val impressora = cmbImpressora?.value?.name ?: "Nenhuma impressora selecionada"
          viewModel.imprimeNota(nota, impressora, nota.lojaDestinoNo ?: 0)
        }
      }
      this.button("Preview") {
        icon = VaadinIcon.PRINT.create()
        this.onLeftClick {
          viewModel.previewNota(nota)
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
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      //setSelectionMode(Grid.SelectionMode.MULTI)
      columnGrid(ProdutoTransfRessu4::codigo, "Código")
      columnGrid(ProdutoTransfRessu4::descricao, "Descrição").expand()
      columnGrid(ProdutoTransfRessu4::grade, "Grade")
      columnGrid(ProdutoTransfRessu4::codigoBarras, "Código de Barras")
      columnGrid(ProdutoTransfRessu4::referencia, "Ref Fornecedor")
      columnGrid(ProdutoTransfRessu4::quant, "Quant", pattern = "#,##0")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoTransfRessu4> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos()
    gridDetail.setItems(listProdutos)
  }
}