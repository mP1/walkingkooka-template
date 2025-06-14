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

import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;

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

    @Override
    public Template parse(final TextCursor text) {
        return this.parseTextCursor(
                text
        );
    }

    /**
     * Calls the provided parser and then asserts a '}' follows.
     */
    @Override
    public Template parseTemplateExpression(final TextCursor text) {
        Objects.requireNonNull(text, "text");

        final Template template = this.expressionParser.apply(text);

        if (text.isEmpty()) {
            throw new IllegalArgumentException("Incomplete expression");
        } else {
            final char c = text.at();
            switch (c) {
                case TemplateContextParseTextCursor.BRACE_CLOSE:
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
    public String evaluateAsString(final Expression expression) {
        return BasicTemplateContextCycleTemplateContext.with(this)
                .evaluateAsString(expression);
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
