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

import walkingkooka.ContextTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.printer.Printer;
import walkingkooka.text.printer.Printers;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.Expression;

public interface TemplateContextTesting<C extends TemplateContext> extends ContextTesting<C>,
        ParseStringTesting<Template>,
        TreePrintableTesting {

    // parseTemplate....................................................................................................

    @Override
    default Template parseString(final String text) {
        return this.createContext()
                .parseTemplateString(text);
    }

    @Override
    default Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    default RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    default void parseTemplateStringAndCheck(final C context,
                                             final String text,
                                             final Template expected) {
        this.parseStringAndCheck(
                context::parseTemplateString,
                text,
                expected
        );
    }

    // parseTemplateAndRender...........................................................................................

    default void parseTemplateAndRenderAndCheck(final C context,
                                                final String template,
                                                final String expected) {
        this.parseTemplateAndRenderAndCheck(
                context,
                TextCursors.charSequence(template),
                expected
        );
    }

    default void parseTemplateAndRenderAndCheck(final C context,
                                                final TextCursor text,
                                                final String expected) {
        final StringBuilder printed = new StringBuilder();

        try (final Printer printer = Printers.stringBuilder(printed, LineEnding.NL)) {
            context.parseTemplateAndRender(
                    text,
                    printer
            );
        }
        this.checkEquals(
                expected,
                printed.toString()
        );
    }

    default void parseTemplateAndCheck(final TemplateContext context,
                                       final String text,
                                       final Template expected) {
        this.parseTemplateAndCheck(
                context,
                TextCursors.charSequence(text),
                expected
        );
    }

    default void parseTemplateAndCheck(final TemplateContext context,
                                       final TextCursor text,
                                       final Template expected) {
        this.checkEquals(
                expected,
                context.parseTemplate(text)
        );

        this.checkEquals(
                true,
                text.isEmpty(),
                () -> "cursor not empty=" + text
        );
    }

    // parseTemplateAndRenderToStringAndCheck ..........................................................................

    default void parseTemplateAndRenderToStringAndCheck(final TemplateContext context,
                                                        final String text,
                                                        final LineEnding lineEnding,
                                                        final String expected) {
        this.checkEquals(
                expected,
                context.parseTemplateAndRenderToString(
                        text,
                        lineEnding
                ),
                () -> "parseTemplateAndRenderToString " + CharSequences.quoteAndEscape(text)
        );
    }

    // evaluateAsString.................................................................................................

    default void evaluateAsStringAndCheck(final TemplateContext context,
                                          final Expression expression,
                                          final String expected) {
        this.checkEquals(
                expected,
                context.evaluateAsString(expression)
        );
    }

    default void parseTemplateExpressionAndCheck(final TemplateContext context,
                                                 final TextCursor text,
                                                 final Template expected) {
        this.checkEquals(
                expected,
                context.parseTemplateExpression(text)
        );
    }

    default void templateValueAndCheck(final TemplateContext context,
                                       final TemplateValueName name,
                                       final String expected) {
        this.checkEquals(
                expected,
                context.templateValue(name)
        );
    }
}
