package br.com.astrosoft.framework.viewmodel

import br.com.astrosoft.produto.model.beans.Impressora
import br.com.astrosoft.produto.model.beans.Local
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci

abstract class TabUsrViewModel(val vm: ViewModel<*>) {
  abstract val subView: ITabUser
  private fun allLocais(): List<Local> = Local.all()
  fun allLocalizacao(): List<String> {
    return allLocais().mapNotNull { it.abreviacao }.distinct().sorted()
  }

  fun allTermica(): List<Impressora> = Impressora.allTermica()
  fun allEtiqueta(): List<Impressora> = Impressora.allEtiqueta()
  fun allImpressoras(): List<Impressora> = Impressora.all()

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun modificarUsuario() = vm.exec {
    val usuario = subView.selectedItem() ?: fail("Usuário não selecionado")
    subView.formUpdUsuario(usuario)
  }

  fun adicionaUsuario() = vm.exec {
    subView.formAddUsuario()
  }

  abstract fun UserSaci.desative()
  abstract fun UserSaci.isActive(): Boolean
  abstract fun UserSaci.update(usuario: UserSaci)

  fun removeUsuario() = vm.exec {
    val usuario = subView.selectedItem() ?: fail("Usuário não selecionado")
    vm.view.showQuestion("Deseja remover o usuário ${usuario.name} da reposição?") {
      usuario.desative()
      UserSaci.updateUser(usuario)
      updateView()
    }
  }

  fun updateView() = vm.exec {
    val filter = subView.filter()
    val usuarios = usuarios().filter {
      (filter == "") ||
      (it.no.toString() == filter) ||
      (it.name?.contains(filter, ignoreCase = true) == true) ||
      (it.login?.startsWith(filter, ignoreCase = true) == true)
    }
    subView.updateUsuarios(usuarios)
  }

  private fun usuarios(): List<UserSaci> {
    val usuarios = UserSaci.findAll().filter {
      it.isActive()
    }
    return usuarios
  }

  fun updUser(usuario: UserSaci) {
    UserSaci.updateUser(usuario)
    updateView()
  }

  fun addUser(userSaci: UserSaci) {
    val user = UserSaci.findUser(userSaci.login)
    user.forEach { u ->
      u.ativo = true
      u.update(userSaci)
      UserSaci.updateUser(u)
    }
    updateView()
  }
}

interface ITabUser : ITabView {
  fun filter(): String
  fun updateUsuarios(usuarios: List<UserSaci>)
  fun formAddUsuario()
  fun selectedItem(): UserSaci?
  fun formUpdUsuario(usuario: UserSaci)
}