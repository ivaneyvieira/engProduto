package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*

class TabDevCliAutorizacaoViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaAutorizacao.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun addNota(chave: NotaAutorizacaoChave) = viewModel.exec {
    val notaPesquisa = NotaAutorizacao.findNota(chave.loja, chave.notaFiscal) ?: fail("Nota não encontrada")

    if (notaPesquisa.ni != null) fail("Nota já devolvida")
    else {
      NotaAutorizacao.insert(chave)
      updateView()
    }
  }

  fun deleteNota(notas: List<NotaAutorizacao>) = viewModel.exec {
    notas.forEach { nota ->
      nota.delete()
    }
    updateView()
  }

  fun updateAutorizacao(bean: NotaAutorizacao?) {
    bean?.let {
      it.update()
      updateView()
    }
  }

  fun formAutoriza(nota: NotaAutorizacao) {
    subView.formAutoriza(nota)
  }

  fun autorizaNota(nota: NotaAutorizacao, login: String, senha: String) = viewModel.exec {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    if (!user.admin) {
      val lojaUserSaci = user.lojaUsuario
      val lojaNoto = nota.loja ?: fail("Loja destino não encontrada")
      if (lojaUserSaci != lojaNoto) fail("Usuário não autorizado para esta loja")
    }

    nota.autoriza(user)

    updateView()
  }

  val subView
    get() = viewModel.view.tabDevCliAutorizacao
}

interface ITabDevCliAutorizacao : ITabView {
  fun filtro(): FiltroNotaAutorizacao
  fun updateNotas(notas: List<NotaAutorizacao>)
  fun formAutoriza(nota: NotaAutorizacao)
}