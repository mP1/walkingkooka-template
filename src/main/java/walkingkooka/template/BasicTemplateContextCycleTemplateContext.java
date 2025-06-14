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

import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Wraps another {@link TemplateContext} and if a {@link TemplateValueName} returned {@link Template} references to the same {@link TemplateValueName}.
 */
final class BasicTemplateContextCycleTemplateContext implements TemplateContext {

    static BasicTemplateContextCycleTemplateContext with(final BasicTemplateContext context) {
        return new BasicTemplateContextCycleTemplateContext(
                Objects.requireNonNull(context, "context")
        );
    }

    private BasicTemplateContextCycleTemplateContext(final BasicTemplateContext context) {
        this.context = context;
    }

    @Override
    public Template parseTemplate(final TextCursor text) {
        return this.context.parseTemplate(text);
    }

    @Override
    public Template parseTemplateExpression(final TextCursor text) {
        return this.context.parseTemplateExpression(text);
    }

    @Override
    public String evaluateAsString(final Expression expression) {
        final ExpressionEvaluationContext context = this.context.expressionEvaluationContext;

        return context.convertOrFail(
                context.enterScope(this::scopedExpressionReference)
                        .evaluateExpression(expression),
                String.class
        );
    }

    private Optional<Optional<Object>> scopedExpressionReference(final ExpressionReference reference) {
        Optional<Optional<Object>> result;

        if (reference instanceof TemplateValueName) {
            result = Optional.of(
                    Optional.of(
                            this.templateValue((TemplateValueName) reference)
                    )
            );
        } else {
            result = this.context.expressionEvaluationContext.reference(reference);
        }

        return result;
    }

    @Override
    public String templateValue(final TemplateValueName name) {
        Objects.requireNonNull(name, "name");

        final Set<TemplateValueName> cycles = this.cycles;
        if (false == cycles.add(name)) {
            // TODO introduce custom exception with a Set<TemplateValueName> property
            final String separator = " -> ";

            // Cycle detected \"Abc\" -> \"Def\" -> \"Ghi\" -> \"Abc\"
            throw new IllegalStateException(
                    "Cycle detected " +
                            cycles.stream()
                                    .map(TemplateValueName::nameInQuotes)
                                    .collect(Collectors.joining(separator)) +
                            separator +
                            name.nameInQuotes()
            );
        }

        final BasicTemplateContext context = this.context;
        final Template template = context.nameToTemplate.apply(name);
        if (null == template) {
            throw new IllegalStateException("Missing template for " + name);
        }

        final String rendered = template.renderToString(
                context.lineEnding,
                this
        );

        cycles.remove(name);

        return rendered;
    }

    // @see BasicTemplateContext
    final Set<TemplateValueName> cycles = new LinkedHashSet<>();

    private final BasicTemplateContext context;

    @Override
    public String toString() {
        return this.context.toString();
    }
}
