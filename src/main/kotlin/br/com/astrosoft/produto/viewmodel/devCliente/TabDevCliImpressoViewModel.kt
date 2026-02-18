package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaImpresso
import br.com.astrosoft.produto.model.printText.ProdutosDevolucao
import br.com.astrosoft.produto.model.printText.ValeTrocaDevolucao
import br.com.astrosoft.produto.model.report.ReportImpresso

class TabDevCliImpressoViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = EntradaDevCli.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun imprimeValeTroca(nota: EntradaDevCli) = viewModel.exec {

    val relatorio = ValeTrocaDevolucao(nota)
    relatorio.print(nota.produtos(), subView.printerPreview(showPrinter = AppConfig.isAdmin, loja = 0) {
      updateView()
    })
  }

  fun imprimeRelatorio() {
    val notas = subView.itensNotasSelecionados()
    val report = ReportImpresso()
    val file = report.processaRelatorio(notas)
    viewModel.view.showReport(chave = "NotaImpresso${System.nanoTime()}", report = file)
  }

  fun geraPlanilha(): ByteArray {
    val notas = subView.itensNotasSelecionados()
    val planilha = PlanilhaImpresso()
    return planilha.write(notas)
  }

  fun imprimeProdutos() {
    val listNi = subView.itensNotasSelecionados().map { it.invno }
    val produtos = EntradaDevCliProList.findAll(listNi)
    if (produtos.isEmpty()) {
      fail("Não há produtos selecionados")
    }
    val relatorio = ProdutosDevolucao("Devolucoes de Clientes")
    relatorio.print(produtos.sortedBy { it.ni }, subView.printerPreview(loja = 0))
  }

  fun autorizaNota(nota: EntradaDevCli, login: String, senha: String) {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.equals(login, ignoreCase = true) && it.senha?.uppercase()?.trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    if (!user.admin) {
      val lojaUserSaci = user.lojaUsuario
      val lojaNoto = nota.loja
      if (lojaUserSaci != lojaNoto) fail("Usuário não autorizado para esta loja")
    }

    nota.autoriza(user)

    updateView()
  }

  fun ajusteProduto(ajuste: AjusteProduto) {
    val produto = ajuste.produto
    produto.marcaAjuste(ajuste)
  }

  fun salvaNfEntRet(nota: NotaVenda, nfEntRet: Int) {
    nota.nfEntRet = nfEntRet
    nota.salvaNfEntRet()
  }

  val subView
    get() = viewModel.view.tabDevCliImpresso
}

interface ITabDevCliImpresso : ITabView {
  fun filtro(): FiltroEntradaDevCli
  fun updateNotas(notas: List<EntradaDevCli>)
  fun itensNotasSelecionados(): List<EntradaDevCli>
  fun ajustaProduto(nota: EntradaDevCli)
}