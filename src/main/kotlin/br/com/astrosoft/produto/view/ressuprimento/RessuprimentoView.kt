package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.ressuprimento.IRessuprimentoView
import br.com.astrosoft.produto.viewmodel.ressuprimento.RessuprimentoViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "ressuprimento")
@PageTitle("Ressuprimento")
@CssImport("./styles/gridTotal.css")
@PermitAll
class RessuprimentoView(val init: Boolean = true, prdno: String = "", grade: String = "") :
    ViewLayout<RessuprimentoViewModel>(), IRessuprimentoView {
  override val viewModel = RessuprimentoViewModel(this)
  override val tabRessuprimentoRessup = TabRessuprimentoRessu(viewModel.tabRessuprimentoRessupViewModel)
  override val tabPedidoRessuprimento = TabPedidoRessuprimento(viewModel.tabPedidoRessuprimentoViewModel)
  override val tabRessuprimentoCD = TabRessuprimentoCD(viewModel.tabRessuprimentoCDViewModel)
  override val tabRessuprimentoColetado = TabRessuprimentoColetado(viewModel.tabRessuprimentoColetadoViewModel)
  override val tabRessuprimentoReceber = TabRessuprimentoReceber(viewModel.tabRessuprimentoReceberViewModel, prdno, grade)
  override val tabRessuprimentoPen = TabRessuprimentoPen(viewModel.tabRessuprimentoPenViewModel)
  override val tabRessuprimentoRecebido = TabRessuprimentoRecebido(viewModel.tabRessuprimentoRecebidoViewModel)
  override val tabRessuprimentoUsr = TabRessuprimentoUsr(viewModel.tabRessuprimentoUsrViewModel)
  
  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.ressuprimento
  }
  
  init {
    if (init) {
      addTabSheat(viewModel)
    }
  }
}