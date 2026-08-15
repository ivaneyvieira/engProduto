package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.ValeTrocaDadosDev

class TabDevDadosViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = DadosDev.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun ajusteProduto(ajuste: AjusteProduto) {
    val produto = ajuste.produto
    produto.marcaAjuste(ajuste)
  }

  fun desfazSolicitacao(nota: DadosDev) {
    nota.apagaDados()
    updateView()
  }

  fun autorizaSolicitacao(nota: DadosDev, solicitacaoTroca: SolicitacaoTroca) = viewModel.exec {
    val login = solicitacaoTroca.login
    val senha = solicitacaoTroca.senha
    val user = UserSaci.userLogin(login, senha)
    user ?: fail("Usuário ou senha inválidos")

    when (solicitacaoTroca.solicitacaoTrocaEnnum) {
      ESolicitacaoTroca.Troca       -> when (solicitacaoTroca.produtoTrocaEnum) {
        EProdutoTroca.Com   -> if (!user.autorizaTrocaP) {
          fail("Troca com produto não autorizada")
        }

        EProdutoTroca.Sem   -> if (!user.autorizaTroca) {
          fail("Troca sem produto não autorizada")
        }

        EProdutoTroca.Misto -> if (!user.autorizaTrocaP || !user.autorizaTroca) {
          fail("Troca mista de produto não autorizada")
        }
      }

      ESolicitacaoTroca.Estorno     -> if (!user.autorizaEstorno) {
        fail("Estorno de produto não autorizado")
      }

      ESolicitacaoTroca.Reembolso   -> if (!user.autorizaReembolso) {
        fail("Reembolso de produto não autorizado")
      }

      ESolicitacaoTroca.MudaCliente -> if (!user.autorizaMuda) {
        fail("Mudança de cliente não autorizada")
      }
    }

    nota.tipoDevEnum = solicitacaoTroca.solicitacaoTrocaEnnum
    nota.produtoTrocaEnum = solicitacaoTroca.produtoTrocaEnum
    nota.userSolicitacao = user.no
    nota.update()

    updateView()
  }

  fun salvaNfEntRet(nota: DadosDev, nfNumero: Int) = viewModel.exec {
    nota.nfEntRet = nfNumero
    nota.salvaNfEntRet()
  }

  fun validaProcesamento(user: UserSaci?, nota: DadosDev, produtos: List<DadosDevProduto>): Boolean {
    try {
      user ?: fail("Usuário inválido")
      val produtosDev = produtos
        .filter { it.dev == true }
      produtosDev.ifEmpty {
        fail("Nenhum produto selecionado")
      }

      val solicitacao = nota.tipoDevEnum ?: fail("Tipo de devolução não informada")
      val produto = nota.produtoTrocaEnum ?: fail("Tipo de devolução (com ou sem produto) não informada")

      val produtosDevComProduto = produtosDev.filter { it.temProduto == true }
      val produtosDevSemProduto = produtosDev.filter { it.temProduto == false }

      val tipoResultante = when {
        produtosDevComProduto.isNotEmpty() && produtosDevSemProduto.isEmpty() -> EProdutoTroca.Com
        produtosDevComProduto.isEmpty() && produtosDevSemProduto.isNotEmpty() -> EProdutoTroca.Sem
        else                                                                  -> EProdutoTroca.Misto
      }

      if (tipoResultante != produto) {
        fail("Divergência: No filtro marcado ${produto.descricao} e na linha do produto marcado como ${tipoResultante.descricao}")
      }
      /*********************************************************************************/

      val valorProdutos = produtosDev.sumOf { prd ->
        (prd.quantidadeTipo ?: 0) * 1.0 * (prd.valorUnitario ?: 0.00)
      }
      val valorDevolucao = user.valorDevolucao

      when {
        solicitacao == ESolicitacaoTroca.Troca       -> {

          if (produto == EProdutoTroca.Com) {
            if (valorProdutos > valorDevolucao) {
              fail("Valor da devolução maior que o autorizado")
            }
          } else {
            if (valorProdutos > valorDevolucao) {
              fail("Valor da devolução maior que o autorizado")
            }
          }
        }

        solicitacao == ESolicitacaoTroca.Estorno     -> {
          if (valorProdutos > valorDevolucao) {
            fail("Valor da devolução maior que o autorizado")
          }
        }

        solicitacao == ESolicitacaoTroca.Reembolso   -> {
          if (valorProdutos > valorDevolucao) {
            fail("Valor da devolução maior que o autorizado")
          }
        }

        solicitacao == ESolicitacaoTroca.MudaCliente -> {
          if (valorProdutos > valorDevolucao) {
            fail("Valor da devolução maior que o autorizado")
          }
        }

        else                                         -> {
          //Não faz nada
        }
      }

      /***********************************************************************************/

    } catch (e: Exception) {
      val msg = e.message
      viewModel.view.showWarning(msg ?: "Erro genérico")
      return false
    }
    return true
  }

  fun autorizaNotaVenda(nota: DadosDev, produtos: List<DadosDevProduto>, login: String, senha: String) =
      viewModel.exec {
        nota.tipoDevEnum ?: fail("Nota sem solicitação de troca")
        nota.produtoTrocaEnum ?: fail("Nota sem produto de troca")

        val user = UserSaci.userLogin(login, senha)

        if (!validaProcesamento(user, nota, produtos)) {
          return@exec
        }

        user ?: fail("Usuário inválido")

        if (!user.autorizaDev) {
          fail("Usuário sem permissão para autorizar devolução")
        }

        nota.userTroca = user.no
        nota.update()
        produtos.forEach { prd ->
          prd.update()
        }
        subView.fechaFormProduto()
        subView.updateProdutos()
        updateView()
      }

  fun desautorizaTroca(nota: DadosDev, produto: DadosDevProduto) = viewModel.exec {
    viewModel.view.showQuestion("Confirma desautorizar devolução do produto ${produto.codigo}?") {
      viewModel.exec {
        val user = AppConfig.userLogin() as? UserSaci

        if (user?.desautorizaDev == false) {
          fail("Usuário sem permissão")
        }

        if (produto.dev == false) {
          fail("Solicitação não foi autorizada")
        }

        if ((produto.ni ?: 0) != 0) {
          fail("A nota de devolução já foi emitida")
        }

        produto.deleteDados()

        subView.updateProdutos()
      }
    }
  }

  /**************************** imprimeValeTroca ************************************/

  fun imprimeValeTroca(nota: DadosDev) = viewModel.exec {
    if (nota.naoLiberado()) {
      fail("Liberar impressão para: Estorno, Reembolso, Muda Cliente e Sem Produto")
    }

    val loginAutorizacao = nota.loginTroca ?: ""
    if (loginAutorizacao.isBlank()) {
      fail("Devolução não foi autorizada.")
    }

    val relatorio = ValeTrocaDadosDev(nota)

    val dados = nota.produtos.filter {
      it.dev == true
    }
    val printer = subView.printerPreview(loja = 0) { impressora ->
      //nota.marcaImpresso(Impressora(0, impressora))
      updateView()
    }

    relatorio.print(
      dados = dados, printer = printer
    )
  }

  /**************************** imprimeValeTroca ************************************/

  val subView
    get() = viewModel.view.tabDevDados
}

interface ITabDevDados : ITabView {
  fun filtro(): FiltroDadosDev
  fun updateNotas(notas: List<DadosDev>)
  fun updateProdutos()

  fun fechaFormProduto()
}
