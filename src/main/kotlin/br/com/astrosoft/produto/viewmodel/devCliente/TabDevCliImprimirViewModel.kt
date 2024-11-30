package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.ValeTrocaDevolucao

class TabDevCliImprimirViewModel(val viewModel: DevClienteViewModel) {
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
    val user = AppConfig.userLogin() as? UserSaci
    val assinado = nota.nameAutorizacao?.isBlank() == false
    val valorNota = nota.valor ?: 0.00
    val valorLimit = user?.valorMinimoTroca ?: 500

    val relatorio = when {
      assinado                         -> {
        ValeTrocaDevolucao(nota = nota, autorizacao = nota.nameAutorizacao ?: "")
      }

      nota.tipoObs.startsWith("TROCA") -> {

        if (nota.isComProduto()) {
          if (valorNota > valorLimit) {
            fail("Valor da nota maior (${valorNota.format()}) que o permitido para troca sem autorização (${valorLimit.format()})")
          }
          ValeTrocaDevolucao(nota = nota, autorizacao = nota.nameAutorizacao ?: "")
        } else {
          fail("Nota não assinada")
        }
      }

      nota.tipoObs.startsWith("EST") ||
      nota.tipoObs.startsWith("REEMB") ||
      nota.tipoObs.startsWith("MUDA")  -> {
        fail("Nota não assinada")
      }

      else                             -> fail("Tipo de devolução não informado")
    }



    ValeTrocaDevolucao(nota = nota, autorizacao = nota.nameAutorizacao ?: "")

    relatorio.print(nota.produtos(), subView.printerPreview(loja = 0) { impressora ->
      nota.marcaImpresso(Impressora(0, impressora))
      updateView()
    })
  }

  fun formAutoriza(nota: EntradaDevCli) {
    //if (nota.tipoDev.isNullOrBlank()) fail("Tipo de devolução não informado")
    subView.formAutoriza(nota)
  }

  fun autorizaNota(nota: EntradaDevCli, login: String, senha: String) = viewModel.exec {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    if (!user.admin) {
      if (user.lojaUsuario != nota.loja) {
        fail("Usuário não autorizado para esta loja")
      }

      when {
        nota.tipoObs.startsWith("TROCA")                            -> {
          if (nota.isComProduto() && !user.autorizaTrocaP) {
            fail("Usuário não autorizado para Troca P")
          }
          if (!nota.isComProduto() && !user.autorizaTroca) {
            fail("Usuário não autorizado para Troca")
          }
        }

        nota.tipoObs.startsWith("EST") && !user.autorizaEstorno     -> {
          fail("Usuário não autorizado para Estorno")
        }

        nota.tipoObs.startsWith("REEMB") && !user.autorizaReembolso -> {
          fail("Usuário não autorizado para Reembolso")
        }

        nota.tipoObs.startsWith("MUDA") && !user.autorizaMuda       -> {
          fail("Usuário não autorizado para Muda Cliente")
        }
      }
    }

    nota.autoriza(user)

    updateView()
  }

  val subView
    get() = viewModel.view.tabDevCliImprimir
}

interface ITabDevCliImprimir : ITabView {
  fun filtro(): FiltroEntradaDevCli
  fun updateNotas(notas: List<EntradaDevCli>)

  fun formAutoriza(nota: EntradaDevCli)
}