package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.model.beans.Representante
import com.vaadin.flow.component.grid.Grid

class DlgRepresentante {
  fun showDialogRepresentante(nota: NotaRecebimentoDev?) {
    nota ?: return
    val listRepresentantes = nota.listRepresentantes()
    val form = SubWindowForm(title = "Representantes do fornecedor ${nota.fornecedor}") {
      createGridRepresentantes(listRepresentantes)
    }
    form.open()
  }

  private fun createGridRepresentantes(listRepresentantes: List<Representante>): Grid<Representante> {
    val gridDetail = Grid(Representante::class.java, false)
    return gridDetail.apply {
      addThemeVariants()
      isMultiSort = false
      setItems(listRepresentantes)
      addColumn(Representante::nome).setHeader("Representante")
      addColumn(Representante::telefone).setHeader("Telefone")
      addColumn(Representante::celular).setHeader("Celular")
      addColumn(Representante::email).setHeader("Email")
    }
  }
}