package com.faisal.usermanager.common.exceptions;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.util.Pair;

import java.util.List;

@Getter
public class DataValidationException extends RuntimeException {
    private final List<Pair<String, String>> fieldErrors;

    public DataValidationException(String filedName, String errorMessage) {
        this.fieldErrors = List.of(
                Pair.of(filedName, errorMessage)
        );
    }

    private DataValidationException(@NotNull Builder builder) {
        this.fieldErrors = builder.fieldErrors;
    }

    public static class Builder {
        List<Pair<String, String>> fieldErrors = List.of();

        public Builder addFiledError(String filedName, String errorMessage) {
            fieldErrors.add(
                    Pair.of(filedName, errorMessage)
            );
            return this;
        }

        public DataValidationException build() {
            return new DataValidationException(this);
        }
    }
}