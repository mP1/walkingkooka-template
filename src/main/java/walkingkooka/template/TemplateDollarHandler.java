/*
 * Copyright 2025 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.template;

import walkingkooka.InvalidCharacterException;
import walkingkooka.text.cursor.TextCursorLineInfo;

import java.util.Objects;

/**
 * Three possible options to handle an orphaned dollar sign.
 */
public enum TemplateDollarHandler {

    IGNORED {
        @Override
        public Template handle(final TextCursorLineInfo at) {
            Objects.requireNonNull(at, "at");

            return EMPTY;
        }

        private final Template EMPTY = Templates.string("");

    },
    INCLUDE {
        @Override
        public Template handle(final TextCursorLineInfo at) {
            Objects.requireNonNull(at, "at");

            return DOLLAR;
        }

        private final Template DOLLAR = Templates.string("$");
    },
    THROW {
        @Override
        public Template handle(final TextCursorLineInfo at) {
            Objects.requireNonNull(at, "at");

            throw new InvalidCharacterException(
                    at.text()
                            .toString(),
                    at.textOffset()
            );
        }
    };

    /**
     * Handle an orphaned open brace.
     */
    public abstract Template handle(final TextCursorLineInfo at);
}
