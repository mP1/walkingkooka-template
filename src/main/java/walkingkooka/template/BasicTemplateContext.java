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

import walkingkooka.NeverError;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link TemplateContext} that uses numerous helpers to support parsing expressions, name lookups, and evaluating
 * any {@link Expression}.
 */
final class BasicTemplateContext implements TemplateContext {

    static BasicTemplateContext with(final Function<TextCursor, Template> expressionParser,
                                     final Function<TemplateValueName, Template> nameToTemplate,
                                     final LineEnding lineEnding,
                                     final ExpressionEvaluationContext expressionEvaluationContext) {
        return new BasicTemplateContext(
                Objects.requireNonNull(expressionParser, "expressionParser"),
                Objects.requireNonNull(nameToTemplate, "nameToTemplate"),
                Objects.requireNonNull(lineEnding, "lineEnding"),
                Objects.requireNonNull(expressionEvaluationContext, "expressionEvaluationContext")
        );
    }

    private BasicTemplateContext(final Function<TextCursor, Template> expressionParser,
                                 final Function<TemplateValueName, Template> nameToTemplate,
                                 final LineEnding lineEnding,
                                 final ExpressionEvaluationContext expressionEvaluationContext) {
        this.expressionParser = expressionParser;
        this.nameToTemplate = nameToTemplate;
        this.lineEnding = lineEnding;
        this.expressionEvaluationContext = expressionEvaluationContext;
    }

    private final static char BRACE_OPEN = '{';
    private final static char DOLLAR_SIGN = '$';
    private final static char BRACE_CLOSE = '}';

    private final static int MODE_TEXT = 1;
    private final static int MODE_BACKSLASH = 2;
    private final static int MODE_OPEN_BRACE = 3;

    @Override
    public Template parse(final TextCursor text) {
        Objects.requireNonNull(text, "text");

        final List<Template> templates = Lists.array();

        int mode = MODE_TEXT;
        final StringBuilder b = new StringBuilder();
        TextCursorLineInfo dollarSignLineInfo = null;

        while (text.isNotEmpty()) {
            final char c = text.at();

            switch (mode) {
                case MODE_TEXT:
                    dollarSignLineInfo = null;

                    switch (c) {
                        case '\\':
                            mode = MODE_BACKSLASH;
                            break;
                        case DOLLAR_SIGN:
                            mode = MODE_OPEN_BRACE;
                            dollarSignLineInfo = text.lineInfo();
                            break;
                        default:
                            b.append(c);
                            break;
                    }
                    text.next();
                    break;
                case MODE_BACKSLASH:
                    b.append(c);
                    text.next();
                    mode = MODE_TEXT;
                    break;
                case MODE_OPEN_BRACE:
                    text.next();

                    addIfNotEmpty(
                            b,
                            templates
                    );

                    switch (c) {
                        case BRACE_OPEN:
                            // ${
                            templates.add(
                                    this.expression(text)
                            );

                            mode = MODE_TEXT;
                            break;
                        default:
                            throw dollarSignLineInfo.invalidCharacterException()
                                    .get();
                    }
                    break;
                default:
                    throw new NeverError("Invalid mode=" + mode);
            }
        }

        addIfNotEmpty(
                b,
                templates
        );

        return Templates.collection(templates);
    }

    private static void addIfNotEmpty(final StringBuilder b,
                                      final List<Template> templates) {
        if (b.length() > 0) {
            templates.add(
                    Templates.string(
                            b.toString()
                    )
            );
            b.setLength(0);
        }
    }

    /**
     * Calls the provided parser and then asserts a '}' follows.
     */
    @Override
    public Template expression(final TextCursor text) {
        Objects.requireNonNull(text, "text");

        final Template template = this.expressionParser.apply(text);

        if (text.isEmpty()) {
            throw new IllegalArgumentException("Incomplete expression");
        } else {
            final char c = text.at();
            switch (c) {
                case BRACE_CLOSE:
                    text.next();
                    break;
                default:
                    throw text.lineInfo()
                            .invalidCharacterException()
                            .get();
            }
        }

        return template;
    }

    private final Function<TextCursor, Template> expressionParser;

    @Override
    public String evaluate(final Expression expression) {
        return BasicTemplateContextCycleTemplateContext.with(this)
                .evaluate(expression);
    }

    // see BasicTemplateContextCycleTemplateContext
    final ExpressionEvaluationContext expressionEvaluationContext;

    @Override
    public String templateValue(final TemplateValueName name) {
        return BasicTemplateContextCycleTemplateContext.with(this)
                .templateValue(name);
    }

    // @see BasicTemplateContextCycleTemplateContext
    final Function<TemplateValueName, Template> nameToTemplate;

    final LineEnding lineEnding;

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
