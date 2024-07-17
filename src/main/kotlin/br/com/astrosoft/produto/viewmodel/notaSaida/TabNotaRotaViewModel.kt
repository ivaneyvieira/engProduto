package br.com.astrosoft.produto.viewmodel.notaSaida

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.NotaExpedicao
import br.com.astrosoft.produto.model.printText.NotaExpedicaoEF
import br.com.astrosoft.produto.model.printText.NotaSeparacao
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class TabNotaRotaViewModel(val viewModel: NotaViewModel) {
  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val user = AppConfig.userLogin() as? UserSaci
    val marca = if (user?.admin == true)
      EMarcaNota.TODOS
    else
      EMarcaNota.TODOS
    val filtro = subView.filtro(marca)
    val notas = NotaSaida.find(filtro).filter {
      it.empnoMotorista != null && it.entrega != null
    }
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

  fun transportadoNota(nota: NotaSaida, numero: Int, data: LocalDate?) = viewModel.exec {
    val funcionario = saci.listFuncionario(numero) ?: fail("Funcionário não encontrado")
    val data = data ?: fail("Data não informada")
    if (funcionario.funcao != "MOTORISTA")
      fail("Funcionário não é motorista")
    nota.empnoMotorista = funcionario.codigo
    nota.entrega = data
    nota.save()
    updateView()
  }

  fun print() = viewModel.exec {
    val listNota = subView.itensSelecionados().ifEmpty { fail("Nenhuma nota selecionada") }

    val listNotaProduto = listNota.flatMap { nota ->
      val produtos = nota.produtos(EMarcaNota.TODOS)
      produtos.filter {
        it.local?.startsWith("CD5A") == true
      }.map { produto ->
        NotaSaidaProduto(
          motorista = nota.nomeMotorista ?: "",
          dataEntrega = nota.entrega,
          usernoPrint = nota.usernoPrint,
          usuarioPrint = nota.usuarioPrint,
          loja = nota.loja,
          pedido = nota.pedido.toString(),
          nota = nota.nota,
          data = nota.data,
          cliente = nota.cliente?.toString() ?: "",
          valorNota = nota.valorNota ?: 0.0,
          codigo = produto.codigo ?: "",
          descricao = produto.descricao ?: "",
          grade = produto.grade ?: "",
          quantidade = produto.quantidade ?: 0,
        )
      }
    }

    listNotaProduto.ifEmpty { fail("Nenhum produto selecionado com localização CD5A") }

    val user = AppConfig.userLogin() as? UserSaci
    val listaRota = listNota.mapNotNull { it.rota }.distinct().sorted()
    val userList = listNota.mapNotNull { it.usuarioPrint }.distinct().filter { it.trim().isNotEmpty() }.sorted()
      .ifEmpty { listOf(user?.name ?: "") }

    val report = NotaSeparacao(listaRota, userList)

    report.print(
      dados = listNotaProduto,
      printer = subView.printerPreview(loja = 0)
    )
  }

  val subView
    get() = viewModel.view.tabNotaRota
}

interface ITabNotaRota : ITabView {
  fun filtro(marca: EMarcaNota): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun findNota(): NotaSaida?
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNFS>
  fun itensSelecionados(): List<NotaSaida>
}