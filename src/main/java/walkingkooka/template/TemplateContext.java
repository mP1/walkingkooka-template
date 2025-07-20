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
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.printer.Printer;
import walkingkooka.tree.expression.Expression;

import java.util.List;
import java.util.Objects;

/**
 * The {@link Context} that handles rendering a template using {@link Template#render(Printer, TemplateContext)}
 */
public interface TemplateContext extends Context {

    /**
     * Combines parsing of the template followed by rendering.
     */
    default void parseTemplateAndRender(final TextCursor text,
                                        final Printer printer) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(printer, "printer");

        this.parseTemplate(text)
            .render(
                printer,
                this
            );
    }

    /**
     * Identical to {@link #parseTemplateAndRenderToString(String, LineEnding)} but returns a {@link String} with the
     * rendered result without requiring a {@link Printer} parameter.
     */
    default String parseTemplateAndRenderToString(final String text,
                                                  final LineEnding lineEnding) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(lineEnding, "lineEnding");

        return this.parseTemplateString(text)
            .renderToString(
                lineEnding,
                this
            );
    }

    default Template parseTemplateString(final String text) {
        return this.parseTemplate(
            TextCursors.charSequence(text)
        );
    }

    /**
     * Token used to mark the beginning of an expression.
     */
    String EXPRESSION_OPEN = "${";

    /**
     * The expression closing token.
     */
    String EXPRESSION_CLOSE = "}";

    /**
     * A default parseTemplate method that handles backslash escaping, and calls {@link #parseTemplateExpression(TextCursor) to handle
     * parsing expressions into a {@link Template}.
     */
    default Template parseTemplateWithBackslashEscaping(final TextCursor text) {
        return TemplateContextParseTextCursor.parse(
            text,
            this
        );
    }

    /**
     * Consumes the {@link TextCursor} which contains a template.
     */
    Template parseTemplate(final TextCursor text);

    /**
     * Handles consuming a template expression into a {@link Template}.
     */
    Template parseTemplateExpression(final TextCursor text);

    /**
     * Factory that creates a {@link Template} containing the given child templates.
     */
    Template templateCollection(final List<Template> templates);

    /**
     * Evaluates the given {@link Expression} into a {@link String string value}
     */
    String evaluateAsString(final Expression expression);

    /**
     * Resolves the given {@link TemplateValueName} into a {@link String}. When the value is not found,
     * the context can either return an empty string or throw an exception.
     */
    String templateValue(final TemplateValueName name);
}
