package br.com.astrosoft.produto.view.devFor

import br.com.astrosoft.devolucao.model.beans.UserSaci
import br.com.astrosoft.devolucao.viewmodel.devolucao.*
import br.com.astrosoft.framework.model.IUser

class TabAvariaRecEditor(viewModel: TabAvariaRecEditorViewModel) :
  TabAvariaRecAbstract<IDevolucaoAvariaRecView>(viewModel),
  ITabAvariaRecEditor {
  override val label: String
    get() = "Editor"
  override val situacaoPendencia: ESituacaoPendencia?
    get() = null

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.avariaRecEditor == true
  }

  override val situacaoPedido: List<ESituacaoPedido>
    get() = ESituacaoPedido.entries
}