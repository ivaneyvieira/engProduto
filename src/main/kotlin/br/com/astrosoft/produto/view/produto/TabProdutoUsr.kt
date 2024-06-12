package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoUsr
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoUsrViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.grid.Grid

class TabProdutoUsr(viewModel: TabProdutoUsrViewModel) : TabPanelUser(viewModel), ITabProdutoUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::produtoList, "Produto")
  }

  override fun FormUsuario.configFields() {
    horizontalBlock {
      verticalBlock("Menus") {
        checkBox("Produto") {
          binder.bind(this, UserSaci::produtoList.name)
        }
        checkBox("Giro") {
          binder.bind(this, UserSaci::produtoEstoqueGiro.name)
        }
        checkBox("Validade") {
          binder.bind(this, UserSaci::produtoEstoqueValidade.name)
        }
        checkBox("Inventario") {
          binder.bind(this, UserSaci::produtoInventario.name)
        }
        checkBox("Editor") {
          binder.bind(this, UserSaci::produtoEditor.name)
        }
      }
      verticalBlock("Comandos")
    }
    verticalBlock("Filtros") {
      filtroLoja(binder, UserSaci::lojaProduto)
      filtroImpressoraTermica(binder, UserSaci::impressoraProduto)
    }
  }
}