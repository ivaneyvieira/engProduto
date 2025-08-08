package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.FornecedorArquivo
import br.com.astrosoft.produto.model.beans.FornecedorClass
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaFornecedorViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.isExpand
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgArquivoFornecedor(val viewModel: TabNotaFornecedorViewModel, val fornecedor: FornecedorClass) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(FornecedorArquivo::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val numeroForn = fornecedor.no?.toString() ?: ""
    val nomeForm = fornecedor.descricao ?: "Fornecedor"

    form = SubWindowForm(
      title = "Arquivos d fornecedor $numeroForn - $nomeForm",
      toolBar = {
        this.upload("Adicionar") { fileName, dados ->
          viewModel.addArquivo(fornecedor, fileName, dados)
        }
        button("Remover") {
          this.icon = VaadinIcon.TRASH.create()
          this.addClickListener {
            viewModel.removeArquivosSelecionado()
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

      addColumnButton(VaadinIcon.EYE, "Arquivo", "Arquivo") { invFile ->
        val file = invFile.file ?: return@addColumnButton
        val fileName = invFile.filename ?: return@addColumnButton
        DialogHelper.showFile("Arquivo", fileName, file)
      }
      addColumnDownload(
        iconButton = VaadinIcon.DOWNLOAD,
        tooltip = "Download",
        header = "Download",
        filename = { invFile ->
          invFile.filename ?: "arquivo"
        }) { invFile ->
        invFile.file
      }

      columnGrid(FornecedorArquivo::filename, "Nome do Arquivo") {
        this.isExpand = true
      }
      columnGrid(FornecedorArquivo::filesize, "Tamanho")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<FornecedorArquivo> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = fornecedor.arquivos()
    gridDetail.setItems(listProdutos)
  }
}