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

import java.util.Objects;

/**
 * A {@link Template} that renders or inserts the value for a given {@link TemplateValueName}.
 */
final class TemplateValueNameTemplate implements Template {

    static TemplateValueNameTemplate with(final TemplateValueName name) {
        Objects.requireNonNull(name, "name");

        return new TemplateValueNameTemplate(name);
    }

    private TemplateValueNameTemplate(final TemplateValueName name) {
        this.name = name;
    }

    @Override
    public void render(final Printer printer,
                       final TemplateContext context) {
        Objects.requireNonNull(printer, "printer");
        Objects.requireNonNull(context, "context");

        printer.print(
            context.templateValue(this.name)
        );
    }

    // Value............................................................................................................

    @Override
    public TemplateValueName value() {
        return this.name;
    }

    private final TemplateValueName name;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof TemplateValueNameTemplate && this.equals0((TemplateValueNameTemplate) other);
    }

    private boolean equals0(final TemplateValueNameTemplate other) {
        return this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return TemplateContext.EXPRESSION_OPEN + this.name + TemplateContext.EXPRESSION_CLOSE;
    }
}
