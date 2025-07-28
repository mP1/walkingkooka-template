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

import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlPathName;
import walkingkooka.template.Template;
import walkingkooka.template.TemplateContext;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class UrlPathTemplateValues implements Value<List<Object>>,
    TreePrintable {

    static UrlPathTemplateValues with(final Template template,
                                      final List<Object> templateComponents,
                                      final UrlPath path,
                                      final List<UrlPathName> pathComponents) {
        return new UrlPathTemplateValues(
            template,
            templateComponents,
            path,
            pathComponents
        );
    }

    private UrlPathTemplateValues(final Template template,
                                  final List<Object> templateComponents,
                                  final UrlPath path,
                                  final List<UrlPathName> pathComponents) {
        this.template = template;
        this.templateComponents = Lists.immutable(templateComponents);
        this.path = path;
        this.pathComponents = pathComponents;
    }

    /**
     * Note the {@link String} given to the parser will most likely include a starting slash, except if the path is
     * missing a leading slash.
     */
    public <T> Optional<T> get(final TemplateValueName name,
                               final Function<String, T> parser) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(parser, "parser");

        T value = null;

        int templateIndex = 0;
        final int lastTemplateComponentIndex = this.templateComponents.size() - 1;

        int pathComponentIndex = 0;

        for (final Object templateComponent : this.templateComponents) {
            if (pathComponentIndex >= this.pathComponents.size()) {
                value = this.parse(
                    parser,
                    "",
                    templateComponent
                );
                break;
            }

            if (name.equals(templateComponent)) {
                final UrlPathName pathComponent = this.pathComponents.get(pathComponentIndex);
                if (null != pathComponent) {
                    String stringValue;

                    if (lastTemplateComponentIndex == templateIndex) {
                        final UrlPath path = this.path;

                        stringValue = path.pathAfter(
                            Math.max(
                                0,
                                path.isStartsWithSeparator() ?
                                    pathComponentIndex - 1 :
                                    pathComponentIndex
                            )
                        ).value();
                    } else {
                        stringValue = pathComponent.value();
                    }

                    value = this.parse(
                        parser,
                        stringValue,
                        templateComponent
                    );
                    break;
                }
            } else {
                if (templateComponent instanceof String) {
                    if (pathComponentIndex > 0 && UrlPathTemplate.PATH_SEPARATOR_STRING.equals(templateComponent)) {
                        pathComponentIndex--;
                    }
                }
            }

            pathComponentIndex++;
            templateIndex++;
        }

        return Optional.ofNullable(value);
    }

    private <T> T parse(final Function<String, T> parser,
                        final String stringValue,
                        final Object templateComponent) {
        try {
            return parser.apply(stringValue);
        } catch (final RuntimeException cause) {
            // "Extract ${value1}=BadInteger in /BadInteger/path2, For input string: \"BadInteger\""
            throw new IllegalArgumentException(
                "Extract " +
                    TemplateContext.EXPRESSION_OPEN +
                    templateComponent +
                    TemplateContext.EXPRESSION_CLOSE +
                    "=" + stringValue +
                    " in " +
                    this.path +
                    ", " +
                    cause.getMessage(),
                cause
            );
        }
    }

    /**
     * The individual {@link UrlPathName}.
     */
    private final List<UrlPathName> pathComponents;

    // Value............................................................................................................

    /**
     * Returns the components of the template. This will be useful to deconstruct the template into components and discover named values.
     */
    @Override
    public List<Object> value() {
        return this.templateComponents;
    }

    private final List<Object> templateComponents;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "template=" + this.template + " path=" + this.path;
    }

    private final Template template;

    private final UrlPath path;

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
                printer.println(
                    this.path.value()
                );
            }
            printer.outdent();
        }
        printer.outdent();
    }
}
