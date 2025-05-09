package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.InvFile
import br.com.astrosoft.produto.model.beans.NotaRecebimento
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaEntradaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.isExpand
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgArquivoNotaEntrada(val viewModel: TabNotaEntradaViewModel, val nota: NotaRecebimento) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(InvFile::class.java, false)
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
      setSelectionMode(Grid.SelectionMode.MULTI)

      addColumnButton(VaadinIcon.FILE, "Arquivo", "Arquivo") { invFile ->
        val file = invFile.file ?: return@addColumnButton
        val fileName = invFile.fileName ?: return@addColumnButton
        val title = invFile.title ?: return@addColumnButton
        DialogHelper.showFile(title, fileName, file)
      }
      columnGrid(InvFile::fileName, "Nome do Arquivo") {
        this.isExpand = true
      }
      columnGrid(InvFile::date, "Data")
      columnGrid(InvFile::filesize, "Tamanho")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<InvFile> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.arquivos()
    gridDetail.setItems(listProdutos)
  }
}