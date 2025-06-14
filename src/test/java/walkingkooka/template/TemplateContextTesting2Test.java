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
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.template.TemplateContextTesting2Test.TestTemplateContext;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.convert.ExpressionNumberConverters;

import java.math.MathContext;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TemplateContextTesting2Test implements TemplateContextTesting2<TestTemplateContext> {

    // parseString......................................................................................................

    @Test
    public void testParseString() {
        final String text = "Hello";
        final Template template = Templates.string(text);

        this.parseStringAndCheck(
                new TestTemplateContext() {

                    @Override
                    public Template parse(final TextCursor text) {
                        return template;
                    }
                },
                template.toString(),
                template
        );
    }

    // parseAndRender...................................................................................................

    @Test
    public void testParseAndRenderString() {
        final String text = "Hello";
        final Template template = Templates.string(text);

        this.parseAndRenderAndCheck(
                new TestTemplateContext() {

                    @Override
                    public Template parse(final TextCursor text) {
                        return template;
                    }
                },
                template.toString(),
                text
        );
    }

    @Test
    public void testParseAndRenderTextCursor() {
        final String text = "Hello";
        final Template template = Templates.string(text);

        this.parseAndRenderAndCheck(
                new TestTemplateContext() {

                    @Override
                    public Template parse(final TextCursor text) {
                        return template;
                    }
                },
                TextCursors.charSequence(template.toString()),
                text
        );
    }

    @Test
    public void testParseAndRenderExpression() {
        final String text = "${12+34}";

        final ExpressionNumberKind expressionNumberKind = ExpressionNumberKind.BIG_DECIMAL;

        this.parseAndRenderAndCheck(
                new TestTemplateContext() {

                    @Override
                    public Template parse(final TextCursor text) {
                        return this.parseTemplateExpression(text);
                    }

                    @Override
                    public Template parseTemplateExpression(final TextCursor text) {
                        Objects.requireNonNull(text, "text");

                        text.end();

                        return Templates.expression(
                                Expression.add(
                                        Expression.value(12),
                                        Expression.value(34)
                                )
                        );
                    }

                    @Override
                    public String evaluateAsString(final Expression expression) {
                        return ExpressionEvaluationContexts.basic(
                                        expressionNumberKind,
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
                                                                )
                                                        )
                                                ).cast(ExpressionNumberConverterContext.class),
                                                ConverterContexts.basic(
                                                        -1,
                                                        Converters.fake(),
                                                        DateTimeContexts.fake(),
                                                        DecimalNumberContexts.american(MathContext.DECIMAL32)
                                                ),
                                                expressionNumberKind
                                        )
                                ).evaluateExpression(expression)
                                .toString();
                    }
                },
                TextCursors.charSequence(text),
                "46"
        );
    }

    // parseAndRenderToString...........................................................................................

    @Test
    public void testParseAndRenderToStringNoParameters() {
        final String text = "Hello";

        this.parseAndRenderToStringAndCheck(
                new TestTemplateContext() {

                    @Override
                    public Template parse(final TextCursor t) {
                        t.end();
                        return Templates.string(text);
                    }
                },
                text,
                LineEnding.NL,
                text
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseStringTemplate() {
        final Template template = Templates.string("Hello");

        this.parseAndCheck(
                new TestTemplateContext() {

                    @Override
                    public Template parse(final TextCursor text) {
                        text.end();
                        return template;
                    }
                },
                template.toString(),
                template
        );
    }

    @Test
    public void testParse() {
        final Template template = Templates.string("Hello");

        this.parseAndCheck(
                new TestTemplateContext() {

                    @Override
                    public Template parse(final TextCursor text) {
                        text.end();
                        return template;
                    }
                },
                TextCursors.charSequence(template.toString()),
                template
        );
    }

    @Test
    public void testParseCursorNotEmpty() {
        assertThrows(
                AssertionError.class,
                () -> this.parseAndCheck(
                        new TestTemplateContext() {

                            @Override
                            public Template parse(final TextCursor text) {
                                return Templates.string("Different");
                            }
                        },
                        TextCursors.fake(),
                        Templates.string("Hello")
                )
        );
    }

    @Test
    public void testParseFails() {
        assertThrows(
                AssertionError.class,
                () -> this.parseAndCheck(
                        new TestTemplateContext() {

                            @Override
                            public Template parse(final TextCursor text) {
                                return Templates.string("Different");
                            }
                        },
                        TextCursors.fake(),
                        Templates.string("Hello")
                )
        );
    }

    // parseTemplateExpression..........................................................................................

    @Test
    public void testParseTemplateExpressionWithTemplate() {
        final Template template = Templates.expression(
                Expression.add(
                        Expression.value(12),
                        Expression.value(34)
                )
        );

        this.parseTemplateExpressionAndCheck(
                new FakeTemplateContext() {

                    @Override
                    public Template parseTemplateExpression(final TextCursor text) {
                        Objects.requireNonNull(text, "text");

                        Parsers.character(
                                CharPredicates.is('}')
                                        .negate()
                        );

                        return template;
                    }
                },
                TextCursors.charSequence(template.toString()),
                template
        );
    }

    @Test
    public void testParseTemplateExpressionWithStringTemplate() {
        final Template template = Templates.string("Hello");

        this.parseTemplateExpressionAndCheck(
                new TestTemplateContext() {

                    @Override
                    public Template parseTemplateExpression(final TextCursor text) {
                        return template;
                    }
                },
                TextCursors.charSequence(template.toString()),
                template
        );
    }

    @Test
    public void testParseTemplateExpressionFails() {
        assertThrows(
                AssertionError.class,
                () -> this.parseTemplateExpressionAndCheck(
                        new TestTemplateContext() {

                            @Override
                            public Template parseTemplateExpression(final TextCursor text) {
                                return Templates.string("Different");
                            }
                        },
                        TextCursors.fake(),
                        Templates.string("Hello")
                )
        );
    }

    // TemplateContext..................................................................................................

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestTemplateContext createContext() {
        return new TestTemplateContext();
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }

    @Override
    public Class<TestTemplateContext> type() {
        return TestTemplateContext.class;
    }

    static class TestTemplateContext implements TemplateContext {

        @Override
        public Template parse(final TextCursor text) {
            Objects.requireNonNull(text, "text");
            throw new UnsupportedOperationException();
        }

        @Override
        public Template parseTemplateExpression(final TextCursor text) {
            Objects.requireNonNull(text, "text");

            throw new UnsupportedOperationException();
        }

        @Override
        public String evaluateAsString(final Expression expression) {
            Objects.requireNonNull(expression, "parseTemplateExpression");

            throw new UnsupportedOperationException();
        }

        @Override
        public String templateValue(final TemplateValueName name) {
            Objects.requireNonNull(name, "name");

            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
