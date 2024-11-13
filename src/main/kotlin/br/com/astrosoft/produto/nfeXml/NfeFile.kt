package br.com.astrosoft.produto.nfeXml

import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota
import com.fincatto.documentofiscal.utils.DFPersister

class NfeFile(xmlContent: String) {
  private val nota: NFNota = DFPersister(false).read(NFNota::class.java, xmlContent)

  fun print() {
    print(nota)
  }
}