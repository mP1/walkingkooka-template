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
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Set;

public interface TemplateTesting extends TreePrintableTesting {

    default void renderAndCheck(final Template template,
                                final TemplateContext context,
                                final String expected) {
        final StringBuilder printed = new StringBuilder();

        try (final Printer printer = Printers.stringBuilder(printed, LineEnding.NL)) {
            template.render(
                    printer,
                    context
            );
        }
        this.checkEquals(
                expected,
                printed.toString()
        );
    }

    // templateValueNames...............................................................................................

    default void templateValueNamesAndCheck(final Template template,
                                            final TemplateValueName... names) {
        this.templateValueNamesAndCheck(
            template,
            Set.of(names)
        );
    }

    default void templateValueNamesAndCheck(final Template template,
                                            final Set<TemplateValueName> names) {
        this.checkEquals(
            names,
            template.templateValueNames(),
            template::toString
        );
    }
}
