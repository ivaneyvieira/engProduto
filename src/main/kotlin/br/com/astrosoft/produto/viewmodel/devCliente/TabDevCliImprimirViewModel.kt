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

  fun ajusteProduto(ajuste: AjusteProduto) {
    val produto = ajuste.produto
    produto.marcaAjuste(ajuste)
  }

  /**************************** imprimeValeTroca ************************************/

  fun imprimeValeTroca(nota: EntradaDevCli) = viewModel.exec {
    val loginAutorizacao = nota.loginAutorizacao ?: ""
    if (loginAutorizacao.isBlank()) {
      fail("Devolução não foi autorizada.")
    }

    val notasAuto = nota.notaAtuoriza()
    val produtosDev = nota.produtos()
    val produtosNota = notasAuto.flatMap { it.produtos() }.filter {
      it.dev == true
    }

    if (produtosNota.isNotEmpty()) {
      produtosDev.forEach { prdDev ->
        val prdAuto = produtosNota.filter { prd ->
          prdDev.prdno == prd.prdno && prdDev.grade == prd.grade
        }

        if (prdAuto.isEmpty()) {
          //fail("Produto devolvido diferente do autorizado")
        }
      }
    }

    val user = AppConfig.userLogin() as? UserSaci
    val assinado = nota.nameAutorizacao?.isBlank() == false
    val valorNota = nota.valor ?: 0.00
    val valorDevolucao = user?.valorDevolucao ?: 0

    val relatorio = when {
      assinado                         -> {
        ValeTrocaDevolucao(nota = nota)
      }

      nota.tipoObs.startsWith("TROCA") -> {

        if (nota.isComProduto()) {
          if (valorNota > valorDevolucao) {
            fail("Valor da nota maior (${valorNota.format()}) que o permitido para troca sem autorização (${valorDevolucao.format()})")
          }
          ValeTrocaDevolucao(nota = nota)
        } else {
          if (valorNota > valorDevolucao) {
            fail("Valor da nota maior (${valorNota.format()}) que o permitido para troca sem autorização (${valorDevolucao.format()})")
          }
          ValeTrocaDevolucao(nota = nota)
        }
      }

      nota.tipoObs.startsWith("EST")   -> {
        if (valorNota > valorDevolucao) {
          fail("Valor da nota maior (${valorNota.format()}) que o permitido para troca sem autorização (${valorDevolucao.format()})")
        }
        ValeTrocaDevolucao(nota = nota)
      }

      nota.tipoObs.startsWith("REEMB") -> {
        if (valorNota > valorDevolucao) {
          fail("Valor da nota maior (${valorNota.format()}) que o permitido para troca sem autorização (${valorDevolucao.format()})")
        }
        ValeTrocaDevolucao(nota = nota)
      }

      nota.tipoObs.startsWith("MUDA")  -> {
        if (valorNota > valorDevolucao) {
          fail("Valor da nota maior (${valorNota.format()}) que o permitido para troca sem autorização (${valorDevolucao.format()})")
        }
        ValeTrocaDevolucao(nota = nota)
      }

      else                             -> {
        fail("Nota não assinada")
      }
    }

    relatorio.print(nota.produtos(), subView.printerPreview(loja = 0) { impressora ->
      nota.marcaImpresso(Impressora(0, impressora))
      updateView()
    })
  }

  /**************************** imprimeValeTroca ************************************/

  fun formAutoriza(nota: EntradaDevCli) = viewModel.exec {
    subView.formAutoriza(nota)
  }

  fun autorizaNota(nota: EntradaDevCli, login: String, senha: String) = viewModel.exec {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.equals(login, ignoreCase = true) && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    if (!user.admin) {
      if (user.lojaUsuario != nota.loja) {
        fail("Usuário não autorizado para esta loja")
      }

      when {
        nota.tipoObs.startsWith("TROCA")                                                   -> {
          if (nota.isComProduto() && !user.autorizaTrocaP) {
            fail("Usuário não autorizado para Troca P")
          }
          if (!nota.isComProduto() && !user.autorizaTroca) {
            fail("Usuário não autorizado para Troca")
          }
        }

        nota.tipoObs.startsWith("EST") && !user.autorizaEstorno                            -> {
          fail("Usuário não autorizado para Estorno")
        }

        nota.tipoObs.startsWith("REEMB") && !user.autorizaReembolso                        -> {
          fail("Usuário não autorizado para Reembolso")
        }

        nota.tipoObs.startsWith("MUDA") && !user.autorizaMuda                              -> {
          fail("Usuário não autorizado para Muda Cliente")
        }

        nota.tipoObs.startsWith("TROCA M") && !(user.autorizaTrocaP && user.autorizaTroca) -> {
          fail("Usuário não autorizado para Troca Mista")
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
  fun ajustaProduto(nota: EntradaDevCli)
}
