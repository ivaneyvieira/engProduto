package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueUsr
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.isExpand
import com.vaadin.flow.component.datepicker.DatePickerVariant
import com.vaadin.flow.component.grid.Grid

class TabEstoqueUsr(viewModel: TabEstoqueUsrViewModel) : TabPanelUser(viewModel), ITabEstoqueUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::estoqueMov, "Mov")
    columnGrid(UserSaci::estoqueConf, "Conf")
    //columnGrid(UserSaci::estoqueInventario, "Inventário")
    columnGrid(UserSaci::estoqueAcerto, "Acerto")
    columnGrid(UserSaci::estoqueAcertoSimples, "Acerto 2")
    columnGrid(UserSaci::estoqueForn, "Fornecedor")
    columnGrid(UserSaci::estoqueAcertoMobile, "Coletor")
    columnGrid(UserSaci::estoqueGarantia, "Garantia")
    columnGrid(UserSaci::estoqueSaldo, "Estoque")
    columnGrid(UserSaci::estoqueLoja, "Estoque Loja")
    columnGrid(UserSaci::estoqueCad, "Cad Loc")
    columnGrid(UserSaci::estoqueCD1A, "CD1A")
  }

  override fun FormUsuario.configFields() {
    verticalBlock("Menus") {
      checkBox("Mov") {
        binder.bind(this, UserSaci::estoqueMov.name)
      }
      checkBox("Estoque") {
        binder.bind(this, UserSaci::estoqueSaldo.name)
      }
      checkBox("Estoque Loja") {
        binder.bind(this, UserSaci::estoqueLoja.name)
      }
      checkBox("Conferência") {
        binder.bind(this, UserSaci::estoqueConf.name)
      }
      checkBox("Acerto") {
        binder.bind(this, UserSaci::estoqueAcerto.name)
      }
      checkBox("Acerto 2") {
        binder.bind(this, UserSaci::estoqueAcertoSimples.name)
      }
      checkBox("Fornecedor") {
        binder.bind(this, UserSaci::estoqueForn.name)
      }
      checkBox("Coletor") {
        binder.bind(this, UserSaci::estoqueAcertoMobile.name)
      }
      checkBox("Garantia") {
        binder.bind(this, UserSaci::estoqueGarantia.name)
      }
      checkBox("Cad Loc") {
        binder.bind(this, UserSaci::estoqueCad.name)
      }
      checkBox("CD1A") {
        binder.bind(this, UserSaci::estoqueCD1A.name)
      }
    }
    verticalBlock("Comandos") {
      checkBox("Grava Acerto") {
        binder.bind(this, UserSaci::estoqueGravaAcerto.name)
      }
      checkBox("Grava Garantia") {
        binder.bind(this, UserSaci::estoqueGravaGarantia.name)
      }
      checkBox("Edita Loc") {
        binder.bind(this, UserSaci::estoqueEditaLoc.name)
      }
      checkBox("Copia Loc") {
        binder.bind(this, UserSaci::estoqueCopiaLoc.name)
      }
      checkBox("Edita Conf") {
        binder.bind(this, UserSaci::estoqueEditaConf.name)
      }
    }
    verticalBlock("Filtros") {
      filtroLocalizacao(binder, UserSaci::listaEstoque)
      horizontalLayout {
        setWidthFull()
        datePicker("Data Inicial Kardec") {
          this.localePtBr()
          this.addThemeVariants(DatePickerVariant.LUMO_SMALL)
          binder.bind(this, UserSaci::dataIncialKardec.name)
        }

        filtroImpressoraTermica(binder, UserSaci::impressoraEstoque) {
          this.isExpand = true
        }

        filtroLoja(binder, UserSaci::lojaConferencia, "Loja Conferência")
      }
    }
  }
}
