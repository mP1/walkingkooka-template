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
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.printer.Printer;
import walkingkooka.text.printer.Printers;

public interface TemplateContextTesting<C extends TemplateContext> extends ContextTesting<C> {

    default void parseAndRenderAndCheck(final C context,
                                        final String template,
                                        final String expected) {
        this.parseAndRenderAndCheck(
                context,
                TextCursors.charSequence(template),
                expected
        );
    }

    default void parseAndRenderAndCheck(final C context,
                                        final TextCursor text,
                                        final String expected) {
        final StringBuilder printed = new StringBuilder();

        try (final Printer printer = Printers.stringBuilder(printed, LineEnding.NL)) {
            context.parseAndRender(
                    text,
                    printer
            );
        }
        this.checkEquals(
                expected,
                printed.toString()
        );
    }

    default void parseAndCheck(final TemplateContext context,
                               final String text,
                               final Template expected) {
        this.parseAndCheck(
                context,
                TextCursors.charSequence(text),
                expected
        );
    }

    default void parseAndCheck(final TemplateContext context,
                               final TextCursor text,
                               final Template expected) {
        this.checkEquals(
                expected,
                context.parse(text)
        );

        this.checkEquals(
                true,
                text.isEmpty(),
                () -> "cursor not empty=" + text
        );
    }

    default void expressionAndCheck(final TemplateContext context,
                                    final TextCursor text,
                                    final Template expected) {
        this.checkEquals(
                expected,
                context.expression(text)
        );
    }

    default void openBraceAndCheck(final TemplateContext context,
                                   final TextCursorLineInfo at,
                                   final Template expected) {
        this.checkEquals(
                context.openBrace(at),
                expected
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
