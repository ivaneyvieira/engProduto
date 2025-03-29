package br.com.astrosoft.produto.view.devFor

import br.com.astrosoft.devolucao.model.beans.UserSaci
import br.com.astrosoft.devolucao.viewmodel.devolucao.*
import br.com.astrosoft.devolucao.viewmodel.devolucao.ESituacaoPedido.*
import br.com.astrosoft.framework.model.IUser

class TabAvariaRecPendente(viewModel: TabAvariaRecPendenteViewModel) :
  TabAvariaRecAbstract<IDevolucaoAvariaRecView>(viewModel),
  ITabAvariaRecPendente {
  override val label: String
    get() = "Pendente"
  override val situacaoPendencia: ESituacaoPendencia?
    get() = null

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.avariaRecPendente == true
  }

  override val situacaoPedido: List<ESituacaoPedido>
    get() = listOf(VAZIO, LIBERADO,  ASSISTENCIA)
}