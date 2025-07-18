package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.produto.model.beans.TermoRecebimento
import java.time.LocalDate

class PrintTermoCupom() : PrintText<TermoRecebimento>() {
  override fun printTitle(bean: TermoRecebimento) {
    printLine(' ')
    writeln("  ${bean.dadosCliente.nome}", negrito = true)
    writeln(" CNPJ: ${bean.dadosCliente.cnpj}", negrito = true)
    writeln(" End: ${bean.dadosCliente.endereco} Bairro ${bean.dadosCliente.bairro}", negrito = true)
    writeln(" Cidade: ${bean.dadosCliente.cidade} Estado: ${bean.dadosCliente.uf}", negrito = true)
    writeln(" E-mail: ${bean.empEmailTermo}", negrito = true)
    writeln(" WhatsApp: (86) 99978-0752", negrito = true)
    writeln("")
        val volumeRec = bean.volumesRec?.toString() ?: "0"
    writeln("Termo de Recebimento de Volume(s) - NI ${bean.ni}", negrito = true, center = true)
    printLine(' ')
    if (bean.volumesDivergencia()) {
      writeln("       Declaramos para  os  devidos  fins  que  recebemos ${volumeRec.lpad(4, " ")}")
      writeln(" volume(s) divergente do que constam na NF-e e CT-e informados")
      writeln(" abaixo, para posterior conferencia e sujeitos as notificacoes")
      writeln(" de irregularidade no recebimento.")
    } else {
      writeln("       Declaramos para  os  devidos  fins  que  recebemos ${volumeRec.lpad(4, " ")}")
      writeln(" volume(s) que constam  na NF-e e CT-e informados abaixo, para")
      writeln(" posterior  conferencia  e   sujeitos   as   notificacoes   de")
      writeln(" irregularidade no recebimento.")
    }
    writeln("")
    writeln(" Fornecedor: ${bean.dadosFornecedor.nome}")
    writeln(" CNPJ: ${bean.dadosFornecedor.cnpj}")

    val notaFiscal = "Nota Fiscal: ${bean.dadosFornecedor.notaFiscal}"
    val emissao = "Emissao: ${bean.dadosFornecedor.emissao?.format() ?: ""}"
    val valor = "Valor: ${bean.dadosFornecedor.valor.format()}"

    val espacoResto = 60 - (notaFiscal.length + valor.length + emissao.length)
    val espaco1 = espacoResto / 2

    writeln("  ${notaFiscal}${" ".repeat(espaco1)}$emissao")

    val volume = " Volumes: ${bean.volumesInf?.format() ?: ""}"
    writeln(volume)
    writeln("")
    writeln(" Transportadora: ${bean.dadosTransportadora.nome}")
    writeln(" CNPJ: ${bean.dadosTransportadora.cnpj}")
    val cte = "CT-e: ${bean.dadosTransportadora.cte}"
    val emissaoTransp = "Emissão: ${bean.dadosTransportadora.emissao?.format() ?: ""}"
    val espacoResto3 = (64 - (cte.length + emissaoTransp.length)) / 2
    writeln("  $cte${" ".repeat(espacoResto3)}$emissaoTransp")
  }

  override fun printSumary(bean: TermoRecebimento?) {
    writeln("Teresina-PI ${LocalDate.now().format("dd 'de' MMMM 'de' yyyy")}", center = true)
    writeln("")
    writeln("")
    val linha = "_____________________________________"
    val nome = bean?.nomeassinatura ?: ""
    val cpf = bean?.cpf ?: ""
    writeln(linha, center = true)
    writeln(nome, center = true)
    writeln("CPF: $cpf", center = true)
    writeln("Recebedor", center = true)
    writeln("")
  }
}