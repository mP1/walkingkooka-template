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

package walkingkooka.template.url;

import walkingkooka.net.UrlPathName;
import walkingkooka.template.Template;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class UrlPathTemplateValues implements TreePrintable {

    static UrlPathTemplateValues with(final Template template,
                                      final List<Object> templateComponents,
                                      final List<UrlPathName> path) {
        return new UrlPathTemplateValues(
            template,
            templateComponents,
            path
        );
    }

    private UrlPathTemplateValues(final Template template,
                                  final List<Object> templateComponents,
                                  final List<UrlPathName> path) {
        this.template = template;
        this.templateComponents = templateComponents;
        this.path = path;
    }

    public <T> Optional<T> get(final TemplateValueName name,
                               final Function<String, T> parser) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(parser, "parser");

        T value = null;

        int pathComponentIndex = 0;
        for (final Object component : this.templateComponents) {
            if (pathComponentIndex >= this.path.size()) {
                continue;
            }

            if (component instanceof TemplateValueName) {
                final UrlPathName pathComponent = this.path.get(pathComponentIndex);
                if (null != pathComponent) {
                    value = parser.apply(pathComponent.value());
                }
            } else {
                if (component instanceof String) {
                    if (pathComponentIndex > 0 && UrlPathTemplate.PATH_SEPARATOR_STRING.equals(component)) {
                        pathComponentIndex--;
                    }
                } else {
                    throw new IllegalStateException("Unknown template component " + component);
                }
            }

            pathComponentIndex++;
        }

        return Optional.ofNullable(value);
    }

    private final List<Object> templateComponents;

    /**
     * The individual {@link UrlPathName}.
     */
    private final List<UrlPathName> path;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "template=" + this.template + " path=" + this.path;
    }

    private final Template template;

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            printer.println("template");
            printer.indent();
            {
                this.template.printTree(printer);
            }
            printer.outdent();

            printer.println("components");
            printer.indent();
            {
                for (final Object component : this.templateComponents) {
                    TreePrintable.printTreeOrToString(
                        component,
                        printer
                    );
                    printer.lineStart();
                }
            }
            printer.outdent();

            printer.println("path");
            printer.indent();
            {
                TreePrintable.printTreeOrToString(
                    this.path,
                    printer
                );
                printer.lineStart();
            }
            printer.outdent();
        }
        printer.outdent();
    }
}
