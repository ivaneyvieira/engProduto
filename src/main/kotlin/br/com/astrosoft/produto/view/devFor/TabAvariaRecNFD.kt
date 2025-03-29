package br.com.astrosoft.produto.view.devFor

import br.com.astrosoft.devolucao.model.beans.UserSaci
import br.com.astrosoft.devolucao.viewmodel.devolucao.ESituacaoPedido.NFD_AUTOZ
import br.com.astrosoft.devolucao.viewmodel.devolucao.ESituacaoPendencia
import br.com.astrosoft.devolucao.viewmodel.devolucao.IDevolucaoAvariaRecView
import br.com.astrosoft.devolucao.viewmodel.devolucao.ITabAvariaRecNFD
import br.com.astrosoft.devolucao.viewmodel.devolucao.TabAvariaRecNFDViewModel
import br.com.astrosoft.framework.model.IUser

class TabAvariaRecNFD(viewModel: TabAvariaRecNFDViewModel) :
  TabAvariaRecAbstract<IDevolucaoAvariaRecView>(viewModel), ITabAvariaRecNFD {
  override val label: String
    get() = "NFD"
  override val situacaoPendencia: ESituacaoPendencia?
    get() = null

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.avariaRecNFD == true
  }

  override val situacaoPedido
    get() = listOf(NFD_AUTOZ)
}
