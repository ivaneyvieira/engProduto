package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.CreditoCliente

class PlanilhaCredito : Planilha<CreditoCliente>("Credito Cliente") {
  init {
    columnSheet(CreditoCliente::codigo, header = "Cód Cliente")
    columnSheet(CreditoCliente::nome, header = "Nome Cliente")
    columnSheet(CreditoCliente::vlCredito, header = "Valor Crédito")
  }
}