package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.NotaSaidaDev
import br.com.astrosoft.produto.model.beans.NotaSaidaDevFile
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaNFDAbertaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.isExpand
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgArquivoNotaNFDAberta(val viewModel: TabNotaNFDAbertaViewModel, val nota: NotaSaidaDev) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(NotaSaidaDevFile::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    val numeroNota = nota.nota ?: ""

    form = SubWindowForm("Arquivos da nota $numeroNota", toolBar = {
      /*
      this.upload("Adicionar") { fileName, dados ->
        viewModel.addArquivo(nota, fileName, dados)
      }
      button("Remover") {
        this.icon = VaadinIcon.TRASH.create()
        this.addClickListener {
          viewModel.removeArquivosSelecionado()
        }
      }

       */
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

      addColumnButton(VaadinIcon.EYE, "Arquivo", "Arquivo") { nfFile ->
        val file = nfFile.file ?: return@addColumnButton
        val fileName = nfFile.filename ?: return@addColumnButton
        DialogHelper.showFile("Arquivo", fileName, file)
      }
      addColumnDownload(
        iconButton = VaadinIcon.DOWNLOAD,
        tooltip = "Download",
        header = "Download",
        filename = { nfFile ->
          nfFile.filename ?: "arquivo"
        }) { nfFile ->
        nfFile.file
      }

      columnGrid(NotaSaidaDevFile::filename, "Nome do Arquivo") {
        this.isExpand = true
      }
      columnGrid(NotaSaidaDevFile::date, "Data")
      columnGrid(NotaSaidaDevFile::filesize, "Tamanho")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<NotaSaidaDevFile> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.listArquivos()
    gridDetail.setItems(listProdutos)
  }
}