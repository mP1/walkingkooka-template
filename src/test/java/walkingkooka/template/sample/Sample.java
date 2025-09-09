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

package walkingkooka.template.sample;

import org.junit.jupiter.api.Test;
import walkingkooka.EmptyTextException;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.template.TemplateContext;
import walkingkooka.template.TemplateContexts;
import walkingkooka.template.TemplateValueName;
import walkingkooka.template.Templates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.printer.Printers;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.convert.ExpressionNumberConverters;

import java.math.MathContext;

public class Sample {

    public static void main(final String[] args) {
        final Sample sample = new Sample();
        sample.testParseAndRender();
    }

    @Test
    public void testParseAndRender() {
        final ExpressionNumberKind expressionNumberKind = ExpressionNumberKind.BIG_DECIMAL;

        final TemplateContext context = TemplateContexts.basic(
                (final TextCursor t) -> Templates.templateValueName(
                        TemplateValueName.parse(t)
                                .orElseThrow(() -> new EmptyTextException("template value name"))
                ),
                (n) -> Templates.string("<<" + n.text().toUpperCase() + ">>"),
                LineEnding.NL,
                ExpressionEvaluationContexts.basic(
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
                                                ),
                                                Converters.objectToString()
                                        )
                                ).cast(ExpressionNumberConverterContext.class),
                                ConverterContexts.basic(
                                    false, // canNumbersHaveGroupSeparator
                                        -1,
                                    ',', // valueSeparator
                                        Converters.fake(),
                                        DateTimeContexts.fake(),
                                        DecimalNumberContexts.american(MathContext.DECIMAL32)
                                ),
                                expressionNumberKind
                        ),
                        LocaleContexts.fake()
                )
        );

        final StringBuilder b = new StringBuilder();

        context.parseTemplateAndRender(
                TextCursors.charSequence("Hello ${abc} 123"),
                Printers.stringBuilder(
                        b,
                        LineEnding.NL
                )
        );

        checkEquals(
                "Hello <<ABC>> 123",
                b.toString()
        );
    }

    private void checkEquals(final String expected, final String actual) {
        System.out.println(
                expected + "\n" +
                        actual
        );
    }
}
