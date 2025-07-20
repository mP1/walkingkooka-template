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

import walkingkooka.text.printer.Printer;
import walkingkooka.tree.expression.Expression;

import java.util.Objects;

/**
 * A {@link Template} that renders or inserts {@link String string value} for a given {@link Expression}.
 */
final class ExpressionTemplate implements Template {

    static ExpressionTemplate with(final Expression expression) {
        Objects.requireNonNull(expression, "expression");

        return new ExpressionTemplate(expression);
    }

    private ExpressionTemplate(final Expression expression) {
        this.expression = expression;
    }

    @Override
    public void render(final Printer printer,
                       final TemplateContext context) {
        Objects.requireNonNull(printer, "printer");
        Objects.requireNonNull(context, "context");

        printer.print(
                context.evaluateAsString(this.expression)
        );
    }

    // Value............................................................................................................

    @Override
    public Expression value() {
        return this.expression;
    }

    private final Expression expression;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.expression.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof ExpressionTemplate && this.equals0((ExpressionTemplate) other);
    }

    private boolean equals0(final ExpressionTemplate other) {
        return this.expression.equals(other.expression);
    }

    @Override
    public String toString() {
        return TemplateContext.EXPRESSION_OPEN + this.expression + TemplateContext.EXPRESSION_CLOSE;
    }
}
