package br.com.astrosoft.framework.model.exceptions

abstract class EViewModel(msg: String?) : Exception(msg)

class EViewModelFail(msg: String?) : EViewModel(msg)

class EModelFail(msg: String?) : EViewModel(msg)

