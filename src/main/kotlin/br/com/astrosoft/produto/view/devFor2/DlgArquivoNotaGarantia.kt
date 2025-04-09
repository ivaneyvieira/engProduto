package br.com.astrosoft.produto.view.devFor2

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.InvFileDev
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.viewmodel.devFor2.TabNotaGarantiaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.isExpand
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgArquivoNotaGarantia(val viewModel: TabNotaGarantiaViewModel, val nota: NotaRecebimentoDev) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(InvFileDev::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val numeroNota = nota.nfEntrada ?: ""

    form = SubWindowForm("Arquivos da nota $numeroNota", toolBar = {
      this.upload("Adicionar") { fileName, dados ->
        viewModel.addArquivo(nota, fileName, dados)
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
      selectionMode = Grid.SelectionMode.MULTI

      addColumnButton(VaadinIcon.EYE, "Arquivo", "Arquivo") { invFile ->
        val file = invFile.file ?: return@addColumnButton
        val fileName = invFile.fileName ?: return@addColumnButton
        DialogHelper.showFile("Arquivo", fileName, file)
      }
      addColumnDownload(
        iconButton = VaadinIcon.DOWNLOAD,
        tooltip = "Download",
        header = "Download",
        filename = { invFile ->
          invFile.fileName ?: "arquivo"
        }) { invFile ->
        invFile.file
      }

      columnGrid(InvFileDev::fileName, "Nome do Arquivo") {
        this.isExpand = true
      }
      columnGrid(InvFileDev::date, "Data")
      columnGrid(InvFileDev::filesize, "Tamanho")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<InvFileDev> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.arquivos()
    gridDetail.setItems(listProdutos)
  }
}