package br.com.astrosoft.produto.view.devFor

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(layout = ProdutoLayout::class)
@PageTitle("Avaria Recebimento")
@CssImport("./styles/gridTotal.css")
class DevolucaoAvariaRecView : ViewLayout<DevolucaoAvariaRecViewModel>(), IDevolucaoAvariaRecView {
  override val viewModel: DevolucaoAvariaRecViewModel = DevolucaoAvariaRecViewModel(this)
  override val tabAvariaRecEditor = TabAvariaRecEditor(viewModel.tabAvariaRecEditorViewModel)
  override val tabAvariaRecPendente = TabAvariaRecPendente(viewModel.tabAvariaRecPendenteViewModel)
  override val tabAvariaRecTransportadora = TabAvariaRecTransportadora(viewModel.tabAvariaRecTransportadoraViewModel)
  override val tabAvariaRecEmail = TabAvariaRecEmail(viewModel.tabAvariaRecEmailViewModel)
  override val tabAvariaRecAcerto = TabAvariaRecAcerto(viewModel.tabAvariaRecAcertoViewModel)
  override val tabAvariaRecReposto = TabAvariaRecReposto(viewModel.tabAvariaRecRepostoViewModel)
  override val tabAvariaRecNFD = TabAvariaRecNFD(viewModel.tabAvariaRecNFDViewModel)
  override val tabAvariaRecUsr = TabAvariaRecUsr(viewModel.tabAvariaRecUsrViewModel)

  override fun isAccept(user: IUser): Boolean {
    val userSaci = user as? UserSaci ?: return false
    return userSaci.menuDevolucaoAvariaRec
  }

  init {
    addTabSheat(viewModel)
  }
}

