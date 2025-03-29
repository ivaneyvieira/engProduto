package br.com.astrosoft.produto.view.devFor

import br.com.astrosoft.devolucao.model.beans.UserSaci
import br.com.astrosoft.devolucao.viewmodel.devolucao.*
import br.com.astrosoft.devolucao.viewmodel.devolucao.ESituacaoPedido.REPOSTO
import br.com.astrosoft.framework.model.IUser

class TabAvariaRecReposto(viewModel: TabAvariaRecRepostoViewModel) :
  TabAvariaRecAbstract<IDevolucaoAvariaRecView>(viewModel), ITabAvariaRecReposto {
  override val label: String
    get() = "Reposto"
  override val situacaoPendencia: ESituacaoPendencia?
    get() = null

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.avariaRecReposto == true
  }

  override val situacaoPedido
    get() = listOf(REPOSTO)
}
