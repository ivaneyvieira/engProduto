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

  fun autorizaSolicitacao(nota: DadosDev, solicitacaoTroca: SolicitacaoTrocaSimples) = viewModel.exec {
    nota.tipoDevEnum = solicitacaoTroca.solicitacaoTrocaEnnum
    nota.produtoTrocaEnum = solicitacaoTroca.produtoTrocaEnum
    nota.userSolicitacao = null
    nota.update()

    updateView()
  }

  fun salvaNfEntRet(nota: DadosDev, nfNumero: Int) = viewModel.exec {
    nota.nfEntRet = nfNumero
    nota.salvaNfEntRet()
  }

  fun validaProcesamento(user: UserSaci?, nota: DadosDev, produtos: List<DadosDevProduto>): Boolean {
    try {
      val assina = nota.loginTroca
      if(assina != null){
        fail("Troca já Assinada")
      }

      user ?: fail("Usuário inválido")
      produtos.ifEmpty {
        fail("Nenhum produto selecionado")
      }

      val solicitacao = nota.tipoDevEnum ?: fail("Tipo de devolução não informada")
      val produto = nota.produtoTrocaEnum ?: fail("Tipo de devolução (com ou sem produto) não informada")

      val produtosDevComProduto = produtos.sumOf { it.quantidadeCom ?: 0 }
      val produtosDevSemProduto = produtos.sumOf { it.quantidadeSem ?: 0 }

      val tipoResultante = when {
        produtosDevComProduto > 0 && produtosDevSemProduto == 0 -> EProdutoTroca.Com
        produtosDevComProduto == 0 && produtosDevSemProduto > 0 -> EProdutoTroca.Sem
        produtosDevComProduto > 0 && produtosDevSemProduto > 0  -> EProdutoTroca.Misto
        else                                                    -> fail("Não há produtos para devolução")
      }

      if (tipoResultante != produto) {
        fail("Divergência: No filtro marcado ${produto.descricao} e na linha do produto marcado como ${tipoResultante.descricao}")
      }
      /*********************************************************************************/

      produtos.forEach { prd ->
        val qtdCom = prd.quantidadeCom ?: 0
        val qtdSem = prd.quantidadeSem ?: 0
        val quantDev = prd.quantidadeDev ?: 0
        if (quantDev != (qtdSem + qtdCom)) {
          fail("Quantidade devolvida diferente da autorizada")
        }
      }

      val valorProdutos = produtos.sumOf { prd ->
        prd.valorTotal
      }
      val valorDevolucao = user.valorDevolucao

      when (solicitacao) {
        ESolicitacaoTroca.Troca       -> {

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

        ESolicitacaoTroca.Estorno     -> {
          if (valorProdutos > valorDevolucao) {
            fail("Valor da devolução maior que o autorizado")
          }
        }

        ESolicitacaoTroca.Reembolso   -> {
          if (valorProdutos > valorDevolucao) {
            fail("Valor da devolução maior que o autorizado")
          }
        }

        ESolicitacaoTroca.MudaCliente -> {
          if (valorProdutos > valorDevolucao) {
            fail("Valor da devolução maior que o autorizado")
          }
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
    val userno = nota.userSolicitacao ?: fail("Usuário ou senha inválidos")
    val user = UserSaci.findUser(userno) ?: fail("Usuário ou senha inválidos")
    imprimeValeTroca(nota, user)
  }

  fun imprimeValeTroca(nota: DadosDev, login: String, senha: String) = viewModel.exec {
    val user = UserSaci.userLogin(login, senha)
    user ?: fail("Usuário ou senha inválidos")

    imprimeValeTroca(nota, user)
  }

  private fun imprimeValeTroca(
    nota: DadosDev,
    user: UserSaci
  ) {
    val solicitacaoTrocaEnum = nota.tipoDevEnum ?: fail("Tipo de Crédito não Informado")
    val produtoTrocaEnum = nota.produtoTrocaEnum ?: fail("Tipo da Devolução não informada")
    val login = user.login

    when (solicitacaoTrocaEnum) {
      ESolicitacaoTroca.Troca       -> when (produtoTrocaEnum) {
        EProdutoTroca.Com   -> if (!user.autorizaImpTrocaP) {
          fail("Impressão de Troca com produto não autorizada para usuário $login")
        }

        EProdutoTroca.Sem   -> if (!user.autorizaImpTroca) {
          fail("Impressão de Troca sem produto não autorizada para usuário $login")
        }

        EProdutoTroca.Misto -> if (!user.autorizaImpTrocaP || !user.autorizaImpTroca) {
          fail("Impressão de Troca mista de produto não autorizada para usuário $login")
        }
      }

      ESolicitacaoTroca.Estorno     -> if (!user.autorizaImpEstorno) {
        fail("Impressão de Estorno de produto não autorizado para usuário $login")
      }

      ESolicitacaoTroca.Reembolso   -> if (!user.autorizaImpReembolso) {
        fail("Impressão de Reembolso de produto não autorizado para usuário $login")
      }

      ESolicitacaoTroca.MudaCliente -> if (!user.autorizaImpMuda) {
        fail("Impressão de Mudança de cliente não autorizada para usuário $login")
      }
    }

    nota.tipoDevEnum = solicitacaoTrocaEnum
    nota.produtoTrocaEnum = produtoTrocaEnum
    nota.userSolicitacao = user.no
    nota.loginSolicitacao = user.login
    nota.nomeSolicitacao = user.name
    nota.update()

    imprime(nota)
  }

  private fun imprime(nota: DadosDev) {
    val relatorio = ValeTrocaDadosDev(nota)

    val dados = nota.produtos
    val printer = subView.printerPreview(loja = 0) { impressora ->
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
