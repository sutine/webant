package org.webant.commons.processor

/**
  * Created by bill on 2016/11/15.
  */
trait BaseProcessor[IN, OUT, DATA] {
  protected def init()
  protected def prepare(in: IN): IN
  protected def process(in: IN): OUT
  protected def extract(out: OUT): DATA
  protected def filter(data: DATA): DATA
  protected def write(data: DATA)
  protected def close()
}
