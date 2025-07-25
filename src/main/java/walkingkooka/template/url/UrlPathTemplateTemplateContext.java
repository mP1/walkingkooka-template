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

import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.UrlPath;
import walkingkooka.template.Template;
import walkingkooka.template.TemplateContext;
import walkingkooka.template.TemplateValueName;
import walkingkooka.template.Templates;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;

import java.util.List;
import java.util.Objects;

/**
 * A {@link TemplateContext} that handles parsing a {@link UrlPath} template.
 */
final class UrlPathTemplateTemplateContext implements TemplateContext {

    /**
     * Singleton
     */
    static UrlPathTemplateTemplateContext INSTANCE = new UrlPathTemplateTemplateContext();

    private UrlPathTemplateTemplateContext() {
    }

    /**
     * Parses the given url path template.<br>
     * Each path separator will add a {@link Templates#string(String)},
     * a path component will add another {@link Templates#string(String)}.
     * Placeholders must occupy a complete path segment.
     */
    @Override
    public Template parseTemplate(final TextCursor text) {
        Objects.requireNonNull(text, "text");

        final char separator = UrlPath.SEPARATOR.character();
        final List<Template> templates = Lists.array();

        final int MODE_SEPARATOR_OR_VALUE_NAME_OR_TEXT = 1;
        final int MODE_SEPARATOR = 2;
        final int MODE_VALUE_NAME_OR_TEXT = 3;
        final int MODE_VALUE_NAME_OPEN_BRACE = 4;
        final int MODE_VALUE_NAME = 5;
        final int MODE_TEXT = 6;

        int mode = MODE_SEPARATOR_OR_VALUE_NAME_OR_TEXT;

        StringBuilder token = null;
        int position = 0;

        while (text.isNotEmpty()) {
            final char c = text.at();

            switch (mode) {
                case MODE_SEPARATOR_OR_VALUE_NAME_OR_TEXT:
                    if (separator == c) {
                        text.next();
                        position++;

                        templates.add(SEPARATOR);
                    }
                    mode = MODE_VALUE_NAME_OR_TEXT;
                    break;
                case MODE_SEPARATOR:
                    if (c != separator) {
                        text.end();
                        throw new InvalidCharacterException(
                            text.text(),
                            position
                        );
                    }
                    templates.add(SEPARATOR);
                    text.next();
                    position++;
                    mode = MODE_VALUE_NAME_OR_TEXT;

                    token = null;
                    break;
                case MODE_VALUE_NAME_OR_TEXT:
                    if ('$' == c) {
                        mode = MODE_VALUE_NAME_OPEN_BRACE;
                        text.next();
                    } else {
                        token = new StringBuilder();
                        mode = MODE_TEXT;
                    }
                    break;
                case MODE_VALUE_NAME_OPEN_BRACE:
                    if ('{' == c) {
                        token = new StringBuilder();
                        text.next();
                        position++;

                        mode = MODE_VALUE_NAME;
                        break;
                    }

                    text.end();
                    throw new InvalidCharacterException(
                        text.text(),
                        position
                    );
                case MODE_VALUE_NAME:
                    text.next();
                    position++;

                    if ('}' == c) {
                        templates.add(
                            Templates.templateValueName(
                                TemplateValueName.with(token.toString())
                            )
                        );
                        token = null;

                        mode = MODE_SEPARATOR;
                        break;
                    }
                    token.append(c);
                    break;
                case MODE_TEXT:
                    if (separator == c) {
                        templates.add(
                            this.templateText(
                                token.toString()
                            )
                        );
                        token = null;
                        mode = MODE_SEPARATOR;
                        break;
                    }
                    if (c < ' ' || Character.isWhitespace(c)) {
                        throw new InvalidCharacterException(
                            text.text(),
                            position
                        );
                    }
                    token.append(c);
                    text.next();
                    position++;
                    break;
                default:
                    throw new IllegalStateException("Invalid mode: " + mode);
            }
        }

        switch (mode) {
            case MODE_SEPARATOR_OR_VALUE_NAME_OR_TEXT:
                templates.add(EMPTY);
                break;
            case MODE_SEPARATOR:
                break;
            case MODE_VALUE_NAME_OR_TEXT:
                break;
            case MODE_VALUE_NAME_OPEN_BRACE:
            case MODE_VALUE_NAME:
                throw new IllegalStateException("Incomplete value name");
            case MODE_TEXT:
                templates.add(
                    this.templateText(
                        token.toString()
                    )
                );
                break;
            default:
                throw new IllegalStateException("Invalid mode: " + mode);

        }

        return this.templateCollection(templates);
    }

    // @VisibleForTesting
    final static Template EMPTY = Templates.string(UrlPath.ROOT.value());

    // @VisibleForTesting
    final static Template SEPARATOR = Templates.string(UrlPath.SEPARATOR.string());

    @Override
    public Template parseTemplateExpression(final TextCursor text) {
        final StringBuilder name = new StringBuilder();

        while (text.isNotEmpty()) {
            final char c = text.at();
            if ('}' == c) {
                break;
            }
            name.append(c);
        }

        return Templates.templateValueName(
            TemplateValueName.with(name.toString())
        );
    }

    @Override
    public Template templateCollection(final List<Template> templates) {
        return Templates.collection(templates);
    }

    @Override
    public Template templateText(final String text) {
        return Templates.string(text);
    }

    @Override
    public String templateValue(final TemplateValueName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String evaluateAsString(Expression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
