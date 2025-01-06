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

import walkingkooka.collect.list.Lists;
import walkingkooka.text.printer.Printer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A {@link Template} that is a container after a template in string form is compiled into individual {@link Template}.
 */
final class TemplateCollection implements Template {

    static TemplateCollection with(final List<Template> templates) {
        Objects.requireNonNull(templates, "templates");

        return new TemplateCollection(
                Lists.immutable(templates)
        );
    }

    private TemplateCollection(final List<Template> templates) {
        this.templates = templates;
    }

    @Override
    public void render(final Printer printer,
                       final TemplateContext context) {
        Objects.requireNonNull(printer, "printer");
        Objects.requireNonNull(context, "context");

        for(final Template template : this.templates) {
            template.render(
                    printer,
                    context
            );
        }
    }

    private final List<Template> templates;

    @Override
    public String toString() {
        return this.templates.stream()
                .map(Object::toString)
                .collect(Collectors.joining(""));
    }
}
