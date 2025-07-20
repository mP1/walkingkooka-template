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

import walkingkooka.Value;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.Printer;
import walkingkooka.text.printer.Printers;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.Expression;

import java.util.List;

/**
 * A template handles rendering a template in {@link String} by printing to a {@link Printer}.
 * The value may be one of several possible values such as
 * <ul>
 *     <li>{@link String} if the template holds and renders some text</li>
 *     <li>{@link Expression} if the template holds an expression and evaluates it when rendered.</li>
 *     <li>{@link TemplateValueName} if the template holds an {@link TemplateValueName} and renders the value for this name.</li>
 *     <li>{@link List} if it is a collection of templates, each rendered in series</li>
 *     <li>Other templates are free to return their own important value.</li>
 * </ul>
 *
 * {@link String} with the template holds text, or the {@link}
 */
public interface Template extends Value<Object>,
    TreePrintable {

    /**
     * Render this template by printing text.
     */
    void render(final Printer printer,
                final TemplateContext context);

    /**
     * Helper that invokes {@link #render(Printer, TemplateContext)}, returning the complete {@link String text}.
     */
    default String renderToString(final LineEnding lineEnding,
                                  final TemplateContext context) {
        final StringBuilder builder = new StringBuilder();

        try (final Printer printer = Printers.stringBuilder(builder, lineEnding)) {
            this.render(printer, context);
            printer.flush();
        }

        return builder.toString();
    }
}
