package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.EMetodo
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.reposicao.ITabReposicaoUsr
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v23.multiSelectComboBox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.binder.Binder

class TabReposicaoUsr(viewModel: TabReposicaoUsrViewModel) : TabPanelUser(viewModel), ITabReposicaoUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::reposicaoSep, "Separar")
    columnGrid(UserSaci::reposicaoMov, "Movimentação")
    columnGrid(UserSaci::reposicaoAcerto, "Acerto")
    columnGrid(UserSaci::reposicaoRetorno, "Retorno")
    columnGrid(UserSaci::reposicaoEnt, "Entregue")
    columnGrid(UserSaci::impressoraRepo, "Impressora")
    columnGrid(UserSaci::localizacaoRepo, "Localização")
    columnGrid(UserSaci::autorizaAcerto, "Autoriza Acerto")
  }

  override fun HorizontalLayout.configFields(binder: Binder<UserSaci>) {
    verticalBlock("Menu") {
      checkBox("Separar") {
        binder.bind(this, UserSaci::reposicaoSep.name)
      }
      checkBox("Movimentação") {
        binder.bind(this, UserSaci::reposicaoMov.name)
      }
      checkBox("Acerto") {
        binder.bind(this, UserSaci::reposicaoAcerto.name)
      }
      checkBox("Retorno") {
        binder.bind(this, UserSaci::reposicaoRetorno.name)
      }
      checkBox("Entregue") {
        binder.bind(this, UserSaci::reposicaoEnt.name)
      }
    }
    verticalBlock("Filtros") {
      filtroLoja(binder, UserSaci::lojaReposicao)
      filtroImpressoraTermica(binder, UserSaci::impressoraRepo)
      filtroLocalizacao(binder, UserSaci::localizacaoRepo)

      multiSelectComboBox<EMetodo>("Tipo") {
        this.setItems(EMetodo.entries)
        this.setItemLabelGenerator { it.descricao }
        this.addValueChangeListener {
          viewModel.updateView()
        }
        binder.bind(this, UserSaci::tipoMetodo.name)
      }
    }
    verticalBlock("Comandos") {
      checkBox("Autoriza Acerto") {
        binder.bind(this, UserSaci::autorizaAcerto.name)
      }

      checkBox("Assina Entrega") {
        binder.bind(this, UserSaci::reposicaoAssinaEntrega.name)
      }

      checkBox("Assina Recebimento") {
        binder.bind(this, UserSaci::reposicaoAssinaRecebimento.name)
      }

      checkBox("Defaz Asssinatura") {
        binder.bind(this, UserSaci::reposicaoDesfazAssina.name)
      }

      checkBox("Usuário do CD") {
        binder.bind(this, UserSaci::reposicaoUsuarioCD.name)
      }

      checkBox("Usuário da Loja") {
        binder.bind(this, UserSaci::reposicaoUsuarioLJ.name)
      }
    }
  }
}