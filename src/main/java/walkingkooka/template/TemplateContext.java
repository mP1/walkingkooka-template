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

import walkingkooka.Context;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;
import walkingkooka.text.printer.Printer;

import java.util.Objects;

/**
 * The {@link Context} that handles rendering a template using {@link Template#render(Printer, TemplateContext)}
 */
public interface TemplateContext extends Context {

    /**
     * Combines parsing of the template followed by rendering.
     */
    default void parseAndRender(final TextCursor text,
                                final Printer printer) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(printer, "printer");

        this.parse(text)
                .render(
                        printer,
                        this
                );
    }

    /**
     * Consumes the {@link TextCursor} which contains a template.
     */
    Template parse(final TextCursor text);

    /**
     * Handles consuming a template expression into a {@link Template}.
     */
    Template expression(final TextCursor text);

    /**
     * Called during parsing to handle an orphaned dollarSign that is not escaped or part of a placeholder expression.
     * Implementations can either throw an exception, return a template that renders nothing or renders the dollarSign sign.
     */
    Template dollarSign(final TextCursorLineInfo at);

    /**
     * Resolves the given {@link TemplateValueName} into a {@link String}. When the value is not found,
     * the context can either return an empty string or throw an exception.
     */
    String templateValue(final TemplateValueName name);
}
