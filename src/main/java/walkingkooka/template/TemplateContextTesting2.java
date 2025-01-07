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

import org.junit.jupiter.api.Test;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.printer.Printers;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface TemplateContextTesting2<C extends TemplateContext> extends TemplateContextTesting<C> {

    @Test
    default void testParseAndRenderWithNullCursorFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .parseAndRender(
                                null,
                                Printers.fake()
                        )
        );
    }

    @Test
    default void testParseAndRenderWithNullPrinterFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .parseAndRender(
                                TextCursors.fake(),
                                null
                        )
        );
    }

    default void parseAndRenderAndCheck(final String template,
                                        final String expected) {
        this.parseAndRenderAndCheck(
                TextCursors.charSequence(template),
                expected
        );
    }


    default void parseAndRenderAndCheck(final TextCursor cursor,
                                        final String expected) {
        this.parseAndRenderAndCheck(
                this.createContext(),
                cursor,
                expected
        );
    }

    // parse............................................................................................................

    @Test
    default void testParseWithNullCursorFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .parse(null)
        );
    }

    default void parseAndCheck(final String template,
                               final Template expected) {
        this.parseAndCheck(
                TextCursors.charSequence(template),
                expected
        );
    }

    default void parseAndCheck(final TextCursor cursor,
                               final Template expected) {
        this.parseAndCheck(
                this.createContext(),
                cursor,
                expected
        );
    }

    // expression.......................................................................................................

    @Test
    default void testExpressionWithNullCursorFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .expression(null)
        );
    }

    default void expressionAndCheck(final TextCursor cursor,
                                    final Template expected) {
        this.expressionAndCheck(
                this.createContext(),
                cursor,
                expected
        );
    }

    // openBrace........................................................................................................

    @Test
    default void testOpenBraceWithNullCursorFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .openBrace(null)
        );
    }

    default void openBraceAndCheck(final TextCursorLineInfo at,
                                   final Template expected) {
        this.openBraceAndCheck(
                this.createContext(),
                at,
                expected
        );
    }

    // templateValue....................................................................................................

    @Test
    default void testTemplateValueWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createContext()
                        .templateValue(null)
        );
    }

    default void templateValueAndCheck(final TemplateValueName name,
                                       final String expected) {
        this.templateValueAndCheck(
                this.createContext(),
                name,
                expected
        );
    }
}
