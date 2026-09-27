package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaNotaCancelada

class TabNotaCanceladaViewModel(val viewModel: VendaRefViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }
  
  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }
  
  fun updateView() {
    val filtro = subView.filtro()
    val itens = NotaSolicitaCancelar.findAll(filtro).filter {
      when(filtro.autorizada){
        ENotaAutorizada.NAO   -> it.loginCancel.isNullOrEmpty()
        ENotaAutorizada.SIM   -> it.loginCancel.isNullOrEmpty().not()
        ENotaAutorizada.TODAS -> true
      }
    }
    subView.updateNotas(itens)
  }
  
  fun geraPlanilha(vendas: List<NotaSolicitaCancelar>): ByteArray {
    val planilha = PlanilhaNotaCancelada()
    return planilha.write(vendas)
  }
  
  fun imprimeRelatorio() {
    TODO() //val notas = subView.itensNotasSelecionados()
    //val report = ReportVendaRef()
    //val file = report.processaRelatorio(notas)
    //viewModel.view.showReport(chave = "Vendas${System.nanoTime()}", report = file)
  }
  
  fun autorizaSolicitacao(nota: NotaSolicitaCancelar, solicitacaoCancelamento: SolicitacaoCancelamento?) =
    viewModel.exec {
        solicitacaoCancelamento ?: fail("Solicitação não informada")
        val login = solicitacaoCancelamento.login
        val senha = solicitacaoCancelamento.senha
        
        val user = UserSaci.userLogin(login, senha)
        user ?: fail("Usuário ou senha inválidos")
        
        nota.motivoEnum = solicitacaoCancelamento.motivo
        nota.userCancel = user.no
        nota.saveMotivo()
        updateView()
      }
  
  fun desfazSolicitacao(nota: NotaSolicitaCancelar) {
    nota.motivo = ""
    nota.userCancel = 0
    nota.saveMotivo()
    updateView()
  }
  
  val subView
    get() = viewModel.view.tabNotaCancelada
}

interface ITabNotaCancelada : ITabView {
  fun filtro(): FiltroSolicitaCancelar
  fun updateNotas(list: List<NotaSolicitaCancelar>)
  fun itensNotasSelecionados(): List<NotaSolicitaCancelar>
}