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
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printer;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link Template} that is a container after a template in string form is compiled into individual {@link Template}.
 */
final class TemplateCollection implements Template {

    static Template with(final List<Template> templates) {
        Objects.requireNonNull(templates, "templates");

        final List<Template> copy = Lists.array();

        for (final Template template : templates) {
            if (template instanceof TemplateCollection) {
                copy.addAll(
                    ((TemplateCollection) template)
                        .templates
                );
            } else {
                copy.add(template);
            }
        }

        final Template templateTemplate;

        switch (copy.size()) {
            case 0:
                templateTemplate = Templates.string("");
                break;
            case 1:
                templateTemplate = copy.get(0);
                break;
            default:
                templateTemplate = new TemplateCollection(
                    Lists.immutable(copy)
                );
                break;
        }

        return templateTemplate;
    }

    private TemplateCollection(final List<Template> templates) {
        this.templates = templates;
    }

    @Override
    public void render(final Printer printer,
                       final TemplateContext context) {
        Objects.requireNonNull(printer, "printer");
        Objects.requireNonNull(context, "context");

        for (final Template template : this.templates) {
            template.render(
                    printer,
                    context
            );
        }
    }

    @Override
    public Set<TemplateValueName> templateValueNames() {
        if (null == this.templateValueNames) {
            final Set<TemplateValueName> templateValueNames = SortedSets.tree();

            for (final Template template : this.templates) {
                templateValueNames.addAll(
                    template.templateValueNames()
                );
            }

            this.templateValueNames = Sets.immutable(templateValueNames);
        }
        return this.templateValueNames;
    }

    private Set<TemplateValueName> templateValueNames;

    // Value............................................................................................................

    @Override
    public List<Template> value() {
        return this.templates;
    }

    private final List<Template> templates;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.templates.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof TemplateCollection && this.equals0((TemplateCollection) other);
    }

    private boolean equals0(final TemplateCollection other) {
        return this.templates.equals(other.templates);
    }

    @Override
    public String toString() {
        return this.templates.stream()
                .map(Object::toString)
                .collect(Collectors.joining(""));
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            for (final Template template : this.templates) {
                template.printTree(printer);
            }
        }
        printer.outdent();
    }
}
