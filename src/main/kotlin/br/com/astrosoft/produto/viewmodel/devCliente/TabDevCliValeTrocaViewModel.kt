package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.ValeTrocaDevolucao

class TabDevCliValeTrocaViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = EntradaDevCli.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun imprimeValeTroca(nota: EntradaDevCli) = viewModel.exec {
    val relatorio = if (nota.isComProduto()) {
      ValeTrocaDevolucao(nota)
    } else {
      val userName = nota.nameAutorizacao ?: ""
      if (userName.isBlank()) {
        fail("Devolução de Cliente sem produto, autorizar para imprimir")
      }else {
        ValeTrocaDevolucao(nota, userName)
      }
    }

    relatorio.print(nota.produtos(), subView.printerPreview(loja = 0) { impressora ->
      nota.marcaImpresso(Impressora(0, impressora))
      updateView()
    })
  }

  fun formAutoriza(nota: EntradaDevCli) {
    //if (nota.tipoDev.isNullOrBlank()) fail("Tipo de devolução não informado")
    subView.formAutoriza(nota)
  }

  fun autorizaNota(nota: EntradaDevCli, login: String, senha: String) {
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
    get() = viewModel.view.tabDevCliValeTroca
}

interface ITabDevCliValeTroca : ITabView {
  fun filtro(): FiltroEntradaDevCli
  fun updateNotas(notas: List<EntradaDevCli>)

  fun formAutoriza(nota: EntradaDevCli)
}