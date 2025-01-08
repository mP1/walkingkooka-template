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
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberConverters;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicTemplateContextTest implements TemplateContextTesting2<BasicTemplateContext> {

    private final static Function<TextCursor, Template> EXPRESSION_PARSER = (final TextCursor t) -> Templates.templateValueName(
            TemplateValueName.parse(t)
                    .orElseThrow(() -> new EmptyTextException("template value name"))
    );

    private final static Function<TemplateValueName, String> NAME_TO_STRING = (n) -> "<<" + n.text().toUpperCase() + ">>";

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    private final static ExpressionEvaluationContext EXPRESSION_EVALUATION_CONTEXT = ExpressionEvaluationContexts.basic(
            EXPRESSION_NUMBER_KIND,
            (n) -> {
                throw new UnsupportedOperationException();
            },
            (e) -> {
                e.printStackTrace();
                throw new UnsupportedOperationException();
            },
            (r) -> {
                throw new UnsupportedOperationException();
            },
            (r) -> {
                throw new UnsupportedOperationException();
            },
            CaseSensitivity.SENSITIVE,
            ExpressionNumberConverterContexts.basic(
                    Converters.collection(
                            Lists.of(
                                    ExpressionNumberConverters.toNumberOrExpressionNumber(
                                            Converters.numberToNumber()
                                    ),
                                    Converters.objectToString() // formats BigDecimal -> String
                            )
                    ).cast(ExpressionNumberConverterContext.class),
                    ConverterContexts.basic(
                            -1,
                            Converters.fake(),
                            DateTimeContexts.fake(),
                            DecimalNumberContexts.american(MathContext.DECIMAL32)
                    ),
                    EXPRESSION_NUMBER_KIND
            )
    );

    // with.............................................................................................................

    @Test
    public void testWithNullExpressionParserFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicTemplateContext.with(
                        null,
                        NAME_TO_STRING,
                        EXPRESSION_EVALUATION_CONTEXT
                )
        );
    }

    @Test
    public void testWithNullNameToStringFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicTemplateContext.with(
                        EXPRESSION_PARSER,
                        null,
                        EXPRESSION_EVALUATION_CONTEXT
                )
        );
    }

    @Test
    public void testWithNullExpressionEvaluationContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicTemplateContext.with(
                        EXPRESSION_PARSER,
                        NAME_TO_STRING,
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
    public void testParseAndRenderIncludesExpression() {
        this.parseAndRenderAndCheck(
                BasicTemplateContext.with(
                        (final TextCursor t) -> {
                            t.next(); // 1
                            t.next(); // +
                            t.next(); // 2

                            return Templates.expression(
                                    Expression.add(
                                            Expression.value(1),
                                            Expression.value(2)
                                    )
                            );
                        },
                        (n) -> {
                            throw new UnsupportedOperationException();
                        },
                        EXPRESSION_EVALUATION_CONTEXT
                ),
                "Hello${1+2} 999",
                "Hello3 999"
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
    public BasicTemplateContext createContext() {
        return BasicTemplateContext.with(
                EXPRESSION_PARSER,
                NAME_TO_STRING,
                EXPRESSION_EVALUATION_CONTEXT
        );
    }

    // class............................................................................................................

    @Override
    public String typeNameSuffix() {
        return TemplateContext.class.getSimpleName();
    }

    @Override
    public Class<BasicTemplateContext> type() {
        return BasicTemplateContext.class;
    }
}
