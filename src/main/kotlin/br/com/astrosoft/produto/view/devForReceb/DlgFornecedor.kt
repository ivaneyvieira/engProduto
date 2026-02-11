package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.textFieldEditor
import br.com.astrosoft.framework.view.vaadin.helper.withEditor
import br.com.astrosoft.produto.model.beans.FornecedorClass
import br.com.astrosoft.produto.model.beans.Representante
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaFornecedorViewModel
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid

class DlgFornecedor(val viewModel: TabNotaFornecedorViewModel, val fornecedor: FornecedorClass?) {
  var gridRep: Grid<Representante>? = null
  fun showDialogRepresentante() {
    fornecedor ?: return
    val vendno = fornecedor.no
    val descricao = fornecedor.descricao ?: ""

    val listRepresentantes = fornecedor.listRepresentantes()
    val form = SubWindowForm("$vendno - $descricao") {
      val grid = createGridRepresentantes(listRepresentantes.toList())
      gridRep = grid
      grid
    }
    form.open()
  }

  private fun createGridRepresentantes(listRepresentantes: List<Representante>): Grid<Representante> {
    val gridDetail = Grid(Representante::class.java, false)
    return gridDetail.apply {
      this.withEditor(
        classBean = Representante::class,
        openEditor = {
          val edit = getColumnBy(Representante::email) as? Focusable<*>
          edit?.focus()
        },
        closeEditor = {
          viewModel.saveEmail(it.bean)
        })
      addThemeVariants()
      isMultiSort = false
      setItems(listRepresentantes)
      columnGrid(Representante::nome, "Representante")
      columnGrid(Representante::telefone, "Telefone")
      columnGrid(Representante::celular, "Celular")
      columnGrid(Representante::email, "Email", isExpand = true).textFieldEditor()
    }
  }

  fun update() {
    gridRep?.setItems(fornecedor?.listRepresentantes().orEmpty())
  }
}