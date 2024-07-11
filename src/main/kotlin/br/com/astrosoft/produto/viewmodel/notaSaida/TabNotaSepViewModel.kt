package br.com.astrosoft.produto.viewmodel.notaSaida

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.NotaExpedicao
import br.com.astrosoft.produto.model.printText.NotaExpedicaoEF
import br.com.astrosoft.produto.model.printText.NotaSeparacao
import br.com.astrosoft.produto.model.saci

class TabNotaSepViewModel(val viewModel: NotaViewModel) {
  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val user = AppConfig.userLogin() as? UserSaci
    val marca = if (user?.admin == true)
      EMarcaNota.TODOS
    else
      EMarcaNota.EXP
    val filtro = subView.filtro(marca)
    val notas = NotaSaida.find(filtro)
    subView.updateNotas(notas)
  }

  fun findGrade(prd: ProdutoNFS?, block: (List<PrdGrade>) -> Unit) = viewModel.exec {
    prd ?: return@exec
    val list = prd.findGrades()
    block(list)
  }

  fun imprimeProdutosNota(nota: NotaSaida, itensSelecionados: List<ProdutoNFS>) = viewModel.exec {
    if (itensSelecionados.isEmpty())
      fail("Nenhum produto selecionado")
    if (nota.cancelada == "S")
      fail("Nota cancelada")
    val tipo = nota.tipoNotaSaida ?: ""
    val report = if (tipo == "ENTRE_FUT") NotaExpedicaoEF(nota) else NotaExpedicao(nota)
    report.print(
      dados = itensSelecionados,
      printer = subView.printerPreview(loja = nota.loja) {
        itensSelecionados.forEach { produto ->
          produto.marcaImpressao()
        }
      },
    )
  }

  fun save(bean: NotaSaida?) {
    bean ?: return
    bean.save()
    updateView()
  }

  fun formAutoriza(nota: NotaSaida) {
    subView.formTransportado(nota)
  }

  fun transportadoNota(nota: NotaSaida, numero: Int) = viewModel.exec {
    val funcionario = saci.listFuncionario(numero) ?: fail("Funcionário não encontrado")
    if (funcionario.funcao != "MOTORISTA")
      fail("Funcionário não é motorista")
    nota.empnoMotorista = funcionario.codigo
    nota.save()
    updateView()
  }

  fun print() = viewModel.exec {
    val listNota = subView.itensSelecionados().ifEmpty { fail("Nenhuma nota selecionada") }

    val listaRota = listNota.mapNotNull { it.rota }.distinct().sorted()

    val report = NotaSeparacao(listaRota)


    report.print(
      dados = listNota,
      printer = subView.printerPreview(loja = 0)
    )
  }

  val subView
    get() = viewModel.view.tabNotaSep
}

interface ITabNotaSep : ITabView {
  fun filtro(marca: EMarcaNota): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun findNota(): NotaSaida?
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNFS>
  fun formTransportado(nota: NotaSaida)
  fun itensSelecionados(): List<NotaSaida>
}