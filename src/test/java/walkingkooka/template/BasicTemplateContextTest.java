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
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.convert.ExpressionNumberConverters;

import java.math.MathContext;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicTemplateContextTest implements TemplateContextTesting2<BasicTemplateContext> {

    private final static Function<TextCursor, Template> EXPRESSION_PARSER = (final TextCursor t) -> Templates.templateValueName(
            TemplateValueName.parse(t)
                    .orElseThrow(() -> new EmptyTextException("template value name"))
    );

    private final static Function<TemplateValueName, Template> NAME_TO_TEMPLATE = (n) -> Templates.string(
            "<<" + n.text().toUpperCase() + ">>"
    );

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    private final static LineEnding LINE_ENDING = LineEnding.NL;

    private final static ExpressionEvaluationContext EXPRESSION_EVALUATION_CONTEXT = ExpressionEvaluationContexts.basic(
            EXPRESSION_NUMBER_KIND,
            (n) -> {
                throw new UnsupportedOperationException();
            },
            (e) -> {
                e.printStackTrace();
                throw e;
            },
            (r) -> Optional.empty(),
            (r) -> {
                throw new RuntimeException("Unknown " + r);
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
            ),
            LocaleContexts.fake()
    );

    // with.............................................................................................................

    @Test
    public void testWithNullExpressionParserFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicTemplateContext.with(
                        null,
                        NAME_TO_TEMPLATE,
                        LINE_ENDING,
                        EXPRESSION_EVALUATION_CONTEXT
                )
        );
    }

    @Test
    public void testWithNullNameToTemplateFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicTemplateContext.with(
                        EXPRESSION_PARSER,
                        null,
                        LINE_ENDING,
                        EXPRESSION_EVALUATION_CONTEXT
                )
        );
    }

    @Test
    public void testWithNullLineEndingFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicTemplateContext.with(
                        EXPRESSION_PARSER,
                        NAME_TO_TEMPLATE,
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
                        NAME_TO_TEMPLATE,
                        LINE_ENDING,
                        null
                )
        );
    }

    // parseTemplate............................................................................................................

    @Test
    public void testParseTemplateAndRenderOnlyText() {
        this.parseTemplateAndRenderAndCheck(
                "Hello",
                "Hello"
        );
    }

    @Test
    public void testParseTemplateAndRenderOnlyTextIncludesBackslashEscaping() {
        this.parseTemplateAndRenderAndCheck(
                "Hello\\\\123",
                "Hello\\123"
        );
    }

    @Test
    public void testParseTemplateDollarSignTextFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createContext()
                        .parseTemplate(TextCursors.charSequence("Hello$123"))
        );

        this.checkEquals(
                "Invalid character '$' at 5",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseTemplateWithEmptyTemplateValueName() {
        final EmptyTextException thrown = assertThrows(
                EmptyTextException.class,
                () -> this.createContext()
                        .parseTemplate(TextCursors.charSequence("Hello${}123"))
        );

        this.checkEquals(
                "Empty \"template value name\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseTemplateAndRenderTextIncludesValue() {
        this.parseTemplateAndRenderAndCheck(
                "Hello${abc}123",
                "Hello<<ABC>>123"
        );
    }

    @Test
    public void testParseTemplateAndRenderTextIncludesValue2() {
        this.parseTemplateAndRenderAndCheck(
                "Hello${abc}${def}123",
                "Hello<<ABC>><<DEF>>123"
        );
    }

    @Test
    public void testParseTemplateAndRenderTextIncludE() {
        this.parseTemplateAndRenderAndCheck(
                "Hello${abc}...${def}123",
                "Hello<<ABC>>...<<DEF>>123"
        );
    }

    @Test
    public void testParseTemplateAndRenderIncludesExpression() {
        this.parseTemplateAndRenderAndCheck(
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
                        LineEnding.NL,
                        EXPRESSION_EVALUATION_CONTEXT
                ),
                "Hello${1+2} 999",
                "Hello3 999"
        );
    }

    @Test
    public void testParseTemplateWithUnclosedExpressionFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createContext()
                        .parseTemplate(TextCursors.charSequence("Hello${abc"))
        );

        this.checkEquals(
                "Incomplete expression",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseTemplateAndRenderToStringWithCycleOneDeepFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> this.createContext(
                        (n) -> Templates.templateValueName(TemplateValueName.with("Parameter111"))
                ).parseTemplateAndRenderToString(
                        "${Parameter111}",
                        LineEnding.NL
                )
        );

        this.checkEquals(
                "Cycle detected \"Parameter111\" -> \"Parameter111\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseTemplateAndRenderToStringWithCycleTwoDeepFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> this.createContext(
                        (n) -> {
                            switch (n.value()) {
                                case "Parameter111":
                                    return Templates.templateValueName(
                                            TemplateValueName.with("Parameter222")
                                    );
                                case "Parameter222":
                                    return Templates.templateValueName(
                                            TemplateValueName.with("Parameter111")
                                    );
                                default:
                                    throw new UnsupportedOperationException(n.toString());
                            }
                        }
                ).parseTemplateAndRenderToString(
                        "${Parameter111}",
                        LineEnding.NL
                )
        );

        this.checkEquals(
                "Cycle detected \"Parameter111\" -> \"Parameter222\" -> \"Parameter111\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseTemplateAndRenderToStringWithCycleThreeDeepFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> this.createContext(
                        (n) -> {
                            switch (n.value()) {
                                case "Parameter111":
                                    return Templates.templateValueName(
                                            TemplateValueName.with("Parameter222")
                                    );
                                case "Parameter222":
                                    return Templates.templateValueName(
                                            TemplateValueName.with("Parameter333")
                                    );
                                case "Parameter333":
                                    return Templates.templateValueName(
                                            TemplateValueName.with("Parameter111")
                                    );
                                default:
                                    throw new UnsupportedOperationException(n.toString());
                            }
                        }
                ).parseTemplateAndRenderToString(
                        "${Parameter111}",
                        LineEnding.NL
                )
        );

        this.checkEquals(
                "Cycle detected \"Parameter111\" -> \"Parameter222\" -> \"Parameter333\" -> \"Parameter111\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseTemplateAndRenderToStringWithTemplateValueNameIndirect() {
        this.parseTemplateAndRenderToStringAndCheck(
                this.createContext(
                        (n) -> {
                            switch (n.value()) {
                                case "Parameter111":
                                    return Templates.templateValueName(
                                            TemplateValueName.with("Parameter222")
                                    );
                                case "Parameter222":
                                    return Templates.string("Value999");
                                default:
                                    throw new UnsupportedOperationException(n.toString());
                            }
                        }
                ),
                "${Parameter111}",
                LineEnding.NL,
                "Value999"
        );
    }

    @Test
    public void testParseTemplateAndRenderToStringWithCycleChainOfTwoFails2() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> this.createContext(
                        (n) -> {
                            switch (n.value()) {
                                case "Parameter111":
                                    return Templates.string("Value111");
                                case "Parameter222":
                                    return Templates.templateValueName(
                                            TemplateValueName.with("Parameter333")
                                    );
                                case "Parameter333":
                                    return Templates.templateValueName(
                                            TemplateValueName.with("Parameter222")
                                    );
                                default:
                                    throw new UnsupportedOperationException(n.toString());
                            }
                        }
                ).parseTemplateAndRenderToString(
                        "${Parameter111}${Parameter222}",
                        LineEnding.NL
                )
        );

        this.checkEquals(
                "Cycle detected \"Parameter222\" -> \"Parameter333\" -> \"Parameter222\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseTemplateAndRenderToStringWithExpression() {
        this.parseTemplateAndRenderToStringAndCheck(
                this.createContext(
                        (n) -> {
                            switch (n.value()) {
                                case "Parameter111":
                                    return Templates.expression(
                                            Expression.value("ExpressionValue111")
                                    );
                                default:
                                    throw new UnsupportedOperationException(n.toString());
                            }
                        }
                ),
                "${Parameter111}",
                LineEnding.NL,
                "ExpressionValue111"
        );
    }

    @Test
    public void testParseTemplateAndRenderToStringWithExpression2() {
        this.parseTemplateAndRenderToStringAndCheck(
                this.createContext(
                        (n) -> {
                            switch (n.value()) {
                                case "Parameter111":
                                    return Templates.string("ParameterValue111");
                                case "Parameter222":
                                    return Templates.expression(
                                            Expression.value("ExpressionValue222")
                                    );
                                default:
                                    throw new UnsupportedOperationException(n.toString());
                            }
                        }
                ),
                "${Parameter111}${Parameter222}",
                LineEnding.NL,
                "ParameterValue111ExpressionValue222"
        );
    }

    @Test
    public void testParseTemplateAndRenderToStringWithExpressionReferenceWithTemplateValueName() {
        this.parseTemplateAndRenderToStringAndCheck(
                this.createContext(
                        (n) -> {
                            switch (n.value()) {
                                case "Parameter111":
                                    return Templates.expression(
                                            Expression.reference(
                                                    TemplateValueName.with("Parameter222")
                                            )
                                    );
                                case "Parameter222":
                                    return Templates.string("Value999");
                                default:
                                    throw new UnsupportedOperationException(n.toString());
                            }
                        }
                ),
                "${Parameter111}",
                LineEnding.NL,
                "Value999"
        );
    }

    @Test
    public void testParseTemplateAndRenderToStringWithExpressionReferenceWithTemplateValueName2() {
        this.parseTemplateAndRenderToStringAndCheck(
                this.createContext(
                        (n) -> {
                            switch (n.value()) {
                                case "Parameter111":
                                    return Templates.expression(
                                            Expression.reference(
                                                    TemplateValueName.with("Parameter222")
                                            )
                                    );
                                case "Parameter222":
                                    return Templates.string("Value999");
                                default:
                                    throw new UnsupportedOperationException(n.toString());
                            }
                        }
                ),
                "${Parameter111} 999",
                LineEnding.NL,
                "Value999 999"
        );
    }

    @Test
    public void testParseTemplateAndRenderToStringWithExpressionCycleOneDeepFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> this.createContext(
                        (n) -> {
                            switch (n.value()) {
                                case "Parameter111":
                                    return Templates.expression(
                                            Expression.reference(
                                                    TemplateValueName.with("Parameter111")
                                            )
                                    );
                                default:
                                    throw new UnsupportedOperationException(n.toString());
                            }
                        }
                ).parseTemplateAndRenderToString(
                        "${Parameter111}${Parameter222}",
                        LineEnding.NL
                )
        );

        this.checkEquals(
                "Cycle detected \"Parameter111\" -> \"Parameter111\"",
                thrown.getMessage()
        );
    }

    // TemplateContext..................................................................................................

    @Override
    public BasicTemplateContext createContext() {
        return this.createContext(
                NAME_TO_TEMPLATE
        );
    }

    private BasicTemplateContext createContext(final Function<TemplateValueName, Template> nameToTemplate) {
        return BasicTemplateContext.with(
                EXPRESSION_PARSER,
                nameToTemplate,
                LINE_ENDING,
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
