package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.devForRecebe.DevFor2ViewModel
import br.com.astrosoft.produto.viewmodel.devForRecebe.IDevFor2View
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "devfor2")
@PageTitle("Dev For Receb")
@CssImport("./styles/gridTotal.css")
@PermitAll
class DevFor2View : ViewLayout<DevFor2ViewModel>(), IDevFor2View {
  override val viewModel = DevFor2ViewModel(this)
  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.devFor2
  }

  override val tabNotaEntrada = TabNotaEntrada(viewModel.tabNotaEntradaViewModel)
  override val tabNotaEditor = TabNotaEditor(viewModel.tabNotaEditorViewModel)
  override val tabNotaDivergente = TabNotaDivergente(viewModel.tabNotaDivergenteViewModel)
  override val tabNotaPedido = TabNotaPedido(viewModel.tabNotaPedidoViewModel)
  override val tabNotaNFD = TabNotaNFD(viewModel.tabNotaNFDViewModel)
  override val tabNotaColeta = TabNotaColeta(viewModel.tabNotaColetaViewModel)
  override val tabNotaNFDAberta = TabNotaNFDAberta(viewModel.tabNotaNFDAbertaViewModel)
  override val tabNotaTransportadora = TabNotaTransportadora(viewModel.tabNotaTransportadoraViewModel)
  override val tabPedidoGarantia = TabPedidoGarantia(viewModel.tabPedidoGarantiaViewModel)
  override val tabNotaGarantia = TabNotaGarantia(viewModel.tabNotaGarantiaViewModel)
  override val tabNotaEmail = TabNotaEmail(viewModel.tabNotaEmailViewModel)
  override val tabNotaReposto = TabNotaReposto(viewModel.tabNotaRepostoViewModel)
  override val tabNotaAcerto = TabNotaAcerto(viewModel.tabNotaAcertoViewModel)
  override val tabNotaAcertoPago = TabNotaAcertoPago(viewModel.tabNotaAcertoPagoViewModel)
  override val tabNotaAjuste = TabNotaAjuste(viewModel.tabNotaAjusteViewModel)
  override val tabNotaDescarte = TabNotaDescarte(viewModel.tabNotaDescarteViewModel)
  override val tabNotaNulo = TabNotaNulo(viewModel.tabNotaNuloViewModel)
  override val tabNotaFornecedor = TabNotaFornecedor(viewModel.tabNotaFornecedorViewModel)
  override val tabNotaUsr = TabNotaUsr(viewModel.tabNotaUsrViewModel)

  init {
    addTabSheat(viewModel)
  }
}