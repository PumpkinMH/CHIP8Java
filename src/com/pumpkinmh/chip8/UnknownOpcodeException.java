package com.pumpkinmh.chip8;

public class UnknownOpcodeException extends RuntimeException {

  public UnknownOpcodeException(String message)
  {
    super(message);
  }
}
