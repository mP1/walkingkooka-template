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

import walkingkooka.collect.list.Lists;
import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlPathName;
import walkingkooka.template.Template;
import walkingkooka.template.TemplateContext;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

// /api/spreadsheet/${SpreadsheetId}/cell/${SpreadsheetExpressionReference}/formatter-edit/${SpreadsheetFormatterSelector}
//
// /api/spreadsheet/1/cell/A1/formatter-edit/abc/def
//
// SpreadsheetId=1
// SpreadsheetExpressionReference=A1
// SpreadsheetFormatterSelector=/abc/def
public final class UrlPathTemplate implements Template {

    public static UrlPathTemplate parse(final String template) {
        Objects.requireNonNull(template, "template");

        return new UrlPathTemplate(
            UrlPathTemplateTemplateContext.INSTANCE.parseTemplateString(template)
        );
    }

    // @VisibleForTesting
    UrlPathTemplate(final Template template) {
        this.template = template;
    }

    @Override
    public void render(final Printer printer,
                       final TemplateContext context) {
        this.template.render(
            printer,
            context
        );
    }

    @Override
    public Template value() {
        return this.template;
    }

    // @VisibleForTesting
    final Template template;

    // UrlPathTemplate..................................................................................................

    final static String PATH_SEPARATOR_STRING = UrlPath.SEPARATOR.string();

    /**
     * Compares the given path with the template, returning a {@link UrlPathTemplateValues} to extract values if a match,
     * otherwise {@link Optional#empty()}.
     */
    public Optional<UrlPathTemplateValues> tryPrepareValues(final UrlPath path) {
        Objects.requireNonNull(path, "path");

        final Template template = this.template;

        final List<Object> templateComponents = Lists.array();

        final Object templateValue = template.value();
        if (templateValue instanceof List) {
            for (final Template component : (List<Template>) templateValue) {
                templateComponents.add(
                    component.value()
                );
            }
        } else {
            if (false == templateValue instanceof String && false == templateValue instanceof TemplateValueName) {
                throw new IllegalArgumentException("Invalid template value " + templateValue);
            }

            templateComponents.add(templateValue);
        }

        final int templateComponentsCount = templateComponents.size();

        final List<UrlPathName> names = path.namesList();
        final int nameCount = names.size();
        int p = 0;
        boolean requirePathSeparator = path.isStartsWithSeparator();
        boolean matched = true;

        for (final Object templateComponent : templateComponents) {
            if (false == matched) {
                break;
            }
            if (p >= templateComponentsCount) {
                matched = true;
                break;
            }
            if (p >= nameCount) {
                matched = false;
                break;
            }

            if (requirePathSeparator) {
                matched = PATH_SEPARATOR_STRING.equals(templateComponent);
                requirePathSeparator = false;
                if (0 == p) {
                    p++;
                }
                continue;
            }

            if (templateComponent instanceof String) {
                matched = templateComponent.equals(
                    names.get(p)
                        .value()
                );
            } else {
                if (false == templateComponent instanceof TemplateValueName) {
                    throw new IllegalArgumentException("Invalid template component: " + templateComponent.getClass().getSimpleName() + "=" + templateComponent);
                }
                // TemplateValueName ignore goto next
                matched = true;
            }
            p++;
            requirePathSeparator = true;
        }

        return Optional.ofNullable(
            matched ?
                UrlPathTemplateValues.with(
                    template,
                    templateComponents,
                    names
                ) :
                null
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.template.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof UrlPathTemplate && this.equals0((UrlPathTemplate) other);
    }

    private boolean equals0(final UrlPathTemplate other) {
        return this.template.equals(other.template);
    }

    @Override
    public String toString() {
        return this.template.toString();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            this.template.printTree(printer);
        }
        printer.outdent();
    }
}
