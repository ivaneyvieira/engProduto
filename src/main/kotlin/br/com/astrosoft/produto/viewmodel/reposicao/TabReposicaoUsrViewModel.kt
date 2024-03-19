package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci

class TabReposicaoUsrViewModel(val viewModel: ReposicaoViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val usuarios = usuarios()
    subView.updateUsuarios(usuarios)
  }

  private fun usuarios(): List<UserSaci> {
    val usuarios = UserSaci.findAll().filter {
      it.reposicao
    }
    return usuarios
  }

  fun salva(bean: UserSaci) {
    UserSaci.updateUser(bean)
    updateView()
  }

  fun adicionaUsuario() = viewModel.exec {
    subView.formAddUsuario()
  }

  fun addUser(userSaci: UserSaci) {
    val user = UserSaci.findUser(userSaci.login)
    user.forEach {
      it.reposicaoEnt = userSaci.reposicaoEnt
      it.reposicaoSep = userSaci.reposicaoSep
      it.impressoraRepo = userSaci.impressoraRepo
      it.localizacaoRepo = userSaci.localizacaoRepo
      salva(it)
    }
    updateView()
  }

  fun modificarUsuario() = viewModel.exec {
    val usuario = subView.selectedItem() ?: fail("Usuário não selecionado")
    subView.formUpdUsuario(usuario)

  }

  fun updUser(userSaci: UserSaci) {
    salva(userSaci)
    updateView()
  }

  fun removeUsuario() = viewModel.exec {
    val usuario = subView.selectedItem() ?: fail("Usuário não selecionado")
    viewModel.view.showQuestion("Deseja remover o usuário ${usuario.name} da reposição?") {
      usuario.reposicao = false
      salva(usuario)
      updateView()
    }
  }

  val subView
    get() = viewModel.view.tabReposicaoUsr
}

interface ITabReposicaoUsr : ITabView {
  fun updateUsuarios(usuarios: List<UserSaci>)
  fun formAddUsuario()
  fun selectedItem(): UserSaci?
  fun formUpdUsuario(usuario: UserSaci)
}

