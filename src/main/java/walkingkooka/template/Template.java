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
import walkingkooka.text.printer.Printer;
import walkingkooka.text.printer.Printers;

/**
 * A template handles rendering a template in {@link String} by printing to a {@link Printer}.
 */
public interface Template {

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
