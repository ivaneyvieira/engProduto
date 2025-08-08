package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.FornecedorClass
import br.com.astrosoft.produto.model.beans.Representante
import com.vaadin.flow.component.grid.Grid

class DlgFornecedor {
  fun showDialogRepresentante(fornecedor: FornecedorClass?) {
    fornecedor ?: return
    val vendno = fornecedor.no
    val descricao = fornecedor.descricao ?: ""

    val listRepresentantes = fornecedor.listRepresentantes()
    val form = SubWindowForm("$vendno - $descricao") {
      createGridRepresentantes(listRepresentantes.toList())
    }
    form.open()
  }

  private fun createGridRepresentantes(listRepresentantes: List<Representante>): Grid<Representante> {
    val gridDetail = Grid(Representante::class.java, false)
    return gridDetail.apply {
      addThemeVariants()
      isMultiSort = false
      setItems(listRepresentantes) //
      columnGrid(Representante::nome, "Representante")
      columnGrid(Representante::telefone, "Telefone")
      columnGrid(Representante::celular, "Celular")
      columnGrid(Representante::email, "Email")
    }
  }
}