package br.com.astrosoft.framework.model

open class Campo<T : Any, B>(val header: String, val produceValue: (B) -> T)

class CampoString<B>(header: String, produceValue: B.() -> String = { "" }) : Campo<String, B>(header, produceValue)

class CampoNumber<B>(header: String, produceValue: B.() -> Double = { 0.00 }) : Campo<Double, B>(header, produceValue)

class CampoInt<B>(header: String, produceValue: B.() -> Int = { 0 }) : Campo<Int, B>(header, produceValue)