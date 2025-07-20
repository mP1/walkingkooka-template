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
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.printer.Printers;
import walkingkooka.tree.expression.Expression;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface TemplateContextTesting2<C extends TemplateContext> extends TemplateContextTesting<C> {

    // parseTemplateString..............................................................................................

    @Override
    default void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    // parseTemplateAndRender...........................................................................................

    @Test
    default void testParseTemplateAndRenderWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .parseTemplateAndRender(
                    null,
                    Printers.fake()
                )
        );
    }

    @Test
    default void testParseTemplateAndRenderWithNullPrinterFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .parseTemplateAndRender(
                    TextCursors.fake(),
                    null
                )
        );
    }

    default void parseTemplateAndRenderAndCheck(final String text,
                                                final String expected) {
        this.parseTemplateAndRenderAndCheck(
            TextCursors.charSequence(text),
            expected
        );
    }


    default void parseTemplateAndRenderAndCheck(final TextCursor text,
                                                final String expected) {
        this.parseTemplateAndRenderAndCheck(
            this.createContext(),
            text,
            expected
        );
    }

    // parseTemplateAndRenderToString...................................................................................

    @Test
    default void testParseTemplateAndRenderToStringWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .parseTemplateAndRenderToString(
                    null,
                    LineEnding.NL
                )
        );
    }

    @Test
    default void testParseTemplateAndRenderToStringWithNullLineEndingFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .parseTemplateAndRenderToString(
                    "",
                    null
                )
        );
    }

    default void parseTemplateAndRenderToStringAndCheck(final String text,
                                                        final String expected) {
        this.parseTemplateAndRenderAndCheck(
            TextCursors.charSequence(text),
            expected
        );
    }


    default void parseTemplateAndRenderToStringAndCheck(final String text,
                                                        final LineEnding lineEnding,
                                                        final String expected) {
        this.parseTemplateAndRenderToStringAndCheck(
            this.createContext(),
            text,
            lineEnding,
            expected
        );
    }

    // parseTemplate....................................................................................................

    @Test
    default void testParseTemplateWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .parseTemplate(null)
        );
    }

    default void parseTemplateAndCheck(final String text,
                                       final Template expected) {
        this.parseTemplateAndCheck(
            TextCursors.charSequence(text),
            expected
        );
    }

    default void parseTemplateAndCheck(final TextCursor text,
                                       final Template expected) {
        this.parseTemplateAndCheck(
            this.createContext(),
            text,
            expected
        );
    }

    // parseTemplateExpression..........................................................................................

    @Test
    default void testParseTemplateExpressionWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .parseTemplateExpression(null)
        );
    }

    default void parseTemplateExpressionAndCheck(final TextCursor text,
                                                 final Template expected) {
        this.parseTemplateExpressionAndCheck(
            this.createContext(),
            text,
            expected
        );
    }

    // evaluateAsString.................................................................................................

    @Test
    default void testEvaluateAsStringWithNullExpressionFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .evaluateAsString(null)
        );
    }

    default void evaluateAsStringAndCheck(final Expression expression,
                                          final String expected) {
        this.checkEquals(
            expected,
            this.createContext()
                .evaluateAsString(expression)
        );
    }

    // templateCollection...............................................................................................

    @Test
    default void testTemplateCollectionWithNullTemplatesFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .templateCollection(null)
        );
    }

    // templateText.....................................................................................................

    @Test
    default void testTemplateTextWithNullStringFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .templateText(null)
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
