package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.Pedido

class PlanilhaRetira : Planilha<Pedido>("Pedido Retira") {
  init {
    columnSheet(Pedido::tipoRetiraStr, "Tipo")
    columnSheet(Pedido::loja, "Loja")
    columnSheet(Pedido::pedido, "Pedido")
    
    columnSheet(Pedido::data, "Data")
    columnSheet(Pedido::hora, "Hora")
    
    columnSheet(Pedido::nfFat, "NF Fat")
    
    columnSheet(Pedido::dataFat, "Data")
    columnSheet(Pedido::horaFat, "Hora")
    columnSheet(Pedido::xanoVenda, "Transação")
    columnSheet(Pedido::area, "Área")
    columnSheet(Pedido::vendno, "Vendedor")
    
    columnSheet(Pedido::frete, "R$ Frete")
    columnSheet(Pedido::valorComFrete, "R$ Nota")
    columnSheet(Pedido::cliente, "Cliente")
  }
}