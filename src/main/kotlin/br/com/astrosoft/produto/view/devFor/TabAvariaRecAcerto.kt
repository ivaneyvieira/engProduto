package br.com.astrosoft.produto.view.devFor

import br.com.astrosoft.devolucao.model.beans.UserSaci
import br.com.astrosoft.devolucao.viewmodel.devolucao.*
import br.com.astrosoft.devolucao.viewmodel.devolucao.ESituacaoPedido.ACERTO
import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.produto.viewmodel.devFor.ITabAvariaRecAcerto
import br.com.astrosoft.produto.viewmodel.devFor.TabAvariaRecAcertoViewModel

class TabAvariaRecAcerto(viewModel: TabAvariaRecAcertoViewModel) :
  TabAvariaRecAbstract<IDevolucaoAvariaRecView>(viewModel), ITabAvariaRecAcerto {
  override val label: String
    get() = "Acerto"
  override val situacaoPendencia: ESituacaoPendencia?
    get() = null

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.avariaRecAcerto == true
  }

  override val situacaoPedido
    get() = listOf(ACERTO)
}
