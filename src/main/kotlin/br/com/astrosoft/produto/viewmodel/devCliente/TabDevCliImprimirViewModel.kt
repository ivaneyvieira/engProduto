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

  fun imprimeValeTroca(nota: EntradaDevCli) = viewModel.exec {
    /*
    val notasAuto = nota.notaAtuoriza()

    notasAuto.forEach { notaAuto ->
      val motivoAuto = notaAuto.motivo()?.uppercase() ?: ""
      nota.nameAutorizacao = notaAuto.nameTroca
      val motivoDev = nota.tipoObs.uppercase()
      if (motivoDev != motivoAuto) {
        fail("Motivos divergentes entre as notas autorizadas e devolvidas: $motivoAuto - $motivoDev")
      }
    }

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
          fail("Produto devolvido diferente do autorizado")
        }

        val qtDev = prdDev.tipoQtdEfetiva ?: 0
        val qtAuto = prdAuto.sumOf { it.quantDev ?: 0 }
        if (qtDev != qtAuto) {
          fail("Quantidade devolvida diferente da autorizada")
        }
      }
    }*/

    if (!nota.temAjusteMisto()) {
      val user = AppConfig.userLogin() as? UserSaci
      if (user?.ajustaMista != true) {
        fail("Usuário sem permissão para ajuste misto")
      }
      subView.ajustaProduto(nota)
      return@exec
    }

    val user = AppConfig.userLogin() as? UserSaci
    val assinado = nota.nameAutorizacao?.isBlank() == false
    val valorNota = nota.valor ?: 0.00
    val valorLimitTrocap = user?.valorMinimoTrocaP ?: 500
    val valorLimitTroca = user?.valorMinimoTroca ?: 0
    val valorLimitEstorno = user?.valorMinimoEstorno ?: 0
    val valorLimitReembolso = user?.valorMinimoReembolso ?: 0
    val valorLimitMuda = user?.valorMinimoMuda ?: 0

    val relatorio = when {
      assinado                         -> {
        ValeTrocaDevolucao(nota = nota, autorizacao = nota.nameAutorizacao ?: "")
      }

      nota.tipoObs.startsWith("TROCA") -> {

        if (nota.isComProduto()) {
          if (valorLimitTrocap == 0) {
            fail("Nota não assinada")
          } else if (valorNota > valorLimitTrocap) {
            fail("Valor da nota maior (${valorNota.format()}) que o permitido para troca sem autorização (${valorLimitTrocap.format()})")
          }
          ValeTrocaDevolucao(nota = nota, autorizacao = nota.nameAutorizacao ?: "")
        } else {
          if (valorLimitTroca == 0) {
            fail("Nota não assinada")
          } else if (valorNota > valorLimitTroca) {
            fail("Valor da nota maior (${valorNota.format()}) que o permitido para troca sem autorização (${valorLimitTroca.format()})")
          }
          ValeTrocaDevolucao(nota = nota, autorizacao = nota.nameAutorizacao ?: "")
        }
      }

      nota.tipoObs.startsWith("EST")   -> {
        if (valorLimitEstorno == 0) {
          fail("Nota não assinada")
        } else if (valorNota > valorLimitEstorno) {
          fail("Valor da nota maior (${valorNota.format()}) que o permitido para troca sem autorização (${valorLimitEstorno.format()})")
        }
        ValeTrocaDevolucao(nota = nota, autorizacao = nota.nameAutorizacao ?: "")
      }

      nota.tipoObs.startsWith("REEMB") -> {
        if (valorLimitReembolso == 0) {
          fail("Nota não assinada")
        } else if (valorNota > valorLimitReembolso) {
          fail("Valor da nota maior (${valorNota.format()}) que o permitido para troca sem autorização (${valorLimitReembolso.format()})")
        }
        ValeTrocaDevolucao(nota = nota, autorizacao = nota.nameAutorizacao ?: "")
      }

      nota.tipoObs.startsWith("MUDA")  -> {
        if (valorLimitMuda == 0) {
          fail("Nota não assinada")
        } else if (valorNota > valorLimitMuda) {
          fail("Valor da nota maior (${valorNota.format()}) que o permitido para troca sem autorização (${valorLimitMuda.format()})")
        }
        ValeTrocaDevolucao(nota = nota, autorizacao = nota.nameAutorizacao ?: "")
      }

      else                             -> {
        fail("Nota não assinada")
      }
    }

    ValeTrocaDevolucao(nota = nota, autorizacao = nota.nameAutorizacao ?: "")

    relatorio.print(nota.produtos(), subView.printerPreview(loja = 0) { impressora ->
      nota.marcaImpresso(Impressora(0, impressora))
      updateView()
    })
  }

  fun formAutoriza(nota: EntradaDevCli) = viewModel.exec {
    if (!nota.temAjusteMisto()) fail("Tipo de devolução não informado")
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

        nota.tipoObs.startsWith("TROCA M") && !user.autorizaMista   -> {
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
  fun ajustaProduto(nota: EntradaDevCli)
}