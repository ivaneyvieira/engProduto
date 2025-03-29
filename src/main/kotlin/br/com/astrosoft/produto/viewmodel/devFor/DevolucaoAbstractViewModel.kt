package br.com.astrosoft.produto.viewmodel.devFor

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

abstract class DevolucaoAbstractViewModel<T : IDevolucaoAbstractView>(view: T) : ViewModel<T>(view)

interface IDevolucaoAbstractView : IView