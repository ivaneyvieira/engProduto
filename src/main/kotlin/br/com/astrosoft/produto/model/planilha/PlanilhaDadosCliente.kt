package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.DadosCliente

class PlanilhaDadosCliente : Planilha<DadosCliente>("Dados do Cliente") {
  init {
    columnSheet(DadosCliente::custno, header = "Número")
    columnSheet(DadosCliente::nome, header = "Nome")
    columnSheet(DadosCliente::cpfCnpj, header = "CPF/CNPJ")
    columnSheet(DadosCliente::rg, header = "RG")
    columnSheet(DadosCliente::endereco, header = "Endereço")
    columnSheet(DadosCliente::bairro, header = "Bairro")
    columnSheet(DadosCliente::rota, header = "Rota")
    columnSheet(DadosCliente::cidade, header = "Cidade")
    columnSheet(DadosCliente::estado, header = "Estado")
    columnSheet(DadosCliente::fone, header = "Fone")
  }
}