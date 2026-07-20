package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.DadosCredito

class PlanilhaDadosCredito : Planilha<DadosCredito>("Dados do Credito") {
  init {
    columnSheet(DadosCredito::custno, header = "Número")
    columnSheet(DadosCredito::nome, header = "Nome")
    columnSheet(DadosCredito::cpfCnpj, header = "CPF/CNPJ")
    columnSheet(DadosCredito::tipoCliente, header = "Tipo")
    columnSheet(DadosCredito::dataCredito, header = "Dara Crédito")
    columnSheet(DadosCredito::limiteCredito, header = "Limite Cŕedito")
    columnSheet(DadosCredito::ultCompra, header = "Ult Compra")
    columnSheet(DadosCredito::valorAberto, header = "V. Aberto")
    columnSheet(DadosCredito::valorAtrasado, header = "V. Atraso")
    columnSheet(DadosCredito::valorDisponivel, header = "V. Disponivel")
  }
}