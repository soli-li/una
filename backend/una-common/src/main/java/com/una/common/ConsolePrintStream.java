package com.una.common;

import com.una.common.utils.LogUtil;
import java.io.OutputStream;
import java.io.PrintStream;
import org.slf4j.Logger;

/**
 * 拦截System.out与System.err输出到日志
 */
public class ConsolePrintStream extends PrintStream {
  private static class PrintFunctional {
    public void print(final boolean error, final boolean outConsole, final Object value,
        final PrintFunctionalInterface func) {
      if (outConsole) {
        func.apply();
      }
      ConsolePrintStream.output(error, value);
    }
  }

  @FunctionalInterface
  private interface PrintFunctionalInterface {
    void apply();
  }

  private static class PrintlnFunctional {
    public void println(final boolean error, final boolean outConsole, final boolean emptyPringln,
        final Object value, final PrintFunctionalInterface func) {
      if (outConsole) {
        func.apply();
      } else {
        if (emptyPringln) {
          ConsolePrintStream.output(error, "");
          return;
        }
        ConsolePrintStream.PRINT.print(error, false, value, null);
      }
    }
  }

  private static final Logger STDOUT_LOGGER = LogUtil.getCustomizeLogger("STDOUT");
  private static final Logger STDERR_LOGGER = LogUtil.getCustomizeLogger("STDERR");

  private static final PrintFunctional PRINT = new PrintFunctional();
  private static final PrintlnFunctional PRINTLN = new PrintlnFunctional();

  private static void output(final boolean error, final Object value) {
    final Thread thread = Thread.currentThread();
    final StackTraceElement[] stackTrace = thread.getStackTrace();

    final StringBuilder buff = new StringBuilder();
    int realClassIndex = -1;
    for (int i = 0; i < stackTrace.length; i++) {
      final StackTraceElement stackTraceElement = stackTrace[i];
      if (ConsolePrintStream.class.getName().indexOf(stackTraceElement.getClassName()) > -1) {
        realClassIndex = Integer.max(realClassIndex, i);
      }
    }
    if (realClassIndex > -1) {
      final StackTraceElement stackTraceElement = stackTrace[realClassIndex + 1];
      buff.append("[");
      buff.append(stackTraceElement.getClassName());
      buff.append(".").append(stackTraceElement.getMethodName());
      buff.append(":").append(stackTraceElement.getLineNumber());
      buff.append("] ");
    }
    buff.append(String.valueOf(value));

    if (error) {
      ConsolePrintStream.STDERR_LOGGER.error(buff.toString());
    } else {
      ConsolePrintStream.STDOUT_LOGGER.info(buff.toString());
    }
  }

  private final boolean err;
  private final boolean outConsole;

  public ConsolePrintStream(final OutputStream out, final boolean err, final boolean outConsole) {
    super(out);
    this.err = err;
    this.outConsole = outConsole;
  }

  @Override
  public void print(final boolean b) {
    ConsolePrintStream.PRINT.print(this.err, this.outConsole, b, () -> super.print(b));
  }

  @Override
  public void print(final char c) {
    ConsolePrintStream.PRINT.print(this.err, this.outConsole, c, () -> super.print(c));
  }

  @Override
  public void print(final char[] s) {
    ConsolePrintStream.PRINT.print(this.err, this.outConsole, s, () -> super.print(s));
  }

  @Override
  public void print(final double d) {
    ConsolePrintStream.PRINT.print(this.err, this.outConsole, d, () -> super.print(d));
  }

  @Override
  public void print(final float f) {
    ConsolePrintStream.PRINT.print(this.err, this.outConsole, f, () -> super.print(f));
  }

  @Override
  public void print(final int i) {
    ConsolePrintStream.PRINT.print(this.err, this.outConsole, i, () -> super.print(i));
  }

  @Override
  public void print(final long l) {
    ConsolePrintStream.PRINT.print(this.err, this.outConsole, l, () -> super.print(l));
  }

  @Override
  public void print(final Object obj) {
    ConsolePrintStream.PRINT.print(this.err, this.outConsole, obj, () -> super.print(obj));
  }

  @Override
  public void print(final String s) {
    ConsolePrintStream.PRINT.print(this.err, this.outConsole, s, () -> super.print(s));
  }

  @Override
  public void println() {
    ConsolePrintStream.PRINTLN.println(this.err, this.outConsole, true, null, super::println);
  }

  @Override
  public void println(final boolean x) {
    ConsolePrintStream.PRINTLN.println(this.err, this.outConsole, false, x, () -> super.println(x));
  }

  @Override
  public void println(final char x) {
    ConsolePrintStream.PRINTLN.println(this.err, this.outConsole, false, x, () -> super.println(x));
  }

  @Override
  public void println(final char[] x) {
    ConsolePrintStream.PRINTLN.println(this.err, this.outConsole, false, x, () -> super.println(x));
  }

  @Override
  public void println(final double x) {
    ConsolePrintStream.PRINTLN.println(this.err, this.outConsole, false, x, () -> super.println(x));
  }

  @Override
  public void println(final float x) {
    ConsolePrintStream.PRINTLN.println(this.err, this.outConsole, false, x, () -> super.println(x));
  }

  @Override
  public void println(final int x) {
    ConsolePrintStream.PRINTLN.println(this.err, this.outConsole, false, x, () -> super.println(x));
  }

  @Override
  public void println(final long x) {
    ConsolePrintStream.PRINTLN.println(this.err, this.outConsole, false, x, () -> super.println(x));
  }

  @Override
  public void println(final Object x) {
    ConsolePrintStream.PRINTLN.println(this.err, this.outConsole, false, x, () -> super.println(x));
  }

  @Override
  public void println(final String x) {
    ConsolePrintStream.PRINTLN.println(this.err, this.outConsole, false, x, () -> super.println(x));
  }
}
