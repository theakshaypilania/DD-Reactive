package com.mmt.ddreactive;

import org.slf4j.MDC;
import reactor.core.publisher.Signal;

import java.util.Optional;
import java.util.function.Consumer;

public class LoggingHelper {
  public static <T> Consumer<Signal<T>> logOnNext(Consumer<T> logStatement) {
    return signal -> {
      if (!signal.isOnNext()) {
        return;
      }
      Optional<String> apiIDMaybe = signal.getContextView().getOrEmpty("correlationKey");
      apiIDMaybe.ifPresent(apiID -> {
        try (MDC.MDCCloseable closeable = MDC.putCloseable("correlationKey", apiID)) {
          logStatement.accept(signal.get());
        }
      });
      if(!apiIDMaybe.isPresent()){
        logStatement.accept(signal.get());
      }
    };
  }
}
