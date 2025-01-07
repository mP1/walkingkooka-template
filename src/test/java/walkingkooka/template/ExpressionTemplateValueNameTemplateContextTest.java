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
import walkingkooka.EmptyTextException;
import walkingkooka.text.cursor.TextCursors;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ExpressionTemplateValueNameTemplateContextTest implements TemplateContextTesting2<ExpressionTemplateValueNameTemplateContext> {

    private final static Function<TemplateValueName, String> NAME_TO_STRING = (n) -> "<<" + n.text().toUpperCase() + ">>";

    // with.............................................................................................................

    @Test
    public void testWithNullNameToStringFails() {
        assertThrows(
                NullPointerException.class,
                () -> ExpressionTemplateValueNameTemplateContext.with(
                        null
                )
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseAndRenderOnlyText() {
        this.parseAndRenderAndCheck(
                "Hello",
                "Hello"
        );
    }

    @Test
    public void testParseAndRenderOnlyTextIncludesBackslashEscaping() {
        this.parseAndRenderAndCheck(
                "Hello\\\\123",
                "Hello\\123"
        );
    }

    @Test
    public void testParseTextDollarSignTextFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createContext()
                        .parse(TextCursors.charSequence("Hello$123"))
        );

        this.checkEquals(
                "Invalid character '$' at 5 in \"Hello$123\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseTextEmptyTemplateValueName() {
        final EmptyTextException thrown = assertThrows(
                EmptyTextException.class,
                () -> this.createContext()
                        .parse(TextCursors.charSequence("Hello${}123"))
        );

        this.checkEquals(
                "Empty \"template value name\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseAndRenderTextIncludesValue() {
        this.parseAndRenderAndCheck(
                "Hello${abc}123",
                "Hello<<ABC>>123"
        );
    }

    @Test
    public void testParseAndRenderTextIncludesValue2() {
        this.parseAndRenderAndCheck(
                "Hello${abc}${def}123",
                "Hello<<ABC>><<DEF>>123"
        );
    }

    @Test
    public void testParseAndRenderTextIncludesValue3() {
        this.parseAndRenderAndCheck(
                "Hello${abc}...${def}123",
                "Hello<<ABC>>...<<DEF>>123"
        );
    }

    @Test
    public void testParseTextUnclosedExpressionFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createContext()
                        .parse(TextCursors.charSequence("Hello${abc"))
        );

        this.checkEquals(
                "Incomplete expression",
                thrown.getMessage()
        );
    }

    // TemplateContext..................................................................................................

    @Override
    public ExpressionTemplateValueNameTemplateContext createContext() {
        return ExpressionTemplateValueNameTemplateContext.with(NAME_TO_STRING);
    }

    // class............................................................................................................

    @Override
    public String typeNameSuffix() {
        return TemplateContext.class.getSimpleName();
    }

    @Override
    public Class<ExpressionTemplateValueNameTemplateContext> type() {
        return ExpressionTemplateValueNameTemplateContext.class;
    }
}
