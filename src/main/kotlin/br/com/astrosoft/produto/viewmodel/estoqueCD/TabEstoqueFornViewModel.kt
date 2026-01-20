package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroFornecedorLoja
import br.com.astrosoft.produto.model.beans.FornecedorLoja
import br.com.astrosoft.produto.model.beans.LoginBean
import br.com.astrosoft.produto.model.beans.UserSaci

class TabEstoqueFornViewModel(val viewModel: EstoqueCDViewModel) {
  val subView
    get() = viewModel.view.tabEstoqueForn

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val list = FornecedorLoja.findAll(filtro)
    subView.updateGrid(list)
  }

  fun saveForn(bean: FornecedorLoja?) = viewModel.exec {
    bean ?: fail("Nenhum produto selecionado")
    bean.updateData()
    updateView()
  }

  fun formAutoriza(forn: FornecedorLoja, loja: Int) = viewModel.exec {
    subView.formAutoriza {
      val user = UserSaci.findUser(login)
      if (user.any { it.senha == senha }) {
        val userno = user.firstOrNull { it.senha == senha }?.no ?: 0
        forn.updateUserno(loja = loja, userno = userno)
        updateView()
      } else {
        viewModel.view.showError("Login ou senha inválidos")
      }
    }
  }

}

interface ITabEstoqueForn : ITabView {
  fun filtro(): FiltroFornecedorLoja
  fun updateGrid(itens: List<FornecedorLoja>)
  fun formAutoriza(block: LoginBean.() -> Unit)
}
