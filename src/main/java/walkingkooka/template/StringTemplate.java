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

import walkingkooka.collect.set.Sets;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printer;

import java.util.Objects;
import java.util.Set;

/**
 * A {@link Template} that renders the text given to its factory.
 */
final class StringTemplate implements Template {

    /**
     * Empty singleton.
     */
    // @VisibleForTesting
    final static StringTemplate EMPTY = new StringTemplate("");

    static StringTemplate with(final String text) {
        Objects.requireNonNull(text, "text");

        return text.isEmpty() ?
                EMPTY :
                new StringTemplate(text);
    }

    private StringTemplate(final String text) {
        this.text = text;
    }

    @Override
    public void render(final Printer printer,
                       final TemplateContext context) {
        Objects.requireNonNull(printer, "printer");
        Objects.requireNonNull(context, "context");

        printer.print(this.text);
    }

    @Override
    public Set<TemplateValueName> templateValueNames() {
        return Sets.empty();
    }

    // Value............................................................................................................

    @Override
    public String value() {
        return this.text;
    }

    private final String text;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.text.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof StringTemplate && this.equals0((StringTemplate) other);
    }

    private boolean equals0(final StringTemplate other) {
        return this.text.equals(other.text);
    }

    @Override
    public String toString() {
        return this.text.replace("\\", "\\\\");
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            printer.println(
                CharSequences.quote(this.text)
            );
        }
        printer.outdent();
    }
}
