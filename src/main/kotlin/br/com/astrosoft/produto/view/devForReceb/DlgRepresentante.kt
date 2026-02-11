package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.model.beans.Representante
import br.com.astrosoft.produto.model.beans.RepresentanteContato
import com.github.mvysny.karibudsl.v10.isExpand
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.data.renderer.ComponentRenderer

class DlgRepresentante {
  fun showDialogRepresentante(nota: NotaRecebimentoDev?) {
    nota ?: return
    val listRepresentantes = nota.listRepresentantes()
    val form = SubWindowForm(title = "Representantes do fornecedor ${nota.fornecedor}") {
      createGridRepresentantes(listRepresentantes)
    }
    form.open()
  }

  private fun createGridRepresentantes(listRepresentantes: List<Representante>): TreeGrid<Representante> {
    val gridDetail = TreeGrid(Representante::class.java, false)
    return gridDetail.apply {
      addThemeVariants()
      isMultiSort = false
      setItems(listRepresentantes, this@DlgRepresentante::getContatos)

      addHierarchyColumn(Representante::nome).setHeader("Representante").apply {
        this.isExpand = false
        this.isAutoWidth = false
        this.width = "25rem"
      }
      columnGrid(Representante::celular).setHeader("Telefone").apply {
        this.isExpand = false
        this.isAutoWidth = false
        this.width = "12rem"
      }
      columnGrid(Representante::email, "Contato")

      this.expand(listRepresentantes)
    }
  }

  private fun getContatos(representante: Representante): List<Representante> {
    return representante.contatos.map {
      Representante(
        repno = null,
        vendno = null,
        nome = "",
        telefone = "",
        celular = it.telefone,
        email = it.obsTel,
        contatos = emptyList()
      )
    }
  }

  private fun createContatosRender(): ComponentRenderer<HorizontalLayout, Representante> {
    return ComponentRenderer { representante ->
      HorizontalLayout().apply {
        val grid = Grid(RepresentanteContato::class.java, false)
        grid.setItems(representante.contatos)
        grid.addThemeVariants(
          GridVariant.LUMO_NO_BORDER,
          GridVariant.LUMO_COMPACT,
          GridVariant.LUMO_ROW_STRIPES,
          GridVariant.LUMO_NO_ROW_BORDERS
        )
        grid.columnGrid(RepresentanteContato::telefone, "Telefone")
        grid.columnGrid(RepresentanteContato::obsTel, "Obs")
        grid.minHeight = "2rem"
        grid.maxHeight = "12rem"
        val espaco = HorizontalLayout().apply { this.width = "4rem" }
        add(espaco)
        addAndExpand(grid)
      }
    }
  }
}